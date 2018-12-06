package dmitriy.deomin.generateawallpaper

import android.app.Activity
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast

import java.io.IOException

import dmitriy.deomin.generateawallpaper.surfase.MySurfaceView
import eu.janmuller.android.simplecropimage.CropImage

import android.R.attr.height
import android.R.attr.width

class Generat : Activity() {
    private val PIC_CROP = 1
    internal lateinit var file: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(Holst(this))
        //setContentView(new MySurfaceView(this));

        //чтобы экран не гас
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //скрывем кнопки навигации
        if (Build.VERSION.SDK_INT < 19) {
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }


        //слушаем  и если че будем реагировать
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        val intentFilter = IntentFilter()
        intentFilter.addAction("Key_signala_pizdec")

        //приёмник  сигналов
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "Key_signala_pizdec") {
                    //получим данные и покажем окошко обобновлении если данные новее
                    file = intent.getStringExtra("key_data_url_imag")

                    //если включена обрезка обрежем и установим
                    if (Main.auto_oboi_costrate) {
                        //запустим в мясорубку
                        runCropImage(file)
                    } else {
                        //если включено установка обоев
                        if (Main.auto_oboi_crete) {
                            //установим
                            val bitmap = BitmapFactory.decodeFile(file)
                            setOboi(bitmap)
                        } else {
                            Toast.makeText(applicationContext, R.string.saved, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        //регистрируем приёмник
        registerReceiver(broadcastReceiver, intentFilter)
        //************************************************************************************
    }


    private fun runCropImage(filePath: String) {

        // create explicit intent
        val intent = Intent(this, CropImage::class.java)

        // tell CropImage activity to look for image to crop
        intent.putExtra(CropImage.IMAGE_PATH, filePath)

        // allow CropImage activity to rescale image
        intent.putExtra(CropImage.SCALE, false)

        // if the aspect ratio is fixed to ratio 3/2
        intent.putExtra(CropImage.ASPECT_X, 1)
        intent.putExtra(CropImage.ASPECT_Y, 1)

        // start activity CropImage with certain request code and listen
        // for result
        startActivityForResult(intent, PIC_CROP)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PIC_CROP) {
            if (data != null) {

                val path = data.getStringExtra(CropImage.IMAGE_PATH) ?: return

                 //если включена установка обоев
                if (Main.auto_oboi_crete) {
                    //увеличим картинку и установим обоями
                    val bitmap = BitmapFactory.decodeFile(file)
                    setOboi(resize(bitmap, Main.hd, Main.hd))
                } else {
                    //иначе скажем
                    Toast.makeText(applicationContext, R.string.obrezano_i_save, Toast.LENGTH_SHORT).show()
                }


            }
        }
    }


    private fun setOboi(bmp: Bitmap?) {
        var bmp = bmp
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        try {
            wallpaperManager.setBitmap(bmp)
            Toast.makeText(applicationContext, R.string.save_i_ustanovleno, Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        bmp = null
    }

    companion object {

        fun resize(bit: Bitmap, newWidth: Int, newHeight: Int): Bitmap {

            val width = bit.width
            val height = bit.height
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            return Bitmap.createBitmap(bit, 0, 0,
                    width, height, matrix, true)
        }
    }
}