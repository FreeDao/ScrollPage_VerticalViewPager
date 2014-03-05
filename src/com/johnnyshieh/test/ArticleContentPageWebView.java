package com.johnnyshieh.test;

import com.view.ExtendedWebView;
import com.view.VerticalViewPager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ArticleContentPageWebView extends ExtendedWebView {

    private VerticalViewPager mPager ;
    private int mMaxYOverscrollDistance;
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 200; 
    private final String PREV_ARTICLE;
    private final String NEXT_ARTICLE;
    private LoadListener mLoadListener;
    
    public ArticleContentPageWebView ( Context context ) {
        this ( context, null ) ;
    }
    public ArticleContentPageWebView(Context context, AttributeSet attr) {
        // TODO Auto-generated constructor stub
        super(context, attr);
        initSetting();
        initNightForWebView();
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();  
        final float density = metrics.density;  
        PREV_ARTICLE = context.getResources().getString(R.string.prev_article);
        NEXT_ARTICLE = context.getResources().getString(R.string.next_article);
        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    private void initSetting() {
        setWillNotDraw(false);
        WebSettings localWebSettings = getSettings();
        localWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        localWebSettings.setJavaScriptEnabled(true);
        localWebSettings.setLightTouchEnabled(false);
        localWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        setScrollbarFadingEnabled(true);
        addJavascriptInterface(this, "js");
        setFocusable(true);
        setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        
    }

    public void initNightForWebView() {
        /*if (ReaderSetting.getInstance().isNight()) {
            setBackgroundColor(getContext().getResources().getColor(
                    R.color.black));
            return;
        }
        setBackgroundColor(getContext().getResources().getColor(R.color.white));*/
    }
    public void setPager ( VerticalViewPager pager ) {
        mPager = pager ;
    }
    public void setLoadListener(LoadListener l){
        mLoadListener = l;
    }
    @Override  
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent){   
        //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance;  
        int newDeltaY = ( int ) ( deltaY * getScrollFactor () ) ;
        return super.overScrollBy(deltaX, newDeltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);    
    } 
    
    private double getScrollFactor () {
        double result = 1.0 ;
        int scrollY = getScrollY () ;
        
        if ( scrollY < 0 ) {
            result = 1.0 * ( mMaxYOverscrollDistance + scrollY ) / mMaxYOverscrollDistance ; 
        } else {
            scrollY = ( int ) ( getScrollY () + getHeight () - getContentHeight() * getScale () ) ;
            if ( scrollY > 0 ) {
                result = 1.0 * ( mMaxYOverscrollDistance - scrollY ) / mMaxYOverscrollDistance ;
            }
        }
        return result ;
//        return result > 1.0 ? 1.0 : result ;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        
        super.onDraw(canvas);
        int deltaY = 0;
        if( getScrollY() < 0 ){
            canvas.save();
            Rect clipRect = new Rect(0, 0, getWidth(), -getScrollY());
//            Rect targetRect = new Rect(0,mMaxYOverscrollDistance/8,getWidth(),mMaxYOverscrollDistance/8*5);
            int bottom = - getScrollY () > ( mMaxYOverscrollDistance/8*1 ) ? - getScrollY () : mMaxYOverscrollDistance/8*1 ;
            Rect targetRect = new Rect(0, 0, getWidth(), bottom );
            String line2 = mLoadListener != null ? mLoadListener.getPrevArticleTitle() : null;
            drawHintText(canvas,getScrollY(),clipRect,targetRect,PREV_ARTICLE,line2);
        }else if( (deltaY = (int)(getScrollY() + getHeight() - getContentHeight()*getScale())) > 0){
            int transY = getScrollY() + getHeight()-mMaxYOverscrollDistance;
            Rect clipRect = new Rect(0, mMaxYOverscrollDistance - deltaY, getWidth(), mMaxYOverscrollDistance);
//            Rect targetRect = new Rect(0,mMaxYOverscrollDistance/8*3,getWidth(),mMaxYOverscrollDistance/8*7);
            int bottom = deltaY < ( mMaxYOverscrollDistance/8*1 ) ?  mMaxYOverscrollDistance/8*7 : mMaxYOverscrollDistance - deltaY ;
            Rect targetRect = new Rect(0, bottom, getWidth(), mMaxYOverscrollDistance );
            String line2 = mLoadListener != null ? mLoadListener.getNextArticleTitle() : null;
            drawHintText(canvas,transY,clipRect,targetRect,NEXT_ARTICLE,line2);
        }
    }
    private void drawHintText(Canvas canvas, int transY, Rect clipRect, Rect targetRect, String line1, String line2){
        canvas.save();
        canvas.translate(0, transY);
        canvas.clipRect(clipRect);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
        paint.setStrokeWidth(3);    
        paint.setTextSize(50);  
        paint.setColor(Color.LTGRAY);
        //change the alpha value
        paint.setAlpha ( (int) ( 255 * ( 1- getScrollFactor () ) ) ) ;
        canvas.drawRect(targetRect, paint); 
        paint.setColor(Color.RED);  
        FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline1 = targetRect.top + ((targetRect.bottom - targetRect.top)/2 - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top; 
        paint.setTextAlign(Paint.Align.CENTER); 
        canvas.drawText(line1, targetRect.centerX(), baseline1, paint);
        int baseline2 = baseline1 + (targetRect.bottom - targetRect.top)/2;
        if( line2 != null ){
            canvas.drawText(line2, targetRect.centerX(), baseline2, paint);
        }
        canvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) { 
        final int action = event.getAction();
        if( (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP ){
            int deltaY = getScrollY();
            if( deltaY < 0 ){
                if( -deltaY > mMaxYOverscrollDistance/8*7){
                    /*ReaderUtils.showToast(getContext(), "取上一篇文章");*/
                    mPager.setCurrentItem ( mPager.getCurrentItem () - 1, true ) ;
                    if( mLoadListener != null ){
                        mLoadListener.onLoadPrevArticle();
                    }
                }
            }else{
                deltaY = (int)(getScrollY() + getHeight() - getContentHeight()*getScale());
                if( deltaY > mMaxYOverscrollDistance/8*7){
                    /*ReaderUtils.showToast(getContext(), "取下一篇文章");*/
                    mPager.setCurrentItem ( mPager.getCurrentItem () + 1, true ) ;
                    if( mLoadListener != null ){
                        mLoadListener.onLoadNextArticle();
                    }
                }
            }
            
        }
        return super.onTouchEvent(event);
    }
    

    public static interface  LoadListener{
        void onLoadNextArticle();
        void onLoadPrevArticle();
        String getNextArticleTitle();
        String getPrevArticleTitle();
    }
}
