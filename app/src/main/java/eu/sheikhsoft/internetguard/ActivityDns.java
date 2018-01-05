package eu.sheikhsoft.internetguard;

/**
 * Created by Sk Shamimul islam on 12/13/2017.
 */


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class ActivityDns extends AppCompatActivity {
    private static final String TAG = "InterNetGuard.DNS";

    private AdapterDns adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resolving);

        getSupportActionBar().setTitle(R.string.setting_show_resolved);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lvDns = findViewById(R.id.lvDns);
        adapter = new AdapterDns(this, DatabaseHelper.getInstance(this).getDns());
        lvDns.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dns, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refresh();
                return true;

            case R.id.menu_cleanup:
                cleanup();
                return true;

            case R.id.menu_clear:
                Util.areYouSure(this, R.string.menu_clear, new Util.DoubtListener() {
                    @Override
                    public void onSure() {
                        clear();
                    }
                });
                return true;
        }
        return false;
    }

    private void refresh() {
        updateAdapter();
    }

    private void cleanup() {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Long doInBackground(Object... objects) {
                Log.i(TAG, "Cleanup DNS");
                DatabaseHelper.getInstance(ActivityDns.this).cleanupDns();
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                ServiceSinkhole.reload("DNS cleanup", ActivityDns.this, false);
                updateAdapter();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void clear() {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Long doInBackground(Object... objects) {
                Log.i(TAG, "Clear DNS");
                DatabaseHelper.getInstance(ActivityDns.this).clearDns();
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                ServiceSinkhole.reload("DNS clear", ActivityDns.this, false);
                updateAdapter();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void updateAdapter() {
        if (adapter != null)
            adapter.changeCursor(DatabaseHelper.getInstance(this).getDns());
    }

    @Override
    protected void onDestroy() {
        adapter = null;
        super.onDestroy();
    }
}
