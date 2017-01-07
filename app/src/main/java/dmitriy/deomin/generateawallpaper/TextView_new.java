package dmitriy.deomin.generateawallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextView_new extends TextView {


    public TextView_new(Context context) {
        super(context);
        this.setTypeface(Main.face);
    }

    public TextView_new(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Main.face);
    }

    public TextView_new(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Main.face);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.setTypeface(Main.face);
        super.onDraw(canvas);
    }
}
