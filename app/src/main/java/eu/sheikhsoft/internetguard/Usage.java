package eu.sheikhsoft.internetguard;

/**
 * Created by Sk Shamimul islam on 12/13/2017.
 */


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Usage {
    public long Time;
    public int Version;
    public int Protocol;
    public String DAddr;
    public int DPort;
    public int Uid;
    public long Sent;
    public long Received;

    private static DateFormat formatter = SimpleDateFormat.getDateTimeInstance();

    @Override
    public String toString() {
        return formatter.format(new Date(Time).getTime()) +
                " v" + Version + " p" + Protocol +
                " " + DAddr + "/" + DPort +
                " uid " + Uid +
                " out " + Sent + " in " + Received;
    }
}
