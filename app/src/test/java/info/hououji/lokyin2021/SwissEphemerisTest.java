package info.hououji.lokyin2021;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import info.hououji.lokyin2021.lib.LokYinGraph;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SwissEphemerisTest {
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }

    @Test
    public void swissEphemerisTest1() throws Exception{

        int[] planets = { SweConst.SE_SUN,
                SweConst.SE_MOON,
                SweConst.SE_MERCURY,
                SweConst.SE_VENUS,
                SweConst.SE_MARS,
                SweConst.SE_JUPITER,
                SweConst.SE_SATURN,
                SweConst.SE_TRUE_NODE };

        int flags =
//                SweConst.SEFLG_SWIEPH |		// fastest method, requires data files
                        SweConst.SEFLG_MOSEPH
//                SweConst.SEFLG_SIDEREAL 	// sidereal zodiac
//                SweConst.SEFLG_NONUT |		// will be set automatically for sidereal calculations, if not set here
//                SweConst.SEFLG_SPEED        // to determine retrograde vs. direct motion
                ;


        //       double longitude = 80 + 17/60.0;	// Chennai
//        double latitude = 13 + 5/60.0;
//        double hour = 7+30./60. - 5.5; // IST

        SwissEph sw = new SwissEph();
        sw.swe_set_ephe_path(null);
        Calendar c = Calendar.getInstance() ;

        double[] xp= new double[6];
        StringBuffer serr = new StringBuffer();

        Date d = new SimpleDateFormat("yyyy-MM-dd").parse("2019-03-21") ;
        c.setTime(d);

        for(int p = 0; p < planets.length; p++) {
            int planet = planets[p];
            String planetName = sw.swe_get_planet_name(planet);
            int ret = sw.swe_calc_ut(getJulDay(c),
                    planet,
                    flags,
                    xp,
                    serr);

            if (ret != flags) {
                if (serr.length() > 0) {
                    System.err.println("Warning: " + serr);
                } else {
                    System.err.println(
                            String.format("Warning, different flags used (0x%x)", ret));
                }
            }

            int sign = (int)(xp[0] / 30) + 1;
            //house = (sign + 12 - ascSign) % 12 +1;
            //retrograde = (xp[3] < 0);

            System.out.println(planetName + " " + toDMS(xp[0]) + " " + sign + " "  + toDMS(xp[0] % 30)+ " " + xp[0]) ;

        }
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


    static String toHMS(double d) {
        d += 0.5/3600.;	// round to one second
        int h = (int) d;
        d = (d - h) * 60;
        int min = (int) d;
        int sec = (int)((d - min) * 60);

        return String.format("%2d:%02d:%02d", h, min, sec);
    }

    static String toDMS(double d) {
        d += 0.5/3600./10000.;	// round to 1/1000 of a second
        int deg = (int) d;
        d = (d - deg) * 60;
        int min = (int) d;
        d = (d - min) * 60;
        double sec = d;

        return String.format("%3d°%02d'%07.4f\"", deg, min, sec);
    }
}