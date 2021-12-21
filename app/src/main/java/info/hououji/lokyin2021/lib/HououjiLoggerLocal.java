package info.hououji.lokyin2021.lib;


import android.util.Log;

public class HououjiLoggerLocal extends HououjiLogger{

    public HououjiLoggerLocal(String TAG) {
        super(TAG);
        this.TAG = TAG ;
    }

    String TAG = "" ;
    public void debug(String s) {

        //System.out.println(s ) ;
    }

    public void warn(String s) {

        //System.out.println(s);
    }
}
