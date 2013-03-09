package models.loc;

import com.google.code.morphia.annotations.Entity;
import play.modules.morphia.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Xiaoping
 * Date: 13-3-9
 * Time: 下午11:13
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Location extends Model {
    public double lat;
    public double lng;
}
