package models.loc;

import com.google.code.morphia.annotations.Entity;
import play.modules.morphia.Model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Xiaoping
 * Date: 13-3-9
 * Time: 下午10:58
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Geo extends Model {
    public List<AddressComponent> address_components;
    public String formatted_address;
    public Geometry geometry;

}
