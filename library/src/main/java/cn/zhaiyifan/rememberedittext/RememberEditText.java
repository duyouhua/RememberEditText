package cn.zhaiyifan.rememberedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class RememberEditText extends EditText {

    private static final String PREFERENCE_KEY = "RememberEditText";

    private int mRememberCount = 1;
    private String mRememberId;
    private boolean mAutoFill = true;
    private Drawable mDeleteDrawable;
    private Drawable mDropDownDrawable;

    private static final int DEFAULT_REMEMBER_COUNT = 3;
    private static final int ICON_MARGIN = 20;
    private static PersistedMap mCacheMap;

    public RememberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initCacheMap(context);
        initData();
    }

    public RememberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initCacheMap(context);
        initData();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RememberEditText);
        try {
            mDeleteDrawable = getResources().getDrawable(a.getResourceId(
                    R.styleable.RememberEditText_deleteIcon, R.drawable.abc_ic_clear_mtrl_alpha));
            if (mDeleteDrawable != null) {
                mDeleteDrawable.setBounds(0, 0, mDeleteDrawable.getIntrinsicWidth(), mDeleteDrawable.getIntrinsicHeight());
            }

            mDropDownDrawable = getResources().getDrawable(a.getResourceId(
                    R.styleable.RememberEditText_dropDownIcon, R.drawable.abc_spinner_mtrl_am_alpha));
            if (mDropDownDrawable != null) {
                mDropDownDrawable.setBounds(0, 0, mDropDownDrawable.getIntrinsicWidth(), mDropDownDrawable.getIntrinsicHeight());
            }

            mRememberCount = a.getInt(R.styleable.RememberEditText_rememberCount, DEFAULT_REMEMBER_COUNT);
            mRememberId = a.getString(R.styleable.RememberEditText_rememberId);
            // if not set rememberId, use view id
            if (null == mRememberId) {
                mRememberId = String.valueOf(getId());
            }

            mAutoFill = a.getBoolean(R.styleable.RememberEditText_autoFill, true);
        } finally {
            a.recycle();
        }
    }

    /**
     * restore last recent input
     */
    private void initData() {
        if (mAutoFill) {
            String cache = mCacheMap.get(mRememberId);
            setText(cache);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        final int compoundPaddingTop = getCompoundPaddingTop();
        final int compoundPaddingBottom = getCompoundPaddingBottom();

        int vspace = getBottom() - getTop() - compoundPaddingBottom - compoundPaddingTop;
        int drawableHeight = mDeleteDrawable.getIntrinsicHeight();
        int dropDownWidth = mDropDownDrawable.getIntrinsicWidth();
        int deleteWidth = mDeleteDrawable.getIntrinsicWidth();

        canvas.translate(getMeasuredWidth() - getCompoundPaddingRight() - dropDownWidth - ICON_MARGIN - deleteWidth,
                getCompoundPaddingTop() + getScrollY() + (vspace - drawableHeight) / 2);
        mDeleteDrawable.draw(canvas);
        canvas.translate(deleteWidth + ICON_MARGIN, 0);
        mDropDownDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * Store text to cache when focus lost.
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        // lose focus
        if (!focused) {
            // if have text, save it
            String text = getText().toString();
            mCacheMap.put(mRememberId, text);
        }
    }

    /**
     * Interrupt onTouchEvent, check icon click event.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private static void initCacheMap(Context context) {
        if (mCacheMap == null) {
            mCacheMap = new PersistedMap(context, PREFERENCE_KEY);
        }
    }

    /**
     * Clear all cache managed by RememberEditText.
     */
    public static void clearCache(Context context) {
        initCacheMap(context);
        mCacheMap.clear();
    }
}