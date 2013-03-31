package controllers;

import com.google.gson.Gson;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import play.*;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.WS;
import play.modules.morphia.Model;
import play.mvc.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import models.*;
import utils.PageUtils;

public class Application extends Controller {

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

    public static void check() throws Exception {
        new FetchAccidentJob().doJob();
    }

    public static List<Accident> cachedAccidents = null;

    public static void index(int page) throws XPatherException, ParseException {
        page = (page <= 0) ? 1 : page;
        int size = 10;
        cachedAccidents = null;
        List<Accident> accidents = cachedAccidents;
        if (cachedAccidents == null) {
            List<Accident> ret = new ArrayList<Accident>();
            accidents = Accident.q()
                    .filter("geo != ", null)
                    .order("-dateTime")
                    .fetch(page, size);
            cachedAccidents = accidents;
        }
        render(accidents, page, size);
    }

    public static void accidents(int page, int size, boolean complete) {
        Model.MorphiaQuery query = Accident.q().order("-dateTime");
        if (complete) {
            query = query.filter("geo != ", null);
        }
        renderJSON(query.fetch(page, size));
    }

    public static void allAccidents() {
        List<Accident> accidents = Accident.q().order("-dateTime").fetchAll();
        render(accidents);
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
}