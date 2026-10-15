package com.example.sideralclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import java.util.Calendar;
import java.util.TimeZone;

public class SideralWidget extends AppWidgetProvider {
    // Sierra de los Padres
    private static final double LONGITUDE = -57.54;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            
            // Hora Local
            Calendar cal = Calendar.getInstance();
            String hl = String.format("%02d:%02d HL", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            
            // Hora Sideral Local y ST - calculo simplificado
            double jd = getJulianDate(cal);
            double gmst = getGMST(jd);
            double lst = gmst + LONGITUDE / 15.0;
            lst = (lst % 24 + 24) % 24;
            
            int h = (int) lst;
            int m = (int) ((lst - h) * 60);
            int s = (int) (((lst - h) * 60 - m) * 60);
            
            String hs = String.format("%02d:%02d:%02d HS", h, m, s);
            String st = String.format("%02d:%02d:%02d ST", h, m, s);

            views.setTextViewText(R.id.hl, hl);
            views.setTextViewText(R.id.hs, hs);
            views.setTextViewText(R.id.st, st);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private double getJulianDate(Calendar cal) {
        int Y = cal.get(Calendar.YEAR);
        int M = cal.get(Calendar.MONTH) + 1;
        int D = cal.get(Calendar.DAY_OF_MONTH);
        double H = cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE)/60.0 + cal.get(Calendar.SECOND)/3600.0;
        if (M <= 2) { Y--; M += 12; }
        int A = Y/100; int B = 2 - A + A/4;
        return Math.floor(365.25*(Y+4716)) + Math.floor(30.6001*(M+1)) + D + B - 1524.5 + H/24.0;
    }

    private double getGMST(double jd) {
        double T = (jd - 2451545.0) / 36525.0;
        double gmst = 280.46061837 + 360.98564736629 * (jd - 2451545.0) + 0.000387933 * T * T - T*T*T/38710000.0;
        gmst = (gmst % 360 + 360) % 360;
        return gmst / 15.0;
    }
    }
