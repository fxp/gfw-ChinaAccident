package models;

import com.google.code.morphia.annotations.Entity;
import org.apache.commons.lang.StringUtils;
import play.modules.morphia.Model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: fxp
 * Date: 13-3-7
 * Time: AM12:48
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Accident extends Model {

    public String aId;

    /**
     * All about time
     */
    public String dateText;

    public long dateTime;

    public int year;

    public int month;

    public int day;

    public int hour;

    public int minute;

    public String accurateDateText;

    public long accurateDateTime;

    public String positionText;

    public String positionJson;

    /**
     * Dead
     */
    public int deadCount;

    public String description;

    public Accident(String dateText, int deadCount, String description) {
        this.dateText = dateText;
        this.deadCount = deadCount;
        this.description = description;
        this.aId = dateText.hashCode() + "." + deadCount + "." + description.hashCode();
    }

    public static Accident getByAid(String aId) {
        return Accident.q().filter("aId", aId).first();
    }

}
