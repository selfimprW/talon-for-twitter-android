package com.klinker.android.talon.ui.drawer_activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.klinker.android.talon.R;
import com.klinker.android.talon.adapters.PeopleCursorAdapter;
import com.klinker.android.talon.settings.AppSettings;
import com.klinker.android.talon.sq_lite.FavoriteUsersDataSource;
import com.klinker.android.talon.ui.LoginActivity;
import com.klinker.android.talon.utils.Utils;

import org.lucasr.smoothie.AsyncListView;

/**
 * Created by luke on 11/27/13.
 */
public class FavoriteUsersActivity extends DrawerActivity {

    FavoriteUsersDataSource dataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        settings = new AppSettings(this);

        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setUpTheme();

        actionBar = getActionBar();
        actionBar.setTitle(getResources().getString(R.string.favorite_users));

        setContentView(R.layout.retweets_activity);

        if (!settings.isTwitterLoggedIn) {
            Intent login = new Intent(context, LoginActivity.class);
            startActivity(login);
            finish();
        }

        listView = (AsyncListView) findViewById(R.id.listView);

        View viewHeader = getLayoutInflater().inflate(R.layout.ab_header, null);
        listView.addHeaderView(viewHeader, null, false);

        if (DrawerActivity.translucent) {
            View footer = new View(context);
            footer.setOnClickListener(null);
            footer.setOnLongClickListener(null);
            ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, Utils.getNavBarHeight(context));
            footer.setLayoutParams(params);
            listView.addFooterView(footer);
            listView.setFooterDividersEnabled(false);

            View view = new View(context);
            view.setOnClickListener(null);
            view.setOnLongClickListener(null);
            ListView.LayoutParams params2 = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, Utils.getStatusBarHeight(context));
            view.setLayoutParams(params2);
            listView.addHeaderView(view);
            listView.setFooterDividersEnabled(false);
        }

        setUpDrawer(5, getResources().getString(R.string.favorite_users));

        new GetFavUsers().execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        /*try {
            dataSource.open();
        } catch (Exception e) {
            // not initialized
        }*/

        new GetFavUsers().execute();
    }

    class GetFavUsers extends AsyncTask<String, Void, Cursor> {

        protected Cursor doInBackground(String... urls) {
            try {
                dataSource = new FavoriteUsersDataSource(context);
                dataSource.open();

                return dataSource.getCursor(sharedPrefs.getInt("current_account", 1));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Cursor cursor) {

            Log.v("fav_users", cursor.getCount() + "");

            listView.setAdapter(new PeopleCursorAdapter(context, cursor));
            listView.setVisibility(View.VISIBLE);

            LinearLayout spinner = (LinearLayout) findViewById(R.id.list_progress);
            spinner.setVisibility(View.GONE);
        }
    }

}