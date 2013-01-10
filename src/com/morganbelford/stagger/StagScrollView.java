package com.morganbelford.stagger;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class StagScrollView extends ScrollView {

    StagLayout _frame;
    
    public StagScrollView(Context context) {
        super(context);
    }

    public StagScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StagScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        _frame = (StagLayout) findViewById(R.id.frame);
        
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldh == 0)
            _frame.setVisibleArea(0, h);
    }
    
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        _frame.setVisibleArea(t, t + getHeight());
    }

}
