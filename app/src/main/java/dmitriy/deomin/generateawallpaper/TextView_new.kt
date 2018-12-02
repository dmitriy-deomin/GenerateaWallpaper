package dmitriy.deomin.generateawallpaper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView

class TextView_new : TextView {


    constructor(context: Context) : super(context) {
        this.typeface = Main.face
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.typeface = Main.face
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.typeface = Main.face
    }

    override fun onDraw(canvas: Canvas) {
        this.typeface = Main.face

        val m = this.text.toString()
        if (m.split(". ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] == (Main.Schema_rand_kartinki + 1).toString()) {
            this.setTextColor(Color.GREEN)
        } else {
            this.setTextColor(Color.BLACK)
        }


        super.onDraw(canvas)
    }
}
