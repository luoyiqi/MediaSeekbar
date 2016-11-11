package susion.com.mediaseekbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by susion on 16/11/10.
 */
public class MediaSeekBar extends View {


    private Context context;
    private MediaSeekBarListener listener;

    int maxProgress;
    int hasBufferProgress;
    int currentProgress;

    int width;
    int height;
    private int centerY;
    private Bitmap thumb;
    private int thumbPos;
    private final int DEFAULT_SIZE = 100;



    private Paint paint;
    private int hasPlayColor;
    private int originBackgroundColor;
    private int hasBufferColor;
    private int progressWidth;

    float x;
    float y;
    private int currentTouchState = NO_TOUCH;
    private static final int NO_TOUCH = 1;
    private static final int CLICK_THUMB = 2;
    private static final int START_DRAW_THUMB = 3;
    private static final int STOP_DRAW_THUMB = 4;
    private static final int CLICK_PROGRESS = 5;
    private boolean isStartDrawThumb = false;
    private int PROGRESS_CLICK_RANGE = 10;



    public MediaSeekBar(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public MediaSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    public MediaSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initDefaultStyle();
        initAttributes(attrs);
        initPaint();
    }


    private void initDefaultStyle() {
        Resources resources = context.getResources();
        hasPlayColor = resources.getColor(R.color.has_play_color);
        originBackgroundColor = resources.getColor(R.color.origin_background_color);
        hasBufferColor = resources.getColor(R.color.has_buffer_color);
        maxProgress = 100;
        progressWidth = dpToPx(2);
        thumb = (new XhsMediaPlayerThumb( dpToPx(30), dpToPx(30) , context)).getBitmap();
    }


    private void initAttributes(AttributeSet attrs) {
        if(attrs != null){
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MediaSeekBar);
            hasBufferColor = ta.getColor(R.styleable.MediaSeekBar_hasBufferColor, hasBufferColor);
            hasPlayColor = ta.getColor(R.styleable.MediaSeekBar_hasPlayColor, hasPlayColor);
            originBackgroundColor = ta.getColor(R.styleable.MediaSeekBar_originBackgroundColor, originBackgroundColor);
            maxProgress = ta.getInt(R.styleable.MediaSeekBar_maxProgress, maxProgress);
            progressWidth = ta.getDimensionPixelOffset(R.styleable.MediaSeekBar_progressWidth, progressWidth);

            Drawable tempDrawable = ta.getDrawable(R.styleable.MediaSeekBar_customThumb);
            if(tempDrawable != null){
                thumb = getBitmapFromDrawable(tempDrawable);
            }

            ta.recycle();
        }
    }

    private void initPaint() {
        paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(progressWidth);
        paint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode =  MeasureSpec.getMode(heightMeasureSpec);
        if(widthSpecMode == MeasureSpec.EXACTLY){
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        else if(widthSpecMode == MeasureSpec.AT_MOST){
            width = DEFAULT_SIZE;
        }
        if(heightSpecMode == MeasureSpec.EXACTLY){
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        else if(heightSpecMode == MeasureSpec.AT_MOST){
            height = DEFAULT_SIZE;
        }


        if(thumb != null){
            int thumbHeight = thumb.getHeight();
            if(thumbHeight > height){
                //scale thumb to fit
                Matrix matrix = new Matrix();
                float scaleValue = (float) (height * 1.0 / thumbHeight);
                matrix.setScale(scaleValue, scaleValue);
                thumb = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);
            }
        }

        centerY = height / 2;
        if(height < PROGRESS_CLICK_RANGE * 2){
            PROGRESS_CLICK_RANGE = height / 2;
        }

        setMeasuredDimension(width, height);
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(0, centerY);
        drawOriginProgressLine(canvas);
        drawHasBufferProgressLine(canvas);
        drawHasPlayProgressLine(canvas);
        drawThumb(canvas);
        canvas.save();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();

                if(isPressThumb(x, y)){
                    currentTouchState = CLICK_THUMB;
                }

                if(isClickProgress(x, y)){
                    currentTouchState = CLICK_PROGRESS;
                    setCurrentProgress(translateXtoProgress(x));
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if(currentTouchState == CLICK_THUMB || currentTouchState == START_DRAW_THUMB){
                    setThumbPositionByAxis(event.getX());

                    if(!isStartDrawThumb){
                        isStartDrawThumb = true;
                        currentTouchState = START_DRAW_THUMB;
                        notifyListener(translateXtoProgress(event.getX()));
                    }
                }
                break;

            case MotionEvent.ACTION_UP:

                if(currentTouchState == CLICK_PROGRESS){
                    notifyListener(getCurrentProgress());
                }

                if(currentTouchState == START_DRAW_THUMB){
                    if(isStartDrawThumb){
                        isStartDrawThumb = false;
                        currentTouchState = STOP_DRAW_THUMB;
                        notifyListener(getCurrentProgress());
                    }
                }

                if(currentTouchState == CLICK_THUMB){
                    notifyListener(0);
                }

                currentTouchState = NO_TOUCH;
                break;
        }
        return true;
    }



    private void notifyListener(int currentProgress) {

        if(listener == null) return;

        if(currentTouchState == CLICK_THUMB){
            listener.onThumbClick();
        }

        if(currentTouchState == CLICK_PROGRESS){
            listener.onProgressChange(currentProgress);
        }

        if(currentTouchState == START_DRAW_THUMB){
            listener.onStartDrawThumb(currentProgress);
        }

        if(currentTouchState == STOP_DRAW_THUMB){
            listener.onStopDrawThumb(currentProgress);
        }

    }

    private boolean isPressThumb(float x, float y) {

        //expand touch range of thumb
        int left = thumbPos - thumb.getWidth();
        int top = 0;
        int right = thumbPos + thumb.getWidth();
        int bottom = height;

        RectF thumbRect = new RectF(left,top, right, bottom);

        return  thumbRect.contains(x, y);
    }

    private boolean isClickProgress(float x, float y) {
        return  y > centerY -  PROGRESS_CLICK_RANGE && y < centerY + PROGRESS_CLICK_RANGE;
    }

    private void drawOriginProgressLine(Canvas canvas) {
        paint.setColor(originBackgroundColor);
        canvas.drawLine(0, 0, width, 0, paint);
    }

    private void drawHasBufferProgressLine(Canvas canvas) {
        int stopX = (int) ((hasBufferProgress * 1.0 / maxProgress) * width);
        paint.setColor(hasBufferColor);
        canvas.drawLine(0, 0, stopX, 0, paint);
    }

    private void drawHasPlayProgressLine(Canvas canvas) {
        int stopX = (int) ((currentProgress * 1.0 / maxProgress) * width);
        paint.setColor(hasPlayColor);
        canvas.drawLine(0, 0, stopX, 0, paint);
    }


    private void drawThumb(Canvas canvas) {
        int  stopX = thumbPos - (thumb.getWidth() / 2);
        int top =  thumb.getHeight() / 2 * -1;
        canvas.drawBitmap(thumb, stopX, top, paint);
    }


    public  void setHasBufferProgress(int bufferProgress){
        if(bufferProgress > maxProgress){
            hasBufferProgress = maxProgress;
        }else {
            hasBufferProgress = bufferProgress;
        }
        invalidate();
    }

    public void  setCurrentProgress(int progress){
        if(progress > maxProgress){
            currentProgress = maxProgress;
        }else {
            currentProgress = progress;
        }
        setThumbPositionByProgress(currentProgress);
        invalidate();
    }

    private void setThumbPositionByAxis(float x) {
        setCurrentProgress(translateXtoProgress(x));
    }

    private void setThumbPositionByProgress(int currentProgress) {
        thumbPos = (int) ((currentProgress * 1.0 / maxProgress) * width);
    }

    private int translateXtoProgress(float x) {
        return (int) (maxProgress * (x / width));
    }


    public  int dpToPx(int values) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }

    public void setThumb(Drawable d) {
        thumb = getBitmapFromDrawable(d);
    }

    public void setMediaSeekBarListener(MediaSeekBarListener listener) {
        this.listener = listener;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public int getHasBufferProgress() {
        return hasBufferProgress;
    }

    public  Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public interface  MediaSeekBarListener{
        void onThumbClick();
        void onStartDrawThumb(int currentProgress);
        void onStopDrawThumb(int cureentProgress);
        void onProgressChange(int currentProgress);
    }

}
