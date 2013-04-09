package controllers;

import models.Accident;
import models.AccidentTag;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.WS;
import play.modules.morphia.Model;
import play.mvc.Controller;
import utils.PageUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Application extends Controller {

    public static void check() throws Exception {
        new FetchAccidentJob().doJob();
    }

    public static void test() {
        render();
    }

    public static void index(int page, String currentTag) throws XPatherException, ParseException {
        page = (page <= 0) ? 1 : page;
        int size = 10;
        Model.MorphiaQuery query = Accident.q()
                .filter("geo != ", null);
        if (!StringUtils.isEmpty(currentTag)) {
            query.filter("tags", currentTag);
        } else {
            currentTag = "all";
        }
        List<Accident> accidents = query.order("-dateTime").fetch(page, size);
        List<AccidentTag> tags = AccidentTag.findAll();
        render(accidents, page, size, currentTag, tags);
    }

    public static void accidents(int page, int size, boolean complete) {
        Model.MorphiaQuery query = Accident.q().order("-dateTime");
        if (complete) {
            query = query.filter("geo != ", null);
        }
        renderJSON(query.fetch(page, size));
    }

    public static void allAccidents() {
        List<Accident> accidents = Accident.q().order("-dateTime").fetch(10);
        render(accidents);
    }

    public static void checkTag() {
        List<Accident> accidents = Accident.q().order("-dateTime").fetchAll();
        for (Accident accident : accidents) {
            Set<AccidentTag> tags = AccidentUtils.extractTag(accident.description);
            Logger.info("accident:%s,%s", tags, accident.description);
            for (AccidentTag tag : tags) {
                if (accident.tags == null) {
                    accident.tags = new HashSet<String>();
                }
                accident.tags.add(tag.tag);
            }
            accident.save();
        }
    }

    public static void refineAllAccidents() {
        List<Accident> accidents = Accident.q()
                .filter("geo == ", null).fetchAll();
        for (Accident accident : accidents) {
            try {
                AccidentUtils.refineAccident(accident);
                accident.save();
            } catch (ParseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        renderText("complete");
    }

    @Every("10min")
    public static class FetchAccidentJob extends Job {
        @Override
        public void doJob() throws Exception {
            List<Accident> ret = new ArrayList<Accident>();

            String content = WS.url("http://media.chinasafety.gov.cn:8090/iSystem/shigulist.jsp").get().getString();
            TagNode root = PageUtils.cleanPage(content);
            Object[] accidents = root.evaluateXPath("//table[@id='listtable']/tbody/tr[@bgcolor='#ffffff']");
            for (Object obj : accidents) {
                TagNode node = (TagNode) obj;
                Object[] fieldObjs = node.evaluateXPath("//td/text()");

                String dateText = fieldObjs[0].toString();
                int deadCount = Integer.valueOf(fieldObjs[1].toString());
                String description = fieldObjs[2].toString();
                Accident accident = new Accident(dateText, deadCount, description);
                if (Accident.getByAid(accident.aId) == null) {
                    accident = accident.save();
                    ret.add(accident);
                    Logger.info("new accident,%s", accident.description);
                } else {
                    Logger.info("exists accident,%s", accident.description);
                }
            }
            Logger.info("added new accidents,%s", ret.size());
            Logger.info("start refining accidents,%s", ret);
            for (Accident accident : ret) {
                AccidentUtils.refineAccident(accident);
                accident.save();
            }
        }
    }
}