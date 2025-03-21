package music.app.my.music.ui

import android.content.Context
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
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
        s4 = v.findViewById(R.id.fade4)

        dataText = v.findViewById(R.id.mixdata)
        mixSwitch = v.findViewById(R.id.mixmodeSwitch)

        mixSwitch?.setOnCheckedChangeListener( object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                log("mix Switch clicked: " + isChecked)
                setMixMode(isChecked)
            }
        })

        fadein = v.findViewById(R.id.f1)
        fadeingap = v.findViewById(R.id.f3)
        fadeout = v.findViewById(R.id.f2)
        fadeoutgap = v.findViewById(R.id.f4)
        crossfade = v.findViewById(R.id.f5)



        iniBars()
        updateData()
        return v
    }

    fun cvtdata(v : Int): String{
        var t = ""
        if(v == 8) t = "11% "
        else if(v == 9) t = "22% "
        else if(v == 10) t = "33% "
        else t =  v.toString() + " seconds."
        return t
    }
    fun updateData(){
        val ig = s2?.progress
        val og = s3?.progress
        val fi = s0?.progress
        val fo = s1?.progress
        val cf = s4?.progress

        var t :String = "Mix in: " + cvtdata(ig!!)
        t +=  " out: " + cvtdata(og!! )
        t += "\n   Fade in/out: " + fi + "/" + fo + " Secs"
        dataText?.setText(t)

        fadein?.text = "Fade in: " + fi
        fadeingap?.text = "Fade in Gap: " + ig
        fadeout?.text = "Fade Out: " + fo
        fadeoutgap?.text = "Fade Out Gap: " + og
        crossfade?.text = "Crossfade: " + cf

    }
    fun setMixMode(mixOn: Boolean){
        if(mixOn){
            s2?.progress = 8
            s3?.progress = 9
            s0?.progress = 7
            s1?.progress = s1!!.max

            s4?.progress = 3

        } else {

            s0?.progress = 4
            s1?.progress = 7
            s2?.progress = 7
            s3?.progress = 7

            s4?.progress = 7
        }

    }
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
////        viewModel = ViewModelProviders.of(this).get(MixViewModel::class.java)
//
////        loadValues()
//    }

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

    private var fadein: TextView? = null
    private var fadeout: TextView? = null
    private var fadeingap: TextView? = null
    private var fadeoutgap: TextView? = null
    private var crossfade: TextView? = null

    private var dataText: TextView? = null
    private var s0: SeekBar? = null
    private  var s1:SeekBar? = null
    private  var s2:SeekBar? = null
    private  var s3:SeekBar? = null
    private  var s4:SeekBar? = null
    private var mixSwitch: Switch? = null

    fun loadValues(){
         s0?.progress =   viewModel.fadeIn
         s1?.progress =   viewModel.fadeOut
        s2?.progress =   viewModel.fadeInGap
        s3?.progress =   viewModel.fadeOutGap
        s4?.progress =   viewModel.crossFade
    }

    fun iniBars(){

        s0?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
//                viewModel.fadeIn = i
                mListener?.fadeInDurationChanged(i)
                updateData()
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
                updateData()
                log("updating Fade Setting.")
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
                updateData()
                log("updating Fade Setting..")
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
                updateData()
                log("updating Fade Setting...")
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