package dmitriy.deomin.generateawallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by dimon on 19.01.17.
 */

public class Buton_generirovat extends Button {
    public Buton_generirovat(Context context) {
        super(context);
        this.setTypeface(Main.face);
    }

    public Buton_generirovat(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Main.face);
    }

    public Buton_generirovat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Main.face);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setTypeface(Main.face);

        //форматирование текста если есть перенос
        if (this.getText().toString().contains("\n")) {
            Main.text = new SpannableString(this.getText().toString());
            Main.text.setSpan(new UnderlineSpan(), 0, this.getText().toString().indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Main.text.setSpan(new RelativeSizeSpan(1.1f), 0, this.getText().toString().indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Main.text.setSpan(new RelativeSizeSpan(0.5f), this.getText().toString().indexOf("\n"), this.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            this.setText(Main.text);

        }
    }
}