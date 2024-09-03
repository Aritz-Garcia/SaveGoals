package com.savegoals.savegoals;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.PendingIntent;
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

public class MiWidgetPorcentajePlural extends AppWidgetProvider {

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        eliminarCosas(context, appWidgetManager, appWidgetId);
        String objetivosIdSeleccionados = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("objetivoPlural_" + appWidgetId, "-1");
        updateAppWidgets(context, appWidgetManager, appWidgetId, objetivosIdSeleccionados);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Se ejecuta cada vez que se actualiza el widget
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Log.d("MiWidgetPorcentajePlural", "onUpdate called");
            String objetivosIdSeleccionados = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("objetivoPlural_" + appWidgetId, "-1");
            eliminarCosas(context, appWidgetManager, appWidgetId);
            updateAppWidgets(context, appWidgetManager, appWidgetId, objetivosIdSeleccionados);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction() != null && intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            boolean manual = intent.getBooleanExtra("manual", false);
            if (manual) {
                Log.d("MiWidgetPorcentajePlural", "onReceive called");
                String objetivosIdSeleccionados = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getString("objetivoPlural_" + appWidgetId, "-1");
                eliminarCosas(context, AppWidgetManager.getInstance(context), appWidgetId);
                updateAppWidgets(context, AppWidgetManager.getInstance(context), appWidgetId, objetivosIdSeleccionados);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit().remove("objetivoPlural_" + appWidgetId).apply();
            Log.d("MiWidgetPorcentajePlural", "onDeleted called");
        }
    }

    public void eliminarCosas(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgets_layout_porcentaje);
        views.removeAllViews(R.id.ll_contenedor);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String objetivosIdSeleccionados) {

        if (!objetivosIdSeleccionados.equals("-1")) {
            Log.d("MiWidgetPorcentajePlural", "updateAppWidget called");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgets_layout_porcentaje);

            String [] objetivosIdSeleccionadosArray = objetivosIdSeleccionados.split(";");
            AppDatabase appDatabase = AppDatabase.getDatabase(context);
            Objetivos objetivo1 = appDatabase.objetivosDao().findById(Integer.parseInt(objetivosIdSeleccionadosArray[0]));
            Objetivos objetivo2 = appDatabase.objetivosDao().findById(Integer.parseInt(objetivosIdSeleccionadosArray[1]));

            if (objetivo1 != null && objetivo2 != null) {
                if (!objetivo1.getArchivado() && !objetivo2.getArchivado()) {
                    RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_text);
                    if (objetivo1.getCategoria() != objetivo2.getCategoria()) {
                        text.setImageViewResource(R.id.iv_icono_widg, R.drawable.otros);
                    } else {
                        switch (objetivo1.getCategoria()) {
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
                    }

                    text.setTextViewText(R.id.tv_texto_widget, objetivo1.getNombre() + " // " + objetivo2.getNombre());
                    float totalAhorrado = objetivo1.getAhorrado() + objetivo2.getAhorrado();
                    float totalCantidad = objetivo1.getCantidad() + objetivo2.getCantidad();
                    text.setTextViewText(R.id.tv_cant_widg, obtieneDosDecimales(totalAhorrado) + "€ / " + obtieneDosDecimales(totalCantidad) + "€");
                    views.addView(R.id.ll_contenedor, text);

                    int porcentajeCalc;

                    if (totalAhorrado == 0) {
                        porcentajeCalc = 1;
                    } else {
                        porcentajeCalc = (int) ((totalAhorrado * 100) / totalCantidad);
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

                    if (totalAhorrado == 0) {
                        porcentajeCalc = 0;
                        progressBar.setProgressBar(R.id.pb_widget_porcentaje, 100, porcentajeCalc, false);
                        progressBar.setTextViewText(R.id.tv_porcentaje_text, porcentajeCalc + "%");
                    }

                    views.addView(R.id.ll_contenedor, progressBar);

                    // PendingIntent para abrir la app al inicio
                    Intent intent = new Intent(context, MenuActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE);
                    views.setOnClickPendingIntent(R.id.ll_contenedor, pendingIntent);
                } else {
                    RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_sin_nada);
                    text.setTextViewText(R.id.tvEmoticono_widget_sin_nada, context.getString(R.string.widget_emoticono_sin_nada));
                    text.setTextViewText(R.id.tv_texto_widget_sin_nada, context.getString(R.string.widget_texto_sin_nada_plural));
                    text.setTextViewText(R.id.tv_texto_widget_sin_nada_2, context.getString(R.string.widget_texto_sin_nada_2));
                    views.addView(R.id.ll_contenedor, text);
                }

            } else {
                RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_sin_nada);
                text.setTextViewText(R.id.tvEmoticono_widget_sin_nada, context.getString(R.string.widget_emoticono_sin_nada));
                text.setTextViewText(R.id.tv_texto_widget_sin_nada, context.getString(R.string.widget_texto_sin_nada_plural));
                text.setTextViewText(R.id.tv_texto_widget_sin_nada_2, context.getString(R.string.widget_texto_sin_nada_2));
                views.addView(R.id.ll_contenedor, text);

            }

            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

    }

    private static String obtieneDosDecimales(float value){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }
}
