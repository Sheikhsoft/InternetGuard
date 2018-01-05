package eu.sheikhsoft.internetguard;


/**
 * Created by Sk Shamimul islam on 12/13/2017.
 */


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;

public class ActivityForwardApproval extends Activity {
    private static final String TAG = "InterNetGuard.Forward";
    private static final String ACTION_START_PORT_FORWARD = "eu.sheikhsoft.internetguard.START_PORT_FORWARD";
    private static final String ACTION_STOP_PORT_FORWARD = "eu.sheikhsoft.internetguard.STOP_PORT_FORWARD";

    static {
        try {
            System.loadLibrary("netguard");
        } catch (UnsatisfiedLinkError ignored) {
            System.exit(1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forwardapproval);

        final int protocol = getIntent().getIntExtra("protocol", 0);
        final int dport = getIntent().getIntExtra("dport", 0);
        String addr = getIntent().getStringExtra("raddr");
        final int rport = getIntent().getIntExtra("rport", 0);
        final int ruid = getIntent().getIntExtra("ruid", 0);
        final String raddr = (addr == null ? "127.0.0.1" : addr);

        try {
            InetAddress iraddr = InetAddress.getByName(raddr);
            if (rport < 1024 && (iraddr.isLoopbackAddress() || iraddr.isAnyLocalAddress()))
                throw new IllegalArgumentException("Port forwarding to privileged port on local address not possible");
        } catch (Throwable ex) {
            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            finish();
        }

        String pname;
        if (protocol == 6)
            pname = getString(R.string.menu_protocol_tcp);
        else if (protocol == 17)
            pname = getString(R.string.menu_protocol_udp);
        else
            pname = Integer.toString(protocol);

        TextView tvForward = findViewById(R.id.tvForward);
        if (ACTION_START_PORT_FORWARD.equals(getIntent().getAction()))
            tvForward.setText(getString(R.string.msg_start_forward,
                    pname, dport, raddr, rport,
                    TextUtils.join(", ", Util.getApplicationNames(ruid, this))));
        else
            tvForward.setText(getString(R.string.msg_stop_forward, pname, dport));

        Button btnOk = findViewById(R.id.btnOk);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ACTION_START_PORT_FORWARD.equals(getIntent().getAction())) {
/*
am start -a eu.sheikhsoft.internetguard.START_PORT_FORWARD \
-n eu.sheikhsoft.internetguard/eu.faircode.internetguard.ActivityForwardApproval \
--ei protocol 17 \
--ei dport 53 \
--es raddr 8.8.4.4 \
--ei rport 53 \
--ei ruid 9999 \
--user 0
*/
                    Log.i(TAG, "Start forwarding protocol " + protocol + " port " + dport + " to " + raddr + "/" + rport + " uid " + ruid);
                    DatabaseHelper dh = DatabaseHelper.getInstance(ActivityForwardApproval.this);
                    dh.deleteForward(protocol, dport);
                    dh.addForward(protocol, dport, raddr, rport, ruid);

                } else if (ACTION_STOP_PORT_FORWARD.equals(getIntent().getAction())) {
/*
am start -a eu.sheikhsoft.internetguard.STOP_PORT_FORWARD \
-n eu.sheikhsoft.internetguard/eu.faircode.internetguard.ActivityForwardApproval \
--ei protocol 17 \
--ei dport 53 \
--user 0
*/
                    Log.i(TAG, "Stop forwarding protocol " + protocol + " port " + dport);
                    DatabaseHelper.getInstance(ActivityForwardApproval.this).deleteForward(protocol, dport);
                }

                ServiceSinkhole.reload("forwarding", ActivityForwardApproval.this, false);

                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
