package dmitriy.deomin.generateawallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

import dmitriy.deomin.generateawallpaper.surfase.MySurfaceView;
import eu.janmuller.android.simplecropimage.CropImage;

import static android.R.attr.height;
import static android.R.attr.width;

public class Generat extends Activity {
    private final int PIC_CROP = 1;
    String file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Holst(this));
        //setContentView(new MySurfaceView(this));

       //чтобы экран не гас
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

       //скрывем кнопки навигации
        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }


        //слушаем  и если че будем реагировать
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Key_signala_pizdec");

        //приёмник  сигналов
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Key_signala_pizdec")) {
                    //получим данные и покажем окошко обобновлении если данные новее
                    file = intent.getStringExtra("key_data_url_imag");

                    //если включена обрезка обрежем и установим
                    if(Main.auto_oboi_costrate){
                        //запустим в мясорубку
                        runCropImage(file);
                    }else{
                        //если включено установка обоев
                        if(Main.auto_oboi_crete) {
                            //установим
                            Bitmap bitmap = BitmapFactory.decodeFile(file);
                            setOboi(bitmap);
                        }else {
                            Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            }
        };

        //регистрируем приёмник
        registerReceiver(broadcastReceiver, intentFilter);
        //************************************************************************************
    }


    private void runCropImage(String filePath) {

        // create explicit intent
        Intent intent = new Intent(this, CropImage.class);

        // tell CropImage activity to look for image to crop
        intent.putExtra(CropImage.IMAGE_PATH, filePath);

        // allow CropImage activity to rescale image
        intent.putExtra(CropImage.SCALE, false);

        // if the aspect ratio is fixed to ratio 3/2
        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);

        // start activity CropImage with certain request code and listen
        // for result
        startActivityForResult(intent, PIC_CROP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PIC_CROP) {
            if (data != null) {

                String path = data.getStringExtra(CropImage.IMAGE_PATH);

                if (path == null) {
                    return;
                }
                //если включена установка обоев
                if(Main.auto_oboi_crete){
                    //увеличим картинку и установим обоями
                    Bitmap bitmap = BitmapFactory.decodeFile(file);
                    setOboi(resize(bitmap,Main.hd,Main.hd));
                }else {
                    //иначе скажем
                    Toast.makeText(getApplicationContext(), R.string.obrezano_i_save, Toast.LENGTH_SHORT).show();
                }


            }
        }
    }


    private void setOboi(Bitmap bmp){
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(bmp);
            Toast.makeText(getApplicationContext(), R.string.save_i_ustanovleno, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bmp = null;
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
}