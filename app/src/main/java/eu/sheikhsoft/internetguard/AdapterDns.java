package eu.sheikhsoft.internetguard;

/**
 * Created by Sk Shamimul islam on 12/13/2017.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdapterDns extends CursorAdapter {
    private int colorExpired;

    private int colTime;
    private int colQName;
    private int colAName;
    private int colResource;
    private int colTTL;

    public AdapterDns(Context context, Cursor cursor) {
        super(context, cursor, 0);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (prefs.getBoolean("dark_theme", false))
            colorExpired = Color.argb(128, Color.red(Color.DKGRAY), Color.green(Color.DKGRAY), Color.blue(Color.DKGRAY));
        else
            colorExpired = Color.argb(128, Color.red(Color.LTGRAY), Color.green(Color.LTGRAY), Color.blue(Color.LTGRAY));

        colTime = cursor.getColumnIndex("time");
        colQName = cursor.getColumnIndex("qname");
        colAName = cursor.getColumnIndex("aname");
        colResource = cursor.getColumnIndex("resource");
        colTTL = cursor.getColumnIndex("ttl");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.dns, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Get values
        long time = cursor.getLong(colTime);
        String qname = cursor.getString(colQName);
        String aname = cursor.getString(colAName);
        String resource = cursor.getString(colResource);
        int ttl = cursor.getInt(colTTL);

        long now = new Date().getTime();
        boolean expired = (time + ttl < now);
        view.setBackgroundColor(expired ? colorExpired : Color.TRANSPARENT);

        // Get views
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvQName = view.findViewById(R.id.tvQName);
        TextView tvAName = view.findViewById(R.id.tvAName);
        TextView tvResource = view.findViewById(R.id.tvResource);
        TextView tvTTL = view.findViewById(R.id.tvTTL);

        // Set values
        tvTime.setText(new SimpleDateFormat("dd HH:mm").format(time));
        tvQName.setText(qname);
        tvAName.setText(aname);
        tvResource.setText(resource);
        tvTTL.setText("+" + Integer.toString(ttl / 1000));
    }
}
