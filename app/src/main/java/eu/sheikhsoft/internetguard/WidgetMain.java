package eu.sheikhsoft.internetguard;

/**
 * Created by Sk Shamimul islam on 12/13/2017.
 */


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetMain extends AppWidgetProvider {
    private static final String TAG = "InterNetGuard.Widget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        update(appWidgetIds, appWidgetManager, context);
    }

    private static void update(int[] appWidgetIds, AppWidgetManager appWidgetManager, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = prefs.getBoolean("enabled", false);

        try {
            try {
                Intent intent = new Intent(enabled ? WidgetAdmin.INTENT_OFF : WidgetAdmin.INTENT_ON);
                intent.setPackage(context.getPackageName());
                PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                for (int id : appWidgetIds) {
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetmain);
                    views.setOnClickPendingIntent(R.id.ivEnabled, pi);
                    views.setImageViewResource(R.id.ivEnabled, enabled ? R.drawable.ic_security_color_24dp : R.drawable.ic_security_white_24dp_60);
                    appWidgetManager.updateAppWidget(id, views);
                }
            } catch (Throwable ex) {
                Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            }
        } catch (Throwable ex) {
            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
        }
    }

    public static void updateWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WidgetMain.class));
        update(appWidgetIds, appWidgetManager, context);
    }
}
