package dmitriy.deomin.generateawallpaper;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import eu.janmuller.android.simplecropimage.CropImage;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static dmitriy.deomin.generateawallpaper.Main.color_fon_main;
import static dmitriy.deomin.generateawallpaper.Main.transformation;
import static dmitriy.deomin.generateawallpaper.Main.wd;

/**
 * Created by dimon on 16.01.17.
 */

public class ScreenSlidePageFragment extends Fragment {

    Context context;
    private final int PIC_CROP = 1;
    int pic;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        context = rootView.getContext();

        pic = getArguments().getInt("pic", 0);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.image_save_file);

        Picasso.with(rootView.getContext())
                .load(Main.filesArray[pic])
                .transform(transformation)
                .into(imageView);

        ((TextView)rootView.findViewById(R.id.t100)).setText(Main.filesArray[pic].getName());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.myalpha);
                v.startAnimation(anim);
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(rootView.getContext(), android.R.style.Theme_Holo));
                final View content = LayoutInflater.from(rootView.getContext()).inflate(R.layout.menu_img_home, null);
                content.setMinimumWidth(wd);
                content.setMinimumHeight(wd / 2);
                builder.setView(content);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                ((TextView)content.findViewById(R.id.textView_name_pic)).setText(Main.filesArray[pic].getName());
                ((TextView)content.findViewById(R.id.textView_ves_pic)).setText(String.valueOf(Main.filesArray[pic].length()/1024)+"kb");

                //устанавливем цвет и загружаем настройки
                ((LinearLayout) content.findViewById(R.id.fon_menu)).setBackgroundColor(color_fon_main);

                Button del,send,oboi,open_sis;
                del = (Button)content.findViewById(R.id.button_del);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.myalpha);
                        v.startAnimation(anim);
                       if(Main.filesArray[pic].delete()){
                           Toast.makeText(rootView.getContext(),"удалено",Toast.LENGTH_SHORT).show();
                           Main.update_list_files();
                           Main.mPagerAdapter.notifyDataSetChanged();
                           Main.mPager.setAdapter(Main.mPagerAdapter);
                           Main.mPager.setCurrentItem(pic-1);
                           if(pic>0){
                               Main.mPager.setCurrentItem(pic-1);
                           }

                           alertDialog.cancel();
                       }else {
                           Toast.makeText(rootView.getContext(),"ошибка",Toast.LENGTH_SHORT).show();
                       }

                    }
                });

                oboi  = (Button)content.findViewById(R.id.button_ustanovit);

                oboi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.myalpha);
                        v.startAnimation(anim);
                        //если включена обрезка обрежем и установим
                        if(Main.auto_oboi_costrate){
                            //запустим в мясорубку
                            runCropImage(Main.filesArray[pic].toString());
                        }else{
                            //если включено установка обоев
                            if(Main.auto_oboi_crete) {
                                //установим
                                Bitmap bitmap = BitmapFactory.decodeFile(Main.filesArray[pic].toString());
                                setOboi(bitmap);
                            }else {
                                Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show();
                            }
                        }

                        alertDialog.cancel();

                    }
                });

                send = ((Button)content.findViewById(R.id.button_send));

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.myalpha);
                        v.startAnimation(anim);

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("image/*");
                       // sharingIntent.setPackage("com.android.bluetooth");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(Main.filesArray[pic].getAbsolutePath()));
                        startActivity(Intent.createChooser(sharingIntent, "Выберите куда отправить"));
                        alertDialog.cancel();
                    }
                });

                open_sis = (Button)content.findViewById(R.id.button_pic_open_sistema);
                 open_sis.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Animation anim = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.myalpha);
                         v.startAnimation(anim);
                         Uri path = Uri.fromFile(Main.filesArray[pic]);
                         Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                         pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         pdfOpenintent.setDataAndType(path, "image/jpeg");
                         try {
                             startActivity(pdfOpenintent);
                         } catch (ActivityNotFoundException e) {

                         }
                         alertDialog.cancel();
                     }
                 });


            }
        });

        return rootView;
    }


    private void runCropImage(String filePath) {

        // create explicit intent
        Intent intent = new Intent(context, CropImage.class);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    Bitmap bitmap = BitmapFactory.decodeFile(Main.filesArray[pic].toString());
                    setOboi(resize(bitmap,Main.hd,Main.hd));
                }else {
                    //иначе скажем
                    Toast.makeText(context, R.string.obrezano_i_save, Toast.LENGTH_SHORT).show();
                }


            }
        }
    }


    private void setOboi(Bitmap bmp){
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            wallpaperManager.setBitmap(bmp);
            Toast.makeText(context, R.string.save_i_ustanovleno, Toast.LENGTH_SHORT).show();
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
