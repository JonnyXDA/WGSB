package com.jonny.wgsb.material.fragments;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.jonny.wgsb.material.R;
import com.jonny.wgsb.material.activities.MainActivity;
import com.jonny.wgsb.material.db.DatabaseHandler;
import com.jonny.wgsb.material.ui.PaletteTransformation;
import com.jonny.wgsb.material.ui.helper.News;
import com.jonny.wgsb.material.util.CompatUtils;
import com.jonny.wgsb.material.util.UIUtils;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class NewsFragmentSpecific extends Fragment implements ObservableScrollViewCallbacks {
    private static NewsFragmentSpecific instance = null;
    private ImageView storyImageView;
    private MainActivity mActivity;
    private FrameLayout.LayoutParams originalParams;
    private FrameLayout frame;
    private View mImageHolder, mHeader, mHeaderBar, mHeaderBackground;
    private ObservableScrollView mScrollView;
    private int mActionBarSize, mFlexibleSpaceImageHeight, mIntersectionHeight, mPrevScrollY;
    private boolean mGapFilled;

    public NewsFragmentSpecific() {
    }

    public static NewsFragmentSpecific getInstance() {
        if (instance == null) instance = new NewsFragmentSpecific();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_specific, container, false);
        setRetainInstance(true);
        int newsId = getArguments().getInt("id");
        mActivity = ((MainActivity) getActivity());
        mActivity.mToolbar.setVisibility(Toolbar.GONE);
        mActivity.getDelegate().getSupportActionBar().hide();
        frame = (FrameLayout) getActivity().findViewById(R.id.fragment_container);
        originalParams = (FrameLayout.LayoutParams) frame.getLayoutParams();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frame.setLayoutParams(params);
        ((MainActivity) getActivity()).setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        TextView titleTextView = (TextView) view.findViewById(R.id.titleArticleNews);
        storyImageView = (ImageView) view.findViewById(R.id.storyNewsImage);
        TextView storyTextView = (TextView) view.findViewById(R.id.storyArticleNews);
        DatabaseHandler dbhandler = DatabaseHandler.getInstance(getActivity());
        News articleNews = dbhandler.getNews(newsId);
        String articleTitle = articleNews.title;
        String articleStory = articleNews.story;
        String imageUrl = articleNews.imageSrc;
        Spanned htmlSpan;
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mIntersectionHeight = getResources().getDimensionPixelSize(R.dimen.intersection_height);
        mActionBarSize = getActionBarSize();
        mImageHolder = view.findViewById(R.id.image_holder);
        mHeader = view.findViewById(R.id.header);
        mHeaderBar = view.findViewById(R.id.header_bar);
        mHeaderBackground = view.findViewById(R.id.header_background);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mActivity.mDrawerToggle.setDrawerIndicatorEnabled(false);
        mActivity.getDelegate().getSupportActionBar().setHomeButtonEnabled(true);
        mActivity.getDelegate().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        mActivity.mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.getSupportFragmentManager().popBackStack();
                mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mActivity.getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                mActivity.getDelegate().getSupportActionBar().setDisplayShowTitleEnabled(true);
                mActivity.mDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        });
        titleTextView.setText(articleTitle);
        htmlSpan = Html.fromHtml(articleStory);
        storyTextView.setText(htmlSpan);
        storyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        Picasso.with(getActivity()).load(imageUrl)
                .transform(PaletteTransformation.instance())
                .into(storyImageView, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) storyImageView.getDrawable()).getBitmap();
                        Palette palette = PaletteTransformation.getPalette(bitmap);
                        Palette.Swatch vibrantSwatch = palette.getDarkVibrantSwatch();
                        if (vibrantSwatch != null) {
                            mHeaderBackground.setBackgroundColor(vibrantSwatch.getRgb());
                            if (CompatUtils.isNotLegacyLollipop()) {
                                Window w = getActivity().getWindow();
                                int statusBarColor = UIUtils.scaleColor(vibrantSwatch.getRgb(), 0.8f, false);
                                w.setStatusBarColor(statusBarColor);
                            }
                        }
                    }
                });
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (CompatUtils.isNotLegacyJellyBean()) {
                    mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                onScrollChanged(0, false, false);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        if (CompatUtils.isNotLegacyLollipop()) {
            Window w = getActivity().getWindow();
            w.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        mActivity.mToolbar.setVisibility(Toolbar.VISIBLE);
        frame.setLayoutParams(originalParams);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mActivity.getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mActivity.getDelegate().getSupportActionBar().setDisplayShowTitleEnabled(true);
        mActivity.mDrawerToggle.setDrawerIndicatorEnabled(true);
        super.onDestroyView();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewHelper.setTranslationY(mImageHolder, scrollY / 2);
        final int headerHeight = mHeaderBar.getHeight();
        int headerTranslationY = mFlexibleSpaceImageHeight - headerHeight;
        if (mFlexibleSpaceImageHeight - headerHeight - mActionBarSize + mIntersectionHeight <= scrollY) {
            headerTranslationY = scrollY + mActionBarSize - mIntersectionHeight;
        }
        ViewHelper.setTranslationY(mHeader, headerTranslationY);
        boolean scrollUp = mPrevScrollY < scrollY;
        if (scrollUp) {
            if (mFlexibleSpaceImageHeight - headerHeight - mActionBarSize <= scrollY) {
                if (!mGapFilled) {
                    mGapFilled = true;
                    hideGap();
                }
            }
        } else {
            if (scrollY <= mFlexibleSpaceImageHeight - headerHeight - mActionBarSize) {
                if (mGapFilled) {
                    mGapFilled = false;
                    showGap();
                }
            }
        }
        mPrevScrollY = scrollY;
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void showGap() {
        changeHeaderBackgroundHeight(mHeaderBar.getHeight() + mActionBarSize, mHeaderBar.getHeight());
    }

    private void hideGap() {
        changeHeaderBackgroundHeight(mHeaderBar.getHeight(), mHeaderBar.getHeight() + mActionBarSize);
    }

    private void changeHeaderBackgroundHeight(float from, float to) {
        ViewPropertyAnimator.animate(mHeaderBackground).cancel();
        ValueAnimator a = ValueAnimator.ofFloat(from, to);
        a.setDuration(100);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float height = (float) animation.getAnimatedValue();
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mHeaderBackground.getLayoutParams();
                lp.height = (int) height;
                lp.topMargin = (int) (mHeaderBar.getHeight() - height);
                mHeaderBackground.requestLayout();
            }
        });
        a.start();
    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = mActivity.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }
}