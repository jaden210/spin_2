package crappydayproductions.com.spin_2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        //for the popup
        Intent intent = getIntent();
        Bundle bundle =  intent.getExtras();

        if (bundle != null) {
            boolean challenge = bundle.getBoolean("challenge");

            if (challenge == true) {
                displayAlertDialog();
            }else {
                displayAlertDialog();
            }

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
                case 1: return StartScreen.newInstance(position + 1);
                case 2: return HistoryScreen.newInstance();
                case 0: return ChallengesScreen.newInstance();
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
                    return "hi";
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
        Bundle bundle =  intent.getExtras();
        Long totalSpins = bundle.getLong("totalSpins");
        Long totalRpm = bundle.getLong("highscoreRpm");
        boolean challenge = bundle.getBoolean("challenge");
        spinCount.setText(Long.toString(totalSpins));
        rpmCount.setText(Long.toString(totalRpm));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setTitle("Login");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("back to the game", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("new challenge!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // launch challenges
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

        //may not be the best place, but lets set the history here
        historyScorer();
    }

    public void historyScorer() {
        Intent intent = getIntent();
        Bundle bundle =  intent.getExtras();
        Long totalSpins = bundle.getLong("totalSpins");
        Long totalRpm = bundle.getLong("highscoreRpm");

        preferenceSettings = getPreferences(PREFERENCE_MODE_PRIVATE);
        prefEditor = preferenceSettings.edit();

        if (preferenceSettings.getLong("rpmHistory", 0) == 0) {
            prefEditor.putLong("rpmHistory", totalRpm);
            prefEditor.putLong("spinHistory", totalSpins);
            prefEditor.apply();
        }else {
            long tempRpm = preferenceSettings.getLong("rpmHistory", 0);
            long tempSpins = preferenceSettings.getLong("spinHistory",0);
            prefEditor.putLong("spinHistory", tempSpins + totalSpins);
            if (tempRpm < totalRpm) {
                prefEditor.putLong("rpmHistory", totalRpm);

            }
            prefEditor.apply();
        }



    }
}