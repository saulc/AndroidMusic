package music.app.my.music.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Switch
import music.app.my.music.R
import music.app.my.music.helpers.FaderSettingListener

class MixFragment : Fragment() {

    companion object {

    fun newInstance() = MixFragment()
    }


    fun log(msg: String){
        Log.d("Music: Mix Fragment ",  msg)
    }
    private lateinit var viewModel: MixViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.mix_fragment, container, false)

        s0 = v!!.findViewById(R.id.fade0)
        s1 = v.findViewById(R.id.fade1)
        s2 = v.findViewById(R.id.fade2)
        s3 = v.findViewById(R.id.fade3)

        mixSwitch = v.findViewById(R.id.mixmodeSwitch)

        mixSwitch?.setOnCheckedChangeListener( object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                log("mix Switch clicked: " + isChecked)
                setMixMode(isChecked)
            }
        })


        iniBars()

        return v
    }

    fun setMixMode(mixOn: Boolean){
        if(mixOn){
            s0?.progress = s0!!.max
            s1?.progress = s1!!.max
            s2?.progress = 8
            s3?.progress = 9

        } else {

            s0?.progress = s0!!.min
            s1?.progress = s1!!.min
            s2?.progress = s0!!.min
            s3?.progress = s1!!.min
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(MixViewModel::class.java)

//        loadValues()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FaderSettingListener) {
            mListener = context
        } else {
            throw RuntimeException(
                    context.toString()
                            + " must implement FaderSettingListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private var mListener: FaderSettingListener? = null

    private var s0: SeekBar? = null
    private  var s1:SeekBar? = null
    private  var s2:SeekBar? = null
    private  var s3:SeekBar? = null
    private var mixSwitch: Switch? = null

    fun loadValues(){
         s0?.progress =   viewModel.fadeIn
         s1?.progress =   viewModel.fadeOut
        s2?.progress =   viewModel.fadeInGap
        s3?.progress =   viewModel.fadeOutGap
    }

    fun iniBars(){

        s0?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
//                viewModel.fadeIn = i
                mListener?.fadeInDurationChanged(i)
                log("updating Fade Setting")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                log("Seekbar started: ")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                log("Seekbar Stopped: ")
            }
        })


        s1?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
//                viewModel.fadeOut = i
                mListener?.fadeOutDurationChanged(i)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                log("Seekbar started: ")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                log("Seekbar Stopped: ")
            }
        })

        s2?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
//                viewModel.fadeInGap = i
                mListener?.fadeInGapChanged(i)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                log("Seekbar started: ")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                log("Seekbar Stopped: ")
            }
        })

        s3?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
//                viewModel.fadeOutGap = i
                mListener?.fadeOutGapChanged(i)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                log("Seekbar started: ")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                log("Seekbar Stopped: ")
            }
        })

    }
}