package music.app.my.music.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import music.app.my.music.R;
import music.app.my.music.player.MusicPlayer;
import music.app.my.music.player.myPlayer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MixxerFragment.MixxerListener} interface
 * to handle interaction events.
 * Use the {@link MixxerFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * shows status of 3 media players that make up
 * the crossfading music player service core.
 *
 * created for debugging volume sync bug.
 * sometimes the current track is faded out when it should be fading in...
 * or shadow tracks playing when should be paused. (early bug)...
 */
public class MixxerFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private void log(String s){
        Log.d(TAG, s);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private MixxerListener mListener;
    private MusicPlayer player = null;
    private MixxerDeck d0, d1, d2;
    private View bg;
    //layout componets

    public class MixxerDeck {
        public SeekBar posbar, volbar;
        public TextView mixText0, posText;
        public ImageButton play, pause, reset, next, prev;
        private int index;
        public int role;  // 0 current, 1 = next, 2 = aux.



        public MixxerDeck(final int i, View deckWrapper){
            bg = deckWrapper;
            if(i==1) bg.setBackgroundColor(Color.BLACK);
            index = i;
            volbar = deckWrapper.findViewById(R.id.volbar);
            volbar.setMax(100);
            posbar = deckWrapper.findViewById(R.id.posbar);
            posbar.setMax(100);
            mixText0 = deckWrapper.findViewById(R.id.mixxertext);
            mixText0.setText(getRoleText());
            posText = deckWrapper.findViewById(R.id.postionText);
            play = deckWrapper.findViewById(R.id.mcpl);
            pause = deckWrapper.findViewById(R.id.mcpu);
            reset = deckWrapper.findViewById(R.id.mcreset);
            next = deckWrapper.findViewById(R.id.mcnx);
            prev = deckWrapper.findViewById(R.id.mcpv);

            next.setBackgroundColor(Color.TRANSPARENT);
            prev.setBackgroundColor(Color.TRANSPARENT);

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    player.getmPlayers().get(index).playAndFadeIn();

                }
            });
            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    player.getmPlayers().get(index).pausePlayback();

                }
            });

            play.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    player.getmPlayers().get(index).resumePlayback();

                    return false;
                }
            });

            pause.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    player.getmPlayers().get(index).pausePlaybackNow();

                    return false;
                }
            });

        }

        private String getRoleText(){
            String t = "Current";
            if(role == 0 ) return  t;
            else if( role == 1 ) return "Next";

            return "AUX";
        }
        public void setRole(MusicPlayer mp){

            int c = mp.getCurrentPlayer();
            int n = mp.getNextPlayer();
            int a = mp.getAuxPlayer();

            if(index == c) role = 0;
            else if(index == n) role = 1;
            else if(index == a) role = 2;


           // log("Set role: c " + c + " n " + n + " a  " + a + " i " + index + " " + getRoleText());
        }

        public void updateState(MusicPlayer mp){
            player = mp;
            if(role == 0)  mixText0.setTextColor(Color.GREEN);
            else mixText0.setTextColor(Color.RED);

            mixText0.setText(getRoleText() + getInfo(mp));


            if(mp.getmPlayers().get(index).isPaused()) pause.setBackgroundColor(Color.GREEN);
            else pause.setBackgroundColor(Color.RED);

            if(mp.getmPlayers().get(index).isPlaying()) play.setBackgroundColor(Color.GREEN);
            else play.setBackgroundColor(Color.RED);

            if(mp.getmPlayers().get(index).isPrepared()) reset.setBackgroundColor(Color.GREEN);
            else reset.setBackgroundColor(Color.RED);



        }
        public void update(myPlayer p){

            volbar.setProgress( (int)(p.getVolumeValue()*100) );
            posText.setText( " pos: " + ( p.getCurrentPosition() / 1000 ) );
            if(p.isPrepared() && !p.isPaused()) {

                double d = p.getDuration();
                double e = p.getCurrentPosition();
                double f = (e / d * 100);
                //log(" d: " + d + " pos: " + e + " pro: " + f);
                posbar.setProgress((int)f);
            }

        }

        public String getInfo(MusicPlayer p){
            int i = index;
              String  info =   " id: " + p.getmPlayers().get(i).getAudioSessionId() + System.lineSeparator()
                        + " Playing: " + p.getmPlayers().get(i).isPlaying() + System.lineSeparator()
                        + " Paused: " + p.getmPlayers().get(i).isPaused() + System.lineSeparator()
                        + " Dur: " + ( p.getmPlayers().get(i).getDuration() / 1000 )        //convert to seconds
                        + " pos: " + ( p.getmPlayers().get(i).getCurrentPosition() / 1000 );

              //  items.add(info);
                log(info);
            return info;
        }
    };

    private void setupDeck(int index, View deckWrapper){
        log("Setup deck: " + index);
        if(index ==0) d0 = new MixxerDeck(index, deckWrapper);
        else if(index == 1) d1 = new MixxerDeck(index, deckWrapper);
        else if(index == 2) d2 = new MixxerDeck(index, deckWrapper);
    }

    public MixxerFragment() {
        // Required empty public constructor
    }
    public static MixxerFragment newInstance( ) {
        MixxerFragment fragment = new MixxerFragment();
        return fragment;
    }

    // TODO: Rename and change types and number of parameters
    public static MixxerFragment newInstance(String param1, String param2) {
        MixxerFragment fragment = new MixxerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy(){
        mListener.onMixxerDestroyed();
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mixxer_layout, container, false);
        log("Mixxer fragment view created");

        setupDeck(0, view.findViewById(R.id.deck0));
        setupDeck(1, view.findViewById(R.id.deck1));
        setupDeck(2, view.findViewById(R.id.deck2));

        mListener.onMixxerCreated();
        return view;
    }

    public void updateMP(MusicPlayer player){

        d0.setRole(player);
        d1.setRole(player);
        d2.setRole(player);
        d0.updateState(player);
        d1.updateState(player);
        d2.updateState(player);

    }

    public void updateMixxerPlayer(MusicPlayer player){
        log("Updating Mixxer Player...");
        d0.update(player.getmPlayers().get(0));
        d1.update(player.getmPlayers().get(1));
        d2.update(player.getmPlayers().get(2));

    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MixxerListener) {
            mListener = (MixxerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface MixxerListener {
        // TODO: Update argument type and name
        public void onMixxerCreated();
        public void onMixxerDestroyed();

    }

}
