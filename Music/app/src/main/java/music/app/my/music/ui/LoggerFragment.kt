package music.app.my.music.ui

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_logger.logText
import music.app.my.music.R
import music.app.my.music.helpers.Logger

class LoggerFragment : Fragment() {

    companion object {
        fun newInstance() = LoggerFragment()
    }

    private lateinit var viewModel: LoggerViewModel
    private lateinit var logText : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v =  inflater.inflate(R.layout.fragment_logger, container, false);
        logText = v.findViewById(R.id.logText)
        return v;
    }
    public fun updateLog(log: ArrayList<String> ){
        var s = ""

        for( i in log.size-1 downTo 0  ) s += log.get(i) + "\n"
//        for(t in log) s += t + "\n"
        logText.text = s
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(LoggerViewModel::class.java)
        var logs = Logger.getLogs()
       var s = ""
        for(t in logs) s += t + "\n"
        logText.text = s
    }

}