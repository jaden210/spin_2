package crappydayproductions.com.spin_2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements ChallengesScreen.OnFragmentInteractionListener, HistoryScreen.OnFragmentInteractionListener, StartScreen.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAnalytics mFirebaseAnalytics;
    Object currentScores;

    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor prefEditor;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("scores");


        //detect when the view is changed
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                currentScores = dataSnapshot.getValue();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);


        //for the popup
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            displayAlertDialog();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Leave blank
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 1:
                    return StartScreen.newInstance(position + 1);
                case 2:
                    return HistoryScreen.newInstance();
                case 0:
                    return ChallengesScreen.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "spin";
                case 1:
                    return "history";
                case 2:
                    return "challenges";
            }
            return null;
        }
    }

    //this is the popup that shows after you finish the game
    public void displayAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        final TextView spinCount = (TextView) alertLayout.findViewById(R.id.spinCount);
        final TextView rpmCount = (TextView) alertLayout.findViewById(R.id.rpmCount);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Long totalSpins = bundle.getLong("totalSpins");
        Long totalRpm = bundle.getLong("highscoreRpm");
        boolean challenge = bundle.getBoolean("challenge");
        spinCount.setText(Long.toString(totalSpins));
        rpmCount.setText(Long.toString(totalRpm));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (bundle.getLong("totalSpins") == 0) {
            alert.setTitle("Nice work");
        } else {
            if (challenge == true) {
                alert.setTitle("You Win");
            } else if (challenge == false) {
                alert.setTitle("You Lose...");
            }
        }

        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("back", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton("new challenge!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewPager.setCurrentItem(0);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

        //may not be the best place, but lets set the history here
        historyScorer();
    }

    //info dialog to start challenges. I put it here because I couldn't get it to work with an onclicklistener...
    public void onClick(View v) {
        AlertDialog alertBuilder = new AlertDialog.Builder(this)
                .create();
        alertBuilder.setTitle(R.string.challengeDialogTitle);
        alertBuilder.setMessage(MainActivity.this.getString(R.string.challengeDialogMessage));
        alertBuilder.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();
    }

    public void historyScorer() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Long totalSpins = bundle.getLong("totalSpins");
        Long totalRpm = bundle.getLong("highscoreRpm");
        Map<String, Object> scores = new HashMap<>();
        preferenceSettings = getSharedPreferences("score",PREFERENCE_MODE_PRIVATE);
        prefEditor = preferenceSettings.edit();


        if (preferenceSettings.getLong("rpmHistory", 0) == 0) {
            prefEditor.putLong("rpmHistory", totalRpm);
            prefEditor.putLong("spinHistory", totalSpins);
            prefEditor.apply();

            scores.put("totalSpin" , bundle.getLong("totalSpins"));
            scores.put("maxRpm", bundle.getLong("highscoreRpm"));
            scores.put("id", new BigDecimal(1).longValue());

            mDatabaseReference.updateChildren(scores);

        }else {
            long tempRpm = preferenceSettings.getLong("rpmHistory", 0);
            long tempSpins = preferenceSettings.getLong("spinHistory",0);
            prefEditor.putLong("spinHistory", tempSpins + totalSpins);
            if (tempRpm < totalRpm) {
                prefEditor.putLong("rpmHistory", totalRpm);

            }
            prefEditor.apply();
            Bundle logBundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "maxRpm");
            mFirebaseAnalytics.logEvent("last added", logBundle);

            }
        }


    }

