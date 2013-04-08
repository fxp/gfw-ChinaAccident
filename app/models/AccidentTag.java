package models;

import com.google.code.morphia.annotations.Entity;
import play.modules.morphia.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: fxp
 * Date: 13-4-6
 * Time: PM3:41
 */
@Entity
public class AccidentTag extends Model {

    public String tag;
    public Set<String> related = new HashSet<String>();

    public AccidentTag(String tag) {
        this.tag = tag;
    }
}
