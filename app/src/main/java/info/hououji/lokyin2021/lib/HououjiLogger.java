package info.hououji.lokyin2021.lib;

import android.util.Log;

public class HououjiLogger {

    public HououjiLogger(String TAG) {
        this.TAG = TAG ;
    }

    String TAG = "" ;
    public void debug(String s) {

        Log.d(TAG,s ) ;
    }

    public void warn(String s) {
        Log.w(TAG,s);
    }
}
