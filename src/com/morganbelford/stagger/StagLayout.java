package com.morganbelford.stagger;

import java.util.ArrayList;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.loopj.android.image.SmartImageView;

public class StagLayout extends ViewGroup {
    
    private float _pxMargin;
    private int _cCols;
    private int _cMaxCachedViews;

    // store our image info
    private ArrayList<ImageInfo> _infos;
    private float _maxBottom;
    
    private float _viewportTop;
    private float _viewportBottom;
    
    // manage subviews
    // hashset of active
    private HashSet<ImageInfo> _activeInfos;
    private ArrayList<SmartImageView> _cachedViews;
    // 
    
    private Paint _paint;
    
    public StagLayout(Context context) {
        super(context);
    }

    public StagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StagLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setUrls(String[] urls, float pxMargin, int cCols)
    {
        _pxMargin = pxMargin;
        _cCols = cCols;
        _cMaxCachedViews = 2 * cCols;
        // build list of 200 ImageInfos, with default sizes
        _infos = new ArrayList<ImageInfo>(200);  // should be urls.length
        for (int i = 0; i< 200; i++)
        {
            final String sUrl = urls[i % urls.length];
            _infos.add(new ImageInfo(sUrl, new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Log.d("PinterestLayout", String.format("Image clicked: url == %s", sUrl));
                }
            }));
        }
        
        _activeInfos = new HashSet<ImageInfo>(_infos.size());
        _cachedViews = new ArrayList<SmartImageView>(_cMaxCachedViews);

        requestLayout();

    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // record scale for layout later
        _infos = new ArrayList<ImageInfo>(); // just to be non-null
        
        _cCols = 1; // for the math, before we get our urls set
        
        _paint = new Paint();
        _paint.setColor(0x22000000);
    }
    
    
    private void computeImageInfo(float width)
    {
        float dxMargin = _pxMargin; 
        float dyMargin = _pxMargin;
        
        float left = 0;
        float tops[] = new float[_cCols];  // start at 0
        float widthCol = (int)((width - (_cCols + 1) * dxMargin) / _cCols);
        
        _maxBottom = 0;
        
        // layout the images -- set their layoutrect based on our current location and their bounds
        for (int i = 0; i < _infos.size(); i++)
        {
            int iCol = i % _cCols;
            // new row
            if (iCol == 0)
            {
               left = dxMargin;
               for (int j = 0; j < _cCols; j++)
                   tops[j] += dyMargin;
            }
            ImageInfo info = _infos.get(i); 
            RectF bounds = info.bounds();
            float scale = widthCol / bounds.width(); // up or down, for now, it does not matter
            float layoutHeight = bounds.height() * scale;
            float top = tops[iCol];
            float bottom = top + layoutHeight;
            info.setLayoutBounds(left, top, left + widthCol, bottom);
            
            if (bottom > _maxBottom)
                _maxBottom = bottom;
            left += widthCol + dxMargin;
            tops[iCol] += layoutHeight;
        }
        
        // TODO build indexes of tops and bottoms
        
        
        // should now set our own height using layoutParams
        _maxBottom += dyMargin;
    }
    
    public void setVisibleArea(int top, int bottom) {

        _viewportTop = top;
        _viewportBottom = bottom;
        
        //fixup views
        if (getWidth() == 0) // if we have never been measured, dont do this - it will happen in first layout shortly
            return;
        requestLayout();
    }
    
    private void setupSubviews()
    {

        // need to compute new set of active
        // TODO for now enumerate -- later do binary search and spread.
        HashSet<ImageInfo> neededInfos = new HashSet<ImageInfo>(_infos.size());
        HashSet<ImageInfo> newInfos = new HashSet<ImageInfo>(_infos.size());
        for (ImageInfo info : _infos)
        {
            if (info.overlaps(_viewportTop, _viewportBottom))
            {
                neededInfos.add(info);
                if (info.view() == null)
                    newInfos.add(info);
            }
        }
        
        // ok, so now we have the active ones. lets get any we need to deactivate        
        HashSet<ImageInfo> unneededInfos = new HashSet<ImageInfo>(_activeInfos); // copy this
        unneededInfos.removeAll(neededInfos);
        // we want to grab all the views from these guys, and possibly reuse them
        ArrayList<SmartImageView> unneededViews = new ArrayList<SmartImageView>(unneededInfos.size());
        for (ImageInfo info : unneededInfos)
        {
            SmartImageView vw = info.view();
            unneededViews.add(vw);
            info.setView(null); // view still attached at this point to partent
        }
        // ok, so now we try to reuse the views, and create new ones if needed
        for (ImageInfo info : newInfos)
        {
            SmartImageView vw = null;
            if (unneededViews.size() > 0)
            {
                vw = unneededViews.remove(0);
            }
            else if (_cachedViews.size() > 0)
            {
                vw = _cachedViews.remove(0);
                addViewInLayout(vw, -1, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }
            else 
            {
                vw = new SmartImageView(getContext());
                //vw.setBackgroundResource(R.drawable.photo_bg);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                addViewInLayout(vw, -1, lp);
            }
            info.setView(vw);  // info should also set its data
        }
        
        // At this point, add any unneeded views to our cache, up to limit
        for (SmartImageView vw : unneededViews)
        {
            // tell view to cancel
            removeViewInLayout(vw);  // always remove from parent
            if (_cachedViews.size() < _cMaxCachedViews)
                _cachedViews.add(vw);
        }
        
        _activeInfos = neededInfos;
        
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Just for kicks draw a 1px "drop shadow" around each image. We could (should) do this with a background on each SmartImageView, but 
        //  I wanted to experiment a little
        if (_activeInfos == null)
            return;
        
        for (ImageInfo info : _activeInfos)
        {
            RectF r = info.layoutBounds();
            canvas.drawRect(r.left , r.top , r.right + 1, r.bottom + 1, _paint);
        }
    }
    
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        
        // measure each real guy, record "natural" size
        for (ImageInfo info : _activeInfos)
        {
            View v = info.view();
            v.measure(MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED));
        }
        
        computeImageInfo(width);  // this computes the layout/rect of all imageinfos, and sets _maxBottom
        setMeasuredDimension(width, (int)_maxBottom);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setupSubviews();

        for (ImageInfo info : _activeInfos)
        {
            RectF rBounds = info.layoutBounds();  // computed in measure
            info.view().layout((int)rBounds.left, (int)rBounds.top, (int)rBounds.right, (int)rBounds.bottom);    
        }

    }

}
