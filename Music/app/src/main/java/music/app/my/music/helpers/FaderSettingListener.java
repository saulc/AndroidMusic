package music.app.my.music.helpers;

public interface FaderSettingListener {
        void mixSwitched(boolean b);

        void fadeSwitched(boolean b);

        void fadeInDurationChanged(int i);

        void fadeOutDurationChanged(int i);

        void fadeOutGapChanged(int i);

        void fadeInGapChanged(int i);

}
