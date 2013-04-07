package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: fxp
 * Date: 13-4-6
 * Time: PM3:41
 */
public class AccidentTag {

    public static final Map<String, List<String>> TAGS = new HashMap<String, List<String>>() {{
        put("carcrash", new ArrayList<String>() {{

        }});
        put("mine", new ArrayList<String>() {{

        }});
        put("gas", new ArrayList<String>() {{

        }});
        put("building", new ArrayList<String>() {{

        }});
    }};

}
