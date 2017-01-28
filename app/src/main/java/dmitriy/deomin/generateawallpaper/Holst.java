package dmitriy.deomin.generateawallpaper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


/**
 * Created by dimon on 31.12.16.
 */

public class Holst extends View implements View.OnTouchListener {

    Paint kist = new Paint();
    Bitmap bmp;
    Handler handler;
    int long_clik;
    float mesto_clik_y; // y
    float mesto_clik_x; // x
    int w;
    int h;

    // String [] mas_pi;

    //место где нажато было для генерации разных картинок
    float pres_x = 0;
    float pres_y = 0;

    public Holst(Context context) {
        super(context);
        setDrawingCacheEnabled(true);
        setFocusable(true);
        //слушаем нажатия
        setOnTouchListener(this);
        //сделаем массив сразу
        // mas_pi = getResources().getString(R.string.pi).split("(?<=\\G.{1})");
    }

    @Override
    protected void onDraw(Canvas canvas) {

        w = canvas.getWidth();
        h = canvas.getHeight();

        if (!Main.run) {
            //По какой схеме рисовать картинки
            switch (Main.Schema_rand_kartinki) {
                case 0:
                    //чистый фон
                    shema0(canvas);
                    break;
                case 1:
                    //круг,квадрат,линии
                    shema1(canvas);
                    break;
                case 2:
                    //много квадратов одинаковых 3 цвета
                    shema2(canvas);
                    break;
                case 3:
                    //осколки
                    shema3(canvas);
                    break;
                case 4:
                    //Много кругов 3-х видов
                    shema4(canvas);
                    break;
                case 5:
                    //много мелких кругов и квадратов разноцветных
                    shema5(canvas);
                    break;
                case 6:
                    //паралельные линии
                    shema6(canvas);
                    break;
                case 7:
                    //линии в разброс
                    shema7(canvas);
                    break;
                case 8:
                    //Одноцветные круги по спирале Архимеда
                    shema8(canvas);
                    break;
                case 9:
                    //Разные круги по спирале Архимеда
                    shema9(canvas);
                    break;
                case 10:
                    //Cпираль Архимеда
                    shema10(canvas);
                    break;
                case 11:
                    //Одноцветные квадраты по спирале Архимеда
                    shema11(canvas);
                    break;
                case 12:
                    //Разноцветные круг в круге разноразмерные
                    shema12(canvas);
                    break;
                case 13:
                    //Числа в разброс
                    shema13(canvas);
                    break;
                case 14:
                    //Буквы(а-я) в разброс
                    shema14(canvas);
                    break;
                case 15:
                    //Разноцветные круг в круге
                    shema15(canvas);
                    break;
                case 16:
                    //сетка
                    shema16(canvas);
                    break;
                case 17:
                    //Линейный градиент
                    shema17(canvas);
                    break;
                case 18:
                    //Круговой градиент
                    shema18(canvas);
                    break;
                case 19:
                    //Радужный градиент
                    shema19(canvas);
                    break;
            }
        }


        //при первом запуске проги
        if (Main.run) {
            help_risunok(canvas);
        }

        super.onDraw(canvas);
    }


/////
    /////
    /////*********** Схемы рисования херни *************************
    /////
/////


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
        canvas.drawText("Свайп с верху в низ", w / 2, h / 2, shadowPaint);
        canvas.drawText("покажет кнопки навигации", w / 2, h / 2 + 60, shadowPaint);

        shadowPaint.setTextSize(Main.wd / 25);
        canvas.drawText("(долгое нажатие сохранит картинку)", w / 2, 250, shadowPaint);
        canvas.drawText("(долгое нажатие запустит автомат тыкания)", w / 2, h - 150, shadowPaint);
        shadowPaint.setTextSize(Main.wd / 20);
        canvas.drawText("Свайп лево/право переключить схему", w / 2, h - 300, shadowPaint);

        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
    }

    //чистый фон
    private void shema0(Canvas canvas) {
        kist.setAntiAlias(true);
        //будем случайно просто цветом заливать
        canvas.drawColor(random_color());

        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
    }

    //круг,квадрат,линии
    private void shema1(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        kist.setStrokeWidth(10);
        kist.setColor(random_color());

        rand_on_of_kist_sglagivanie();
        rand_on_of_zalivka();

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

    //много одинаковых квадратов
    private void shema2(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        kist.setStrokeWidth(random_nomer(2, 100));
        kist.setColor(random_color());

        rand_on_of_kist_sglagivanie();
        rand_on_of_zalivka();

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

    //осколки
    private void shema3(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        kist.setStrokeWidth(random_nomer(2, 100));
        kist.setColor(random_color());


        rand_on_of_kist_sglagivanie();
        rand_on_of_zalivka();

        Path path = new Path();

        path.moveTo(random_nomer(0, w), random_nomer(0, w));
        //осколки
        for (int i = 0; i < random_nomer(0, 777); i++) {
            path.lineTo(random_nomer(0, w), random_nomer(0, h));
        }
        path.close();

        canvas.drawPath(path, kist);


        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    //Много кругов 3-х видов
    private void shema4(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        kist.setStrokeWidth(random_nomer(2, 100));
        kist.setColor(random_color());

        rand_on_of_kist_sglagivanie();
        rand_on_of_zalivka();

        //много кругов
        for (int i = 0; i < random_nomer(0, 777); i++) {
            canvas.drawCircle(random_nomer(0, w), random_nomer(0, h), random_nomer(0, w / 5), kist);
        }

        //еще много кругов
        kist.setColor(random_color());
        for (int i = 0; i < random_nomer(0, 777); i++) {
            canvas.drawCircle(random_nomer(0, w), random_nomer(0, h), random_nomer(0, w / 5), kist);
        }


        //и еще много кругов
        kist.setColor(random_color());
        for (int i = 0; i < random_nomer(0, 777); i++) {
            canvas.drawCircle(random_nomer(0, w), random_nomer(0, h), random_nomer(0, w / 5), kist);
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    //много мелких кругов и квадратов
    private void shema5(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        kist.setStrokeWidth(random_nomer(2, 100));
        kist.setColor(random_color());


        rand_on_of_kist_sglagivanie();
        rand_on_of_zalivka();

        //много кругов и много квадратов
        for (int i = 0; i < random_nomer(0, 2000); i++) {

            kist.setColor(random_color());
            canvas.drawCircle(random_nomer(0, w), random_nomer(0, h), random_nomer(0, w / 8), kist);

            kist.setColor(random_color());
            //размер квадрата
            int x = random_nomer(0, w);
            int y = random_nomer(0, h);
            int r = random_nomer(0, w / 8);
            canvas.drawRect(x, y, x + r, y + r, kist);
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    //паралельные линии
    private void shema6(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        kist.setStrokeWidth(random_nomer(2, 300));
        kist.setColor(random_color());

        //направление линий неразобрался кок лучше пока зделать
        int ugol = random_nomer(0, 1);

        //много линий
        for (int i = 0; i < random_nomer(0, 2000); i++) {

            kist.setColor(random_color());
            kist.setStrokeWidth(random_nomer(2, 100));

            //int x = random_nomer(Integer.valueOf((int) pres_x),w);

            int y = random_nomer(0, h);
            int x = random_nomer(0, w);

            int dlina = random_nomer(0, h + w);


            //пока только два направления хер знает как расширить
            if (ugol == 0) {
                canvas.drawLine(x, y, x + dlina, y + dlina, kist);
            } else {
                canvas.drawLine(x, y, x + dlina, y - dlina, kist);
            }

        }


        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    // линии в разброс
    private void shema7(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        kist.setStrokeWidth(random_nomer(2, 300));
        kist.setColor(random_color());

        //много линий в разброс
        for (int i = 0; i < random_nomer(0, 2000); i++) {

            kist.setColor(random_color());
            kist.setStrokeWidth(random_nomer(2, 100));

            int x = random_nomer(Integer.valueOf((int) pres_x), w);

            canvas.drawLine(random_nomer(x - 200, w), random_nomer(0, h), random_nomer(0, w + 200), random_nomer(0, h + 200), kist);

        }


        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    //Одноцветные круги по спирале Архимеда
    private void shema8(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        //размер кисти
        kist.setStrokeWidth(random_nomer(1, 50));
        kist.setColor(random_color());

        rand_on_of_kist_sglagivanie();
        rand_on_of_zalivka();


        int phi, r, k, spase, razmer_tochek;

        //растояние между витками
        k = 1;
        //растояние между точек
        spase = 1;
        razmer_tochek = random_nomer(1, 50);

        for (int i = 0; i != h; ++i) {
            phi = i * spase;
            r = k * i;
            double x = r * cos(phi);
            double y = r * sin(phi);
            canvas.drawCircle((int) x + w / 2, h / 2 - (int) y, razmer_tochek, kist);
        }

        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    //Разные круги по спирале Архимеда
    private void shema9(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        //размер кисти
        kist.setStrokeWidth(random_nomer(1, 50));
        kist.setColor(random_color());

        rand_on_of_kist_sglagivanie();
        rand_on_of_zalivka();


        int phi, r, k, spase, razmer_tochek;

        //растояние между витками
        k = 1;
        //растояние между точек
        spase = 1;

        for (int i = 1; i != h; ++i) {
            phi = i * spase;
            r = k * i;
            double x = r * cos(phi);
            double y = r * sin(phi);
            razmer_tochek = random_nomer(1, 50);
            kist.setColor(random_color());
            canvas.drawCircle((int) x + w / 2, h / 2 - (int) y, razmer_tochek, kist);
        }

        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    //Cпираль Архимеда
    private void shema10(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        //размер кисти
        kist.setStrokeWidth(random_nomer(1, 10));
        kist.setColor(random_color());
        kist.setStyle(Paint.Style.STROKE);


        int phi, r, k, spase, razmer_tochek;

        //растояние между витками
        k = random_nomer(3, 10);
        //растояние между точек
        spase = random_nomer(20, 70);

        Path curve = new Path();

        curve.moveTo(w / 2, h / 2);

        for (int i = 1; i != w; ++i) {
            phi = i * spase;
            r = k * i;
            double x = r * cos(phi);
            double y = r * sin(phi);

            curve.lineTo((int) x + w / 2, h / 2 - (int) y);
        }
        // curve.close();


        //сглаживание краёв
        float radius = 15.0f;
        CornerPathEffect mCornerPathEffect = new CornerPathEffect(radius);
        kist.setPathEffect(mCornerPathEffect);


        canvas.drawPath(curve, kist);

        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    //Одноцветные квадраты по спирале Архимеда
    private void shema11(Canvas canvas) {
        canvas.drawColor(random_color());

        kist.setAntiAlias(true);
        //размер кисти
        kist.setStrokeWidth(random_nomer(1, 100));
        kist.setColor(random_color());

        rand_on_of_kist_sglagivanie();
        rand_on_of_zalivka();


        int phi, r, k, spase, razmer_tochek;

        //растояние между витками
        k = 1;
        //растояние между точек
        spase = 1;

        for (int i = 0; i != h; ++i) {
            phi = i * spase;
            r = k * i;
            double x = r * cos(phi);
            double y = r * sin(phi);
            canvas.drawPoint((int) x + w / 2, h / 2 - (int) y, kist);
        }

        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //------------------------------------
    }

    //Разноцветные круг в круге разноразмерные
    private void shema12(Canvas canvas) {

        canvas.drawColor(random_color());
        kist.setAntiAlias(true);
        //размер кисти
        kist.setStrokeWidth(1);
        kist.setColor(random_color());
        kist.setStyle(Paint.Style.FILL);


        //начальные координаты
        int xn = w / 2;
        int yn = h / 2;


        for (int i = w; i != 0; i--) {


            //сначала большой круг нарисуем, потом рандомно будем вставлять в него разные
            if (i == w) {
                canvas.drawCircle(xn, yn, i, kist);
            } else {
                //радиус будем брать случайный , максимальный будет предыдущий -1

                int radius = random_nomer(1, i - 1);
                i = radius;
                kist.setColor(random_color());
                canvas.drawCircle(xn, yn, radius, kist);

            }


        }


        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //-------------------------------------
    }

    //Числа в разброс
    private void shema13(Canvas canvas) {

        canvas.drawColor(random_color());
        kist.setAntiAlias(true);
        //размер кисти
        kist.setStrokeWidth(1);
        kist.setColor(random_color());
        kist.setStyle(Paint.Style.STROKE);




        String[] mas_num = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "/","*","-","+","=","~","#","%","№",
                "y = x2","y = kx","y = kx + b","y = xn","y = k/x","y = sinx","y = cosx","cos(5*x+10)"};

        int count_number = random_nomer(mas_num.length, w / 20);

        for (int i = 0; i != count_number; i++) {

            kist.setTextSize(random_nomer(w/100, w/10));

            int xn = random_nomer(0, w);
            int yn = random_nomer(0, h);
            int number = random_nomer(0, mas_num.length-1);

            kist.setColor(random_color());


            canvas.drawText(mas_num[number], xn, yn, kist);
        }

    }

    //Буквы(а-я) в разброс
    private void shema14(Canvas canvas) {

        canvas.drawColor(random_color());
        kist.setAntiAlias(true);
        //размер кисти
        kist.setStrokeWidth(1);
        kist.setColor(random_color());

        //прозрачночть будет случайная
        rand_on_of_zalivka();



        int count_number = random_nomer(10, w / 20);

        String[] mas_num = {"А","а", "Б","б", "В","в", "Г","г", "Д","д","Е", "е","Ё", "ё","Ж", "ж","З", "з","И", "и"
        ,"Й","й","К","к","Л","л","М","м","Н","н","О","о","П","п","Р","р","С","с","Т","т","У","у","Ф","ф","Ц","ц","Ч","ч","Ш","ш"
                ,"Щ","щ","Ъ","ъ","Ы","ы","Ь","ь","Э","э","Ю","ю","Я","я"};


        for (int i = 0; i != count_number; i++) {

            kist.setTextSize(random_nomer(100, 200));

            int xn = random_nomer(0, w);
            int yn = random_nomer(0, h);
            int number = random_nomer(0, 9);

            kist.setColor(random_color());


            canvas.drawText(mas_num[number], xn, yn, kist);
        }

    }

    //Разноцветные круг в круге
    private void shema15(Canvas canvas) {

        canvas.drawColor(random_color());
        kist.setAntiAlias(true);
        //размер кисти
        kist.setStrokeWidth(10);
        kist.setColor(random_color());
        kist.setStyle(Paint.Style.STROKE);


        //начальные координаты
        int xn = w / 2;
        int yn = h / 2;


        for (int i = 0; i != w; i=i+10) {
            kist.setColor(random_color());
            canvas.drawCircle(xn, yn,i, kist);
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //-------------------------------------
    }

    //сетка
    private void shema16(Canvas canvas) {

        canvas.drawColor(random_color());
        kist.setAntiAlias(true);
        kist.setColor(random_color());

        rand_on_of_zalivka();
        rand_on_of_kist_sglagivanie();

        int step = random_nomer(10,60);
        kist.setStrokeWidth(random_nomer(5,step));

        for (int i = 0; i <= h;i=i+step) {
            for(int r =0;r<=w;r=r+step){

                canvas.drawPoint(r,i, kist);
            }
        }

        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //-------------------------------------
    }

    //Линейный градиент
    private void shema17(Canvas canvas) {

        //градиент линейный
        kist.setStyle(Paint.Style.FILL);
         kist.setShader(random_liner_gradient());
         canvas.drawRect(0,0,w,h,kist);

        //выключаем градиент
        kist.setShader(null);

        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //-------------------------------------
    }

    //Круговой градиент
    private void shema18(Canvas canvas) {

        //Круговой градиент
        kist.setStyle(Paint.Style.FILL);
         kist.setShader(random_radial_gradient());
        canvas.drawRect(0,0,w,h,kist);

        //выключаем градиент
        kist.setShader(null);

        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //-------------------------------------
    }

    //Радужный градиент
    private void shema19(Canvas canvas) {

        //Радужный градиент
        kist.setStyle(Paint.Style.FILL);
        kist.setShader(random_sweep_gradient());
        canvas.drawRect(0,0,w,h,kist);

        //выключаем градиент
        kist.setShader(null);

        //это херня обязательна
        //--------------------
        buildDrawingCache();
        bmp = getDrawingCache();
        canvas.drawBitmap(bmp, 0, 0, null);
        //-------------------------------------
    }



//////
    /////
    /////*********** Схемы рисования херни *************************
    /////
/////


    private void rand_on_of_kist_sglagivanie(){
        //сглаживание краёв
        if(random_nomer(0,1)==0){
            kist.setPathEffect(null);
        }else{
            float radius = 15.0f;
            CornerPathEffect mCornerPathEffect = new CornerPathEffect(radius);
            kist.setPathEffect(mCornerPathEffect);
        }
    }

    private void rand_on_of_zalivka(){
        switch (random_nomer(0,2)){
            case 0:
                kist.setStyle(Paint.Style.STROKE);
                break;
            case 1:
                kist.setStyle(Paint.Style.FILL);
                break;
            case 2:
                kist.setStyle(Paint.Style.FILL_AND_STROKE);
        }
    }

    private int random_color() {
        int r = random_nomer(0, 255);
        int g = random_nomer(0, 255);
        int b = random_nomer(0, 255);
        return Color.rgb(r, g, b);
    }

    private Shader random_liner_gradient(){

        int count_color = random_nomer(2,15);

        int[] mas_color = new int[count_color];

        for(int i=0;i<count_color;i++){
            mas_color[i] = random_color();
        }


        LinearGradient linearGradient = new LinearGradient(
                random_nomer(0,w),random_nomer(0,h),
                100,20,
                mas_color,
                null,
                Shader.TileMode.MIRROR);

        return  linearGradient;
    }

    private Shader random_radial_gradient(){

        int count_color = random_nomer(2,15);

        int[] mas_color = new int[count_color];

        for(int i=0;i<count_color;i++){
            mas_color[i] = random_color();
        }


        RadialGradient radialGradient = new RadialGradient(
                random_nomer(0,w),random_nomer(0,h),
                random_nomer(1,w/2),
                mas_color,
                null,
                Shader.TileMode.MIRROR);

        return  radialGradient;
    }

    private Shader random_sweep_gradient(){

        int count_color = random_nomer(2,15);

        int[] mas_color = new int[count_color];

        for(int i=0;i<count_color;i++){
            mas_color[i] = random_color();
        }


        SweepGradient sweepGradient = new SweepGradient(
                w/2,h/2,
                mas_color,
                null);

        return  sweepGradient;
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
            mesto_clik_y = event.getY();
            mesto_clik_x = event.getX();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {

            //сравним время нажатия и отпускания

            //если долго посмотрим че он жал
            if ((long_clik + 500) < Integer.valueOf((int) System.currentTimeMillis())) {

                //если выше то сохраним картинку в файл
                if (event.getY() < (h / 2)) {
                    //путь и имя файла
                    String url_img = "/Pictures/Generateawallpaper/" + "Gen" + System.currentTimeMillis() + ".png";
                    save_imag_file(true, url_img);
                } else {
                    //если где сгенерировать, то запустим автомат
                    clik_view_avtomat(true);
                }


                //если задержка маленькая остановим автомат если он работал и экран не трогаем
            } else {

                if (handler != null) {
                    clik_view_avtomat(false);
                } else {
                    //и дальше будем смотреть куды тыкнул пользователь
                    if (event.getY() < (h / 2)) {

                        //если это был свайп вниз(чтобы кнопки навигации показать)
                        if (mesto_clik_y + 100 < event.getY()) {

                            //нечего не делаем , система покажет кнопки

                        } else {

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
                        //Смотрим свайпы в нижней части

                        //если они вертикальные нечего пока небудем делать
                        if (mesto_clik_y+100 < event.getY()) {
                               //сверху вниз
                        } else if (mesto_clik_y-100 > event.getY()) {
                               // снизу вверх
                        } else if (mesto_clik_x+100 < event.getX()) {
                               // лево
                            final String [] mas_shem=getResources().getStringArray(R.array.shemy);
                            if(Main.Schema_rand_kartinki>0) {
                                Main.Schema_rand_kartinki--;
                                Main.save_value("nomer_stroki",String.valueOf(Main.Schema_rand_kartinki));

                                //обновим картинку
                                //**********************
                                Main.run = false;
                                bmp = null;
                                pres_x = event.getX();
                                pres_y = event.getY();
                                destroyDrawingCache();
                                invalidate();
                                //*************************
                                //запустим анимацию
                                Main.Run_anim_view(this.getRootView(),24);
                               // Toast.makeText(getContext(), mas_shem[Main.Schema_rand_kartinki], Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getContext(), "Первая", Toast.LENGTH_SHORT).show();
                                //запустим анимацию
                                Main.Run_anim_view(this.getRootView(),3);
                            }
                        } else if (mesto_clik_x-100 > event.getX()) {
                              //право
                            final String [] mas_shem=getResources().getStringArray(R.array.shemy);
                            if(Main.Schema_rand_kartinki<mas_shem.length-1) {
                                Main.Schema_rand_kartinki++;
                                Main.save_value("nomer_stroki",String.valueOf(Main.Schema_rand_kartinki));

                                //обновим картинку
                                //**********************
                                Main.run = false;
                                bmp = null;
                                pres_x = event.getX();
                                pres_y = event.getY();
                                destroyDrawingCache();
                                invalidate();
                                //*************************
                                //запустим анимацию
                                Main.Run_anim_view(this.getRootView(),25);
                               // Toast.makeText(getContext(), mas_shem[Main.Schema_rand_kartinki], Toast.LENGTH_SHORT).show();
                            }else {
                                //запустим анимацию
                                Main.Run_anim_view(this.getRootView(),3);
                                Toast.makeText(getContext(), "Последняя", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            //обновим картинку
                            Main.run = false;
                            bmp = null;
                            pres_x = event.getX();
                            pres_y = event.getY();

                            destroyDrawingCache();
                            invalidate();

                            //запустим анимацию
                            Main.Run_anim_view(this.getRootView(),52);
                        }

                    }
                }
            }
        }
        return true;
    }
}
