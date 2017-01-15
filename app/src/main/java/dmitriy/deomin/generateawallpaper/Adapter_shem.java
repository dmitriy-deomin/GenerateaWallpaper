package dmitriy.deomin.generateawallpaper;

import android.content.Context;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by dimon on 15.01.17.
 */

public class Adapter_shem extends SimpleAdapter {
    public Adapter_shem(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }
}
