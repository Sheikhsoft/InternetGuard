package eu.sheikhsoft.internetguard;
/**
 * Created by Sk Shamimul islam on 12/13/2017.
 */


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.List;

public class ActivityForwarding extends AppCompatActivity {
    private boolean running;
    private ListView lvForwarding;
    private AdapterForwarding adapter;
    private AlertDialog dialog = null;

    private DatabaseHelper.ForwardChangedListener listener = new DatabaseHelper.ForwardChangedListener() {
        @Override
        public void onChanged() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null)
                        adapter.changeCursor(DatabaseHelper.getInstance(ActivityForwarding.this).getForwarding());
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forwarding);
        running = true;

        getSupportActionBar().setTitle(R.string.setting_forwarding);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lvForwarding = findViewById(R.id.lvForwarding);
        adapter = new AdapterForwarding(this, DatabaseHelper.getInstance(this).getForwarding());
        lvForwarding.setAdapter(adapter);

        lvForwarding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                final int protocol = cursor.getInt(cursor.getColumnIndex("protocol"));
                final int dport = cursor.getInt(cursor.getColumnIndex("dport"));
                final String raddr = cursor.getString(cursor.getColumnIndex("raddr"));
                final int rport = cursor.getInt(cursor.getColumnIndex("rport"));

                PopupMenu popup = new PopupMenu(ActivityForwarding.this, view);
                popup.inflate(R.menu.forward);
                popup.getMenu().findItem(R.id.menu_port).setTitle(
                        Util.getProtocolName(protocol, 0, false) + " " +
                                dport + " > " + raddr + "/" + rport);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.menu_delete) {
                            DatabaseHelper.getInstance(ActivityForwarding.this).deleteForward(protocol, dport);
                            ServiceSinkhole.reload("forwarding", ActivityForwarding.this, false);
                            adapter = new AdapterForwarding(ActivityForwarding.this,
                                    DatabaseHelper.getInstance(ActivityForwarding.this).getForwarding());
                            lvForwarding.setAdapter(adapter);
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHelper.getInstance(this).addForwardChangedListener(listener);
        if (adapter != null)
            adapter.changeCursor(DatabaseHelper.getInstance(ActivityForwarding.this).getForwarding());
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseHelper.getInstance(this).removeForwardChangedListener(listener);
    }

    @Override
    protected void onDestroy() {
        running = false;
        adapter = null;
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forwarding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                LayoutInflater inflater = LayoutInflater.from(this);
                View view = inflater.inflate(R.layout.forwardadd, null, false);
                final Spinner spProtocol = view.findViewById(R.id.spProtocol);
                final EditText etDPort = view.findViewById(R.id.etDPort);
                final EditText etRAddr = view.findViewById(R.id.etRAddr);
                final EditText etRPort = view.findViewById(R.id.etRPort);
                final ProgressBar pbRuid = view.findViewById(R.id.pbRUid);
                final Spinner spRuid = view.findViewById(R.id.spRUid);

                final AsyncTask task = new AsyncTask<Object, Object, List<Rule>>() {
                    @Override
                    protected void onPreExecute() {
                        pbRuid.setVisibility(View.VISIBLE);
                        spRuid.setVisibility(View.GONE);
                    }

                    @Override
                    protected List<Rule> doInBackground(Object... objects) {
                        return Rule.getRules(true, ActivityForwarding.this);
                    }

                    @Override
                    protected void onPostExecute(List<Rule> rules) {
                        ArrayAdapter spinnerArrayAdapter =
                                new ArrayAdapter(ActivityForwarding.this,
                                        android.R.layout.simple_spinner_item, rules);
                        spRuid.setAdapter(spinnerArrayAdapter);
                        pbRuid.setVisibility(View.GONE);
                        spRuid.setVisibility(View.VISIBLE);
                    }
                };
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                dialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int pos = spProtocol.getSelectedItemPosition();
                                    String[] values = getResources().getStringArray(R.array.protocolValues);
                                    final int protocol = Integer.valueOf(values[pos]);
                                    final int dport = Integer.parseInt(etDPort.getText().toString());
                                    final String raddr = etRAddr.getText().toString();
                                    final int rport = Integer.parseInt(etRPort.getText().toString());
                                    final int ruid = ((Rule) spRuid.getSelectedItem()).uid;

                                    InetAddress iraddr = InetAddress.getByName(raddr);
                                    if (rport < 1024 && (iraddr.isLoopbackAddress() || iraddr.isAnyLocalAddress()))
                                        throw new IllegalArgumentException("Port forwarding to privileged port on local address not possible");

                                    new AsyncTask<Object, Object, Throwable>() {
                                        @Override
                                        protected Throwable doInBackground(Object... objects) {
                                            try {
                                                DatabaseHelper.getInstance(ActivityForwarding.this)
                                                        .addForward(protocol, dport, raddr, rport, ruid);
                                                return null;
                                            } catch (Throwable ex) {
                                                return ex;
                                            }
                                        }

                                        @Override
                                        protected void onPostExecute(Throwable ex) {
                                            if (running)
                                                if (ex == null) {
                                                    ServiceSinkhole.reload("forwarding", ActivityForwarding.this, false);
                                                    adapter = new AdapterForwarding(ActivityForwarding.this,
                                                            DatabaseHelper.getInstance(ActivityForwarding.this).getForwarding());
                                                    lvForwarding.setAdapter(adapter);
                                                } else
                                                    Toast.makeText(ActivityForwarding.this, ex.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } catch (Throwable ex) {
                                    Toast.makeText(ActivityForwarding.this, ex.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                task.cancel(false);
                                dialog.dismiss();
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                dialog = null;
                            }
                        })
                        .create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
