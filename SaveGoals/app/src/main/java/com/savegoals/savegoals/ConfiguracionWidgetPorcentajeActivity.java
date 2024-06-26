package com.savegoals.savegoals;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionWidgetPorcentajeActivity extends AppCompatActivity implements View.OnClickListener {

    int appWidgetId;
    SharedPreferences settingssp;
    Button btnObjetivo, btnSalir;
    TextView tvNoObjetivos, tvTituloWidget;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_widget_porcentaje);

        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        List<Objetivos> objetivosSinCompletar = appDatabase.objetivosDao().getAllNotCompleted();

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);

        btnObjetivo = findViewById(R.id.btnGuardarObjWidg);
        btnSalir = findViewById(R.id.btnSalirObjWidg);
        tvNoObjetivos = findViewById(R.id.tv_noObjWidget);
        tvTituloWidget = findViewById(R.id.tv_titulo_widget_1);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (objetivosSinCompletar.size() > 0) {
            spinner = findViewById(R.id.sp_objetivos);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getLista());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            btnSalir.setVisibility(View.GONE);
            tvNoObjetivos.setVisibility(View.GONE);
            btnObjetivo.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            tvTituloWidget.setVisibility(View.VISIBLE);
        } else {
            btnSalir.setVisibility(View.VISIBLE);
            tvNoObjetivos.setVisibility(View.VISIBLE);
            btnObjetivo.setVisibility(View.GONE);
            tvTituloWidget.setVisibility(View.GONE);
        }

        btnObjetivo.setOnClickListener(this);
        btnSalir.setOnClickListener(this);

    }

    private ArrayList<String> getLista() {
        ArrayList<String> listaObjetivos = new ArrayList<>();
        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        List<Objetivos> objetivosSinCompletar = appDatabase.objetivosDao().getAllNotCompleted();

        for (int i = 0; i < objetivosSinCompletar.size(); i++) {
            Objetivos objetivo = objetivosSinCompletar.get(i);
            listaObjetivos.add(objetivo.getNombre());
        }

        return listaObjetivos;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnObjetivo.getId()) {
            AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
            List<Objetivos> objetivosSinCompletar = appDatabase.objetivosDao().getAllNotCompleted();

            Objetivos objetivo = objetivosSinCompletar.get(spinner.getSelectedItemPosition());
            settingssp.edit().putInt("objetivo_" + appWidgetId, objetivo.getId()).apply();

            Intent resultValue = new Intent(getApplicationContext(), MiWidgetPorcentaje.class);
            resultValue.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            resultValue.putExtra("manual", true);
            getApplicationContext().sendBroadcast(resultValue);
            setResult(RESULT_OK, resultValue);

            finish();
        } else if (v.getId() == btnSalir.getId()) {
            Intent resultValue = new Intent(getApplicationContext(), MiWidgetPorcentaje.class);
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(Activity.RESULT_CANCELED, resultValue);

            finish();

        }

    }
}