package models;

import com.google.code.morphia.annotations.Entity;
import org.apache.commons.lang.StringUtils;
import play.modules.morphia.Model;

import java.util.HashSet;
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

    @Override
    public boolean equals(Object other) {
        return (other instanceof AccidentTag) && (StringUtils.equals(((AccidentTag) other).tag, this.tag));
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }

    @Override
    public String toString() {
        return tag;
    }
}
