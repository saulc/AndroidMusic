package music.app.my.music.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

        dataText = v.findViewById(R.id.mixdata)
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

        var t :String = "Mix in: " + cvtdata(ig!!)
        t +=  " out: " + cvtdata(og!! )
        t += "   Fade in/out: " + fi + "/" + fo + " Secs"
        dataText?.setText(t)

    }
    fun setMixMode(mixOn: Boolean){
        if(mixOn){
            s2?.progress = 9
            s3?.progress = 10
            s0?.progress = s0!!.max
            s1?.progress = s1!!.max

        } else {

            s0?.progress = 6
            s1?.progress = 7
            s2?.progress = 3
            s3?.progress = 5
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

    private var dataText: TextView? = null
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