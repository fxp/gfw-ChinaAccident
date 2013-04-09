package controllers.admin;

import com.google.gson.Gson;
import controllers.CRUD;
import models.AccidentTag;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Xiaoping
 * Date: 13-4-7
 * Time: 下午9:45
 * To change this template use File | Settings | File Templates.
 */
public class AccidentTags extends CRUD {

    @OnApplicationStart
    public static class Loader extends Job {
        @Override
        public void doJob() throws Exception {
            Logger.info("import accident tag start");
            List<String> lines = FileUtils.readLines(new File("conf/accident_tag"));
            for (String line : lines) {
                int splitPos = line.indexOf(":");
                String tagName = line.substring(0, splitPos);
                line = line.substring(splitPos + 1);
                String[] parts = line.split(",");
                AccidentTag tag = AccidentTag.q().filter("tag", tagName).get();
                if (tag == null) {
                    tag = new AccidentTag(tagName);
                }
                for (String part : parts) {
                    tag.related.add(part);
                }
                AccidentTag accident = tag.save();
                Logger.info("accident added,%s", new Gson().toJson(accident));

            }
            Logger.info("import accident tag complete");
        }
    }

}
