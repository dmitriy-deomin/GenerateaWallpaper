package dmitriy.deomin.generateawallpaper.surfase
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import android.os.Environment
import android.os.Handler
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

import dmitriy.deomin.generateawallpaper.Main
import dmitriy.deomin.generateawallpaper.R

import java.lang.Math.cos
import java.lang.Math.sin

class MySurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, View.OnTouchListener {

    internal var kist = Paint()
    internal var bmp: Bitmap? = null
    internal var handler: Handler? = null
    internal lateinit var canvas: Canvas
    internal lateinit var holder: SurfaceHolder
    internal var long_clik: Int = 0
    internal var mesto_clik_y: Float = 0.toFloat() // y
    internal var mesto_clik_x: Float = 0.toFloat() // x
    internal var w: Int = 0
    internal var h: Int = 0

    // String [] mas_pi;

    //место где нажато было для генерации разных картинок
    internal var pres_x = 0f
    internal var pres_y = 0f

    init {
        getHolder().addCallback(this)
        rootView.isDrawingCacheEnabled = true
        isFocusable = true
        //слушаем нажатия
        setOnTouchListener(this)
        //сделаем массив сразу
        // mas_pi = getResources().getString(R.string.pi).split("(?<=\\G.{1})");
    }

    override fun surfaceCreated(holder: SurfaceHolder) { //вызывается, когда surfaceView появляется на экране
        this.holder = holder
        vibor_shemi_i_draw()
    }


    private fun vibor_shemi_i_draw() {
        try {
            canvas = holder.lockCanvas() //получаем canvas
            synchronized(holder) {}
        } catch (e: NullPointerException) {/*если canvas не доступен*/
        }

        w = canvas.width
        h = canvas.height

        if (!Main.run) {
            //По какой схеме рисовать картинки
            when (Main.Schema_rand_kartinki) {
                0 ->
                    //чистый фон
                    shema0(canvas)
                1 ->
                    //круг,квадрат,линии
                    shema1(canvas)
                2 ->
                    //много квадратов одинаковых 3 цвета
                    shema2(canvas)
                3 ->
                    //осколки
                    shema3(canvas)
                4 ->
                    //Много кругов 3-х видов
                    shema4(canvas)
                5 ->
                    //много мелких кругов и квадратов разноцветных
                    shema5(canvas)
                6 ->
                    //паралельные линии
                    shema6(canvas)
                7 ->
                    //линии в разброс
                    shema7(canvas)
                8 ->
                    //Одноцветные круги по спирале Архимеда
                    shema8(canvas)
                9 ->
                    //Разные круги по спирале Архимеда
                    shema9(canvas)
                10 ->
                    //Cпираль Архимеда
                    shema10(canvas)
                11 ->
                    //Одноцветные квадраты по спирале Архимеда
                    shema11(canvas)
                12 ->
                    //Разноцветные круг в круге разноразмерные
                    shema12(canvas)
                13 ->
                    //Числа в разброс
                    shema13(canvas)
                14 ->
                    //Буквы(а-я) в разброс
                    shema14(canvas)
                15 ->
                    //Разноцветные круг в круге
                    shema15(canvas)
                16 ->
                    //сетка
                    shema16(canvas)
                17 ->
                    //Линейный градиент
                    shema17(canvas)
                18 ->
                    //Круговой градиент
                    shema18(canvas)
                19 ->
                    //Радужный градиент
                    shema19(canvas)
                20 ->
                    //круговой фрактал 1
                    shema20(canvas)
                21 ->
                    //круговой фрактал 2 рандом
                    shema21(canvas)
                22 ->
                    //Фрактал Дракон Хартера-Хейтуэя
                    shema22(canvas)
                23 ->
                    //Снежинка Коха
                    shema23(canvas)
                24 ->
                    //Н-фрактал
                    shema24(canvas)
                25 ->
                    //Мандельбротовы облака
                    shema25(canvas)
                26 ->
                    //Центр масс треугольника
                    shema26(canvas)
            }
        }
        //при первом запуске проги
        if (Main.run) {
            help_risunok(canvas)
        }

        //это херня обязательна
        //--------------------
        holder.unlockCanvasAndPost(canvas)
        //        getRootView().buildDrawingCache();
        //        bmp = getRootView().getDrawingCache();
        //   canvas.drawBitmap(bmp, 0, 0, null);
        // holder.unlockCanvasAndPost(canvas);
        //-------------------------------------
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        //когда view меняет свой размер
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) { //когда view исчезает из поля зрения
    }

    /////
    /////
    /////*********** Схемы рисования херни *************************
    /////
    /////
    private fun help_risunok(canvas: Canvas) {

        canvas.drawColor(Main.color_fon_main)

        val shadowPaint = Paint()

        shadowPaint.isAntiAlias = true
        shadowPaint.textAlign = Paint.Align.CENTER
        shadowPaint.color = Color.WHITE
        shadowPaint.textSize = (Main.wd / 10).toFloat()
        shadowPaint.strokeWidth = 2.0f
        shadowPaint.style = Paint.Style.STROKE
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK)

        canvas.drawText(context.getString(R.string.Ustanovit), (w / 2).toFloat(), 200f, shadowPaint)
        canvas.drawText(context.getString(R.string.Sgeneririvat), (w / 2).toFloat(), (h - 200).toFloat(), shadowPaint)

        shadowPaint.textSize = (Main.wd / 15).toFloat()
        canvas.drawText(context.getString(R.string.Svayp_sverhu_v_niz), (w / 2).toFloat(), (h / 2).toFloat(), shadowPaint)
        canvas.drawText(context.getString(R.string.pokaget_knopki_navigacii), (w / 2).toFloat(), (h / 2 + 60).toFloat(), shadowPaint)

        shadowPaint.textSize = (Main.wd / 25).toFloat()
        canvas.drawText(context.getString(R.string.dolgoe_nagatie_save_kartinku), (w / 2).toFloat(), 250f, shadowPaint)
        canvas.drawText(context.getString(R.string.dolgoe_nagatie_zapustit_tik), (w / 2).toFloat(), (h - 150).toFloat(), shadowPaint)
        shadowPaint.textSize = (Main.wd / 20).toFloat()
        canvas.drawText(context.getString(R.string.svayp_levo_pravo), (w / 2).toFloat(), (h - 300).toFloat(), shadowPaint)

        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
    }

    //чистый фон
    private fun shema0(canvas: Canvas) {
        kist.isAntiAlias = true
        //будем случайно просто цветом заливать
        canvas.drawColor(random_color())

        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
    }

    //круг,квадрат,линии
    private fun shema1(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        kist.strokeWidth = 10f
        kist.color = random_color()

        rand_on_of_kist_sglagivanie()
        rand_on_of_zalivka()

        for (l in 0 until random_nomer(0, 20)) {
            val wrand = random_nomer(0, w)

            for (i in 0 until random_nomer(0, h)) {
                canvas.drawPoint(wrand.toFloat(), i.toFloat(), kist)
            }
        }

        canvas.drawRect(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), kist)
        canvas.drawCircle(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), random_nomer(0, w).toFloat(), kist)

        buildDrawingCache()
        bmp = drawingCache

        canvas.drawBitmap(bmp!!, 0f, 0f, null)
    }

    //много одинаковых квадратов
    private fun shema2(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        kist.strokeWidth = random_nomer(2, 100).toFloat()
        kist.color = random_color()

        rand_on_of_kist_sglagivanie()
        rand_on_of_zalivka()

        //много одинаковых квадратов
        for (i in 0 until random_nomer(0, 777)) {
            canvas.drawPoint(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), kist)
        }

        //еще квадраты другова цвета
        kist.color = random_color()
        for (i in 0 until random_nomer(0, 777)) {
            canvas.drawPoint(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), kist)
        }


        //и еще квадраты другова цвета
        kist.color = random_color()
        for (i in 0 until random_nomer(0, 777)) {
            canvas.drawPoint(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), kist)
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    //осколки
    private fun shema3(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        kist.strokeWidth = random_nomer(2, 100).toFloat()
        kist.color = random_color()


        kist.pathEffect = null
        rand_on_of_zalivka()

        val path = Path()

        path.moveTo(random_nomer(0, w).toFloat(), random_nomer(0, w).toFloat())
        //осколки
        for (i in 0 until random_nomer(0, 777)) {
            path.lineTo(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat())
        }
        path.close()

        canvas.drawPath(path, kist)


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    //Много кругов 3-х видов
    private fun shema4(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        kist.strokeWidth = random_nomer(2, 100).toFloat()
        kist.color = random_color()

        rand_on_of_kist_sglagivanie()
        rand_on_of_zalivka()

        //много кругов
        for (i in 0 until random_nomer(0, 777)) {
            canvas.drawCircle(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), random_nomer(0, w / 5).toFloat(), kist)
        }

        //еще много кругов
        kist.color = random_color()
        for (i in 0 until random_nomer(0, 777)) {
            canvas.drawCircle(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), random_nomer(0, w / 5).toFloat(), kist)
        }


        //и еще много кругов
        kist.color = random_color()
        for (i in 0 until random_nomer(0, 777)) {
            canvas.drawCircle(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), random_nomer(0, w / 5).toFloat(), kist)
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    //много мелких кругов и квадратов
    private fun shema5(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        kist.strokeWidth = random_nomer(2, 100).toFloat()
        kist.color = random_color()


        rand_on_of_kist_sglagivanie()
        rand_on_of_zalivka()

        //много кругов и много квадратов
        for (i in 0 until random_nomer(0, 2000)) {

            kist.color = random_color()
            canvas.drawCircle(random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(), random_nomer(0, w / 8).toFloat(), kist)

            kist.color = random_color()
            //размер квадрата
            val x = random_nomer(0, w)
            val y = random_nomer(0, h)
            val r = random_nomer(0, w / 8)
            canvas.drawRect(x.toFloat(), y.toFloat(), (x + r).toFloat(), (y + r).toFloat(), kist)
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    //паралельные линии
    private fun shema6(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        kist.strokeWidth = random_nomer(2, 300).toFloat()
        kist.color = random_color()

        //направление линий неразобрался кок лучше пока зделать
        val ugol = random_nomer(0, 1)

        //много линий
        for (i in 0 until random_nomer(0, 2000)) {

            kist.color = random_color()
            kist.strokeWidth = random_nomer(2, 100).toFloat()

            //int x = random_nomer(Integer.valueOf((int) pres_x),w);

            val y = random_nomer(0, h)
            val x = random_nomer(0, w)

            val dlina = random_nomer(0, h + w)


            //пока только два направления хер знает как расширить
            if (ugol == 0) {
                canvas.drawLine(x.toFloat(), y.toFloat(), (x + dlina).toFloat(), (y + dlina).toFloat(), kist)
            } else {
                canvas.drawLine(x.toFloat(), y.toFloat(), (x + dlina).toFloat(), (y - dlina).toFloat(), kist)
            }

        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    // линии в разброс
    private fun shema7(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        kist.strokeWidth = random_nomer(2, 300).toFloat()
        kist.color = random_color()

        //много линий в разброс
        for (i in 0 until random_nomer(0, 2000)) {

            kist.color = random_color()
            kist.strokeWidth = random_nomer(2, 100).toFloat()

            val x = random_nomer(Integer.valueOf(pres_x.toInt()), w)

            canvas.drawLine(random_nomer(x - 200, w).toFloat(), random_nomer(0, h).toFloat(), random_nomer(0, w + 200).toFloat(), random_nomer(0, h + 200).toFloat(), kist)

        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    //Одноцветные круги по спирале Архимеда
    private fun shema8(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        //размер кисти
        kist.strokeWidth = random_nomer(1, 25).toFloat()
        kist.color = random_color()

        rand_on_of_kist_sglagivanie()
        rand_on_of_zalivka()


        var phi: Int
        var r: Int
        val k: Int
        val spase: Int
        val razmer_tochek: Int

        //растояние между витками
        k = 1
        //растояние между точек
        spase = 1
        razmer_tochek = random_nomer(1, 30)

        for (i in 0 until h) {
            phi = i * spase
            r = k * i
            val x = r * cos(phi.toDouble())
            val y = r * sin(phi.toDouble())
            canvas.drawCircle((x.toInt() + w / 2).toFloat(), (h / 2 - y.toInt()).toFloat(), razmer_tochek.toFloat(), kist)
        }

        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    //Разные круги по спирале Архимеда
    private fun shema9(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        //размер кисти
        kist.strokeWidth = random_nomer(1, 50).toFloat()
        kist.color = random_color()

        rand_on_of_kist_sglagivanie()
        rand_on_of_zalivka()


        var phi: Int
        var r: Int
        val k: Int
        val spase: Int
        var razmer_tochek: Int

        //растояние между витками
        k = 1
        //растояние между точек
        spase = 1

        for (i in 1 until h) {
            phi = i * spase
            r = k * i
            val x = r * cos(phi.toDouble())
            val y = r * sin(phi.toDouble())
            razmer_tochek = random_nomer(1, 50)
            kist.color = random_color()
            canvas.drawCircle((x.toInt() + w / 2).toFloat(), (h / 2 - y.toInt()).toFloat(), razmer_tochek.toFloat(), kist)
        }

        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    //Cпираль Архимеда
    private fun shema10(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        //размер кисти
        kist.strokeWidth = random_nomer(1, 10).toFloat()
        kist.color = random_color()
        kist.style = Paint.Style.STROKE


        var phi: Int
        var r: Int
        val k: Int
        val spase: Int
        val razmer_tochek: Int

        //растояние между витками
        k = random_nomer(3, 10)
        //растояние между точек
        spase = random_nomer(20, 70)

        val curve = Path()

        curve.moveTo((w / 2).toFloat(), (h / 2).toFloat())

        for (i in 1 until w) {
            phi = i * spase
            r = k * i
            val x = r * cos(phi.toDouble())
            val y = r * sin(phi.toDouble())

            curve.lineTo((x.toInt() + w / 2).toFloat(), (h / 2 - y.toInt()).toFloat())
        }
        // curve.close();


        //сглаживание краёв
        val radius = 15.0f
        val mCornerPathEffect = CornerPathEffect(radius)
        kist.pathEffect = mCornerPathEffect


        canvas.drawPath(curve, kist)

        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    //Одноцветные квадраты по спирале Архимеда
    private fun shema11(canvas: Canvas) {
        canvas.drawColor(random_color())

        kist.isAntiAlias = true
        //размер кисти
        kist.strokeWidth = random_nomer(1, 60).toFloat()
        kist.color = random_color()

        rand_on_of_kist_sglagivanie()
        rand_on_of_zalivka()


        var phi: Int
        var r: Int
        val k: Int
        val spase: Int

        //растояние между витками
        k = 1
        //растояние между точек
        spase = 1

        for (i in 0..h) {
            phi = i * spase
            r = k * i
            val x = r * cos(phi.toDouble())
            val y = r * sin(phi.toDouble())
            canvas.drawPoint((x.toInt() + w / 2).toFloat(), (h / 2 - y.toInt()).toFloat(), kist)
        }

        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //------------------------------------
    }

    //Разноцветные круг в круге разноразмерные
    private fun shema12(canvas: Canvas) {

        canvas.drawColor(random_color())
        kist.isAntiAlias = true
        //размер кисти
        kist.strokeWidth = 1f
        kist.color = random_color()
        kist.style = Paint.Style.FILL


        //начальные координаты
        val xn = w / 2
        val yn = h / 2


        var i = w
        while (i != 0) {


            //сначала большой круг нарисуем, потом рандомно будем вставлять в него разные
            if (i == w) {
                canvas.drawCircle(xn.toFloat(), yn.toFloat(), i.toFloat(), kist)
            } else {
                //радиус будем брать случайный , максимальный будет предыдущий -1

                val radius = random_nomer(1, i - 1)
                i = radius
                kist.color = random_color()
                canvas.drawCircle(xn.toFloat(), yn.toFloat(), radius.toFloat(), kist)

            }
            i--


        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //Числа в разброс
    private fun shema13(canvas: Canvas) {

        canvas.drawColor(random_color())
        kist.isAntiAlias = true
        //размер кисти
        kist.strokeWidth = 1f
        kist.color = random_color()
        kist.style = Paint.Style.STROKE


        val mas_num = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "/", "*", "-", "+", "=", "~", "#", "%", "№", "y = x2", "y = kx", "y = kx + b", "y = xn", "y = k/x", "y = sinx", "y = cosx", "cos(5*x+10)")

        val count_number = random_nomer(mas_num.size, w / 20)

        for (i in 0 until count_number) {

            kist.textSize = random_nomer(w / 100, w / 10).toFloat()

            val xn = random_nomer(0, w)
            val yn = random_nomer(0, h)
            val number = random_nomer(0, mas_num.size - 1)

            kist.color = random_color()


            canvas.drawText(mas_num[number], xn.toFloat(), yn.toFloat(), kist)
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------


    }

    //Буквы(а-я, a-z) в разброс
    private fun shema14(canvas: Canvas) {

        canvas.drawColor(random_color())
        kist.isAntiAlias = true
        //размер кисти
        kist.strokeWidth = 1f
        kist.color = random_color()

        //прозрачночть будет случайная
        rand_on_of_zalivka()


        val count_number = random_nomer(10, w / 20)

        val mas_num = arrayOf("А", "а", "Б", "б", "В", "в", "Г", "г", "Д", "д", "Е", "е", "Ё", "ё", "Ж", "ж", "З", "з", "И", "и", "Й", "й", "К", "к", "Л", "л", "М", "м", "Н", "н", "О", "о", "П", "п", "Р", "р", "С", "с", "Т", "т", "У", "у", "Ф", "ф", "Ц", "ц", "Ч", "ч", "Ш", "ш", "Щ", "щ", "Ъ", "ъ", "Ы", "ы", "Ь", "ь", "Э", "э", "Ю", "ю", "Я", "я",

                "A", "a", "B", "b", "C", "c", "D", "d", "E", "e", "F", "f", "G", "g", "H", "h", "I", "i", "J", "j", "K", "k", "L", "l", "M", "m", "N", "n", "O", "o", "P", "p", "Q", "q", "R", "r", "S", "s", "T", "t", "U", "u", "V", "v", "W", "w", "X", "x", "Y", "y", "Z", "z")


        for (i in 0 until count_number) {

            kist.textSize = random_nomer(100, 200).toFloat()

            val xn = random_nomer(0, w)
            val yn = random_nomer(0, h)
            val number = random_nomer(0, mas_num.size - 1)

            kist.color = random_color()


            canvas.drawText(mas_num[number], xn.toFloat(), yn.toFloat(), kist)
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------

    }

    //Разноцветные круг в круге
    private fun shema15(canvas: Canvas) {

        canvas.drawColor(random_color())
        kist.isAntiAlias = true
        //размер кисти
        kist.strokeWidth = 10f
        kist.color = random_color()
        kist.style = Paint.Style.STROKE


        //начальные координаты
        val xn = w / 2
        val yn = h / 2


        var i = 0
        while (i < w) {
            kist.color = random_color()
            canvas.drawCircle(xn.toFloat(), yn.toFloat(), i.toFloat(), kist)
            i = i + 10
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //сетка
    private fun shema16(canvas: Canvas) {

        canvas.drawColor(random_color())
        kist.isAntiAlias = true
        kist.color = random_color()

        rand_on_of_zalivka()
        rand_on_of_kist_sglagivanie()

        val step = random_nomer(10, 60)
        kist.strokeWidth = random_nomer(5, step).toFloat()

        var i = 0
        while (i <= h) {
            var r = 0
            while (r <= w) {

                canvas.drawPoint(r.toFloat(), i.toFloat(), kist)
                r = r + step
            }
            i = i + step
        }

        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //Линейный градиент
    private fun shema17(canvas: Canvas) {

        //градиент линейный
        kist.style = Paint.Style.FILL
        kist.shader = random_liner_gradient()
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), kist)

        //выключаем градиент
        kist.shader = null

        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //Круговой градиент
    private fun shema18(canvas: Canvas) {

        //Круговой градиент
        kist.style = Paint.Style.FILL
        kist.shader = random_radial_gradient()
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), kist)

        //выключаем градиент
        kist.shader = null

        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //Радужный градиент
    private fun shema19(canvas: Canvas) {

        //Радужный градиент
        kist.style = Paint.Style.FILL
        kist.shader = random_sweep_gradient()
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), kist)

        //выключаем градиент
        kist.shader = null

        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //круговой фрактал 1
    private fun shema20(canvas: Canvas) {

        //толщина линии
        kist.strokeWidth = 1f
        //
        kist.style = Paint.Style.STROKE
        //
        kist.isAntiAlias = true
        //будем случайно просто цветом заливать
        canvas.drawColor(random_color())
        //
        kist.color = random_color()
        //рекурсивная функция
        DrCirc((w / 2).toDouble(), (h / 2).toDouble(), w / 2, canvas)


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //круговой фрактал 2 random
    private fun shema21(canvas: Canvas) {
        //толщина линии
        kist.strokeWidth = 1f
        //
        kist.style = Paint.Style.STROKE
        //
        kist.isAntiAlias = true
        //будем случайно просто цветом заливать
        canvas.drawColor(random_color())
        //
        kist.color = random_color()
        //рекурсивная функция
        DrCirc_random((w / 2).toDouble(), (h / 2).toDouble(), w / 2, canvas)


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //Фрактал Дракон Хартера-Хейтуэя
    private fun shema22(canvas: Canvas) {


        //толщина линии
        kist.strokeWidth = random_nomer(1, 3).toFloat()
        //
        kist.style = Paint.Style.STROKE
        //
        kist.isAntiAlias = true
        //будем случайно просто цветом заливать
        canvas.drawColor(random_color())
        //
        kist.color = random_color()
        //рекурсивная функция
        drawDragon(random_nomer(0, w), random_nomer(0, h), random_nomer(0, w), random_nomer(0, h), 15, canvas)


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //Снежинка Коха
    private fun shema23(canvas: Canvas) {

        //
        kist.style = Paint.Style.STROKE
        //
        kist.strokeWidth = random_nomer(1, 10).toFloat()

        kist.isAntiAlias = true
        //фон будем случайно просто цветом заливать
        canvas.drawColor(random_color())
        //
        kist.color = random_color()


        /**
         * Рисует рекурсивно линию Коха. При этом отрезок (a; b) делится на 3 равных
         * части. Средняя из них заменяется равносторонним треугольником со стороной
         * равной данной и без данной стороны (рисуется только две, не принадлежащие
         * отрезку стороны треугольника).
         *
         * @param g
         * the specified Graphics context
         * @param a
         * начальная точка линии
         * @param b
         * конечная точка линии
         * @param fi
         * угол поворота линии
         * @param n
         * оставшаяся глубина рекурсии
         */

        for (i in 0..1) {
            if (i == 0) {
                val a = Point(0, 10)
                val b = Point(w, 10)
                //рекурсивная функция
                drawKochLine(canvas, a, b, 0.0, 7)
            }
            if (i == 1) {
                val a = Point(w, 10)
                val b = Point(10, h)
                //рекурсивная функция
                drawKochLine(canvas, a, b, 90.0, 7)
            }
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //Н-фрактал
    private fun shema24(canvas: Canvas) {

        //
        kist.style = Paint.Style.STROKE
        //
        kist.strokeWidth = random_nomer(1, 3).toFloat()
        //
        kist.isAntiAlias = true
        //фон будем случайно просто цветом заливать
        canvas.drawColor(random_color())
        //
        kist.color = random_color()

        DrawHF(canvas, w / 2, h / 2, random_nomer(w / 5, w / 3), random_nomer(3, 7))


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //Мандельбротовы облака (взято из http://grafika.me/node/372)
    private fun shema25(canvas: Canvas) {

        //подготовка кисти
        //*************************************
        //стиль(заливка или обводка)
        kist.style = Paint.Style.FILL
        //толщина кисти
        kist.strokeWidth = 1f
        //сглаживание
        kist.isAntiAlias = false
        //фон будем случайно просто цветом заливать
        canvas.drawColor(random_color())
        //случайный цвет кисти
        // kist.setColor(random_color());
        //********************************************

        val SX = 0.007// Чем больше значение тем больше точки похожи на плоскость
        val SY = 0.007
        val DX = (-w / 2).toDouble()// для центровки внутри фрейма
        val DY = (-h / 2).toDouble()// для центровки внутри фрейма
        val COUNT_ITER = 3// число итераций, чем больше число - тем больше точек
        val BAIL_OUT = 20
        val STEP_X = 15
        val STEP_Y = 15

        var i = 0
        while (i < w) {
            var j = 0
            while (j < h) {
                val c = SX * (i + DX) // центрируем по X
                val d = SY * (j + DY) // центрируем по Y
                var x = c // ось х
                var y = d // ось y
                var t: Double
                var k = 0

                kist.color = random_color()// при каждой итерации цикла получаем новый цвет

                while (x * x + y * y < BAIL_OUT && k < COUNT_ITER) { // алгоритм
                    t = x * x - y * y + c
                    y = 2.0 * x * y + d
                    x = t
                    canvas.drawCircle((x / SX - DX).toInt().toFloat(), (y / SY - DY).toInt().toFloat(), 4f, kist)
                    ++k
                }
                j += STEP_Y
            }
            i += STEP_X
        }


        //это херня обязательна
        //--------------------
        buildDrawingCache()
        bmp = drawingCache
        canvas.drawBitmap(bmp!!, 0f, 0f, null)
        //-------------------------------------
    }

    //Центр масс треугольника (взято из http://grafika.me/node/371)
    private fun shema26(canvas: Canvas) {

        //подготовка кисти
        //*************************************
        //стиль(заливка или обводка)
        kist.style = Paint.Style.FILL
        //толщина кисти
        kist.strokeWidth = random_nomer(1, 5).toFloat()
        //сглаживание
        kist.isAntiAlias = true
        //фон будем случайно просто цветом заливать
        canvas.drawColor(random_color())
        //случайный цвет кисти
        // kist.setColor(random_color());
        //********************************************

        val pozicia = w / 2

        val A = Point(w / 2, pozicia)//выберем начальные точки, чтоб красиво смотрелись
        val B = Point(w - 20, w + pozicia)
        val C = Point(20, w + pozicia)
        canvas.drawLine(A.x.toFloat(), A.y.toFloat(), B.x.toFloat(), B.y.toFloat(), kist)//рисуем изначальный треугольник
        canvas.drawLine(B.x.toFloat(), B.y.toFloat(), C.x.toFloat(), C.y.toFloat(), kist)
        canvas.drawLine(A.x.toFloat(), A.y.toFloat(), C.x.toFloat(), C.y.toFloat(), kist)


        drawMCT(canvas, A, B, C, 7)//вызываем отрисовку фрактала

    }
    //////
    /////
    /////*********** Схемы рисования херни *************************
    /////
    /////

    //Центр масс треугольника взял от сюда
    //http://grafika.me/node/371
    fun drawMCT(g: Canvas, A: Point, B: Point, C: Point, iter: Int): Int {
        //в качестве параметров точки А В С и кол-во итераций
        //g - экземпляр библиотечного класса, ответственного за отрисовку
        if (iter == 0)
        //если итераций 0, выход
            return 0
        val D = Point() //точка центра масс
        val v1 = Point()//вектор AB
        val v2 = Point()//вектор AC
        v1.x = B.x - A.x
        v1.y = B.y - A.y
        v2.x = C.x - A.x
        v2.y = C.y - A.y
        D.x = A.x + (v1.x + v2.x) / 3 //к точке А прибавим сумму векторов AВ и AC, деленную на 3
        D.y = A.y + (v1.y + v2.y) / 3 //и получим координаты центра масс


        kist.color = random_color()

        g.drawLine(A.x.toFloat(), A.y.toFloat(), D.x.toFloat(), D.y.toFloat(), kist)//рисуем отрезки от вершин к центру масс
        g.drawLine(B.x.toFloat(), B.y.toFloat(), D.x.toFloat(), D.y.toFloat(), kist)
        g.drawLine(C.x.toFloat(), C.y.toFloat(), D.x.toFloat(), D.y.toFloat(), kist)

        drawMCT(g, A, B, D, iter - 1)//вызываем рекурсивно процендуру для полученных
        drawMCT(g, B, C, D, iter - 1)//треугольников, с итерацией, меньшей на 1
        drawMCT(g, A, C, D, iter - 1)

        return 0

    }

    //Н-фрактал
    //***************************************************
    //взял от сюда
    //http://grafika.me/node/438
    fun DrawH(g: Canvas, x: Float, y: Float, raz: Int) {        // метод рисующий Н


        g.drawLine(x - raz, y - raz, x - raz, y + raz, kist)
        g.drawLine(x - raz, y, x + raz, y, kist)
        g.drawLine(x + raz, y - raz, x + raz, y + raz, kist)


    }

    private fun DrawHF(g: Canvas, x1: Int, y1: Int, razmer: Int, min: Int) {
        var razmer = razmer

        val x11 = x1 + razmer
        val y11 = y1 + razmer
        val x01 = x1 - razmer
        val y01 = y1 + razmer
        val x00 = x1 - razmer
        val y00 = y1 - razmer
        val x10 = x1 + razmer
        val y10 = y1 - razmer

        DrawH(g, x1.toFloat(), y1.toFloat(), razmer)
        razmer = razmer / 2

        if (razmer >= min) {
            DrawHF(g, x11, y11, razmer, min)
            DrawHF(g, x01, y01, razmer, min)
            DrawHF(g, x10, y10, razmer, min)
            DrawHF(g, x00, y00, razmer, min)
        }

    }

    //**************************************************************
    //Снежинка Коха
    fun drawKochLine(g: Canvas, a: Point, b: Point, fi: Double, n: Int) {
        var n = n
        //взял от сюда
        //http://grafika.me/node/437
        //


        if (n <= 0) {
            // рисуем прямую, если достигнута необходимая глубина рекурсии.
            g.drawLine(a.x.toFloat(), a.y.toFloat(), b.x.toFloat(), b.y.toFloat(), kist)
        } else {
            // находим длину отрезка (a; b).
            val length = Math.pow(Math.pow((b.y - a.y).toDouble(), 2.0) + Math.pow((b.x - a.x).toDouble(), 2.0), 0.5)
            // находим длину 1/3 отрезка (a; b)
            val length1of3 = length / 3

            // находим т., делящую отрезок как 1:3.
            val a1 = Point(a.x + Math.round(length1of3 * Math.cos(fi)).toInt(), a.y + Math.round(length1of3 * Math.sin(fi)).toInt())

            // находим т., делящую отрезок как 2:3.
            val b1 = Point(a1.x + Math.round(length1of3 * Math.cos(fi)).toInt(), a1.y + Math.round(length1of3 * Math.sin(fi)).toInt())

            // находим т., которая будет вершиной равностороннего
            // треугольника.
            val c = Point(a1.x + Math
                    .round(length1of3 * Math.cos(fi + Math.PI / 3)).toInt(),
                    a1.y + Math.round(length1of3 * Math.sin(fi + Math.PI / 3)).toInt())

            n--
            drawKochLine(g, a1, c, fi + Math.PI / 3, n)
            drawKochLine(g, c, b1, fi - Math.PI / 3, n)

            n--
            drawKochLine(g, a, a1, fi, n)
            drawKochLine(g, b1, b, fi, n)
        }
    }

    //Фрактал Дракон Хартера-Хейтуэя
    private fun drawDragon(x1: Int, y1: Int, x2: Int, y2: Int, n: Int, g: Canvas)//Основная рекурсивная функция
    {
        //взял от сюда
        //http://grafika.me/node/463
        //


        val xn: Int
        val yn: Int
        if (n > 0)
        //Пока n > 0 - продолжаем поворачивать прямые на 90 градусов и  уменьшать их размеры
        {
            xn = (x1 + x2) / 2 + (y2 - y1) / 2
            yn = (y1 + y2) / 2 - (x2 - x1) / 2
            drawDragon(x2, y2, xn, yn, n - 1, g)
            drawDragon(x1, y1, xn, yn, n - 1, g)
        }
        if (n == 3)
        //Приступаем к рисованию прямых
            g.drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), kist)
    }

    //№20 круговой фрактал
    fun DrCirc(x: Double, y: Double, size: Int, g: Canvas) {
        //------------------------------
        // взял от сюда
        //----------------------------
        //http://grafika.me/node/532
        //-----------------------------

        //количество кругов
        val N = 6

        val c = 3
        var rad = 0
        var den = 0
        //всего получится (N+1) окружность
        //новые окружности будут иметь радиус size/c
        if (size > 3) {
            rad = Math.round((size / c).toFloat())       //уменьшаем радиус
            den = Math.round((size * (c - 1) / c).toFloat())
            DrCirc(x, y, rad, g)           //центральная окружность
            for (i in 0 until N) {     //все остальные окружности
                DrCirc(x - Math.round(den * Math.sin(2 * Math.PI / N * i)), y + Math.round(den * Math.cos(2 * Math.PI / N * i)), rad, g)
            }
            g.drawCircle((x - size).toFloat(), y.toFloat() - size, (2 * size).toFloat(), kist)
        }
    }

    //№21 круговой фрактал random
    fun DrCirc_random(x: Double, y: Double, size: Int, g: Canvas) {
        //------------------------------
        // взял от сюда
        //----------------------------
        //http://grafika.me/node/532
        //-----------------------------

        //количество кругов
        val N = random_nomer(6, 10)

        val c = N / 2
        var rad = 0
        var den = 0
        //всего получится (N+1) окружность
        //новые окружности будут иметь радиус size/c
        if (size > 3) {
            rad = Math.round((size / c).toFloat())       //уменьшаем радиус
            den = Math.round((size * (c - 1) / c).toFloat())
            DrCirc_random(x, y, rad, g)           //центральная окружность
            for (i in 0 until N) {     //все остальные окружности
                DrCirc_random(x - Math.round(den * Math.sin(2 * Math.PI / N * i)), y + Math.round(den * Math.cos(2 * Math.PI / N * i)), rad, g)
            }
            g.drawCircle((x - size).toFloat(), y.toFloat() - size, (2 * size).toFloat(), kist)
        }
    }

    private fun rand_on_of_kist_sglagivanie() {
        //сглаживание краёв
        if (random_nomer(0, 1) == 0) {
            kist.pathEffect = null
        } else {
            val radius = 15.0f
            val mCornerPathEffect = CornerPathEffect(radius)
            kist.pathEffect = mCornerPathEffect
        }
    }

    private fun rand_on_of_zalivka() {
        when (random_nomer(0, 2)) {
            0 -> kist.style = Paint.Style.STROKE
            1 -> kist.style = Paint.Style.FILL
            2 -> kist.style = Paint.Style.FILL_AND_STROKE
        }
    }

    private fun random_color(): Int {
        val r = random_nomer(0, 255)
        val g = random_nomer(0, 255)
        val b = random_nomer(0, 255)
        return Color.rgb(r, g, b)
    }

    private fun random_liner_gradient(): Shader {

        val count_color = random_nomer(2, 15)

        val mas_color = IntArray(count_color)

        for (i in 0 until count_color) {
            mas_color[i] = random_color()
        }


        return LinearGradient(
                random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(),
                100f, 20f,
                mas_color, null,
                Shader.TileMode.MIRROR)
    }

    private fun random_radial_gradient(): Shader {

        val count_color = random_nomer(2, 15)

        val mas_color = IntArray(count_color)

        for (i in 0 until count_color) {
            mas_color[i] = random_color()
        }


        return RadialGradient(
                random_nomer(0, w).toFloat(), random_nomer(0, h).toFloat(),
                random_nomer(1, w / 2).toFloat(),
                mas_color, null,
                Shader.TileMode.MIRROR)
    }

    private fun random_sweep_gradient(): Shader {

        val count_color = random_nomer(2, 15)

        val mas_color = IntArray(count_color)

        for (i in 0 until count_color) {
            mas_color[i] = random_color()
        }


        return SweepGradient(
                (w / 2).toFloat(), (h / 2).toFloat(),
                mas_color, null)
    }

    private fun random_nomer(min: Int, max: Int): Int {
        var max = max
        max -= min
        return (Math.random() * ++max).toInt() + min
    }

    private fun clik_view_avtomat(run: Boolean?) {

        if (handler == null) {
            handler = Handler()
            handler!!.post(object : Runnable {
                override fun run() {

                    // действие
                    //--------------------
                    Main.run = false
                    bmp = null
                    destroyDrawingCache()
                    invalidate()
                    //---------------------

                    if (run!!) {
                        //повторы 1 секуннд
                        handler!!.postDelayed(this, Main.Time_dolbeshki_ekrana.toLong())
                    }

                }
            })
        } else {
            if ((!run!!)!!) {
                handler!!.removeMessages(0)
                handler = null
            }

        }

    }

    private fun save_imag_file(pokaz_toast: Boolean?, url_img: String) {

        //создадим папки если нет
        val sddir = File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Generateawallpaper/")
        if (!sddir.exists()) {
            sddir.mkdirs()
        }

        try {
            val fos = FileOutputStream(File(Environment.getExternalStorageDirectory().toString(), url_img))
            bmp!!.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (pokaz_toast!!) {
            Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        v.buildDrawingCache()
        bmp = v.drawingCache

        if (event.action == MotionEvent.ACTION_DOWN) {
            //сохраним время нажатия
            long_clik = Integer.valueOf(System.currentTimeMillis().toInt())
            //сохраним место нажатия
            mesto_clik_y = event.y
            mesto_clik_x = event.x
        }

        if (event.action == MotionEvent.ACTION_UP) {

            //сравним время нажатия и отпускания

            //если долго посмотрим че он жал
            if (long_clik + 500 < Integer.valueOf(System.currentTimeMillis().toInt())) {

                //если выше то сохраним картинку в файл
                if (event.y < h / 2) {
                    //путь и имя файла
                    val url_img = "/Pictures/Generateawallpaper/" + "Gen" + System.currentTimeMillis() + ".png"
                    save_imag_file(true, url_img)
                } else {
                    //если где сгенерировать, то запустим автомат
                    clik_view_avtomat(true)
                }


                //если задержка маленькая остановим автомат если он работал и экран не трогаем
            } else {

                if (handler != null) {
                    clik_view_avtomat(false)
                } else {
                    //и дальше будем смотреть куды тыкнул пользователь
                    if (event.y < h / 2) {

                        //если это был свайп вниз(чтобы кнопки навигации показать)
                        if (mesto_clik_y + 100 < event.y) {

                            //нечего не делаем , система покажет кнопки

                        } else {

                            //путь и имя файла
                            val url_img = "/Pictures/Generateawallpaper/" + "Gen" + System.currentTimeMillis() + ".png"

                            //сночала сахраним картинку
                            save_imag_file(false, url_img)

                            //после сохранения установим картинку обоями если включено или обрежем её
                            // будем слать сигналы в
                            //**************
                            val imag = Intent("Key_signala_pizdec")
                            imag.putExtra("key_data_url_imag", Environment.getExternalStorageDirectory().toString() + url_img)
                            context.sendBroadcast(imag)
                            //************
                        }
                    } else {
                        //Смотрим свайпы в нижней части

                        //если они вертикальные нечего пока небудем делать
                        if (mesto_clik_y + 100 < event.y) {
                            //сверху вниз
                        } else if (mesto_clik_y - 100 > event.y) {
                            // снизу вверх
                        } else if (mesto_clik_x + 100 < event.x) {
                            // лево
                            val mas_shem = resources.getStringArray(R.array.shemy)
                            if (Main.Schema_rand_kartinki > 0) {
                                Main.Schema_rand_kartinki--
                                Main.save_value("nomer_stroki", Main.Schema_rand_kartinki.toString())

                                //обновим картинку
                                //**********************
                                Main.run = false
                                bmp = null
                                pres_x = event.x
                                pres_y = event.y
                                destroyDrawingCache()
                                vibor_shemi_i_draw()
                                //*************************

                                //запустим анимацию если включено
                                if (Main.ANIMACIA_RUN) {
                                    val anim_clik = AnimationUtils.loadAnimation(context, R.anim.anim_levo)
                                    v.startAnimation(anim_clik)
                                }


                            } else {
                                Toast.makeText(context, "Первая", Toast.LENGTH_SHORT).show()
                                //запустим анимацию если включено
                                if (Main.ANIMACIA_RUN) {
                                    val anim_clik = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                                    v.startAnimation(anim_clik)
                                }
                            }
                        } else if (mesto_clik_x - 100 > event.x) {
                            //право
                            val mas_shem = resources.getStringArray(R.array.shemy)
                            if (Main.Schema_rand_kartinki < mas_shem.size - 1) {
                                Main.Schema_rand_kartinki++
                                Main.save_value("nomer_stroki", Main.Schema_rand_kartinki.toString())

                                //обновим картинку
                                //**********************
                                Main.run = false
                                bmp = null
                                pres_x = event.x
                                pres_y = event.y
                                destroyDrawingCache()
                                vibor_shemi_i_draw()
                                //*************************
                                //запустим анимацию если включено
                                if (Main.ANIMACIA_RUN) {
                                    val anim_l = AnimationUtils.loadAnimation(context, R.anim.anim_pravo)
                                    v.startAnimation(anim_l)
                                }

                            } else {
                                Toast.makeText(context, "Последняя", Toast.LENGTH_SHORT).show()
                                //запустим анимацию если включено
                                if (Main.ANIMACIA_RUN) {
                                    val anim_clik = AnimationUtils.loadAnimation(context, R.anim.myalpha)
                                    v.startAnimation(anim_clik)
                                }

                            }


                        } else {
                            //обновим картинку
                            Main.run = false
                            bmp = null
                            pres_x = event.x
                            pres_y = event.y

                            destroyDrawingCache()
                            vibor_shemi_i_draw()

                            //запустим анимацию если включено
                            if (Main.ANIMACIA_RUN) {
                                val anim_clik = AnimationUtils.loadAnimation(context, R.anim.myscale)
                                v.startAnimation(anim_clik)
                            }
                        }

                    }
                }
            }
        }
        return true
    }

}