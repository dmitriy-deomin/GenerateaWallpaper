package dmitriy.deomin.generateawallpaper;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dimon on 31.12.16.
 */

public class Holst extends View implements View.OnTouchListener {

    Paint kist = new Paint();
    int run = 0;
    int w;
    int h;

    public Holst(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        w = canvas.getWidth();
        h = canvas.getHeight();

            canvas.drawColor(random_color());


            kist.setAntiAlias(true);
            kist.setStrokeWidth(10);
            kist.setColor(random_color());

            if (run == 1) {
                // при генерации картинок

                for (int l = 0; l < random_nomer(0, 20); l++) {
                    int wrand = random_nomer(0, w);

                    for (int i = 0; i < random_nomer(0, h); i++) {
                        canvas.drawPoint(wrand, i, kist);
                    }
                }

                canvas.drawRect(random_nomer(0, w), random_nomer(0, h), random_nomer(0, w), random_nomer(0, h), kist);
                canvas.drawCircle(random_nomer(0, w), random_nomer(0, h), random_nomer(0, w), kist);


            }

            if (run == 0) {
                //при первом запуске проги
                canvas.drawColor(random_color());

                Paint shadowPaint = new Paint();

                shadowPaint.setAntiAlias(true);
                shadowPaint.setTextAlign(Paint.Align.CENTER);
                shadowPaint.setColor(Color.WHITE);
                shadowPaint.setTextSize(100);
                shadowPaint.setStrokeWidth(2.0f);
                shadowPaint.setStyle(Paint.Style.STROKE);
                shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);

                canvas.drawText(getContext().getString(R.string.Ustanovit), w / 2, 200, shadowPaint);
                canvas.drawText(getContext().getString(R.string.Sgeneririvat), w / 2, h - 200, shadowPaint);

                run = 1;
            }
        }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getY()<(h/2)) {

            String url_img = "/Pictures/Generateawallpaper/" + "Gen" + System.currentTimeMillis() + ".png";

            v.setDrawingCacheEnabled(true);
            Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);

//создадим папки если нет
            File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Generateawallpaper/");
            if (!sddir.exists()) {
                sddir.mkdirs();
            }

            try {
                FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().toString(), url_img));
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //после сохранения установим картинку обоями

            //********
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
            try {
                wallpaperManager.setBitmap(bmp);
                Toast.makeText(getContext(), R.string.save_i_ustanovleno, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //************


        }else {
            invalidate();
        }

        return false;
    }
    
    public int random_color(){
        int r = random_nomer(0,255);
        int g = random_nomer(0,255);
        int b = random_nomer(0,255);
        return Color.rgb(r, g, b);
    }
    public int random_nomer(int min,int max){
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }
}
