package com.xiaojun.expendedview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;


/**
 * 展开收缩Layout
 * Crated by xiaojun on 2019/9/2 15:08
 */
public class ExpendedLayout extends FrameLayout implements View.OnClickListener {

    private String mTitleText = "DefaultTitle:";
    private int mTitleSize = 18;
    private View mContentView;
    private int mTitleLayoutId;
    private ImageView mIvDown;
    private TextView mTvTitle;
    private RelativeLayout mTitleLayout;
    private int mTitleLayoutBackColor;
    private int mRightDrawable;
    private int mTitleLayoutHeight;

    public ExpendedLayout(Context context) {
        this(context, null);
    }

    public ExpendedLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpendedLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ExpendedLayout);
        mTitleText = ta.getString(R.styleable.ExpendedLayout_lee_titleText);
        if (TextUtils.isEmpty(mTitleText))
            mTitleText = "DefaultText";
        mTitleSize = (int) ta.getDimension(R.styleable.ExpendedLayout_lee_titleSize, 18);
        mTitleLayoutBackColor = ta.getColor(R.styleable.ExpendedLayout_lee_titleBackgroundColor, Color.YELLOW);
        mRightDrawable = ta.getResourceId(R.styleable.ExpendedLayout_lee_rightDownIcon, R.drawable.down);
        mTitleLayoutHeight = (int) ta.getDimension(R.styleable.ExpendedLayout_lee_titleLayoutHeight, dip2px(48));
        Log.e("xiaojun", "mTitleLayoutHeight=" + mTitleLayoutHeight);
        ta.recycle();
    }

    private void init(Context context) {
        int dp48 = dip2px(48);
        int dp16 = dip2px(16);
        mContentView = getContentView(context);

        //-----------TitleLayout------------
        mTitleLayout = new RelativeLayout(context);
        mTitleLayout.setPadding(dp16, 0, dp16, 0);
        mTitleLayoutId = View.generateViewId();
        mTitleLayout.setId(mTitleLayoutId);
        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTitleLayoutHeight);
        mTitleLayout.setLayoutParams(titleLayoutParams);
        mTitleLayout.setBackgroundColor(mTitleLayoutBackColor);

        //-----------Title--------------
        mTvTitle = new TextView(context);
        mTvTitle.setTextSize(mTitleSize);
        mTvTitle.setText(mTitleText);
        mTvTitle.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams titleTvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titleTvParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        mTvTitle.setLayoutParams(titleTvParams);

        //----------downIcon------------
        mIvDown = new ImageView(context);
        RelativeLayout.LayoutParams ivDownParams = new RelativeLayout.LayoutParams(dp48, dp48);
        ivDownParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        ivDownParams.addRule(RelativeLayout.CENTER_VERTICAL);
        mIvDown.setLayoutParams(ivDownParams);
        int dp2 = dip2px(8);
        mIvDown.setPadding(dp2, dp2, dp2, dp2);
        mIvDown.setImageDrawable(ContextCompat.getDrawable(context, mRightDrawable));

        mTitleLayout.addView(mTvTitle);
        mTitleLayout.addView(mIvDown);

        this.addView(mContentView);
        this.addView(mTitleLayout);

        mTitleLayout.setOnClickListener(this);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.mTvTitle.setText(title);
    }

    /**
     * 设置标题字体大小大小
     *
     * @param size
     */
    public void setTextSize(int size) {
        this.mTvTitle.setTextSize(size);
    }

    /**
     * 设置TitleLayout颜色
     *
     * @param color
     */
    public void setTitleLayoutColor(int color) {
        this.mTitleLayout.setBackgroundColor(color);
    }

    /**
     * 设置TitleLayout高度
     *
     * @param height
     */
    public void setTitleLayoutHeight(int height) {
        this.mTitleLayout.getLayoutParams().height = height;
    }

    /**
     * 设置Title的右边图片
     *
     * @param drawableId
     */
    public void setRightIcon(int drawableId) {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), drawableId, null);
        this.mIvDown.setImageDrawable(drawable);
    }

    /**
     * 设置此View用于执行收缩动画的时候跟着一起动
     *
     * @param views
     */
    public ExpendedLayout setNextViews(View... views) {
        this.mNextViews = views;
        for (int i = 0; i < views.length; i++) {
            if (views[i] instanceof ExpendedLayout){
                for (int j = i+1; j < views.length; j++) {
                    ((ExpendedLayout)views[i]).setNextViews(views[j]);
                }
            }
        }
        return this;
    }

    private View[] mNextViews;

    private void expendOrShrink() {
        boolean visible = mContentView.getVisibility() == VISIBLE;
        if (visible) {
            mIvDown.animate().rotation(180).setDuration(200).start();
            mContentView.animate().translationY(-mContentView.getMeasuredHeight()).setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mContentView.setVisibility(GONE);
                            if (mNextViews != null)
                                for (View nextView : mNextViews) {
                                    nextView.setTranslationY(0);
                                }
                        }
                    }).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (mNextViews != null) {
                        for (View nextView : mNextViews) {
                            nextView.setTranslationY(mContentView.getTranslationY());
                        }
                    }
                }

            }).start();
        } else {
            mIvDown.animate().rotation(0).setDuration(200).start();
            mContentView.animate().translationY(0).setDuration(200).setListener(null).start();
            this.mContentView.setVisibility(VISIBLE);
        }

    }

    protected View getContentView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.test, this, false);
        FrameLayout.LayoutParams paramsContentView = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        paramsContentView.setMargins(0, mTitleLayoutHeight, 0, 0);
        view.setLayoutParams(paramsContentView);
        return view;
    }

    //由外部传入ContentView
    public void setContentView(View view) {
        if (view == null)
            throw new NullPointerException("ContentView不能为空");
        int index = indexOfChild(mContentView);
        FrameLayout.LayoutParams params = (LayoutParams) mContentView.getLayoutParams();
        removeView(mContentView);
        mContentView = view;
        this.addView(mContentView, index, params);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == mTitleLayoutId) {
            expendOrShrink();
        }
    }
}
