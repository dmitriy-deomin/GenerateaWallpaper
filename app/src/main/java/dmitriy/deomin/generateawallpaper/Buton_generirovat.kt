package dmitriy.deomin.generateawallpaper

import android.content.Context
import android.graphics.Canvas
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.widget.Button

/**
 * Created by dimon on 19.01.17.
 */

class Buton_generirovat : Button {
    constructor(context: Context) : super(context) {
        this.typeface = Main.face
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.typeface = Main.face
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.typeface = Main.face

        //форматирование текста если есть перенос
        if (this.text.toString().contains("\n")) {
            Main.text = SpannableString(this.text.toString())
            (Main.text as SpannableString).setSpan(UnderlineSpan(), 0, this.text.toString().indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            (Main.text as SpannableString).setSpan(RelativeSizeSpan(1.1f), 0, this.text.toString().indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            (Main.text as SpannableString).setSpan(RelativeSizeSpan(0.5f), this.text.toString().indexOf("\n"), this.text.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            this.text = Main.text

        }
    }
}