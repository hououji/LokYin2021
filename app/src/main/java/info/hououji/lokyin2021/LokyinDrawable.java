package info.hououji.lokyin2021;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import info.hououji.lokyin2021.lib.Constants;
import info.hououji.lokyin2021.lib.LokYinGraph;

public class LokyinDrawable extends Drawable {


    static SimpleDateFormat dispalySdf = new SimpleDateFormat("yyyy/MM/dd HH:mm") ;

    private final Paint redPaint;
    private final Paint defaultPaint;

    LokYinGraph ly;

    public LokyinDrawable(LokYinGraph lokYinGraph) {
        // Set up color and text size
        redPaint = new Paint();
        redPaint.setARGB(255, 255, 0, 0);

        defaultPaint = new Paint() ;
        defaultPaint.setARGB(255, 0, 0, 0);
        this.ly = lokYinGraph ;
    }

    private Canvas canvas ;
    float textSize = 50;
    float lineHeight = 50 * 1.2f;

    @Override
    public void draw(@NonNull Canvas canvas) {

        this.canvas = canvas ;
        // Get the drawable's bounds
        int width = getBounds().width();
        int height = getBounds().height();

        //textSize = Math.min(width, height) / 16 ;
        textSize = Math.min(width/16, height/22)  ;
        lineHeight = textSize * 1.3f;

        defaultPaint.setTextSize((float)(textSize));

        ly.setDate(Common.getCurrentDate());

        defaultPaint.setARGB(255, 0, 0, 0);

        String dateStr = dispalySdf.format(Common.getCurrentDate()) ;
        canvas.drawText(dateStr,textSize*0.5f,1*lineHeight, defaultPaint);

        String date = Constants.skys[ly.eight[0] % 10] + Constants.grounds[ly.eight[0] % 12] + "年　"  ;
        date += Constants.skys[ly.eight[1] % 10] + Constants.grounds[ly.eight[1] % 12]  + "月　";
        date += Constants.skys[ly.eight[2] % 10] + Constants.grounds[ly.eight[2] % 12]  + "日　";
        date += Constants.skys[ly.eight[3] % 10] + Constants.grounds[ly.eight[3] % 12]  + "時　";
        canvas.drawText(date, textSize*0.5f,2*lineHeight, defaultPaint);

        String detail = "　　　　　" + Constants.grounds[ ly.monthlead ] + "將　空" + Constants.grounds[ly.space[0]] + Constants.grounds[ly.space[1]] ;
        canvas.drawText(detail, textSize*0.5f,3*lineHeight, defaultPaint);

        float resultX = textSize * 4.5f;
        float resultY = lineHeight * 5;

        float textMargin = 1.3f;
        String result[] = ly.getResult() ;
        for(int row=0; row <14; row++){
            for(int col=0; col<6; col++) {
                canvas.drawText(result[col+row*6], resultX + col * textSize * textMargin,resultY+row*textSize*textMargin, defaultPaint);
            }
        }


//        float radius = Math.min(width, height) / 2;
//        defaultPaint.setStyle(Paint.Style.STROKE);
//        defaultPaint.setStrokeWidth(5);
//        canvas.drawCircle(width/2, height/2, radius, defaultPaint);
//       // canvas.drawRect(0,0,width-1,height-1,redPaint);
        defaultPaint.setARGB(255, 0, 255, 0);
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setStrokeWidth(5);
        int round = Math.min(width,height) / 20 ;
        canvas.drawRoundRect(5,5,width-10,height-10,round,round,redPaint);

    }

//    private void drawText(Paint paint,int col, int row, String text) {
//        for(int i =0; i<text.length(); i++) {
//            canvas.drawText(""+text.charAt(i), (col+i)*textSize,(row+1)*textSize, paint);
//        }
//
//    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @SuppressLint("WrongConstant")
    @Override
    public int getOpacity() {
        return 0;
    }
}
