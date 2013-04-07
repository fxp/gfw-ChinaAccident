package controllers.admin;

import controllers.CRUD;
import org.apache.commons.io.FileUtils;
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
            List<String> lines = FileUtils.readLines(new File("conf/accident_tag"));
            for (String line : lines) {
                int splitPos = line.indexOf(":");
                String tag = line.substring(0, splitPos);
                line = line.substring(splitPos + 1);
                String[] parts = line.split(",");
                System.out.println("tag:" + tag + ",line:" + line);
            }
        }
    }
}
