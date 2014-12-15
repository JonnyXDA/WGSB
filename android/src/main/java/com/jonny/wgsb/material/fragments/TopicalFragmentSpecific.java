package com.jonny.wgsb.material.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jonny.wgsb.material.MainActivity;
import com.jonny.wgsb.material.R;
import com.jonny.wgsb.material.db.DatabaseHandler;
import com.jonny.wgsb.material.ui.helper.Topical;

public class TopicalFragmentSpecific extends Fragment {
    DatabaseHandler dbhandler;
    TextView titleTextView, storyTextView;

    public TopicalFragmentSpecific() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topical_specific, container, false);
        setRetainInstance(true);
        int topicalId = getArguments().getInt("id");
        titleTextView = (TextView) view.findViewById(R.id.titleArticleTopical);
        storyTextView = (TextView) view.findViewById(R.id.storyArticleTopical);
        dbhandler = DatabaseHandler.getInstance(getActivity());
        Topical articleTopical = dbhandler.getTopical(topicalId);
        String articleTitle = articleTopical.title;
        String articleStory = articleTopical.story;
        Spanned htmlSpan;
        htmlSpan = Html.fromHtml(articleStory);
        MainActivity mActivity = ((MainActivity) getActivity());
        mActivity.setupActionBar(articleTitle);
        mActivity.mDrawerToggle.setDrawerIndicatorEnabled(false);
        mActivity.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mActivity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        titleTextView.setText(articleTitle);
        storyTextView.setText(htmlSpan);
        storyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override
    public void onDetach() {
        ((MainActivity) getActivity()).mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        super.onDetach();
    }
}
