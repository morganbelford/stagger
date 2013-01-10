package com.morganbelford.stagger;

import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;

import com.loopj.android.image.SmartImageTask;
import com.loopj.android.image.SmartImageView;

public class ImageInfo {

    private String _sUrl;

    // these rects are in float dips
    private RectF _rLoaded;
    private RectF _rDefault;
    private RectF _rLayout;

    private SmartImageView _vw;
    
    private View.OnClickListener _clickListener;

    public ImageInfo(String sUrl, View.OnClickListener clickListener) {
        _rDefault = new RectF(0, 0, 100, 100);
        _sUrl = sUrl;
        _rLayout = new RectF();
        _clickListener = clickListener;
    }

    public RectF bounds() {
        // if we have a loaded h/w, use it -- it should not change
        if (_rLoaded == null && _vw != null) {
            int h = _vw.getMeasuredHeight();
            int w = _vw.getMeasuredWidth();
            Log.d("ImageInfo", String.format("Dynamic Measured size: %d x %d for url %s", w, h, _sUrl));
            if (h > 0 && w > 0) {
                _rLoaded = new RectF(0, 0, w, h);
            }
        }
        if (_rLoaded != null)
            return _rLoaded;
        return _rDefault;
    }

    // reuse our rect -- this gets called a lot
    public void setLayoutBounds(float left, float top, float right, float bottom) {
        _rLayout.top = top;
        _rLayout.left = left;
        _rLayout.right = right;
        _rLayout.bottom = bottom;
    }

    public RectF layoutBounds() {
        return _rLayout;
    }

    public SmartImageView view() {
        return _vw;
    }

    public void setView(SmartImageView vw) // may be null
    {
        if (vw == null && _vw != null)
        {
            _vw.setImage(null, (SmartImageTask.OnCompleteListener)null);
            _vw.setOnClickListener(null);
        }
        _vw = vw;
        if (_vw != null)
        {
            _vw.setImageUrl(_sUrl, R.drawable.default_image, new SmartImageTask.OnCompleteListener() {
                final private View vw = _vw;
                @Override
                public void onComplete() {
                    vw.measure(MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED));
                    int h = vw.getMeasuredHeight();
                    int w = vw.getMeasuredWidth();
                    _rLoaded = new RectF(0, 0, w, h);
                    Log.d("ImageInfo", String.format("Settings loaded size onComplete %d x %d for %s", w, h, _sUrl));
                }
            });
            _vw.setOnClickListener(_clickListener);
        }
    }

    public boolean overlaps(float top, float bottom) {
        if (_rLayout.bottom < top)
            return false;
        if (_rLayout.top > bottom)
            return false;

        return true;
    }

}
