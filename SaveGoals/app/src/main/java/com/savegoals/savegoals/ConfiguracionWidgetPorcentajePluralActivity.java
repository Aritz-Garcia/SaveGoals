package com.savegoals.savegoals;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionWidgetPorcentajePluralActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    int appWidgetId;
    SharedPreferences settingssp;
    Button btnObjetivo, btnSalir;
    TextView tvNoObjetivos, tvTituloWidget;
    Spinner spinner1, spinner2;
    List<String> listaObj = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_widget_porcentaje_plural);

        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        List<Objetivos> objetivosSinCompletar = appDatabase.objetivosDao().getAllNotCompleted();

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);

        btnObjetivo = findViewById(R.id.btnGuardarObPluWidg);
        btnSalir = findViewById(R.id.btnSalirObjPluWidg);
        tvNoObjetivos = findViewById(R.id.tv_noObjPluWidget);
        tvTituloWidget = findViewById(R.id.tv_titulo_widget_2);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (objetivosSinCompletar.size() > 1) {
            spinner1 = findViewById(R.id.sp_objetivosPlu1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getLista());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner1.setAdapter(adapter);

            spinner2 = findViewById(R.id.sp_objetivosPlu2);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getLista());
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);

            btnSalir.setVisibility(View.GONE);
            tvNoObjetivos.setVisibility(View.GONE);
            btnObjetivo.setVisibility(View.VISIBLE);
            spinner1.setVisibility(View.VISIBLE);
            spinner2.setVisibility(View.VISIBLE);
            tvTituloWidget.setVisibility(View.VISIBLE);

            if (spinner1.getSelectedItemPosition() == 0) {
                btnObjetivo.setEnabled(false);
                spinner2.setEnabled(false);
            } else {
                spinner2.setEnabled(true);
            }

            if (spinner2.getSelectedItemPosition() == 0) {
                btnObjetivo.setEnabled(false);
            } else {
                btnObjetivo.setEnabled(true);
            }

            spinner1.setOnItemSelectedListener(this);
            spinner2.setOnItemSelectedListener(this);

        } else {
            btnSalir.setVisibility(View.VISIBLE);
            tvNoObjetivos.setVisibility(View.VISIBLE);
            btnObjetivo.setVisibility(View.GONE);
            tvTituloWidget.setVisibility(View.GONE);
        }

        btnObjetivo.setOnClickListener(this);
        btnSalir.setOnClickListener(this);

        // Meter cosas en listaObj
        listaObj.add("Selecciona un objetivo");
        listaObj.add("Selecciona un objetivo");

    }

    private ArrayList<String> getLista() {
        ArrayList<String> listaObjetivos = new ArrayList<>();
        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        List<Objetivos> objetivosSinCompletar = appDatabase.objetivosDao().getAllNotCompleted();
        listaObjetivos.add("Selecciona un objetivo");

        for (int i = 0; i < objetivosSinCompletar.size(); i++) {
            Objetivos objetivo = objetivosSinCompletar.get(i);
            listaObjetivos.add(objetivo.getNombre());
        }

        for (int i = 0; i < listaObj.size(); i++) {
            if (!listaObj.get(i).equals("Selecciona un objetivo")) {
                for (int j = 0; j < listaObjetivos.size(); j++) {
                    if (listaObj.get(i).equals(listaObjetivos.get(j))) {
                        listaObjetivos.remove(j);
                    }
                }
            }
        }

        return listaObjetivos;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnObjetivo.getId()) {
            AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
            List<Objetivos> objetivosSinCompletar = appDatabase.objetivosDao().getAllNotCompleted();

            // Guardar numeros
            String ids = "";
            for (int i = 0; i < listaObj.size(); i++) {
                for (int j = 0; j < objetivosSinCompletar.size(); j++) {
                    if (listaObj.get(i).equals(objetivosSinCompletar.get(j).getNombre())) {
                        ids += objetivosSinCompletar.get(j).getId() + ";";
                    }
                }
            }
            settingssp.edit().putString("objetivoPlural_" + appWidgetId, ids).apply();

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getId() == spinner1.getId()) {
            if (spinner1.getSelectedItemPosition() != 0) {
                spinner2.setEnabled(true);
            } else {
                spinner2.setEnabled(false);
                btnObjetivo.setEnabled(false);
            }

            listaObj.set(0, spinner1.getSelectedItem().toString());

            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getLista());
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);

        } else if (adapterView.getId() == spinner2.getId()) {
            if (spinner2.getSelectedItemPosition() != 0) {
                btnObjetivo.setEnabled(true);
            } else {
                btnObjetivo.setEnabled(false);
            }
            listaObj.set(1, spinner2.getSelectedItem().toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}