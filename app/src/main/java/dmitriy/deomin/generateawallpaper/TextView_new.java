package dmitriy.deomin.generateawallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

        String m = this.getText().toString();
        if(m.split(". ")[0].equals(String.valueOf(Main.Schema_rand_kartinki+1))){
            this.setTextColor(Color.GREEN);
        }else {
            this.setTextColor(Color.BLACK);
        }


        super.onDraw(canvas);
    }
}
