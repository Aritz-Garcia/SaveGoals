package com.savegoals.savegoals;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;
import com.savegoals.savegoals.dialog.DatePickerFragment;
import com.savegoals.savegoals.formularios.CustomAdapter;
import com.savegoals.savegoals.formularios.CustomItem;

import java.util.ArrayList;
import java.util.Date;

public class AddObjetivosActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner spinnerCategoria;
    ArrayList<CustomItem> customList;
    Button btnGuardar, btnVolverObjetivos;
    EditText etNombre, etFecha, etCantidad;
    TextView tvErrorCategoria, tvErrorFecha;
    boolean rDatos;
    AppDatabase db;
    SharedPreferences settingssp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_objetivos);

        db = AppDatabase.getDatabase(this);

        rDatos = getIntent().getBooleanExtra("rDatos", false);

        etNombre = findViewById(R.id.etNombre);
        etFecha = findViewById(R.id.etFecha);
        etCantidad = findViewById(R.id.etCantidad);

        tvErrorCategoria = findViewById(R.id.tvErrorCategoria);
        tvErrorFecha = findViewById(R.id.tvErrorFecha);

        btnGuardar = findViewById(R.id.btnAddObjetivo);
        btnVolverObjetivos = findViewById(R.id.btnVolverObjetivos);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);

        etFecha.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnVolverObjetivos.setOnClickListener(this);

        customList = getCustomList();
        CustomAdapter adapter = new CustomAdapter(this, customList);
        if (spinnerCategoria != null) {
            spinnerCategoria.setAdapter(adapter);
            spinnerCategoria.setOnItemSelectedListener(this);
        }

        if (rDatos) {
            String fecha = getIntent().getStringExtra("fecha");
            String cantidad = getIntent().getStringExtra("cantidad");
            etFecha.setText(fecha);
            etCantidad.setText(cantidad);
        }

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        setDayNight();

    }

    private ArrayList<CustomItem> getCustomList() {
        customList = new ArrayList<>();
        customList.add(new CustomItem(getString(R.string.categoria_seleccionar), 0));
        customList.add(new CustomItem(getString(R.string.categoria_viaje), R.drawable.avion));
        customList.add(new CustomItem(getString(R.string.categoria_ahorrar), R.drawable.hucha));
        customList.add(new CustomItem(getString(R.string.categoria_regalo), R.drawable.regalo));
        customList.add(new CustomItem(getString(R.string.categoria_compras), R.drawable.carrito));
        customList.add(new CustomItem(getString(R.string.categoria_clase), R.drawable.clase));
        customList.add(new CustomItem(getString(R.string.categoria_juego), R.drawable.mando));
        customList.add(new CustomItem(getString(R.string.categoria_otros), R.drawable.otros));
        return customList;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {
                final String selectedDate = dosDigitos(dia) + "/" + dosDigitos(mes+1) + "/" + anio;
                etFecha.setText(selectedDate);
            }
        });
        newFragment.show(this.getSupportFragmentManager(), "datePicker");
    }

    private String dosDigitos(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnGuardar.getId()) {
            // Guardar en la base de datos
            guardarBaseDeDatos();
        } else if (v.getId() == etFecha.getId()) {
            showDatePickerDialog();
        } else if (v.getId() == btnVolverObjetivos.getId()) {
            // Volver a la pantalla de objetivos
            finish();
        }
    }

    private void guardarBaseDeDatos() {
        // Guardar en la base de datos
        if (spinnerCategoria.getSelectedItemId() != 0) {
            tvErrorCategoria.setVisibility(View.GONE);
            if (leerString(etNombre.getText().toString(), etNombre)) {
                if (leerNumero(etCantidad.getText().toString(), etCantidad)) {
                    if (leerFecha(etFecha.getText().toString(), tvErrorFecha)) {
                        Objetivos objetivo = new Objetivos();
                        objetivo.setCategoria((int) spinnerCategoria.getSelectedItemId());
                        objetivo.setNombre(etNombre.getText().toString());
                        objetivo.setCantidad(Float.parseFloat(etCantidad.getText().toString()));
                        objetivo.setFecha(etFecha.getText().toString());
                        objetivo.setAhorrado(0);
                        objetivo.setCompletado(false);
                        db.objetivosDao().insertAll(objetivo);

                        finish();
                    }
                }
            }
        } else {
            tvErrorCategoria.setVisibility(View.VISIBLE);
        }
    }

    private boolean leerString(String textua, EditText text){
        if( textua.length()==0 )  {
            text.setError(getString(R.string.necesario));
            return false;
        } else{
            return true;
        }
    }

    private boolean leerNumero(String textua, EditText text){
        if( textua.length()==0 ) {
            text.setError(getString(R.string.necesario));
            return false;
        }else if(Float.parseFloat(textua)<0) {
            text.setError(getString(R.string.numero_negativo));
            return false;
        }else if((!textua.matches("([0-9]*[.])?[0-9]+")) ){
            text.setError(getString(R.string.solo_numero));
            return false;
        }else if (textua.length()>6) {
            text.setError(getString(R.string.numero_grande));
            return false;
        }else{
            return true;
        }
    }

    private boolean leerFecha(String textua, TextView text){
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        Date fecha = new Date();
        if (textua.length() != 0) {
            String[] fechaSeparada = textua.split("/");
            fecha.setYear(Integer.parseInt(fechaSeparada[2]) - 1900);
            fecha.setMonth(Integer.parseInt(fechaSeparada[1]) - 1);
            fecha.setDate(Integer.parseInt(fechaSeparada[0]));
        }

        if( textua.length() == 0 ) {
            text.setText(getString(R.string.necesario));
            text.setVisibility(View.VISIBLE);
            return false;
        } else if (fecha.getDate() == today.getDate() && fecha.getMonth() == today.getMonth() && fecha.getYear() == today.getYear()) {
            text.setText(getString(R.string.error_misma_fecha));
            text.setVisibility(View.VISIBLE);
            return false;
        } else if (fecha.before(today)) {
            text.setText(getString(R.string.error_fecha_anterior));
            text.setVisibility(View.VISIBLE);
            return false;
        } else {
            text.setVisibility(View.GONE);
            return true;
        }
    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}