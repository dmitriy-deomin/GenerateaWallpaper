package dmitriy.deomin.generateawallpaper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main extends Activity {

    private AdView mAdView;
    private final String vk_grupa = "https://vk.com/generateawallpaper";

    //размеры экрана
    //--------------------
    public static int wd;
    public static int hd;
    //--------------------

    public static boolean run; //какой запуск
    public static boolean auto_oboi_crete;  //установка обоев автоматом
    public static boolean auto_oboi_costrate; //обрезка обоев перед установкой
    public static int Schema_rand_kartinki; //по какой схеме будет риоваться картинка

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

        if(isOnline(this)){
            mAdView.setVisibility(View.VISIBLE);
        }else {
            mAdView.setVisibility(View.GONE);
        }
        //---------------------------


        //размеры экрана
        //----------------------
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //если юзер выбирал ранее установим его если нет поставим по умолчанию размер экрана
        if(save_read_int("wd")==0){
            wd=display.getWidth();
        }else{
            wd =save_read_int("wd");
        }
        if(save_read_int("hd")==0){
            hd=display.getHeight();
        }else {
            hd = save_read_int("hd");
        }
        //----------------------

        //правим вид
        //-----------
        //инфо
        text_logo = (TextView)findViewById(R.id.name_i_versia);
        text_logo.setTypeface(face);
        text_logo.setText("Генератор обоев "+getVersion());
        //кнопки
        ((Button)findViewById(R.id.menu)).setTypeface(face);
        ((Button)findViewById(R.id.open_random_oboi)).setTypeface(face);
        //фон рандомный будет
        ((LinearLayout)findViewById(R.id.main)).setBackgroundColor(random_color());
        //----------------

        //настройки загружаем
        run = save_read_bool("run");
        auto_oboi_crete = save_read_bool("auto_oboi_crete");
        auto_oboi_costrate = save_read_bool("auto_oboi_costrate");
        Schema_rand_kartinki = save_read_int("Schema_rand_kartinki");

    }

    public void Gen(View v) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
        Intent i = new Intent(this,Generat.class);
        startActivity(i);
    }

    public void Menu(View v) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(Main.this).inflate(R.layout.menu_progi,null);
        content.setMinimumWidth(wd);
        content.setMinimumHeight(wd/2);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //устанавливем цвет и загружаем настройки
        ((LinearLayout)content.findViewById(R.id.menu_diaolog)).setBackgroundColor(random_color());

        ((TextView)content.findViewById(R.id.textView_info_1)).setTypeface(face);
        ((TextView)content.findViewById(R.id.textView_info_2)).setTypeface(face);
        ((TextView)content.findViewById(R.id.textView_info_3)).setTypeface(face);
        ((TextView)content.findViewById(R.id.textView_info_4)).setTypeface(face);

        CheckBox checkBox_run = (CheckBox)content.findViewById(R.id.checkBox_run);
        checkBox_run.setTypeface(face);
        checkBox_run.setChecked(save_read_bool("run"));
        checkBox_run.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              run = isChecked;
              save_value_bool("run",isChecked);
            }
        });

        CheckBox checkBox_auto_oboi = (CheckBox)content.findViewById(R.id.checkBox_ustanovka_oboev);
        checkBox_auto_oboi.setTypeface(face);
        checkBox_auto_oboi.setChecked(save_read_bool("auto_oboi_crete"));
        checkBox_auto_oboi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                auto_oboi_crete = isChecked;
                save_value_bool("auto_oboi_crete",isChecked);
            }
        });

        CheckBox checkBox_auto_obrezka = (CheckBox)content.findViewById(R.id.checkBox_obrezka_oboev);
        checkBox_auto_obrezka.setTypeface(face);
        checkBox_auto_obrezka.setChecked(save_read_bool("auto_oboi_costrate"));
        checkBox_auto_obrezka.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                auto_oboi_costrate = isChecked;
                save_value_bool("auto_oboi_costrate",isChecked);
            }
        });

        Button razmer = (Button)content.findViewById(R.id.button_razreshenie_pic);
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

                        save_value_int("hd",hd);
                        save_value_int("wd",wd);

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
        }
        return 0;
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

        //устанавливем цвет и загружаем настройки
        ((LinearLayout)content.findViewById(R.id.info_loaut)).setBackgroundColor(random_color());

    }

    public void Vibrat_shemu(View v){
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
        v.startAnimation(anim);
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(Main.this).inflate(R.layout.shemy,null);
        content.setMinimumWidth(wd);
        content.setMinimumHeight(wd/2);
        builder.setView(content);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //устанавливем цвет и загружаем настройки
        ((LinearLayout)content.findViewById(R.id.fon_shemy)).setBackgroundColor(random_color());

        Button sh1,sh2,sh777,sh_user;

        sh1 = ((Button)content.findViewById(R.id.button_shema1));
        sh2 = ((Button)content.findViewById(R.id.button_shema2));
        sh777 = ((Button)content.findViewById(R.id.button_shema777));
        sh_user = ((Button)content.findViewById(R.id.button_shema_user));


        sh1.setTypeface(face);
        sh2.setTypeface(face);
        sh777.setTypeface(face);
        sh_user.setTypeface(face);


        switch (Schema_rand_kartinki){
            case 777:
                sh777.setTextColor(Color.GREEN);
                break;
            case 1:
                sh1.setTextColor(Color.GREEN);
                break;
            case 2:
                sh2.setTextColor(Color.GREEN);
                break;
            case 1000:
                sh_user.setTextColor(Color.GREEN);
                break;
        }

        sh777.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);
                Schema_rand_kartinki = 777;
                save_value_int("Schema_rand_kartinki",777);
                alertDialog.cancel();
            }
        });
        sh1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);
                Schema_rand_kartinki = 1;
                save_value_int("Schema_rand_kartinki",1);
                alertDialog.cancel();
            }
        });
        sh2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);
                Schema_rand_kartinki = 2;
                save_value_int("Schema_rand_kartinki",2);
                alertDialog.cancel();
            }
        });
        sh_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);
                Schema_rand_kartinki = 1000;
                save_value_int("Schema_rand_kartinki",1000);
                alertDialog.cancel();
            }
        });




    }
}
