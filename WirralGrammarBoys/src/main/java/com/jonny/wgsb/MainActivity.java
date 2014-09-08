package com.jonny.wgsb;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.jonny.wgsb.CommonUtilities.DISPLAY_MESSAGE_ACTION;

public class MainActivity extends ActionBarActivity {
    private Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        supportInvalidateOptionsMenu();
        id = getIntent().getIntExtra("id", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setup(savedInstanceState, id);
        } else {
            setupLegacy(savedInstanceState, id);
        }
    }

    @Override
    public void onNewIntent(Intent i) {
        super.onNewIntent(i);
        id = i.getIntExtra("id", 0);
        if (id != 0) {
            Bundle args = new Bundle();
            args.putInt("id", id);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                GCMFragmentSpecific GCMFragmentSpecific = new GCMFragmentSpecific();
                GCMFragmentSpecific.setArguments(args);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                        .replace(R.id.fragment_container, GCMFragmentSpecific, "GCM_SPECIFIC_FRAGMENT").commit();
            } else {
                GCMFragmentSpecificLegacy GCMFragmentSpecificLegacy = new GCMFragmentSpecificLegacy();
                GCMFragmentSpecificLegacy.setArguments(args);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                        .replace(R.id.fragment_container, GCMFragmentSpecificLegacy, "GCM_SPECIFIC_FRAGMENT").commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final NewsFragment news = (NewsFragment) getSupportFragmentManager().findFragmentByTag("NEWS_FRAGMENT");
            final TopicalFragment topical = (TopicalFragment) getSupportFragmentManager().findFragmentByTag("TOPICAL_FRAGMENT");
            final SettingsFragment settings = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT");
            if (news != null && news.isVisible()) {
                if (news.onBackPressed()) {
                    super.onBackPressed();
                }
            } else if (topical != null && topical.isVisible()) {
                if (topical.onBackPressed()) {
                    super.onBackPressed();
                }
            } else if (settings != null && settings.isVisible()) {
                if (settings.changed == 1) {
                    if (!settings.getRegistrationId().isEmpty()) {
                        settings.updateDetails();
                        settings.changed = 0;
                        super.onBackPressed();
                    } else {
                        RegisterFragment registerFragment = new RegisterFragment();
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                                .replace(R.id.fragment_container, registerFragment, "REGISTER_FRAGMENT").addToBackStack(null).commit();
                    }
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } else {
            onBackPressedLegacy();
        }
    }

    private void onBackPressedLegacy() {
        final NewsFragmentLegacy news = (NewsFragmentLegacy) getSupportFragmentManager().findFragmentByTag("NEWS_FRAGMENT");
        final TopicalFragmentLegacy topical = (TopicalFragmentLegacy) getSupportFragmentManager().findFragmentByTag("TOPICAL_FRAGMENT");
        final SettingsFragmentLegacy settings = (SettingsFragmentLegacy) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT");
        if (news != null && news.isVisible()) {
            if (news.onBackPressed()) {
                super.onBackPressed();
            }
        } else if (topical != null && topical.isVisible()) {
            if (topical.onBackPressed()) {
                super.onBackPressed();
            }
        } else if (settings != null && settings.isVisible()) {
            if (settings.changed == 1) {
                if (!settings.getRegistrationId().isEmpty()) {
                    settings.updateDetails();
                    settings.changed = 0;
                    super.onBackPressed();
                } else {
                    RegisterFragmentLegacy registerFragmentLegacy = new RegisterFragmentLegacy();
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                            .replace(R.id.fragment_container, registerFragmentLegacy, "REGISTER_FRAGMENT").addToBackStack(null).commit();
                }
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void setup(Bundle savedInstanceState, Integer id) {
        if (id != 0) {
            Bundle args = new Bundle();
            args.putInt("id", id);
            GCMFragmentSpecific GCMFragmentSpecific = new GCMFragmentSpecific();
            GCMFragmentSpecific.setArguments(args);
            if (findViewById(R.id.fragment_container) != null) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                        .replace(R.id.fragment_container, GCMFragmentSpecific, "GCM_SPECIFIC_FRAGMENT").commit();
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, GCMFragmentSpecific, "GCM_SPECIFIC_FRAGMENT").commit();
            }
        } else if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment, "MAIN_FRAGMENT").commit();
        }
    }

    private void setupLegacy(Bundle savedInstanceState, Integer id) {
        if (id != 0) {
            Bundle args = new Bundle();
            args.putInt("id", id);
            GCMFragmentSpecificLegacy GCMFragmentSpecificLegacy = new GCMFragmentSpecificLegacy();
            GCMFragmentSpecificLegacy.setArguments(args);
            if (findViewById(R.id.fragment_container) != null) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                        .replace(R.id.fragment_container, GCMFragmentSpecificLegacy, "GCM_SPECIFIC_FRAGMENT").commit();
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, GCMFragmentSpecificLegacy, "GCM_SPECIFIC_FRAGMENT").commit();
            }
        } else if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            MainFragmentLegacy mainFragment = new MainFragmentLegacy();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment, "MAIN_FRAGMENT").commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MainFragment extends Fragment {
        private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getActivity().invalidateOptionsMenu();
            }
        };
        Context context;
        DatabaseHandler dbhandler;
        ConnectionDetector cd;
        ListView listView;
        Button websiteButton, facebookButton, twitterButton;
        OnClickListener handler = new OnClickListener() {
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.website_btn:
                        Uri website = Uri.parse("http://wirralgrammarboys.com/");
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, website);
                        startActivity(websiteIntent);
                        break;
                    case R.id.facebook_btn:
                        Uri facebook = Uri.parse("https://www.facebook.com/WirralGSB");
                        Intent facebookIntent = new Intent(Intent.ACTION_VIEW, facebook);
                        startActivity(facebookIntent);
                        break;
                    case R.id.twitter_btn:
                        Uri twitter = Uri.parse("https://twitter.com/WGSB");
                        Intent twitterIntent = new Intent(Intent.ACTION_VIEW, twitter);
                        startActivity(twitterIntent);
                        break;
                }
            }
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            setupActionBar();
            dbhandler = DatabaseHandler.getInstance(getActivity());
            getOverflowMenu();
            getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
            ArrayList<MainDetails> arrayList = GetSearchResults();
            listView = (ListView) view.findViewById(R.id.main_list);
            websiteButton = (Button) view.findViewById(R.id.website_btn);
            facebookButton = (Button) view.findViewById(R.id.facebook_btn);
            twitterButton = (Button) view.findViewById(R.id.twitter_btn);
            websiteButton.setOnClickListener(handler);
            facebookButton.setOnClickListener(handler);
            twitterButton.setOnClickListener(handler);
            listView.setAdapter(new MainListBaseAdapter(this.getActivity(), arrayList));
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    switch (position) {
                        case 0:
                            NewsFragment newsFragment = new NewsFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                                    .replace(R.id.fragment_container, newsFragment, "NEWS_FRAGMENT").addToBackStack(null).commit();
                            break;
                        case 1:
                            TopicalFragment topicalFragment = new TopicalFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                                    .replace(R.id.fragment_container, topicalFragment, "TOPICAL_FRAGMENT").addToBackStack(null).commit();
                            break;
                        case 2:
                            ContentResolver cr = getActivity().getContentResolver();
                            Cursor cursor = cr.query(TimetableProvider.PERIODS_URI, new String[]{TimetableProvider.ID, TimetableProvider.DAY},
                                    TimetableProvider.DAY + "='set_up'", null, null);
                            File dir = new File(Environment.getExternalStorageDirectory(), "WGSB\backup");
                            File file = new File(dir, "backup.txt");
                            if (cursor.getCount() == 0 && file.exists()) {
                                cursor.close();
                                onCreateDialog();
                            } else {
                                cursor.close();
                                startActivity(new Intent(getActivity(), TimetableTabController.class));
                            }
                            break;
                        case 3:
                            CalendarFragment calendarFragment = new CalendarFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                                    .replace(R.id.fragment_container, calendarFragment, "CALENDAR_FRAGMENT").addToBackStack(null).commit();
                            break;
                    }
                }
            });
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
            getActivity().invalidateOptionsMenu();
            setupActionBar();
        }

        @Override
        public void onPause() {
            super.onPause();
            try {
                getActivity().unregisterReceiver(mHandleMessageReceiver);
            } catch (Exception e) {
                Log.e("UnRegister Receiver Error", "> " + e.getMessage());
            }
        }

        @Override
        public void onDetach() {
            super.onDestroy();
            try {
                getActivity().unregisterReceiver(mHandleMessageReceiver);
            } catch (Exception e) {
                Log.e("UnRegister Receiver Error", "> " + e.getMessage());
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.main, menu);
            final MenuItem item = menu.findItem(R.id.badge);
            MenuItemCompat.setActionView(item, R.layout.actionbar_badge_layout);
            View view = MenuItemCompat.getActionView(item);
            TextView notificationText = (TextView) view.findViewById(R.id.actionbar_notifcation_textview);
            Integer i = dbhandler.getUnreadNotificationsCount();
            if (i > 0) {
                notificationText.setText(i.toString());
            } else {
                notificationText.setVisibility((View.INVISIBLE));
            }
            final ImageView item2 = (ImageView) view.findViewById(R.id.badge_circle);
            OnClickListener handler = new OnClickListener() {
                public void onClick(View view) {
                    onOptionsItemSelected(item);
                }
            };
            item.getActionView().setOnClickListener(handler);
            item2.setOnClickListener(handler);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.settings) {
                SettingsFragment settingsFragment = new SettingsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                        .replace(R.id.fragment_container, settingsFragment, "SETTINGS_FRAGMENT").addToBackStack(null).commit();
                return true;
            } else if (item.getItemId() == R.id.badge) {
                GCMFragment GCMFragment = new GCMFragment();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                        .replace(R.id.fragment_container, GCMFragment, "GCM_FRAGMENT").addToBackStack(null).commit();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void setupActionBar() {
            setHasOptionsMenu(true);
            ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setIcon(R.drawable.banner);
            actionBar.setTitle(R.string.app_name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
                SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintColor(Color.parseColor("#FF004890"));
            }
        }

        @TargetApi(19)
        private void setTranslucentStatus(boolean on) {
            Window win = getActivity().getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }

        private Dialog onCreateDialog() {
            Dialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            AlertDialog alert;
            final CharSequence[] bool = {getString(R.string.yes), getString(R.string.no)};
            builder.setTitle(R.string.restore_deleted_backup);
            builder.setItems(bool, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        TimetableBackupRestore.restore(getActivity());
                    }
                    startActivity(new Intent(getActivity(), TimetableTabController.class));
                    dialog.cancel();
                }
            });
            alert = builder.create();
            dialog = alert;
            return dialog;
        }

        private void getOverflowMenu() {
            try {
                ViewConfiguration config = ViewConfiguration.get(getActivity());
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                if (menuKeyField != null) {
                    menuKeyField.setAccessible(true);
                    menuKeyField.setBoolean(config, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private ArrayList<MainDetails> GetSearchResults() {
            ArrayList<MainDetails> arrayList = new ArrayList<MainDetails>();

            MainDetails mainDetails = new MainDetails();
            mainDetails.setName(R.string.news);
            mainDetails.setImageNumber(1);
            arrayList.add(mainDetails);

            MainDetails mainDetails2 = new MainDetails();
            mainDetails2.setName(R.string.topical_information);
            mainDetails2.setImageNumber(2);
            arrayList.add(mainDetails2);

            MainDetails mainDetails3 = new MainDetails();
            mainDetails3.setName(R.string.timetable_name);
            mainDetails3.setImageNumber(3);
            arrayList.add(mainDetails3);

            MainDetails mainDetails4 = new MainDetails();
            mainDetails4.setName(R.string.calendar);
            mainDetails4.setImageNumber(4);
            arrayList.add(mainDetails4);

            return arrayList;
        }
    }

    public static class MainFragmentLegacy extends Fragment {
        private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        DatabaseHandler dbhandler;
        ListView listView;
        Button websiteButton, facebookButton, twitterButton;
        OnClickListener handler = new OnClickListener() {
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.website_btn:
                        Uri website = Uri.parse("http://wirralgrammarboys.com/");
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, website);
                        startActivity(websiteIntent);
                        break;
                    case R.id.facebook_btn:
                        Uri facebook = Uri.parse("https://www.facebook.com/WirralGSB");
                        Intent facebookIntent = new Intent(Intent.ACTION_VIEW, facebook);
                        startActivity(facebookIntent);
                        break;
                    case R.id.twitter_btn:
                        Uri twitter = Uri.parse("https://twitter.com/WGSB");
                        Intent twitterIntent = new Intent(Intent.ACTION_VIEW, twitter);
                        startActivity(twitterIntent);
                        break;
                }
            }
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            setupActionBar();
            dbhandler = DatabaseHandler.getInstance(getActivity());
            getOverflowMenu();
            getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
            ArrayList<MainDetails> arrayList = GetSearchResults();
            listView = (ListView) view.findViewById(R.id.main_list);
            websiteButton = (Button) view.findViewById(R.id.website_btn);
            facebookButton = (Button) view.findViewById(R.id.facebook_btn);
            twitterButton = (Button) view.findViewById(R.id.twitter_btn);
            websiteButton.setOnClickListener(handler);
            facebookButton.setOnClickListener(handler);
            twitterButton.setOnClickListener(handler);
            listView.setAdapter(new MainListBaseAdapter(this.getActivity(), arrayList));
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    switch (position) {
                        case 0:
                            NewsFragmentLegacy newsFragmentLegacy = new NewsFragmentLegacy();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                                    .replace(R.id.fragment_container, newsFragmentLegacy, "NEWS_FRAGMENT").addToBackStack(null).commit();
                            break;
                        case 1:
                            TopicalFragmentLegacy topicalFragmentLegacy = new TopicalFragmentLegacy();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                                    .replace(R.id.fragment_container, topicalFragmentLegacy, "TOPICAL_FRAGMENT").addToBackStack(null).commit();
                            break;
                        case 2:
                            ContentResolver cr = getActivity().getContentResolver();
                            Cursor cursor = cr.query(TimetableProvider.PERIODS_URI, new String[]{TimetableProvider.ID, TimetableProvider.DAY},
                                    TimetableProvider.DAY + "='set_up'", null, null);
                            File dir = new File(Environment.getExternalStorageDirectory(), "WGSB\backup");
                            File file = new File(dir, "backup.txt");
                            if (cursor.getCount() == 0 && file.exists()) {
                                cursor.close();
                                onCreateDialog();
                            } else {
                                cursor.close();
                                Intent tabs = new Intent(getActivity(), TimetableTabControllerLegacy.class);
                                startActivity(tabs);
                            }
                            break;
                        case 3:
                            CalendarFragmentLegacy calendarFragmentLegacy = new CalendarFragmentLegacy();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                                    .replace(R.id.fragment_container, calendarFragmentLegacy, "CALENDAR_FRAGMENT").addToBackStack(null).commit();
                            break;
                    }
                }
            });
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
            setupActionBar();
        }

        @Override
        public void onPause() {
            super.onPause();
            try {
                getActivity().unregisterReceiver(mHandleMessageReceiver);
            } catch (Exception e) {
                Log.e("UnRegister Receiver Error", "> " + e.getMessage());
            }
        }

        @Override
        public void onDetach() {
            super.onDestroy();
            try {
                getActivity().unregisterReceiver(mHandleMessageReceiver);
            } catch (Exception e) {
                Log.e("UnRegister Receiver Error", "> " + e.getMessage());
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.main, menu);
            final MenuItem item = menu.findItem(R.id.badge);
            MenuItemCompat.setActionView(item, R.layout.actionbar_badge_layout);
            View view = MenuItemCompat.getActionView(item);
            TextView notificationText = (TextView) view.findViewById(R.id.actionbar_notifcation_textview);
            Integer i = dbhandler.getUnreadNotificationsCount();
            if (i > 0) {
                notificationText.setText(i.toString());
            } else {
                notificationText.setVisibility((View.INVISIBLE));
            }
            final ImageView item2 = (ImageView) view.findViewById(R.id.badge_circle);
            OnClickListener handler = new OnClickListener() {
                public void onClick(View view) {
                    onOptionsItemSelected(item);
                }
            };
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onOptionsItemSelected(item);
                    return false;
                }
            });
            item2.setOnClickListener(handler);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.settings) {
                SettingsFragmentLegacy settingsFragmentLegacy = new SettingsFragmentLegacy();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                        .replace(R.id.fragment_container, settingsFragmentLegacy, "SETTINGS_FRAGMENT").addToBackStack(null).commit();
                return true;
            } else if (item.getItemId() == R.id.badge) {
                GCMFragmentLegacy GCMFragmentLegacy = new GCMFragmentLegacy();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.zoom_enter, 0, 0, R.anim.zoom_exit)
                        .replace(R.id.fragment_container, GCMFragmentLegacy, "GCM_FRAGMENT").addToBackStack(null).commit();
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }

        private void setupActionBar() {
            setHasOptionsMenu(true);
            final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setIcon(R.drawable.banner);
            actionBar.setTitle(R.string.app_name);
        }

        private Dialog onCreateDialog() {
            Dialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            AlertDialog alert;
            final CharSequence[] bool = {getString(R.string.yes), getString(R.string.no)};
            builder.setTitle(R.string.restore_deleted_backup);
            builder.setItems(bool, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        TimetableBackupRestore.restore(getActivity());
                    }
                    Intent setup = new Intent(getActivity(), TimetableTabControllerLegacy.class);
                    startActivity(setup);
                    dialog.cancel();
                }
            });
            alert = builder.create();
            dialog = alert;
            return dialog;
        }

        private void getOverflowMenu() {
            try {
                ViewConfiguration config = ViewConfiguration.get(getActivity());
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                if (menuKeyField != null) {
                    menuKeyField.setAccessible(true);
                    menuKeyField.setBoolean(config, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private ArrayList<MainDetails> GetSearchResults() {
            ArrayList<MainDetails> arrayList = new ArrayList<MainDetails>();

            MainDetails mainDetails = new MainDetails();
            mainDetails.setName(R.string.news);
            mainDetails.setImageNumber(1);
            arrayList.add(mainDetails);

            MainDetails mainDetails2 = new MainDetails();
            mainDetails2.setName(R.string.topical_information);
            mainDetails2.setImageNumber(2);
            arrayList.add(mainDetails2);

            MainDetails mainDetails3 = new MainDetails();
            mainDetails3.setName(R.string.timetable_name);
            mainDetails3.setImageNumber(3);
            arrayList.add(mainDetails3);

            MainDetails mainDetails4 = new MainDetails();
            mainDetails4.setName(R.string.calendar);
            mainDetails4.setImageNumber(4);
            arrayList.add(mainDetails4);

            return arrayList;
        }
    }
}