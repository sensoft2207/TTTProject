package com.senindia.tictactoe.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mxicoders on 11/9/17.
 */

public class MyTextviewBold extends TextView {

    public MyTextviewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextviewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextviewBold(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/MONTSERRAT-BOLD.OTF");
        setTypeface(tf, 1);

    }

}
