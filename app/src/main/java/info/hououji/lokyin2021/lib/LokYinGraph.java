package info.hououji.lokyin2021.lib;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;

public class LokYinGraph implements  Constants{

    static final String TAG = "LokYinGraph" ;

    //static Logger log = Logger.getLogger(LokYinGraph.class.getName());
    //static Log log = LogFactory.getLog(LokYinGraph.class) ;
    //static HououjiLogger log = new HououjiLogger(TAG);
    static HououjiLogger log = new HououjiLoggerLocal(TAG);

    SwissEph sw = new SwissEph();



    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss") ;

    static Date firstDay ;
    static Date firstYear;
    static Date first60Day ;
    static long dayLong = 1000 * 60 * 60 * 24 ;
    static long solarReturn =  (long)(365.2421990741 * 24 * 60 * 60 * 1000) ;

    static {
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("Hongkong")) ;
            firstDay = sdf.parse("19111222 124500") ; // it from hongkong calendar
            firstYear = sdf.parse("19240205 095000") ; // it from hongkong calendar

            sdf.setTimeZone(TimeZone.getDefault()) ;
            first60Day = sdf.parse("19120218 000000") ; // 甲子日
        } catch (ParseException e) {
            throw new RuntimeException(e) ;
        }
    }

    public LokYinGraph() {
        sw.swe_set_ephe_path(null);
    }

    Date date ;

    // 0=子, ..., 11=亥
    public int hour ;
    public int monthlead ;
    public int month ;
    public int skyPlane[] = new int[12] ;
    public int skyPlanelead[] = new int[12] ;
    public int fourChapter[][] = new int[4][2] ;// [0][0]=日干
    public int fourEle[][] = new int[4][2];

    public int cycleGround2Sky[] = new int[12] ;
    public int eight[] = new int [4] ;
    public int space[] = new int[2] ;

    public int threePass[] = new int[]{-1,-1,-1} ;

    public String resultSevenBodies[] = new String[7] ;

    String name = "" ;

    public void setDate(Date date) {
        this.date = date ;

        log.debug("timezone:" + TimeZone.getDefault()) ;
        log.debug("date:" + date) ;

        int i;

        Calendar c = Calendar.getInstance() ;
        c.setTimeZone(TimeZone.getDefault());
        c.setTime(date);


        // get year
        //eight[0] = (int)((date.getTime() - firstYear.getTime()) / solarReturn) % 60;
        eight[0] = get60(date.getYear() + 1900 - 1984);


                // get 24
//        long monthRemain = (date.getTime() - firstDay.getTime()) % solarReturn ;
//        log.debug("monthRemain=" + monthRemain) ;
//        for(i=0; i<sTermInfo.length; i++){
//            if( sTermInfo[i]*60000 > monthRemain ) {
//                // bingo
//                break ;
//            }
//        }
//        i = i -2 ; // algroithm adjust

        double[] xp= new double[6];
        StringBuffer serr = new StringBuffer();
        int ret = sw.swe_calc_ut(getJulDay(c),
                SweConst.SE_SUN,
                SweConst.SEFLG_MOSEPH,
                xp,
                serr);
        i = get24((int)(xp[0] / 15) + 5) ;

        if(date.getMonth() ==0 ||
                (date.getMonth() ==1 && i<=1))
        {
            // 新曆年過而未過立春
            eight[0] = get60(eight[0] - 1) ;
        }

        log.debug("SolarTerm=" + SolarTerm[get24(i)]) ;

        month = get12((i + 22) / 2 + 2) ;
        log.debug("月:" + grounds[month]) ;

        // get month
        eight[1] = (eight[0] % 5) * 12 + month ;
        if(month > 10) {
            eight[1] = (eight[1] + 12 ) % 60 ;
        }

        monthlead = get12( (24 - i) / 2 + 1) ;
        log.debug("將:" + grounds[monthlead]) ;

        // 七政
        int[] planets = { SweConst.SE_SUN,
                SweConst.SE_MOON,
                SweConst.SE_MERCURY,
                SweConst.SE_VENUS,
                SweConst.SE_MARS,
                SweConst.SE_JUPITER,
                SweConst.SE_SATURN
//                SweConst.SE_TRUE_NODE
        };

        for(int j=0; j<planets.length; j++) {
            ret = sw.swe_calc_ut(getJulDay(c),
                    planets[j],
                    SweConst.SEFLG_MOSEPH,
                    xp,
                    serr);
            int deg = ((int)xp[0]) % 30 ;
            int min = (int)((xp[0] - (int)xp[0]) * 60) ;
            int castle = (int)(xp[0] / 30) ;
            resultSevenBodies[j] = sevenBodyName[j] + "  " + padleft(""+deg,"0", 2)
                    +grounds[get12(10 - castle)] + padleft(""+min, "0", 2) ;
        }

        long hourRemain = ((date.getTime() - first60Day.getTime()) % dayLong) ;
        log.debug("hourRemain 1:" + hourRemain) ;
        hourRemain = hourRemain / (1000*60*60) ;
        log.debug("hourRemain 2:" + hourRemain) ;
        hour = get12( (int)(hourRemain + 1)/2 ) ;
        log.debug("時:" + grounds[hour]) ;

        // make 4 class
        long datediff = (date.getTime() - first60Day.getTime()) / (1000 * 60 * 60 * 24) ;
        int day = (int) (datediff % 60) ;

        // get day + hours
        eight[2] = day ;
        eight[3] = (day % 5) * 12 + hour ;
        space[0] = ((day / 10 * 10) + 10) % 12 ;
        space[1] = space[0] + 1 ;

        int cycleHead = ((day / 10 * 10 ) % 12) ;
        for(i=0; i< 10; i++) {
            cycleGround2Sky[get12(i + cycleHead)] = i ;
        }
        cycleGround2Sky[get12(cycleHead + 10)] = 10 ;
        cycleGround2Sky[get12(cycleHead + 11)] = 10 ;

        makeThreePass(monthlead-hour, day) ;
        log.debug("課名:" + name) ;

        // make sky lead
        int lead ;
        if(hour >=4 && hour <= 9) {
            // day
            lead = sky2lead1[day%10] ;
            log.debug("day, 貴人在:" + grounds[lead]) ;
        }else{
            // night
            lead = sky2lead2[day%10] ;
            log.debug("night, 貴人在:" + grounds[lead]) ;
        }
        for(i=0;i<12;i++){
            if(skyPlane[i] == lead) break;
        }
        if(i >= 5 && i<=10) {
            // inverse
            for(int j=0;j<12;j++) {
                skyPlanelead[get12(i-j)] = j ;
            }
        }else{
            for(int j=0;j<12;j++) {
                skyPlanelead[get12(i+j)] = j ;
            }
        }

    }

    public void makeThreePass(int skyPlaneStart, int day) {
        int i;

        for(i=0; i<12; i++) {
            skyPlane[get12(i)] = get12(skyPlaneStart + i) ;
        }

        for(i=0; i<fourChapter.length; i++) {
            fourChapter[i] = new int[2] ;
        }


        fourChapter[0][0] = day%10 ;
        fourChapter[0][1] = skyPlane[sky2ground[day%10]] ;
        fourChapter[1][0] = fourChapter[0][1] ;
        fourChapter[1][1] = skyPlane[fourChapter[1][0]] ;
        fourChapter[2][0] = day % 12 ;
        fourChapter[2][1] = skyPlane[fourChapter[2][0]] ;
        fourChapter[3][0] = fourChapter[2][1] ;
        fourChapter[3][1] = skyPlane[fourChapter[3][0]] ;

        // check for special
//		if( skyPlaneStart ==6 && day == 0) threePass[0] = 8 ;
//		if( skyPlaneStart ==1 && day == 3) threePass[0] = 4 ;
//		if( skyPlaneStart ==4 && day == 3) threePass[0] = 7 ;
//		if( skyPlaneStart ==10 && day == 3) threePass[0] = 11 ;
//		if( skyPlaneStart ==6 && day == 4) threePass[0] = 5 ;
//		if( skyPlaneStart ==7 && day == 4) threePass[0] = 0 ;
//		if( skyPlaneStart ==4 && day == 5) threePass[0] = 8 ;
//		if( skyPlaneStart ==7 && day == 5) threePass[0] = 0 ;


        // check 賊剋
        boolean hasHard = false ;
        if( isMutualHard( skysEle[fourChapter[0][0]], groundsEle[fourChapter[0][1]] )) {
            hasHard = true ;
        }
        for(i=1;i<=3;i++) {
            if( isMutualHard( groundsEle[fourChapter[i][0]], groundsEle[fourChapter[i][1]] )) {
                hasHard = true ;
                break ;
            }
        }
        log.debug("賊剋:" + hasHard) ;

        // check 遙剋
        boolean hasLongHard = false ;
        if(hasHard) hasLongHard = true;
        for(i=1;i<3;i++) {
            if( isMutualHard( skysEle[fourChapter[0][0]], groundsEle[fourChapter[i][1]] )) {
                hasLongHard = true ;
                break ;
            }
        }
        log.debug("遙剋:"+hasLongHard) ;

        //伏吟
        if(skyPlane[0] == 0) {
            name = "伏吟" ;
            if( !hasHard ) {
                threePass[0] = fourChapter[(day % 2)*2][1] ;
            }else{
                threePass[0] = fourChapter[0][1] ;
            }
            if(threePass[0] == punish[threePass[0]]) {
                threePass[1] = fourChapter[(day % 2 + 2)%4][1] ;
                if(threePass[1] == punish[threePass[1]]) {
                    threePass[2] = (threePass[1] + 6) % 12 ;
                    name = "杜傳" ;
                }else{
                    threePass[2] = punish[threePass[1]] ;
                }
            }else{
                threePass[1] = punish[threePass[0]] ;
                if(threePass[1] == 0) {
                    // 卯子午
                    name = "杜傳" ;
                    threePass[2] = 6;
                }else{
                    threePass[2] = punish[threePass[1]] ;
                }

            }
            return ;
        }

        // 反吟
        if(skyPlane[0] == 6) {
            if( !hasHard ) {

                name = "反吟" ;

                // 丑日用亥未用巳, 支上馬星為用
                if( (day % 12) == 1 ) {
                    threePass[0] = 11 ;
                }
                if( (day % 12) == 7 ) {
                    threePass[0] = 5 ;
                }

                threePass[1] = fourChapter[2][1] ;
                threePass[2] = fourChapter[0][1] ;

                return ;
            }
            // 有剋正常取用及發傳
        }

        // 別責
        Set<Integer> totalChapter = new HashSet<Integer>() ;
        for(i=0;i<fourChapter.length;i++) {
            totalChapter.add(fourChapter[i][1]) ;
        }
        if(totalChapter.size() == 3) {
            if( (! hasHard) && (!hasLongHard) ) {

                name = "別責" ;

                if(day % 2 == 0) {
                    // 陽日干合上頭
                    threePass[0] = sky2ground[(day + 5) % 10];
                }else{
                    // 柔日支前三合寄
                    threePass[0] = get12(day + 4);
                }
                threePass[1] = fourChapter[0][1] ;
                threePass[2] = fourChapter[0][1] ;
                return ;
            }

        }

        // 八專
        if(fourChapter[0][1] == fourChapter[2][1]) {
            if( ! hasHard) {
                name = "八專" ;
                if(day % 2 == 0) {
                    // 陽日日陽三位前
                    threePass[0] = get12(fourChapter[0][1] + 2);
                }else{
                    // 陰日辰陰逆三位
                    threePass[0] = get12(fourChapter[3][1] - 2);
                }
                threePass[1] = fourChapter[0][1] ;
                threePass[2] = fourChapter[0][1] ;

                return ;
            }
        }

        // 昴星
        if( (!hasHard) && (!hasLongHard) ) {
            name = "昴星" ;
            if(day % 2 == 0) {
                threePass[0] = skyPlane[9] ;
                threePass[1] = fourChapter[3][0] ;
                threePass[2] = sky2ground[fourChapter[0][0]] ;
            }else{
                for(i=0; i<12;i++){
                    if(skyPlane[i] == 9) {
                        threePass[0] = i ;
                        break ;
                    }
                }
                threePass[1] = sky2ground[fourChapter[0][0]] ;
                threePass[2] = fourChapter[3][0] ;
            }

            return ;
        }

        List<Integer> totalHard = new ArrayList<Integer>();
        List<Integer>  totalThief = new ArrayList<Integer>();
        List<Integer>  totalLongHard = new ArrayList<Integer>();
        List<Integer>  totalLongThief = new ArrayList<Integer>();

        for(i=0;i<4;i++) {
            fourEle[i] = new int[2] ;
            fourEle[i][0] = groundsEle[fourChapter[i][0]] ;
            fourEle[i][1] = groundsEle[fourChapter[i][1]] ;
        }
        fourEle[0][0] = skysEle[fourChapter[0][0]] ;

        for(i=0;i<4;i++) {
            if(isHard(fourEle[i][1], fourEle[i][0])) {
                totalHard.add(i) ;
            }
            if(isHard(fourEle[i][0], fourEle[i][1])) {
                totalThief.add(i) ;
            }
            if(isHard(fourEle[0][0], fourEle[i][1] )) {
                totalLongHard.add(i) ;
            }
            if(isHard(fourEle[i][1], fourEle[0][0])) {
                // 課剋日君為賊
                totalLongThief.add(i) ;
            }
        }

        if(totalThief.size() == 1) {
            name = "賊剋" ;
            threePass[0] = fourChapter[totalThief.get(0)][1] ;
        }else if(totalThief.size() >= 2) {
            secondJudge(totalThief, day) ;
        }else{
            // 無賊, 用尅
            if(totalHard.size() == 1) {
                name = "賊剋" ;
                threePass[0] = fourChapter[totalHard.get(0)][1] ;
            }else if(totalHard.size() >= 2) {
                secondJudge(totalHard, day) ;
            }else{
                name = "遙剋" ;
                // 支遙日
                if(totalLongThief.size() == 1) {
                    threePass[0] = fourChapter[totalLongThief.get(0)][1] ;
                }else if(totalLongThief.size() >= 2) {
                    secondJudge(totalLongThief, day) ;
                }else{
                    // 日遙支
                    if(totalLongHard.size() == 1) {
                        threePass[0] = fourChapter[totalLongHard.get(0)][1] ;
                    }else if(totalLongHard.size() >= 2) {
                        secondJudge(totalLongHard, day) ;
                    }
                }
            }
        }

        normalPass() ;

    }

    private void secondJudge(List<Integer> chapters, int day) {
        List<Integer> result = new ArrayList<Integer>() ;
        for(int chapter : chapters) {
            if((day % 2) == (fourChapter[chapter][1] % 2)) {
                result.add(chapter) ;
            }
        }

        if(result.size() == 1) {
            name = "比用" ;
            threePass[0] = fourChapter[result.get(0)][1] ;
            return ;
        }if(result.size()==2 && fourChapter[result.get(0)][1] == fourChapter[result.get(1)][1]) {
            // 俱比俱不比, 皆可作發用
            threePass[0] = fourChapter[result.get(0)][1] ;
            return ;
        }if(result.size() == 0 && chapters.size() == 2 && fourChapter[chapters.get(0)][1] == fourChapter[chapters.get(1)][1]){
            // 俱不比, 皆可作發用
            threePass[0] = fourChapter[chapters.get(0)][1] ;
            return ;
        }

        // 涉害
        if(result.size() > 0) {
            // 兩課比, 一課不比
            chapters = result ;
        }

        if(chapters.size() < 2) {
            log.warn("涉害不是兩課?:" + chapters.size()) ;
            // 不會吧
            return ;
        }
        if(chapters.size() > 2) {
            log.warn("涉害課數量:" + chapters.size()) ;
        }

        if( (fourChapter[chapters.get(0)][1] % 2) != (fourChapter[chapters.get(1)][1] % 2)) {
            log.warn("涉害不同陰陽?") ;
            return ;
        }

        boolean hardNormal ;
        if( 	isHard( fourEle[chapters.get(0)][0], fourEle[chapters.get(0)][1], true )
                &&	isHard( fourEle[chapters.get(1)][0], fourEle[chapters.get(1)][1], true ) )
        {
            hardNormal = true;
        }else if( 	isHard( fourEle[chapters.get(0)][0], fourEle[chapters.get(0)][1], false )
                &&	isHard( fourEle[chapters.get(1)][0], fourEle[chapters.get(1)][1], false ) )
        {
            hardNormal = false;
        }else{
            log.warn("涉害不同賊剋?") ;
            return ;
        }

        name = "涉害" ;
        int hardCount[] = new int[chapters.size()] ;
        int diff = get12(12 - skyPlane[0]) ;

        for(int j=0;j<chapters.size();j++) {
            log.debug("審第 "+chapters.get(j)+" 課涉害") ;
            for(int i=0; i<12; i++) {
                log.debug(grounds[fourChapter[chapters.get(j)][1]] + " 歷 " + grounds[get12(fourChapter[chapters.get(j)][1]  + diff + i)]) ;
                if( isHard( groundsEle[ get12(fourChapter[chapters.get(j)][1] + diff + i) ], fourEle[chapters.get(j)][1], hardNormal ) ) {
                    hardCount[j] ++ ;
                    log.debug("一重剋") ;
                }
                if( isHard( skysEle[ground2skyA[ get12(fourChapter[chapters.get(j)][1] + diff  + i) ]], fourEle[chapters.get(j)][1], hardNormal ) ) {
                    hardCount[j] ++ ;
                    log.debug("寄宮一重剋") ;
                }
                if( isHard( skysEle[ground2skyB[ get12(fourChapter[chapters.get(j)][1] + diff  + i) ]], fourEle[chapters.get(j)][1], hardNormal ) ) {
                    hardCount[j] ++ ;
                    log.debug("寄宮一重剋") ;
                }
                // end condition
                if( fourChapter[chapters.get(j)][1] == get12(fourChapter[chapters.get(j)][1] + diff + i) ) {
                    break;
                }
            }
        }

        int maxChapter = 0 ;
        boolean sameHard = false ;
        for(int j=1;j<chapters.size();j++) {
            if( hardCount[j] > hardCount[maxChapter]) {
                sameHard = false ;
                maxChapter = j ;
            }else if(hardCount[j] == hardCount[maxChapter]) {
                sameHard = true ;
            }
        }

        if( ! sameHard ) {
            threePass[0] = fourChapter[chapters.get(maxChapter)][1] ;
            return ;
        }

        log.warn("涉害相同") ;
        List<Integer> maxChapters = new ArrayList<Integer>() ;
        for(int j=0;j<chapters.size();j++) {
            if(hardCount[j] == hardCount[maxChapter]) {
                maxChapters.add(chapters.get(j)) ;
            }
        }

        int countFirstSeason = 0 ;
        int idxFirstSeason = -1 ;
        for(int j=0; j<maxChapters.size(); j++) {
            if( (fourChapter[maxChapters.get(j)][1] + diff) % 3 == 2 ) {
                // 地盤見孟
                idxFirstSeason = j ;
                countFirstSeason ++ ;
            }
        }

        if(countFirstSeason == 1) {
            log.debug("一課下神為孟") ;
            name = "見機" ;
            threePass[0] = fourChapter[maxChapters.get(idxFirstSeason)][1] ;
            return ;
        }else if(countFirstSeason > 1){
            log.warn("多課下神為孟") ;
            sameHardSameSeason(maxChapters, day) ;
//			if( (day == 4 || day == 34) && fourChapter[0][1] == 11 ) {
//				// 戊辰/戊戌日, 干上亥反吟
//				name = "綴瑕" ;
//				threePass[0] = fourChapter[0][1] ;
//			}

            return ;
        }

        int countiddleSeason = 0 ;
        int idxMiddleSeason = -1 ;
        for(int j=0; j<maxChapters.size(); j++) {
            if( (fourChapter[maxChapters.get(j)][1] + diff) % 3 == 0 ) {
                // 地盤見仲
                idxMiddleSeason = j ;
                countiddleSeason ++ ;
            }
        }

        if(countiddleSeason == 1) {
            log.debug("一課下神為仲") ;
            name = "察微" ;
            threePass[0] = fourChapter[maxChapters.get(idxMiddleSeason)][1] ;
            return ;
        }else if(countiddleSeason > 1) {
            log.warn("多課下神為仲") ;
            sameHardSameSeason(maxChapters, day) ;
            return ;
        }

        if(sameHardSameSeason(maxChapters, day)){
            return ;
        }

        log.warn("Unknown three class") ;

    }

    private boolean sameHardSameSeason(List<Integer> maxChapters, int day) {
        if(day % 2 == 0) {
            if( maxChapters.contains(0) ) {
                log.warn("綴瑕") ;
                name = "綴瑕" ;
                threePass[0] = fourChapter[0][1] ;
                return true;
            }
        }else{
            if( maxChapters.contains(3) ) {
                log.warn("復等") ;
                name = "復等" ;
                threePass[0] = fourChapter[3][1] ;
                return true;
            }
        }
        log.warn("綴瑕復等 不入干支") ;
        return false ;
    }

    private void normalPass() {
        if(threePass[0] == -1) return ;
        threePass[1] = skyPlane[threePass[0]] ;
        threePass[2] = skyPlane[threePass[1]] ;
    }

    private boolean isMutualHard(int a, int b){
        return isHard(a,b) || isHard(b,a) ;
    }
    private boolean isHard(int a, int b) {
        if(a == -1 || b == -1) return false;
        return ((b-a+5)%5) == 2 ;
    }
    private boolean isHard(int a, int b, boolean isNormal) {
        if(isNormal) {
            return isHard(a,b) ;
        }else{
            return isHard(b,a) ;
        }
    }

    String rels[] = new String[]{"兄","父","官","財","子"} ;
    private String getRelation(int day, int pass) {
        int diff = (day - pass + 5) % 5 ;
        return rels[diff] ;
    }

    String result[];
    public String[] getResult() {
        result = new String[6 * 14] ;
        for(int i=0; i<result.length;i++) {
            result[i] = "　" ;
        }

        setSky(1,8) ;
        setSkyPlaneLead(0,7) ;
        setFourChapter(1,4) ;
        setThreePass(1,0) ;

        return result ;
    }

    private void setThreePass(int x, int y) {
        for(int i=0; i<3; i++) {
            setString(x  , y+i, getRelation( skysEle[fourChapter[0][0]], groundsEle[threePass[i]] ) ) ;
            setString(x+1, y+i, skys[cycleGround2Sky[threePass[i]]]) ;
            setString(x+2, y+i, grounds[threePass[i]]) ;
            setString(x+3, y+i, gods[skyPlanelead[(threePass[i]-skyPlane[0]+12)%12]]) ;

        }
//
//		setString(x+0, y+1, grounds[threePass[1]]) ;
//		setString(x+0, y+2, grounds[threePass[2]]) ;
    }

    private void setSkyPlaneLead(int x, int y) {
        setString(x+3, y+5, gods[skyPlanelead[0]]) ;
        setString(x+2, y+5, gods[skyPlanelead[1]]) ;
        setString(x+1, y+5, gods[skyPlanelead[2]]) ;
        setString(x+0, y+3, gods[skyPlanelead[3]]) ;
        setString(x+0, y+2, gods[skyPlanelead[4]]) ;
        setString(x+1, y+0, gods[skyPlanelead[5]]) ;
        setString(x+2, y+0, gods[skyPlanelead[6]]) ;
        setString(x+3, y+0, gods[skyPlanelead[7]]) ;
        setString(x+4, y+0, gods[skyPlanelead[8]]) ;
        setString(x+5, y+2, gods[skyPlanelead[9]]) ;
        setString(x+5, y+3, gods[skyPlanelead[10]]) ;
        setString(x+4, y+5, gods[skyPlanelead[11]]) ;
    }

    private void setFourChapter(int x,int y) {
        setString(x+3,y+1,skys[fourChapter[0][0]]) ;
        setString(x+3,y+0,grounds[fourChapter[0][1]]) ;
        setString(x+2,y+1,grounds[fourChapter[1][0]]) ;
        setString(x+2,y+0,grounds[fourChapter[1][1]]) ;
        setString(x+1,y+1,grounds[fourChapter[2][0]]) ;
        setString(x+1,y+0,grounds[fourChapter[2][1]]) ;
        setString(x+0,y+1,grounds[fourChapter[3][0]]) ;
        setString(x+0,y+0,grounds[fourChapter[3][1]]) ;
    }

    private void setSky(int x, int y) {
        setString(x+2,y+3,grounds[skyPlane[0]]) ;
        setString(x+1,y+3,grounds[skyPlane[1]]) ;
        setString(x+0,y+3,grounds[skyPlane[2]]) ;
        setString(x+0,y+2,grounds[skyPlane[3]]) ;
        setString(x+0,y+1,grounds[skyPlane[4]]) ;
        setString(x+0,y+0,grounds[skyPlane[5]]) ;
        setString(x+1,y+0,grounds[skyPlane[6]]) ;
        setString(x+2,y+0,grounds[skyPlane[7]]) ;
        setString(x+3,y+0,grounds[skyPlane[8]]) ;
        setString(x+3,y+1,grounds[skyPlane[9]]) ;
        setString(x+3,y+2,grounds[skyPlane[10]]) ;
        setString(x+3,y+3,grounds[skyPlane[11]]) ;
    }

    /*
     * row col :
     *  0  1  2  3  4  5
     *  6  7  8  9 10 11
     */
    private void setString(int x, int y, String s) {
        result[y*6 + x] = s ;
    }


    private int get12(int i) {
        return ((i % 12) + 24) % 12 ;
    }
    private int get24(int i) {
        return ((i % 24) + 48) % 24 ;
    }
    private int get60(int i) {
        return ((i % 60) + 120) % 60 ;
    }


    public static double getJulDay(Calendar time)
    {
        time.setTimeZone(TimeZone.getTimeZone("Universal"));

        double hour = time.get(Calendar.HOUR_OF_DAY)
                + time.get(Calendar.MINUTE) / 60.0
                + time.get(Calendar.SECOND) / 3600.0;
        double jday = SweDate.getJulDay(time.get(Calendar.YEAR),
                time.get(Calendar.MONTH) + 1,
                time.get(Calendar.DAY_OF_MONTH),
                hour);
        return jday;
    }

    public static String padleft(String s, String pad, int length) {
        if(s == null) s ="" ;
        while(s.length() <length) {
            s = pad + s ;
        }
        return s;
    }


    public static void main(String arg[]){
        main2(arg) ;
    }


    public static void main2(String arg[]) {
        // check all the 720 chapter not cover in logic
        for(int i=0; i<60; i++) {
            for(int j=0;j<12;j++) {
                LokYinGraph ly = new LokYinGraph() ;
                ly.setDate(new Date()) ;
                ly.threePass = new int[]{-1,-1,-1} ;
                ly.makeThreePass((12-sky2ground[i%10] + j)%12, i) ;
                System.out.println(i + " " + skys[i%10] + grounds[i%12] + "  干上 "+grounds[ly.fourChapter[0][1]] + " "  + j
                    + grounds[ly.threePass[0]] + grounds[ly.threePass[1]] + grounds[ly.threePass[2]]
                        + " " +ly.name
                ) ;
                if(ly.threePass[0] == -1) {
                    System.out.println(i + " " + skys[i%10] + grounds[i%12] + "  干上 "+grounds[ly.fourChapter[0][1]] + " "  + j) ;
                }

            }
        }
    }

    public static void main1(String arg[]) {
//		for(int i=0;i<12;i++) {
//			System.out.println(grounds[i] + " " + grounds[punish[i]]);
//		}
        LokYinGraph ly = new LokYinGraph() ;
        ly.setDate(new Date()) ;
        ly.threePass = new int[]{-1,-1,-1} ;
        int day = 3;
        int skyStart = (12-sky2ground[day%10] + 7)%12 ;
        ly.makeThreePass(skyStart, day) ; // 子上神, day

        System.out.println("name:" + ly.name) ;
        String result[] = ly.getResult() ;
        for(int i=0; i<14; i++) {
            for(int j=0;j<6;j++){
                System.out.print(result[i*6+j]) ;
            }
            System.out.println();
        }
    }
}
