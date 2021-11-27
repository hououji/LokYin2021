package info.hououji.lokyin2021;

import info.hououji.lokyin2021.lib.LokYinGraph;
import java.util.Date ;

public class Common {
    public static LokYinGraph lokYinGraph = new LokYinGraph() ;

    static {
        lokYinGraph.setDate(new Date());
    }

    static Date currentDate = new Date() ;

    public static Date getCurrentDate() {
        return currentDate ;
    }
}
