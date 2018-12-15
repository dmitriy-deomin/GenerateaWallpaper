package dmitriy.deomin.generateawallpaper

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.kotlinpermissions.KotlinPermissions
import com.squareup.picasso.Transformation
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import org.jetbrains.anko.browse

import java.io.File
import java.util.ArrayList
import java.util.HashMap

class Main : FragmentActivity() {
    private var mAdView: AdView? = null
    private val vk_grupa = "https://vk.com/generateawallpaper"
    private val donat_link = "https://money.yandex.ru/to/41001566605499"
    val APP_PREFERENCES = "mysettings" // файл сохранялки

    private lateinit var text_logo: TextView


    companion object {

        var ANIMACIA_RUN = false


        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context


        //для обводки картинок рамкой
        lateinit var transformation: Transformation

        //сохранялка
        lateinit var mSettings: SharedPreferences // сохранялка
        //шрифт
        lateinit var face: Typeface
        //для текста
        var text: Spannable? = null

        //ДЛЯ картинок на главном экране
        //------------------------------------
        @SuppressLint("StaticFieldLeak")
        lateinit var mPager: ViewPager
        lateinit var mPagerAdapter: PagerAdapter
        var NUM_PAGES: Int = 0 //количество картинок в нашей папке (столькоже будет страниц для просмотра)
        lateinit var filesArray: Array<File>
        var img_vibrno = 0
        //--------------------------------


        //размеры экрана
        //--------------------
        var wd: Int = 0
        var hd: Int = 0
        //--------------------

        var run: Boolean = false //какой запуск
        var auto_oboi_crete: Boolean = false  //установка обоев автоматом
        var auto_oboi_costrate: Boolean = false //обрезка обоев перед установкой
        var Schema_rand_kartinki: Int = 0 //по какой схеме будет риоваться картинка
        var Time_dolbeshki_ekrana: Int = 0 // время срабатывания цикла автомата
        var color_fon_main: Int = 0 // цвет фона общий , меняется кликом на логотипе


        fun update_list_files() {
            //получаем список файлов из нашей папки
            //--------------------------------------------------
            //создадим папки если нет
            val sddir = File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Generateawallpaper/")
            if (!sddir.exists()) {
                sddir.mkdirs()
            } else {
                //если там не пусто
                if(sddir.listFiles()!=null){
                    //посчитаем че там есть
                    filesArray = sddir.listFiles()

                    NUM_PAGES = filesArray.size
                }else{
                    NUM_PAGES = 0
                }

            }
            //-------------------------------------------------------------
        }

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

        //чтение и сохранение настроек
        //-------------------------------------
        fun save_value(Key: String, Value: String) { //сохранение строки
            val editor = mSettings.edit()
            editor.putString(Key, Value)
            editor.apply()
        }

        fun save_read(key_save: String): String? {  // чтение строки
            return if (mSettings.contains(key_save)) {
                mSettings.getString(key_save, "")
            } else ""
        }

        fun save_value_bool(Key: String, Value: Boolean?) { //сохранение
            val editor = mSettings.edit()
            editor.putBoolean(Key, Value!!)
            editor.apply()
        }

        fun save_read_bool(key_save: String): Boolean {  // чтение
            return if (mSettings.contains(key_save)) {
                mSettings.getBoolean(key_save, true)
                //анимацию выключим при первом запуске
            } else key_save != "ANIMACIA_RUN"
        }

        fun save_value_int(Key: String, Value: Int) { //сохранение
            val editor = mSettings.edit()
            editor.putInt(Key, Value)
            editor.apply()
        }

        fun save_read_int(key_save: String): Int {  // чтение
            return if (mSettings.contains(key_save)) {
                mSettings.getInt(key_save, 0)
            } else {
                0
            }
        }
    }
    //id
    //возьмём часть
    val uniqueID: String
        get() {
            var myAndroidDeviceId = "123456789"
            myAndroidDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            myAndroidDeviceId = myAndroidDeviceId.substring(0, myAndroidDeviceId.length / 3)
            return myAndroidDeviceId
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        //эта штука нужна чтобы выключить нововведение чертей, и сократить 100500 строк ненужного кода
        //It will ignore URI exposure  (оставляет "file://" как я понял )
        //---------------------------------------------------------------------------------------------
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        //-----------------------------------------------------------------------------------------

        //посмотрим есть ли разрешения
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
        val permissionFileR = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionFileW = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionSetWl = ContextCompat.checkSelfPermission(this, Manifest.permission.SET_WALLPAPER)

        //получим ебучие разрешения , если не дали их еще
        if (permissionFileR < 0) {
            //----------------------
            this.onPause()
            KotlinPermissions.with(this).permissions(Manifest.permission.READ_EXTERNAL_STORAGE).ask()
            //------------------------
        }
        if (permissionFileW < 0) {
            //----------------------
            this.onPause()
            KotlinPermissions.with(this).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).ask()
            //------------------------
        }
        if (permissionCheck < 0) {
            //----------------------
            this.onPause()
            KotlinPermissions.with(this).permissions(Manifest.permission.INTERNET).ask()
            //------------------------
        }
        if (permissionSetWl < 0) {
            //----------------------
            this.onPause()
            KotlinPermissions.with(this).permissions(Manifest.permission.SET_WALLPAPER).ask()
            //------------------------
        }


        //во весь экран
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        context = this
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        face = Typeface.createFromAsset(assets, "fonts/Tweed.ttf")


        //реклама
        //--------------------
        mAdView = findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)

        if (isOnline(this)) {
            mAdView!!.visibility = View.VISIBLE
        } else {
            mAdView!!.visibility = View.GONE
        }
        //---------------------------


        //размеры экрана
        //----------------------
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        //если юзер выбирал ранее установим его если нет поставим по умолчанию размер экрана
        wd = if (save_read_int("wd") == 0) {
            display.width
        } else {
            save_read_int("wd")
        }
        hd = if (save_read_int("hd") == 0) {
            display.height
        } else {
            save_read_int("hd")
        }
        //----------------------


        //получаем список файлов из нашей папки
        update_list_files()


        //для обводки картинок рамкой
        //------------------------------------------------------
        transformation = RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1f)
                .cornerRadiusDp(10f)
                .oval(false)
                .build()
        //------------------------------------------------------


        mPager = findViewById<View>(R.id.pager) as ViewPager
        mPagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.offscreenPageLimit = 0
        mPager.adapter = mPagerAdapter
        //промотаем к самой свежей
        mPager.currentItem = NUM_PAGES


        //настройки загружаем
        run = save_read_bool("run")
        auto_oboi_crete = save_read_bool("auto_oboi_crete")
        auto_oboi_costrate = save_read_bool("auto_oboi_costrate")
        ANIMACIA_RUN =save_read_bool("ANIMACIA_RUN")


        //при первом запуске сохранялки выбраной схемы нет поэтому поставим 2
        Schema_rand_kartinki = if (Main.save_read("nomer_stroki") != null) {
            if (Main.save_read("nomer_stroki") != "") {
                Integer.valueOf(save_read("nomer_stroki")!!)
            } else {
                save_value("nomer_stroki", "1")
                1
            }
        } else {
            save_value("nomer_stroki", "1")
            1
        }

        Time_dolbeshki_ekrana = if (save_read_int("Time_dolbeshki_ekrana") == 0) {
            1000
        } else {
            save_read_int("Time_dolbeshki_ekrana")
        }

        //посмотрим если есть сохранёный свет фона поставим его иначе рандомно
        color_fon_main = if (save_read("color_fon_main")!!.length > 0) {
            Integer.valueOf(save_read("color_fon_main")!!)
        } else {
            random_color()
        }


        //правим вид
        //-----------
        //инфо
        text_logo = findViewById<View>(R.id.name_i_versia) as TextView
        text_logo.typeface = face
        text_logo.text = getString(R.string.app_name) + " " +  getString(R.string.versionName)
        //кнопки
        (findViewById<View>(R.id.menu) as Button).typeface = face

        //----------------
        (findViewById<View>(R.id.main) as LinearLayout).setBackgroundColor(color_fon_main)

        //матня для измены цвета фона и его сохраннения
        ///****************************************

        text_logo.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
            v.startAnimation(anim)
            //поставим рандомный цвет фону
            color_fon_main = random_color()
            (findViewById<View>(R.id.main) as LinearLayout).setBackgroundColor(color_fon_main)
        }
        text_logo.setOnLongClickListener {
            //если есть сохранёный проверим его с текущим если совпадает то уберём его вообще
            if (save_read("color_fon_main") == color_fon_main.toString()) {
                save_value("color_fon_main", "")
                Toast.makeText(applicationContext, R.string.fon_pri_zapuske_budet_random, Toast.LENGTH_SHORT).show()
            } else {
                save_value("color_fon_main", color_fon_main.toString())
                Toast.makeText(applicationContext, R.string.fon_save, Toast.LENGTH_SHORT).show()
                (findViewById<View>(R.id.main) as LinearLayout).setBackgroundColor(color_fon_main)
                //
            }
            true
        }

        ///*******************************************


        //устанавливаем надпись на кнопе создать обои
        shema()

    }

    internal fun format_text(value: String): Spannable {
        //******************************************
        //форматирование текста
        val text = SpannableString(value)

        //UnderlineSpan() - подчеркнутый текст
        //StyleSpan(Typeface.BOLD) - полужирный тектс
        //StyleSpan(Typeface.ITALIC) - курсив
        //ForegroundColorSpan(Color.GREEN) - цвет
        //text.setSpan(new UnderlineSpan(), 0, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(ForegroundColorSpan(Color.BLUE), text.length - 1, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        //****************************************************
        return text
    }


    fun shema() {
        val mas_shem = resources.getStringArray(R.array.shemy)
        (findViewById<View>(R.id.open_random_oboi) as Button).text = getString(R.string.sozadot_oboi) + "\n" + mas_shem[Schema_rand_kartinki]
    }


    override fun onResume() {
        super.onResume()
        //обновим список файлов
        update_list_files()
        //обновим адаптер
        mPagerAdapter.notifyDataSetChanged()
        //перезальём адаптер в пежер
        mPager.adapter = Main.mPagerAdapter
        //промотаем к самой свежей
        mPager.currentItem = NUM_PAGES
        //устанавливаем надпись на кнопе создать обои
        shema()
    }


    fun Clik_moi_kartinki_lokalno(v: View) {
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
        v.startAnimation(anim)
    }

    fun Clik_vse_kartinki_v_seti(v: View) {
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
        v.startAnimation(anim)
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val page = ScreenSlidePageFragment()
            val args = Bundle()
            args.putInt("pic", position)
            page.arguments = args
            return page
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }
    }

    fun Gen(v: View) {
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
        v.startAnimation(anim)
        val i = Intent(this, Generat::class.java)
        startActivity(i)
    }

    fun Menu(v: View) {
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
        v.startAnimation(anim)
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, android.R.style.Theme_Holo))
        val content = LayoutInflater.from(this@Main).inflate(R.layout.menu_progi, null)
        content.minimumWidth = wd
        content.minimumHeight = wd / 2
        builder.setView(content)
        val alertDialog = builder.create()
        alertDialog.show()


        //устанавливем цвет и загружаем настройки
        (content.findViewById<View>(R.id.menu_diaolog) as LinearLayout).setBackgroundColor(color_fon_main)


        (content.findViewById<View>(R.id.textView_info_1) as TextView).typeface = face
        (content.findViewById<View>(R.id.textView_info_2) as TextView).typeface = face
        (content.findViewById<View>(R.id.textView_info_3) as TextView).typeface = face
        (content.findViewById<View>(R.id.textView_info_4) as TextView).typeface = face
        (content.findViewById<View>(R.id.textView_info_5) as TextView).typeface = face
        (content.findViewById<View>(R.id.textView_info_6) as TextView).typeface = face

        val checkBox_run = content.findViewById<View>(R.id.checkBox_run) as CheckBox
        checkBox_run.typeface = face
        checkBox_run.isChecked = save_read_bool("run")
        checkBox_run.setOnCheckedChangeListener { buttonView, isChecked ->
            run = isChecked
            save_value_bool("run", isChecked)
        }


        val checkBox_anim = content.findViewById<View>(R.id.checkBox_animcia) as CheckBox
        checkBox_anim.typeface = face
        checkBox_anim.isChecked = save_read_bool("ANIMACIA_RUN")
        checkBox_anim.setOnCheckedChangeListener { buttonView, isChecked ->
            ANIMACIA_RUN = isChecked
            save_value_bool("ANIMACIA_RUN", isChecked)
        }

        val checkBox_auto_oboi = content.findViewById<View>(R.id.checkBox_ustanovka_oboev) as CheckBox
        checkBox_auto_oboi.typeface = face
        checkBox_auto_oboi.isChecked = save_read_bool("auto_oboi_crete")
        checkBox_auto_oboi.setOnCheckedChangeListener { buttonView, isChecked ->
            auto_oboi_crete = isChecked
            save_value_bool("auto_oboi_crete", isChecked)
        }

        val checkBox_auto_obrezka = content.findViewById<View>(R.id.checkBox_obrezka_oboev) as CheckBox
        checkBox_auto_obrezka.typeface = face
        checkBox_auto_obrezka.isChecked = save_read_bool("auto_oboi_costrate")
        checkBox_auto_obrezka.setOnCheckedChangeListener { buttonView, isChecked ->
            auto_oboi_costrate = isChecked
            save_value_bool("auto_oboi_costrate", isChecked)
        }


        val editText = content.findViewById<View>(R.id.editText_teme_avtomat) as EditText
        editText.setText(Time_dolbeshki_ekrana.toString())
        editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // сохраняем текст, введенный до нажатия Enter в переменную
                if (editText.text.toString().length == 0) {
                    //скажем ченить
                    Toast.makeText(applicationContext, R.string.nugno_kakoenibut_znachenie, Toast.LENGTH_LONG).show()
                    Time_dolbeshki_ekrana = 1000
                    save_value_int("Time_dolbeshki_ekrana", Time_dolbeshki_ekrana)
                    editText.setText(Time_dolbeshki_ekrana.toString())
                } else {
                    Time_dolbeshki_ekrana = Integer.valueOf(editText.text.toString())
                    save_value_int("Time_dolbeshki_ekrana", Time_dolbeshki_ekrana)
                }

                return@OnKeyListener true
            }
            false
        })


        val button_save = content.findViewById<View>(R.id.button_save_menu) as Button
        button_save.typeface = face
        button_save.setOnClickListener { v ->
            val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
            v.startAnimation(anim)
            Time_dolbeshki_ekrana = Integer.valueOf(editText.text.toString())
            save_value_int("Time_dolbeshki_ekrana", Time_dolbeshki_ekrana)
            alertDialog.cancel()
        }


        val razmer = content.findViewById<View>(R.id.button_razreshenie_pic) as Button
        razmer.typeface = face
        razmer.setOnClickListener { v ->
            val mas = resources.getStringArray(R.array.spisok_razresheniy)

            val bu = AlertDialog.Builder(ContextThemeWrapper(v.context, android.R.style.Theme_Holo))
            val con = LayoutInflater.from(v.context).inflate(R.layout.spisok_razmerov, null)
            bu.setView(con)
            val ag = bu.create()
            ag.show()

            (con.findViewById<View>(R.id.fon_list_razmerov) as LinearLayout).setBackgroundColor(random_color())


            val stringArrayAdapter = ArrayAdapter(v.context, R.layout.delegat_list, mas)
            (con.findViewById<View>(R.id.listView_razmery) as ListView).adapter = stringArrayAdapter as ListAdapter?
            (con.findViewById<View>(R.id.listView_razmery) as ListView).onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                val n = (v as TextView).text.toString()

                hd = Integer.valueOf(n.split("x".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                wd = Integer.valueOf(n.split("x".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])

                save_value_int("hd", hd)
                save_value_int("wd", wd)

                ag.cancel()
            }
        }

    }

    fun opengruppa(v: View) {
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
        v.startAnimation(anim)
        browse(vk_grupa)
    }

    fun donat(v: View) {
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
        v.startAnimation(anim)
        browse(donat_link)
    }

    //------------------------------------
    fun random_color(): Int {
        val r = random_nomer(0, 255)
        val g = random_nomer(0, 255)
        val b = random_nomer(0, 255)
        return Color.rgb(r, g, b)
    }

    fun random_nomer(min: Int, max: Int): Int {
        var max = max
        max -= min
        return (Math.random() * ++max).toInt() + min
    }

    fun Vibrat_shemu(v: View) {
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
        v.startAnimation(anim)
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, android.R.style.Theme_Holo))
        val content = LayoutInflater.from(this@Main).inflate(R.layout.shemy, null)
        content.minimumWidth = wd
        content.minimumHeight = wd / 2
        builder.setView(content)
        val alertDialog = builder.create()
        alertDialog.show()

        //устанавливем цвет и загружаем настройки
        (content.findViewById<View>(R.id.fon_shemy) as LinearLayout).setBackgroundColor(color_fon_main)

        //заполняем наш список с схемами
        val listView = content.findViewById<View>(R.id.listview_shemy) as ListView

        val mas_shem = resources.getStringArray(R.array.shemy)
        val data = ArrayList<Map<String, Any>>(mas_shem.size)

        var m: MutableMap<String, Any>

        for (i in mas_shem.indices) {
            m = HashMap()
            m["shema"] = mas_shem[i]
            data.add(m)
        }

        // массив имен атрибутов, из которых будут читаться данные
        val from = arrayOf("shema")
        // массив ID View-компонентов, в которые будут вставлять данные
        val to = intArrayOf(R.id.textView)

        val adapter_shem = Adapter_shem(applicationContext, data, R.layout.delegat_list, from, to)
        listView.adapter = adapter_shem
        listView.isTextFilterEnabled = true

        (content.findViewById<View>(R.id.editText_shemy) as EditText).addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // текст только что изменили
                adapter_shem.filter.filter(s)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // текст будет изменен
            }

            override fun afterTextChanged(s: Editable) {
                // текст уже изменили
            }
        })

        //пролистываем до нужного элемента
        if (Main.save_read("nomer_stroki") != null) {
            if (Main.save_read("nomer_stroki") != "") {
                listView.setSelection(Integer.valueOf(Main.save_read("nomer_stroki")!!))
            }
        }


        //Обрабатываем щелчки на элементах ListView:
        listView.onItemClickListener = AdapterView.OnItemClickListener { a, v, position, id ->
            //сохраняем позицию
            Main.save_value("nomer_stroki", position.toString())
            Schema_rand_kartinki = position
            shema()
            alertDialog.cancel()
        }

    }


    fun O_proge(v: View) {
        val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.myalpha)
        v.startAnimation(anim)
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, android.R.style.Theme_Holo))
        val content = LayoutInflater.from(this@Main).inflate(R.layout.info_o_proge, null)
        content.minimumWidth = wd
        content.minimumHeight = wd / 2
        builder.setView(content)
        val alertDialog = builder.create()
        alertDialog.show()

        //устанавливем цвет фону и загружаем настройки
        (content.findViewById<View>(R.id.info_loaut) as LinearLayout).setBackgroundColor(color_fon_main)
    }
}
