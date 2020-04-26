package music.app.my.music.ui

import android.arch.lifecycle.ViewModel

class MixViewModel : ViewModel() {
//    fade vaules in mili seconds 1/1000
    var fadeIn : Int = 7000
    var fadeInGap : Int = 9000
    var fadeOut : Int = 7000
    var fadeOutGap : Int = 9000

    var fadeOn : Boolean = true
    var mixOn : Boolean = true

}