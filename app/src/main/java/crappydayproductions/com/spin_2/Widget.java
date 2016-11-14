package crappydayproductions.com.spin_2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Created by jaden on 11/14/16.
 */

public class Widget extends AppWidgetProvider {
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SharedPreferences prefs = context.getSharedPreferences("score", PREFERENCE_MODE_PRIVATE);
        Long number = prefs.getLong("spinHistory", 0);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.simple_widget);

        remoteViews.setTextViewText(R.id.textView, number.toString());

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity( context,
                0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

}
