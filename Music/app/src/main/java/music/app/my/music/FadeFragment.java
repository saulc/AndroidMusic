package music.app.my.music;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import music.app.my.music.helpers.FaderSettingListener;

/*
    original fade fragment for adjusting cross fade settings.
    moving to kotlin from now on to infinity?
    but not about to rewrite this shit again....again..
 */
public class FadeFragment extends Fragment {

    private FaderSettingListener mListener;

    private SeekBar s0, s1, s2, s3;
    private Switch mixSwitch, fadeSwitch;

    public FadeFragment() {
        // Required empty public constructor
    }

    public static FadeFragment newInstance() {
        FadeFragment fragment = new FadeFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//        this is a test oh shit, some is fired.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_fade, container, false);
        s0 = v.findViewById(R.id.fade0);
        s1 = v.findViewById(R.id.fade1);
        s2 = v.findViewById(R.id.fade2);
        s3 = v.findViewById(R.id.fade3);
        //all out of 100 %. for 'easier' math.
        s0.setMax(10);
        s1.setMax(10);
        s2.setMax(10);
        s3.setMax(10);

        //fade in
        s0.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mListener.fadeInDurationChanged(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //fade out
        s1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mListener.fadeOutDurationChanged(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //fade in gap
        s2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mListener.fadeInGapChanged(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //fade out gap
        s3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mListener.fadeOutGapChanged(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mixSwitch = v.findViewById(R.id.mixmodeSwitch);
//        fadeSwitch = v.findViewById(R.id.crossfadeSwitch);

        mixSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mListener.mixSwitched(b);
            }
        });
//
//        fadeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                mListener.fadeSwitched(b);
//            }
//        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FaderSettingListener) {
            mListener = (FaderSettingListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FaderListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
