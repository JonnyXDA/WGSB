package com.jonny.wgsb.material.fragments;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.jonny.wgsb.material.R;
import com.jonny.wgsb.material.activities.MainActivity;
import com.jonny.wgsb.material.db.DatabaseHandler;
import com.jonny.wgsb.material.ui.helper.Notifications;
import com.jonny.wgsb.material.util.CompatUtils;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class GCMFragmentSpecific extends Fragment implements ObservableScrollViewCallbacks {
    private static GCMFragmentSpecific instance = null;
    private MainActivity mActivity;
    private FrameLayout.LayoutParams originalParams;
    private FrameLayout frame;
    private View mImageHolder, mHeader, mHeaderBar, mHeaderBackground;
    private ObservableScrollView mScrollView;
    private int mActionBarSize, mFlexibleSpaceImageHeight, mIntersectionHeight, mPrevScrollY;
    private boolean mGapFilled;

    public GCMFragmentSpecific() {
    }

    public static GCMFragmentSpecific getInstance() {
        if (instance == null) instance = new GCMFragmentSpecific();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gcm_specific, container, false);
        setRetainInstance(true);
        final Integer id = getArguments().getInt("id", 1);
        mActivity = ((MainActivity) getActivity());
        mActivity.mToolbar.setVisibility(Toolbar.GONE);
        mActivity.getDelegate().getSupportActionBar().hide();
        frame = (FrameLayout) getActivity().findViewById(R.id.fragment_container);
        originalParams = (FrameLayout.LayoutParams) frame.getLayoutParams();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frame.setLayoutParams(params);
        ((MainActivity) getActivity()).setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        TextView tDisplay = (TextView) view.findViewById(R.id.tDisplay);
        TextView dDisplay = (TextView) view.findViewById(R.id.dDisplay);
        TextView mDisplay = (TextView) view.findViewById(R.id.mDisplay);
        DatabaseHandler dbhandler = DatabaseHandler.getInstance(getActivity());
        Notifications notifications = dbhandler.getNotification(id);
        final String title = notifications.title;
        final String date = notifications.date;
        final String message = notifications.message;
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
        if (title == null) tDisplay.setVisibility(View.INVISIBLE);
        else tDisplay.setText(title);
        if (date == null) dDisplay.setVisibility(View.INVISIBLE);
        else dDisplay.setText(date);
        if (message != null) mDisplay.append(message + "\n");
        dbhandler.updateNotifications(new Notifications(id, 1));
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
        mActivity.mToolbar.setVisibility(Toolbar.VISIBLE);
        frame.setLayoutParams(originalParams);
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