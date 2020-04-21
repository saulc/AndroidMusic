package music.app.my.music.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import music.app.my.music.R

class MixFragment : Fragment() {

    companion object {

    fun newInstance() = MixFragment()
    }

    private lateinit var viewModel: MixViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mix_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MixViewModel::class.java)

    }

    fun setFadeVal(v: Int, par: Int){
        when(par){

        }
    }

}