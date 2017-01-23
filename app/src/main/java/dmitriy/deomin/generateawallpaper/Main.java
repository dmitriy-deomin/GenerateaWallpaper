package dmitriy.deomin.generateawallpaper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Transformation;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends FragmentActivity {

    private AdView mAdView;
    private final String vk_grupa = "https://vk.com/generateawallpaper";

    //для обводки картинок рамкой
    public static Transformation transformation;


    //для текста
    public static Spannable text;

    //ДЛЯ картинок на главном экране
    //------------------------------------
    public static ViewPager mPager;
    public static PagerAdapter mPagerAdapter;
    public static int NUM_PAGES; //количество картинок в нашей папке (столькоже будет страниц для просмотра)
    public static File[] filesArray;
    public static int img_vibrno=0;
    //--------------------------------


    //размеры экрана
    //--------------------
    public static int wd;
    public static int hd;
    //--------------------

    public static boolean run; //какой запуск
    public static boolean auto_oboi_crete;  //установка обоев автоматом
    public static boolean auto_oboi_costrate; //обрезка обоев перед установкой
    public static int Schema_rand_kartinki; //по какой схеме будет риоваться картинка
    public static int Time_dolbeshki_ekrana; // время срабатывания цикла автомата
    public static int color_fon_main; // цвет фона общий , меняется кликом на логотипе

    //шрифт
    public static Typeface face;

    //сохранялка
    public static SharedPreferences mSettings; // сохранялка
    public final String APP_PREFERENCES = "mysettings"; // файл сохранялки

    TextView text_logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //сохранялка
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        face = Typeface.createFromAsset(getAssets(), "fonts/Tweed.ttf");


        //реклама
        //--------------------
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (isOnline(this)) {
            mAdView.setVisibility(View.VISIBLE);
        } else {
            mAdView.setVisibility(View.GONE);
        }
        //---------------------------



        //размеры экрана
        //----------------------
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //если юзер выбирал ранее установим его если нет поставим по умолчанию размер экрана
        if (save_read_int("wd") == 0) {
            wd = display.getWidth();
        } else {
            wd = save_read_int("wd");
        }
        if (save_read_int("hd") == 0) {
            hd = display.getHeight();
        } else {
            hd = save_read_int("hd");
        }
        //----------------------


        //получаем список файлов из нашей папки
         update_list_files();


        //для обводки картинок рамкой
        //------------------------------------------------------
        transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .cornerRadiusDp(10)
                .oval(false)
                .build();
        //------------------------------------------------------


        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setOffscreenPageLimit(0);
        mPager.setAdapter(mPagerAdapter);
        //промотаем к самой свежей
        mPager.setCurrentItem(NUM_PAGES);




        //настройки загружаем
        run = save_read_bool("run");
        auto_oboi_crete = save_read_bool("auto_oboi_crete");
        auto_oboi_costrate = save_read_bool("auto_oboi_costrate");


        //при первом запуске сохранялки выбраной схемы нет поэтому поставим 2
        if(Main.save_read("nomer_stroki")!=null){
            if(!Main.save_read("nomer_stroki").equals("")) {
                Schema_rand_kartinki = Integer.valueOf(save_read("nomer_stroki"));
            }else {
                save_value("nomer_stroki","1");
                Schema_rand_kartinki = 1;
            }
        }else {
            save_value("nomer_stroki","1");
            Schema_rand_kartinki = 1;
        }

        if (save_read_int("Time_dolbeshki_ekrana") == 0) {
            Time_dolbeshki_ekrana = 1000;
        } else {
            Time_dolbeshki_ekrana = save_read_int("Time_dolbeshki_ekrana");
        }

        //посмотрим если есть сохранёный свет фона поставим его иначе рандомно
        if(save_read("color_fon_main").length()>0){
            color_fon_main = Integer.valueOf(save_read("color_fon_main"));
        }else{
            color_fon_main = random_color();
        }


        //правим вид
        //-----------
        //инфо
        text_logo = (TextView) findViewById(R.id.name_i_versia);
        text_logo.setTypeface(face);
        text_logo.setText("Генератор обоев " + getVersion());
        //кнопки
        ((Button) findViewById(R.id.menu)).setTypeface(face);

        //----------------
        ((LinearLayout) findViewById(R.id.main)).setBackgroundColor(color_fon_main);

        //матня для измены цвета фона и его сохраннения
        ///****************************************

        text_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);
                //поставим рандомный цвет фону
                color_fon_main = random_color();
                ((LinearLayout) findViewById(R.id.main)).setBackgroundColor(color_fon_main);
            }
        });
        text_logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //если есть сохранёный проверим его с текущим если совпадает то уберём его вообще
                if(save_read("color_fon_main").equals(String.valueOf(color_fon_main))){
                    save_value("color_fon_main","");
                    Toast.makeText(getApplicationContext(),"Фон при запуске будет рандомный",Toast.LENGTH_SHORT).show();
                }else{
                    save_value("color_fon_main",String.valueOf(color_fon_main));
                    Toast.makeText(getApplicationContext(),"Фон сохранён",Toast.LENGTH_SHORT).show();
                    ((LinearLayout) findViewById(R.id.main)).setBackgroundColor(color_fon_main);
                    //
                }
                return true;
            }
        });

        ///*******************************************



        //устанавливаем надпись на кнопе создать обои
        shema();

    }


    public void shema(){
        final String [] mas_shem=getResources().getStringArray(R.array.shemy);
        ((Button) findViewById(R.id.open_random_oboi)).setText("Создать обои"+"\n"+mas_shem[Schema_rand_kartinki]);
    }

    public static void update_list_files(){
        //получаем список файлов из нашей папки
        //--------------------------------------------------
        //создадим папки если нет
        File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Generateawallpaper/");
        if (!sddir.exists()) {
            sddir.mkdirs();
        }else{
            //иначе посчитаем че там есть
            filesArray = sddir.listFiles();
            System.out.println("файлов: " + filesArray.length);

            NUM_PAGES = filesArray.length;

            for (File f: filesArray) {
                if (f.isDirectory()) System.out.println("Folder: " + f);
                else if (f.isFile()) System.out.println("File: " + f);
            }
        }
        //-------------------------------------------------------------
    }


    @Override
    protected void onResume() {
        super.onResume();
        //обновим список файлов
        update_list_files();
        //обновим адаптер
        mPagerAdapter.notifyDataSetChanged();
        //перезальём адаптер в пежер
        mPager.setAdapter(Main.mPagerAdapter);
        //промотаем к самой свежей
        mPager.setCurrentItem(NUM_PAGES);
        //устанавливаем надпись на кнопе создать обои
        shema();
    }



    public void Clik_moi_kartinki_lokalno(View v) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
    }

    public void Clik_vse_kartinki_v_seti(View v) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlidePageFragment page =  new ScreenSlidePageFragment();
            Bundle args = new Bundle();
            args.putInt("pic", position);
            page.setArguments(args);
            return page;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void Gen(View v) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
        Intent i = new Intent(this, Generat.class);
        startActivity(i);
    }

    public void Menu(View v) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(Main.this).inflate(R.layout.menu_progi, null);
        content.setMinimumWidth(wd);
        content.setMinimumHeight(wd / 2);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        //устанавливем цвет и загружаем настройки
        ((LinearLayout) content.findViewById(R.id.menu_diaolog)).setBackgroundColor(color_fon_main);


        ((TextView) content.findViewById(R.id.textView_info_1)).setTypeface(face);
        ((TextView) content.findViewById(R.id.textView_info_2)).setTypeface(face);
        ((TextView) content.findViewById(R.id.textView_info_3)).setTypeface(face);
        ((TextView) content.findViewById(R.id.textView_info_4)).setTypeface(face);
        ((TextView) content.findViewById(R.id.textView_info_5)).setTypeface(face);

        CheckBox checkBox_run = (CheckBox) content.findViewById(R.id.checkBox_run);
        checkBox_run.setTypeface(face);
        checkBox_run.setChecked(save_read_bool("run"));
        checkBox_run.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                run = isChecked;
                save_value_bool("run", isChecked);
            }
        });

        CheckBox checkBox_auto_oboi = (CheckBox) content.findViewById(R.id.checkBox_ustanovka_oboev);
        checkBox_auto_oboi.setTypeface(face);
        checkBox_auto_oboi.setChecked(save_read_bool("auto_oboi_crete"));
        checkBox_auto_oboi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                auto_oboi_crete = isChecked;
                save_value_bool("auto_oboi_crete", isChecked);
            }
        });

        CheckBox checkBox_auto_obrezka = (CheckBox) content.findViewById(R.id.checkBox_obrezka_oboev);
        checkBox_auto_obrezka.setTypeface(face);
        checkBox_auto_obrezka.setChecked(save_read_bool("auto_oboi_costrate"));
        checkBox_auto_obrezka.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                auto_oboi_costrate = isChecked;
                save_value_bool("auto_oboi_costrate", isChecked);
            }
        });


        final EditText editText = (EditText) content.findViewById(R.id.editText_teme_avtomat);
        editText.setText(String.valueOf(Time_dolbeshki_ekrana));
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // сохраняем текст, введенный до нажатия Enter в переменную
                    if(editText.getText().toString().length()==0){
                        //скажем ченить
                        Toast.makeText(getApplicationContext(),"Нужно какоенибуть значение(Установим по умолчанию)",Toast.LENGTH_LONG).show();
                        Time_dolbeshki_ekrana = 1000;
                        save_value_int("Time_dolbeshki_ekrana",Time_dolbeshki_ekrana);
                        editText.setText(String.valueOf(Time_dolbeshki_ekrana));
                    }else {
                        Time_dolbeshki_ekrana = Integer.valueOf(editText.getText().toString());
                        save_value_int("Time_dolbeshki_ekrana",Time_dolbeshki_ekrana);
                    }

                    return true;
                }
                return false;
            }
        });


        Button button_save = (Button)content.findViewById(R.id.button_save_menu);
        button_save.setTypeface(face);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);
                Time_dolbeshki_ekrana = Integer.valueOf(editText.getText().toString());
                save_value_int("Time_dolbeshki_ekrana",Time_dolbeshki_ekrana);
                alertDialog.cancel();
            }
        });


        Button razmer = (Button) content.findViewById(R.id.button_razreshenie_pic);
        razmer.setTypeface(face);
        razmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mas = getResources().getStringArray(R.array.spisok_razresheniy);

                final AlertDialog.Builder bu = new AlertDialog.Builder(new ContextThemeWrapper(v.getContext(), android.R.style.Theme_Holo));
                final View con = LayoutInflater.from(v.getContext()).inflate(R.layout.spisok_razmerov, null);
                bu.setView(con);
                final AlertDialog ag = bu.create();
                ag.show();

                ((LinearLayout) con.findViewById(R.id.fon_list_razmerov)).setBackgroundColor(random_color());


                final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(v.getContext(), R.layout.delegat_list, mas);
                ((ListView) con.findViewById(R.id.listView_razmery)).setAdapter(stringArrayAdapter);
                ((ListView) con.findViewById(R.id.listView_razmery)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        String n = ((TextView) v).getText().toString();

                        hd = Integer.valueOf(n.split("x")[0]);
                        wd = Integer.valueOf(n.split("x")[1]);

                        save_value_int("hd", hd);
                        save_value_int("wd", wd);

                        ag.cancel();
                    }
                });
            }
        });

    }

    public void opengruppa(View v) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(vk_grupa));
        startActivity(browseIntent);
    }

    private String getVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "?";
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    //чтение и сохранение настроек
    //-------------------------------------
    public static void save_value(String Key, String Value) { //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public static String save_read(String key_save) {  // чтение строки
        if (mSettings.contains(key_save)) {
            return (mSettings.getString(key_save, ""));
        }
        return "";
    }

    public static void save_value_bool(String Key, Boolean Value) { //сохранение
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(Key, Value);
        editor.apply();
    }

    public static Boolean save_read_bool(String key_save) {  // чтение
        if (mSettings.contains(key_save)) {
            return (mSettings.getBoolean(key_save, true));
        }
        return true;
    }

    public static void save_value_int(String Key, int Value) { //сохранение
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(Key, Value);
        editor.apply();
    }

    public static int save_read_int(String key_save) {  // чтение
        if (mSettings.contains(key_save)) {
            return (mSettings.getInt(key_save, 0));
        } else {
            return 0;
        }
    }

    //------------------------------------
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

    public void Vibrat_shemu(View v) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(Main.this).inflate(R.layout.shemy, null);
        content.setMinimumWidth(wd);
        content.setMinimumHeight(wd / 2);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //устанавливем цвет и загружаем настройки
        ((LinearLayout) content.findViewById(R.id.fon_shemy)).setBackgroundColor(color_fon_main);

        //заполняем наш список с схемами
        ListView  listView =(ListView)content.findViewById(R.id.listview_shemy);

        final String [] mas_shem=getResources().getStringArray(R.array.shemy);
        final ArrayList<Map<String,Object>> data = new ArrayList<Map<String,Object>>(mas_shem.length);

        Map<String,Object> m ;

        for(int i = 0;i<mas_shem.length;i++){
            m= new HashMap<String,Object>();
            m.put("shema",(mas_shem[i]));
            data.add(m);
        }

        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { "shema" };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.textView};

        final Adapter_shem adapter_shem = new Adapter_shem(getApplicationContext(),data,R.layout.delegat_list,from,to);
        listView.setAdapter(adapter_shem);
        listView.setTextFilterEnabled(true);

        ((EditText)content.findViewById(R.id.editText_shemy)).addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // текст только что изменили
                adapter_shem.getFilter().filter(s);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // текст будет изменен
            }
            @Override
            public void afterTextChanged(Editable s) {
                // текст уже изменили
            }
        });

        //пролистываем до нужного элемента
        if(Main.save_read("nomer_stroki")!=null){
            if(!Main.save_read("nomer_stroki").equals("")) {
                listView.setSelection(Integer.valueOf(Main.save_read("nomer_stroki")));
            }
        }


        //Обрабатываем щелчки на элементах ListView:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //сохраняем позицию
                Main.save_value("nomer_stroki", String.valueOf(position));
                Schema_rand_kartinki = position;
                shema();
                alertDialog.cancel();
            }
        });

    }


    public void O_proge(View v) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(Main.this).inflate(R.layout.info_o_proge,null);
        content.setMinimumWidth(wd);
        content.setMinimumHeight(wd/2);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //устанавливем цвет фону и загружаем настройки
        ((LinearLayout)content.findViewById(R.id.info_loaut)).setBackgroundColor(color_fon_main);
    }


    //id
    public String getUniqueID() {
        String myAndroidDeviceId = "123456789";
        myAndroidDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //возьмём часть
        myAndroidDeviceId = myAndroidDeviceId.substring(0, myAndroidDeviceId.length() / 3);
        return myAndroidDeviceId;
    }

}
