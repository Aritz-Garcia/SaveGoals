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

public class EditObjetivosActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Spinner spinnerCategoria;
    ArrayList<CustomItem> customList;
    Button btnEditar, btnVolverObjetivos;
    EditText etNombre, etFecha, etCantidad;
    TextView tvErrorCategoria, tvErrorFecha;
    AppDatabase db;
    SharedPreferences settingssp;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_objetivos);

        db = AppDatabase.getDatabase(this);

        id = getIntent().getIntExtra("id", 0);

        Objetivos objetivo = db.objetivosDao().findById(id);

        etNombre = findViewById(R.id.etEditNombreObjetivo);
        etFecha = findViewById(R.id.etEditFechaObjetivo);
        etCantidad = findViewById(R.id.etEditCantidadObjetivo);

        tvErrorCategoria = findViewById(R.id.tvEditErrorCategoriaObjetivo);
        tvErrorFecha = findViewById(R.id.tvEditErrorFechaObjetivo);

        btnEditar = findViewById(R.id.btnEditObjetivo);
        btnVolverObjetivos = findViewById(R.id.btnEditVolverObjetivos);

        spinnerCategoria = findViewById(R.id.spinnerEditCategoriaObjetivo);

        etFecha.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        btnVolverObjetivos.setOnClickListener(this);

        customList = getCustomList();
        CustomAdapter adapter = new CustomAdapter(this, customList);
        if (spinnerCategoria != null) {
            spinnerCategoria.setAdapter(adapter);
            spinnerCategoria.setOnItemSelectedListener(this);
        }

        cargarDatos(objetivo);

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        setDayNight();

    }

    private void cargarDatos(Objetivos objetivo) {
        etNombre.setText(objetivo.getNombre());
        etFecha.setText(objetivo.getFecha());
        etCantidad.setText(String.valueOf(objetivo.getCantidad()));
        spinnerCategoria.setSelection(getIdCategoria(objetivo.getCategoria()));
    }

    private ArrayList<CustomItem> getCustomList() {
        customList = new ArrayList<>();
        customList.add(new CustomItem("Seleccionar", 0));
        customList.add(new CustomItem("Viaje", R.drawable.avion));
        customList.add(new CustomItem("Ahorrar", R.drawable.hucha));
        customList.add(new CustomItem("Regalo", R.drawable.regalo));
        customList.add(new CustomItem("Compras", R.drawable.carrito));
        customList.add(new CustomItem("Clase", R.drawable.clase));
        customList.add(new CustomItem("Juego", R.drawable.mando));
        customList.add(new CustomItem("Otros", R.drawable.otros));
        return customList;
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnEditar.getId()) {
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

                        Objetivos objetivo = db.objetivosDao().findById(id);

                        int i = 0;

                        if (spinnerCategoria.getSelectedItemId() != getIdCategoria(objetivo.getCategoria())) {
                            // Cambiar categoria
                            objetivo.setCategoria(getCategoria((int) spinnerCategoria.getSelectedItemId()));
                            i++;
                        }

                        if (!objetivo.getNombre().equals(etNombre.getText().toString())) {
                            // Cambiar nombre
                            objetivo.setNombre(etNombre.getText().toString());
                            i++;
                        }

                        if (objetivo.getCantidad() != Float.parseFloat(etCantidad.getText().toString())) {
                            // Cambiar cantidad
                            objetivo.setCantidad(Float.parseFloat(etCantidad.getText().toString()));

                            // Objetivo Completado
                            if (objetivo.getAhorrado() >= objetivo.getCantidad()) {
                                objetivo.setCompletado(true);
                            } else {
                                objetivo.setCompletado(false);
                            }

                            i++;
                        }

                        if (!objetivo.getFecha().equals(etFecha.getText().toString())) {
                            // Cambiar fecha
                            objetivo.setFecha(etFecha.getText().toString());
                            i++;
                        }

                        if (i > 0) {
                            db.objetivosDao().update(objetivo.getId(), objetivo.getCategoria(), objetivo.getNombre(), objetivo.getFecha(), objetivo.getCantidad(), objetivo.getAhorrado(), objetivo.getCompletado());
                        }

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
            text.setError("Campo necesario");
            return false;
        }else if((!textua.matches("[a-zA-Z ]+\\.?"))){
            text.setError("Solo letras");
            return false;
        }else{
            return true;
        }
    }

    private boolean leerNumero(String textua, EditText text){
        if( textua.length()==0 ) {
            text.setError("Campo necesario");
            return false;
        }else if(Float.parseFloat(textua)<0) {
            text.setError("No puede ser un numero negativo");
            return false;
        }else if((!textua.matches("([0-9]*[.])?[0-9]+")) ){
            text.setError("Solo numeros");
            return false;
        }else if (textua.length()>6) {
            text.setError("El numero es muy grande");
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
            text.setText("Campo necesario");
            text.setVisibility(View.VISIBLE);
            return false;
        } else if (fecha.getDay() == today.getDay() && fecha.getMonth() == today.getMonth() && fecha.getYear() == today.getYear()) {
            text.setText("La fecha no puede ser la actual");
            text.setVisibility(View.VISIBLE);
            return false;
        } else if (fecha.before(today)) {
            text.setText("La fecha no puede ser posterior a la actual");
            text.setVisibility(View.VISIBLE);
            return false;
        } else {
            text.setVisibility(View.GONE);
            return true;
        }
    }

    private String getCategoria(int posicion) {
        switch (posicion) {
            case 1:
                return "Viaje";
            case 2:
                return "Ahorrar";
            case 3:
                return "Regalo";
            case 4:
                return "Compras";
            case 5:
                return "Clase";
            case 6:
                return "Juego";
            case 7:
                return "Otros";
            default:
                return "Seleccionar";
        }
    }

    private int getIdCategoria(String categoria) {
        switch (categoria) {
            case "Viaje":
                return 1;
            case "Ahorrar":
                return 2;
            case "Regalo":
                return 3;
            case "Compras":
                return 4;
            case "Clase":
                return 5;
            case "Juego":
                return 6;
            case "Otros":
                return 7;
            default:
                return 0;
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