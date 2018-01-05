package eu.sheikhsoft.internetguard;

/**
 * Created by Sk Shamimul islam on 12/13/2017.
 */


import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class AdapterForwarding extends CursorAdapter {
    private int colProtocol;
    private int colDPort;
    private int colRAddr;
    private int colRPort;
    private int colRUid;

    public AdapterForwarding(Context context, Cursor cursor) {
        super(context, cursor, 0);
        colProtocol = cursor.getColumnIndex("protocol");
        colDPort = cursor.getColumnIndex("dport");
        colRAddr = cursor.getColumnIndex("raddr");
        colRPort = cursor.getColumnIndex("rport");
        colRUid = cursor.getColumnIndex("ruid");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.forward, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Get values
        int protocol = cursor.getInt(colProtocol);
        int dport = cursor.getInt(colDPort);
        String raddr = cursor.getString(colRAddr);
        int rport = cursor.getInt(colRPort);
        int ruid = cursor.getInt(colRUid);

        // Get views
        TextView tvProtocol = view.findViewById(R.id.tvProtocol);
        TextView tvDPort = view.findViewById(R.id.tvDPort);
        TextView tvRAddr = view.findViewById(R.id.tvRAddr);
        TextView tvRPort = view.findViewById(R.id.tvRPort);
        TextView tvRUid = view.findViewById(R.id.tvRUid);

        tvProtocol.setText(Util.getProtocolName(protocol, 0, false));
        tvDPort.setText(Integer.toString(dport));
        tvRAddr.setText(raddr);
        tvRPort.setText(Integer.toString(rport));
        tvRUid.setText(TextUtils.join(", ", Util.getApplicationNames(ruid, context)));
    }
}
