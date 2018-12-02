package dmitriy.deomin.generateawallpaper

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.squareup.picasso.Picasso

import java.io.File
import java.io.IOException

import eu.janmuller.android.simplecropimage.CropImage

class ScreenSlidePageFragment : Fragment() {

    internal lateinit var context: Context
    private val PIC_CROP = 1
    internal var pic: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_screen_slide_page, container, false) as ViewGroup

        context = rootView.context

        pic = arguments!!.getInt("pic", 0)
        val imageView = rootView.findViewById<View>(R.id.image_save_file) as ImageView

        Picasso.with(rootView.context)
                .load(Main.filesArray[pic])
                .transform(Main.transformation)
                .into(imageView)

        (rootView.findViewById<View>(R.id.t100) as TextView).text = Main.filesArray[pic].name

        imageView.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(rootView.context, R.anim.myalpha)
            v.startAnimation(anim)
            val builder = AlertDialog.Builder(ContextThemeWrapper(rootView.context, android.R.style.Theme_Holo))
            val content = LayoutInflater.from(rootView.context).inflate(R.layout.menu_img_home, null)
            content.minimumWidth = Main.wd
            content.minimumHeight = Main.wd / 2
            builder.setView(content)
            val alertDialog = builder.create()
            alertDialog.show()


            //размер картинки
            //--------------------------------
            var size_file = ""
            var size_file_bit = Main.filesArray[pic].length().toInt().toDouble()
            size_file_bit = size_file_bit / 1024 // kb

            if (size_file_bit > 1024) {
                size_file_bit = size_file_bit / 1024 // mb
                //костыль для уменьшения символов после запятой
                size_file_bit = round(size_file_bit, 1)
                size_file = size_file_bit.toString() + " mb"
            } else {
                //костыль для уменьшения символов после запятой
                size_file_bit = round(size_file_bit, 1)
                size_file = size_file_bit.toString() + " kb"
            }
            //--------------------------------------------


            (content.findViewById<View>(R.id.textView_name_pic) as TextView).text = Main.filesArray[pic].name
            (content.findViewById<View>(R.id.textView_ves_pic) as TextView).text = size_file

            //устанавливем цвет и загружаем настройки
            (content.findViewById<View>(R.id.fon_menu) as LinearLayout).setBackgroundColor(Main.color_fon_main)

            val del: Button
            val send: Button
            val oboi: Button
            val open_sis: Button
            del = content.findViewById<View>(R.id.button_del) as Button
            del.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(rootView.context, R.anim.myalpha)
                v.startAnimation(anim)
                if (Main.filesArray[pic].delete()) {
                    Toast.makeText(rootView.context, R.string.deletes, Toast.LENGTH_SHORT).show()
                    Main.update_list_files()
                    Main.mPagerAdapter.notifyDataSetChanged()
                    Main.mPager.adapter = Main.mPagerAdapter
                    Main.mPager.currentItem = pic - 1
                    if (pic > 0) {
                        Main.mPager.currentItem = pic - 1
                    }

                    alertDialog.cancel()
                } else {
                    Toast.makeText(rootView.context, R.string.error, Toast.LENGTH_SHORT).show()
                }
            }

            oboi = content.findViewById<View>(R.id.button_ustanovit) as Button

            oboi.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(rootView.context, R.anim.myalpha)
                v.startAnimation(anim)
                //если включена обрезка обрежем и установим
                if (Main.auto_oboi_costrate) {
                    //запустим в мясорубку
                    runCropImage(Main.filesArray[pic].toString())
                } else {
                    //если включено установка обоев
                    if (Main.auto_oboi_crete) {
                        //установим
                        val bitmap = BitmapFactory.decodeFile(Main.filesArray[pic].toString())
                        setOboi(bitmap)
                    } else {
                        Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show()
                    }
                }

                alertDialog.cancel()
            }

            send = content.findViewById<View>(R.id.button_send) as Button

            send.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(rootView.context, R.anim.myalpha)
                v.startAnimation(anim)

                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "image/*"
                // sharingIntent.setPackage("com.android.bluetooth");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Main.filesArray[pic].absolutePath))
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.viberite_kuda_otpravlat)))
                alertDialog.cancel()
            }

            open_sis = content.findViewById<View>(R.id.button_pic_open_sistema) as Button
            open_sis.setOnClickListener { v ->
                val anim = AnimationUtils.loadAnimation(rootView.context, R.anim.myalpha)
                v.startAnimation(anim)
                val path = Uri.fromFile(Main.filesArray[pic])
                val pdfOpenintent = Intent(Intent.ACTION_VIEW)
                pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                pdfOpenintent.setDataAndType(path, "image/jpeg")
                try {
                    startActivity(pdfOpenintent)
                } catch (e: ActivityNotFoundException) {

                }

                alertDialog.cancel()
            }
        }

        return rootView
    }


    //уменьшает количество символов после заятой
    private fun round(number: Double, scale: Int): Double {
        var pow = 10
        for (i in 1 until scale)
            pow *= 10
        val tmp = number * pow
        return (if (tmp - tmp.toInt() >= 0.5) tmp + 1 else tmp).toInt().toDouble() / pow
    }


    private fun runCropImage(filePath: String) {

        // create explicit intent
        val intent = Intent(context, CropImage::class.java)

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
                    val bitmap = BitmapFactory.decodeFile(Main.filesArray[pic].toString())
                    setOboi(resize(bitmap, Main.hd, Main.hd))
                } else {
                    //иначе скажем
                    Toast.makeText(context, R.string.obrezano_i_save, Toast.LENGTH_SHORT).show()
                }


            }
        }
    }


    private fun setOboi(bmp: Bitmap?) {
        var bmp = bmp
        val wallpaperManager = WallpaperManager.getInstance(context)
        try {
            wallpaperManager.setBitmap(bmp)
            Toast.makeText(context, R.string.save_i_ustanovleno, Toast.LENGTH_SHORT).show()
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
