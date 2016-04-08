package crappydayproductions.com.spin_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChallengesScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChallengesScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengesScreen extends Fragment {

    private OnFragmentInteractionListener mListener;
    TextView testView;
    TextView textviewPitch;
    TextView textviewRoll;



    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    int counter = 0;
    int priorcounter = 0;

    int cCount;
    int i1;
    TextView textviewTurnTag;
    TextView textviewRpmTag;
    private static SensorManager mySensorManager;
    private boolean sersorrunning;

    private Animation rotation;
    int spinCounter = 0;
    int priorSpinCounter = 0;
    int timeout = 0;
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

    public ChallengesScreen() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment ChallengesScreen.
     */
    public static ChallengesScreen newInstance() {
        ChallengesScreen fragment = new ChallengesScreen();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_challenges_screen, container, false);
        testView = (TextView) myView.findViewById(R.id.testtext);


        return inflater.inflate(R.layout.fragment_challenges_screen, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mySensorManager = (SensorManager) getActivity().getSystemService (Context.SENSOR_SERVICE);
            List<Sensor> mySensors = mySensorManager.getSensorList(Sensor.TYPE_GAME_ROTATION_VECTOR);

            mySensorManager.registerListener(mySensorEventListener, mySensors.get(0), SensorManager.SENSOR_DELAY_FASTEST);
            sersorrunning = true;
            Log.v("Running", "Started Spin");
        }else if (sersorrunning) {
            mySensorManager.unregisterListener(mySensorEventListener);
            sersorrunning = false;
        }
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
                //Reset all the checks
                checkOne = false;
                checkTwo = false;
                checkThree = false;
            }



            //set spin count
            priorSpinCounter = spinAngle;
            if (spinCounter == 2) {
                Random rnum = new Random();
                i1 = rnum.nextInt(5000 - 500) + 500;
                spinWheel();
                mySensorManager.unregisterListener(mySensorEventListener);
                spinCounter = 0;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    };












    public void spinWheel () {

        Button dialView = (Button) getActivity().findViewById(R.id.dial);
        dialView.animate().rotation(i1).setInterpolator(new DecelerateInterpolator()).setDuration(8000).start();

        //start a new challenge

    }







    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (sersorrunning) {
            mySensorManager.unregisterListener(mySensorEventListener);

        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
