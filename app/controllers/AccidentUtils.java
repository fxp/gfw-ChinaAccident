package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import models.Accident;
import org.htmlcleaner.TagNode;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.WS;
import utils.PageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: fxp
 * Date: 13-3-7
 * Time: AM2:54
 * To change this template use File | Settings | File Templates.
 */
public class AccidentUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static class GeoResult {
        public List<Geo> results;
        public String status;
    }

    public static class Geo {
        public List<AddressConponent> address_components;
        public String formatted_address;

    }

    public static class AddressConponent {
        public String long_name;
        public String short_name;
    }

    public static void refineAccident(Accident accident) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date date = sdf.parse(accident.dateText);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            accident.dateTime = date.getTime();
            accident.year = cal.get(Calendar.YEAR);
            accident.month = cal.get(Calendar.MONTH);
            accident.day = cal.get(Calendar.DAY_OF_MONTH);

            String[] parts = accident.description.split("，");
            accident.accurateDateText = parts[0];
            accident.accurateDateTime = getAccurateTime(accident);

            accident.positionText = parts[1];

            JsonElement json = WS.url("http://maps.google.com/maps/api/geocode/json")
                    .setParameter("address", accident.positionText)
                    .setParameter("sensor", true)
                    .setParameter("language", "zh-CN")
                    .get().getJson();
            Logger.info("json:%s", json.toString());

            GeoResult result = new Gson().fromJson(json, GeoResult.class);
            accident.positionJson = new Gson().toJson(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Integer getHour(String accurateText) {
        Integer ret = null;
        Pattern p = Pattern.compile("(\\d+)时");
        Matcher m = p.matcher(accurateText);
        if (m.find()) {
            ret = Integer.valueOf(m.group(1));
        } else if (accurateText.contains("零时")) {
            ret = 0;
        }
        return ret;
    }

    private static Integer getMinute(String accurateText) {
        Integer ret = null;
        Pattern p = Pattern.compile("(\\d+)分");
        Matcher m = p.matcher(accurateText);
        if (m.find()) {
            ret = Integer.valueOf(m.group(1));
        }
        return ret;
    }

    private static long getAccurateTime(Accident accident) {
        Logger.debug("get accurate time for accident,%s", accident.accurateDateText);
        Integer hour = getHour(accident.accurateDateText);
        Integer minute = getMinute(accident.accurateDateText);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(accident.dateTime));
        if (hour != null) {
            cal.set(Calendar.HOUR, hour);
            accident.hour = hour;
            Logger.debug("get accurate hour,%s", hour);
        }
        if (minute != null) {
            cal.set(Calendar.MINUTE, minute);
            accident.minute = minute;
            Logger.debug("get accurate minute,%s", minute);
        }
        return cal.getTimeInMillis();
    }

}