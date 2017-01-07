package dmitriy.deomin.generateawallpaper;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by dimon on 07.01.17.
 */

public class Generat extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Holst(this));
    }
}
