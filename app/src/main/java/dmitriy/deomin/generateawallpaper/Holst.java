package dmitriy.deomin.generateawallpaper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
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

public class Holst extends View implements View.OnTouchListener {

    Paint kist = new Paint();
    Bitmap bmp;
    Handler handler;
    int long_clik;
    float mesto_clik;
    int w;
    int h;

    public Holst(Context context) {
        super(context);
        setDrawingCacheEnabled(true);
        setFocusable(true);
        //слушаем нажатия
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        w = canvas.getWidth();
        h = canvas.getHeight();

        if (!Main.run) {
            //По какой схеме рисовать картинки
            switch (Main.Schema_rand_kartinki) {
                //по умолчанию сохранялка кидает 0 поетому так запутано
                case 0:
                    //круг,квадрат,линии
                    shema0(canvas);
                    break;
                case 1:
                    //много квадратов
                    shema1(canvas);
                    break;
                case 2:
                    //непонятно что
                    shema2(canvas);
                    break;
                case 777:
                    //чистый фон
                    shema777(canvas);
                    break;
            }
        }


        //при первом запуске проги
        if (Main.run) {
            help_risunok(canvas);
        }

        super.onDraw(canvas);
    }


    private void shema0(Canvas canvas) {
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

    private void shema1(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        kist.setStrokeWidth(random_nomer(2, 100));
        kist.setColor(random_color());


        //много одинаковых квадратов
        for (int i = 0; i < random_nomer(0, 777); i++) {
            canvas.drawPoint(random_nomer(0, w), random_nomer(0, h), kist);
        }

        //еще квадраты другова цвета
        kist.setColor(random_color());
        for (int i = 0; i < random_nomer(0, 777); i++) {
            canvas.drawPoint(random_nomer(0, w), random_nomer(0, h), kist);
        }


        //и еще квадраты другова цвета
        kist.setColor(random_color());
        for (int i = 0; i < random_nomer(0, 777); i++) {
            canvas.drawPoint(random_nomer(0, w), random_nomer(0, h), kist);
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    private void shema2(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        kist.setStrokeWidth(random_nomer(2, 100));
        kist.setColor(random_color());


        Path path = new Path();

        path.moveTo(random_nomer(0,w), random_nomer(0,w));
        //много херни
        for (int i = 0; i < random_nomer(0, 777); i++) {
            path.lineTo(random_nomer(0,w), random_nomer(0,h));
        }
        path.close();


        canvas.drawPath(path,kist);


        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    private void shema777(Canvas canvas) {
        canvas.drawColor(random_color());
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
    }

    private void help_risunok(Canvas canvas) {
        canvas.drawColor(Main.color_fon_main);

        Paint shadowPaint = new Paint();

        shadowPaint.setAntiAlias(true);
        shadowPaint.setTextAlign(Paint.Align.CENTER);
        shadowPaint.setColor(Color.WHITE);
        shadowPaint.setTextSize(Main.wd / 10);
        shadowPaint.setStrokeWidth(2.0f);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);

        canvas.drawText(getContext().getString(R.string.Ustanovit), w / 2, 200, shadowPaint);
        canvas.drawText(getContext().getString(R.string.Sgeneririvat), w / 2, h - 200, shadowPaint);

        shadowPaint.setTextSize(Main.wd / 12);
        canvas.drawText("Свайп с верху в низ", w / 2, h/2, shadowPaint);
        canvas.drawText("покажет кнопки навигации", w / 2, h/2+60, shadowPaint);

        shadowPaint.setTextSize(Main.wd / 25);
        canvas.drawText("(долгое нажатие сохранит картинку)", w / 2, 250, shadowPaint);
        canvas.drawText("(долгое нажатие запустит автомат тыкания)", w / 2, h - 150, shadowPaint);

        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
    }

    private int random_color() {
        int r = random_nomer(0, 255);
        int g = random_nomer(0, 255);
        int b = random_nomer(0, 255);
        return Color.rgb(r, g, b);
    }

    private int random_nomer(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }


    private void clik_view_avtomat(final Boolean run) {

        if (handler == null) {
            handler = new Handler();
            handler.post(new Runnable() {
                public void run() {

                    // действие
                    //--------------------
                    Main.run = false;
                    bmp = null;
                    destroyDrawingCache();
                    invalidate();
                    //---------------------

                    if (run) {
                        //повторы 1 секуннд
                        handler.postDelayed(this, Main.Time_dolbeshki_ekrana);
                    }

                }
            });
        } else {
            if (!run) {
                handler.removeMessages(0);
                handler = null;
            }

        }

    }

    private void save_imag_file(Boolean pokaz_toast, String url_img) {

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

        if (pokaz_toast) {
            Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //сохраним время нажатия
            long_clik = Integer.valueOf((int) System.currentTimeMillis());
            //сохраним место нажатия
            mesto_clik = event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {

            //сравним время нажатия и отпускания
            if ((long_clik + 500) < Integer.valueOf((int) System.currentTimeMillis())) {

                //если долго посмотрим че он жал
                if (event.getY() < (h / 2)) {
                    //если выше то сохраним картинку в файл
                    //путь и имя файла
                    String url_img = "/Pictures/Generateawallpaper/" + "Gen" + System.currentTimeMillis() + ".png";
                    save_imag_file(true, url_img);
                } else {
                    //если где сгенерировать, то запустим автомат
                    clik_view_avtomat(true);
                }

            } else {
                //если задержка маленькая остановим автомат если он работал и экран не трогаем
                if (handler != null) {
                    clik_view_avtomat(false);
                } else {
                    //и дальше будем смотреть куды тыкнул пользователь
                    if (event.getY() < (h / 2)) {


                        //если это был свайп вниз(чтобы кнопки навигации показать)
                        if(mesto_clik+10<event.getY()){
                            //нечего не делаем
                        }else {
                            //путь и имя файла
                            String url_img = "/Pictures/Generateawallpaper/" + "Gen" + System.currentTimeMillis() + ".png";

                            //сночала сахраним картинку
                            save_imag_file(false, url_img);

                            //после сохранения установим картинку обоями если включено или обрежем её
                            // будем слать сигналы в
                            //**************
                            Intent imag = new Intent("Key_signala_pizdec");
                            imag.putExtra("key_data_url_imag", Environment.getExternalStorageDirectory().toString() + url_img);
                            getContext().sendBroadcast(imag);
                            //************
                        }
                    } else {
                        Main.run = false;
                        bmp = null;
                        destroyDrawingCache();
                        invalidate();
                    }
                }
            }
        }
        return true;
    }
}
