package music.app.my.music.helpers;

import android.util.Log;

import java.util.ArrayList;

public class Logger {

    public interface LogCallback{
        void updateLogs(ArrayList<String> log);
    }
    private static LogCallback mlistener = null;
    private static ArrayList<String> logs = new ArrayList<>();;

    public static ArrayList<String> getLogs(){
        return logs;
    }

    public static void setListener(LogCallback l){
        mlistener = l;
    }
    public static void log(String tag, String s){
        Log.d(tag, s);
//        if(logs == null)
//            logs = new ArrayList<>();
        logs.add(tag + " :" + s);
        if(mlistener != null) mlistener.updateLogs(logs);
    }

}
