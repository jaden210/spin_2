package crappydayproductions.com.spin_2;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SpinActivity extends AppCompatActivity {


    TextView textviewTurnTag;
    TextView textviewRpmTag;
    private static SensorManager mySensorManager;
    private boolean sersorrunning;
    int spinCounter = 0;
    int priorSpinCounter = 0;
    int timeout = 0;
    long highRpm = 0;
    long rpm;
    long finalRollValueTime;
    long rollValueTime;
    boolean checkOne;
    boolean checkTwo;
    boolean checkThree;
    boolean challengeComplete;
    //Variables for challanges
    String challengePick;
    int challengeNum = 1;
    int challengeSpins;
    int challengeTime;
    int challengeRpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        textviewTurnTag = (TextView) findViewById(R.id.turn_tag);
        textviewRpmTag = (TextView) findViewById(R.id.rpm_tag);

        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> mySensors = mySensorManager.getSensorList(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mySensorManager.registerListener(mySensorEventListener, mySensors.get(0), SensorManager.SENSOR_DELAY_FASTEST);
        sersorrunning = true;

        //check for challenge bundle. created in ChallengesScreen

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });

    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {

            int spinAngle = Integer.valueOf(Math.round(event.values[2]));

            if (spinAngle == 0 && !checkOne) {
                checkOne = true;
                onSensorChanged(event);
            }
            if (spinAngle == 1 && !checkTwo) {
                checkTwo = true;
                onSensorChanged(event);
            }
            if (spinAngle == -1 && !checkThree) {
                checkThree = true;
                onSensorChanged(event);
            }

            //see if a full rotation has been performed
            if (spinAngle == 0 && checkOne && checkTwo && checkThree && spinAngle != priorSpinCounter) {
                //up the spin count
                spinCounter++;

                //calculate RPM
                finalRollValueTime = System.currentTimeMillis();
                rpm = finalRollValueTime - rollValueTime;
                //set live RPM text
                textviewRpmTag.setText(String.valueOf(getRpm()) + " RPM");
                rollValueTime = System.currentTimeMillis();
                timeout = 0;
                //set highRpm
                if (getRpm() > highRpm){
                    highRpm = getRpm();
                }
                //Reset all the checks
                checkOne = false;
                checkTwo = false;
                checkThree = false;
            }

            //RPM Reset
            if(spinAngle == priorSpinCounter){
                timeout++;
                if (timeout == 250){
                    textviewRpmTag.setText("0 RPM");
                    timeout = 0;
                }
            }

            //set spin count
            priorSpinCounter = spinAngle;
            rpm = (rollValueTime - finalRollValueTime);
            textviewTurnTag.setText("SPINS: " + spinCounter);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    };

    private long getRpm() {
        if (rpm != 0) {
            return 60000 / rpm;
        } else {
            return 0;
        }
    }


    public void stop() {


        //this really needs to go to a popup inside of MainActivity.
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("highscoreRpm", highRpm);
        bundle.putLong("totalSpins", spinCounter);
        bundle.putBoolean("challenge",challengeComplete);
        intent.putExtras(bundle);
        startActivity(intent);
        //add spin counter and highest rpm to history

        /*
        StartScreen startScreen = new StartScreen();
        Bundle bundle = new Bundle();
        bundle.putString("highscoreRpm", String.valueOf(highRpm));
        bundle.putString("totalSpins", String.valueOf(spinCounter));
        startScreen.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .commit();
        */


        if (sersorrunning) {
            mySensorManager.unregisterListener(mySensorEventListener);
        }
        sersorrunning = false;
    }

    @Override
    public  void onBackPressed() {
        /*
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("highScoreRpm", highRpm);
        intent.putExtra("totalSpins", spinCounter);
        startActivity(intent);
        sersorrunning = false;
        finish();*/

        //can maybe just do this instead of duplicating code
        stop();

    }



}
