package dmitriy.deomin.generateawallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dimon on 31.12.16.
 */

public class Holst extends View {

    Paint kist = new Paint();
    Bitmap bmp;
    int w;
    int h;

    public Holst(Context context) {
        super(context);
        setDrawingCacheEnabled(true);
        w = Main.wd;
        h = Main.hd;

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!Main.run) {


            canvas.drawColor(random_color());

            kist.setAntiAlias(true);
            kist.setStrokeWidth(10);
            kist.setColor(random_color());


            for (int l = 0; l < random_nomer(0, 20); l++) {
                int wrand = random_nomer(0, w);

                for (int i = 0; i < random_nomer(0, h); i++) {
                    canvas.drawPoint(wrand, i, kist);
                }
            }

            canvas.drawRect(random_nomer(0, w), random_nomer(0, h), random_nomer(0, w), random_nomer(0, h), kist);
            canvas.drawCircle(random_nomer(0, w), random_nomer(0, h), random_nomer(0, w), kist);

            buildDrawingCache();
            bmp = getDrawingCache();
            canvas.drawBitmap(bmp, 0, 0, null);

        }

        if (Main.run) {
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

            buildDrawingCache();
            bmp = getDrawingCache();
            canvas.drawBitmap(bmp, 0, 0, null);
        }

        super.onDraw(canvas);
    }

    public int random_color() {
        int r = random_nomer(0, 255);
        int g = random_nomer(0, 255);
        int b = random_nomer(0, 255);
        return Color.rgb(r, g, b);
    }
    public int random_nomer(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }

    public static Bitmap resize(Bitmap bit, int newWidth, int newHeight) {

        int width = bit.getWidth();
        int height = bit.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bit, 0, 0,
                width, height, matrix, true);
        return resizedBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getY()<(h/2)) {

            String url_img = "/Pictures/Generateawallpaper/" + "Gen" + System.currentTimeMillis() + ".png";

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


            //после сохранения установим картинку обоями если включено
            if(Main.auto_oboi_crete){
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
                Toast.makeText(getContext(),"Сохранено", Toast.LENGTH_SHORT).show();
            }

        }else {
            Main.run = false;
            bmp= null;
            destroyDrawingCache();
            invalidate();
        }
        return  false;
    }
}
