package controllers;

import com.google.gson.Gson;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import play.*;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.WS;
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
        }
    }

    public static void index() throws XPatherException, ParseException {
        List<Accident> ret = new ArrayList<Accident>();
        List<Accident> accidents = Accident.q().limit(2).fetchAll();
        for (Accident accident : accidents) {
            AccidentUtils.refineAccident(accident);
            accident.save();
        }
//        renderJSON(accident);
        render(accidents);

    }

}