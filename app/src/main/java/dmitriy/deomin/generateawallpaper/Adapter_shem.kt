package dmitriy.deomin.generateawallpaper

import android.content.Context
import android.widget.SimpleAdapter

/**
 * Created by dimon on 15.01.17.
 */

class Adapter_shem(context: Context, data: List<Map<String, *>>, resource: Int, from: Array<String>, to: IntArray) : SimpleAdapter(context, data, resource, from, to)
