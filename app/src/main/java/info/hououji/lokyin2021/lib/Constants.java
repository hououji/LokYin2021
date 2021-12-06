package info.hououji.lokyin2021.lib;

public interface Constants {
    //										  0    1    2    3    4
    public static String[] elements = new String[]{"木","火","土","金","水"} ;
    public static String[] skys = new String[]{"甲","乙","丙","丁","戊","己","庚","辛","壬","癸", ""} ;
    public static int[] skysEle = new int[]{0,0,1,1,2,2,3,3,4,4, -1} ;
    public static int[] sky2ground = new int[]{2,4,5,7,5,7,8,10,11,1} ;
    public static int[] sky2lead1 = new int[]{2,0,11,11,2,0,2,6,5,5} ;
    public static int[] sky2lead2 = new int[]{7,8,9,9,7,8,7,2,3,3} ;
    //										  0   1   2     3         4    5    6    7         8    9    10   11
    public static String[] grounds = new String[]{"子","丑","寅","卯",    "辰","巳","午","未",     "申","酉","戌","亥"} ;
    public static int[] groundsEle = new int[]{4,2,0,0,  2,1,1,2,  3,3,2,4} ;
    public static int[] ground2skyA = new int[]{10,9,0,10,   1, 2, 10, 3,   6, 10, 7,8 } ; // must use with the skyEle
    public static int[] ground2skyB = new int[]{10,10,10,10,   10, 4, 10,5,   10, 10, 10,10 } ;// must use with the skyEle
    public static int[] punish = new int[]{ 3,10,5,0,4,8,6,1,2,9,7,11 } ;
    public static String[] gods = new String[]{"貴","蛇","朱","合","勾","青","空","虎","常","玄","陰","后"} ;
    public static String[] SolarTerm = new String[] { "小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至" };
//    public static long sTermInfo[] = new long[]{0,21208,42467,63836,85337,107014,128867,150921,173149,195551,218072,240693,263343,285989,308563,331033,353350,375494,397447,419210,440795,462224,483532,504758} ;
    public static String[] sevenBodyName = new String[]{"日","月","水","金","火","木","土"} ;
}
