package com.savegoals.savegoals;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.text.DecimalFormat;

public class MiWidgetPorcentaje extends AppWidgetProvider {

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        eliminarCosas(context, appWidgetManager, appWidgetId);
        int objetivoIdSeleccionado = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("objetivo_" + appWidgetId, -1);
        updateAppWidgets(context, appWidgetManager, appWidgetId, objetivoIdSeleccionado);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Se ejecuta cada vez que se actualiza el widget
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Log.d("MiWidgetPorcentaje", "onUpdate called");
            int objetivoIdSeleccionado = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("objetivo_" + appWidgetId, -1);
            eliminarCosas(context, appWidgetManager, appWidgetId);
            updateAppWidgets(context, appWidgetManager, appWidgetId, objetivoIdSeleccionado);
        }
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        super.onReceive(context, intent);
//
//        if (intent.getAction() != null && intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
//            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
//            if (appWidgetIds != null) {
//                for (int appWidgetId : appWidgetIds) {
//                    int objetivoIdSeleccionado = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("objetivo_" + appWidgetId, -1);
//                    updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId, objetivoIdSeleccionado);
//                }
//            }
//        }
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction() != null && intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            boolean manual = intent.getBooleanExtra("manual", false);
            if (manual) {
                Log.d("MiWidgetPorcentaje", "onReceive called");
                int objetivoIdSeleccionado = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("objetivo_" + appWidgetId, -1);
                eliminarCosas(context, AppWidgetManager.getInstance(context), appWidgetId);
                updateAppWidgets(context, AppWidgetManager.getInstance(context), appWidgetId, objetivoIdSeleccionado);
            }
        }
    }

    public void eliminarCosas(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgets_layout_porcentaje);
        views.removeAllViews(R.id.ll_contenedor);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int appWidgetId, int objetivoIdSeleccionado) {

        if (objetivoIdSeleccionado != -1) {
            Log.d("MiWidgetPorcentaje", "updateAppWidget called");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgets_layout_porcentaje);

            AppDatabase appDatabase = AppDatabase.getDatabase(context);
            Objetivos objetivo = appDatabase.objetivosDao().findById(objetivoIdSeleccionado);
            RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_text);
            switch (objetivo.getCategoria()) {
                case 1:
                    text.setImageViewResource(R.id.iv_icono_widg, R.drawable.avion);
                    break;

                case 2:
                    text.setImageViewResource(R.id.iv_icono_widg, R.drawable.hucha);
                    break;

                case 3:
                    text.setImageViewResource(R.id.iv_icono_widg, R.drawable.regalo);
                    break;

                case 4:
                    text.setImageViewResource(R.id.iv_icono_widg, R.drawable.carrito);
                    break;

                case 5:
                    text.setImageViewResource(R.id.iv_icono_widg, R.drawable.clase);
                    break;

                case 6:
                    text.setImageViewResource(R.id.iv_icono_widg, R.drawable.mando);
                    break;

                case 7:
                    text.setImageViewResource(R.id.iv_icono_widg, R.drawable.otros);
                    break;

            }



            text.setTextViewText(R.id.tv_texto_widget, objetivo.getNombre());
            text.setTextViewText(R.id.tv_cant_widg, obtieneDosDecimales(objetivo.getAhorrado()) + "€ / " + obtieneDosDecimales(objetivo.getCantidad()) + "€");
            views.addView(R.id.ll_contenedor, text);

            int porcentajeCalc;

            if (objetivo.getAhorrado() == 0) {
                porcentajeCalc = 1;
            } else {
                porcentajeCalc = (int) ((objetivo.getAhorrado() * 100) / objetivo.getCantidad());
            }

            RemoteViews progressBar = null;

            if (porcentajeCalc < 50) {
                progressBar = new RemoteViews(context.getPackageName(), R.layout.widget_char_porcentaje_rojo);
            } else if (porcentajeCalc < 75) {
                progressBar = new RemoteViews(context.getPackageName(), R.layout.widget_char_porcentaje_amarillo);
            } else if (porcentajeCalc < 100) {
                progressBar = new RemoteViews(context.getPackageName(), R.layout.widget_char_porcentaje_verde);
            } else {
                progressBar = new RemoteViews(context.getPackageName(), R.layout.widget_char_porcentaje_completado);
            }

            progressBar.setProgressBar(R.id.pb_widget_porcentaje, 100, porcentajeCalc, false);

            progressBar.setTextViewText(R.id.tv_porcentaje_text, porcentajeCalc + "%");

            if (objetivo.getAhorrado() == 0) {
                porcentajeCalc = 0;
                progressBar.setProgressBar(R.id.pb_widget_porcentaje, 100, porcentajeCalc, false);
                progressBar.setTextViewText(R.id.tv_porcentaje_text, porcentajeCalc + "%");
            }

            views.addView(R.id.ll_contenedor, progressBar);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }
}
