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
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;
import com.savegoals.savegoals.dialog.DatePickerFragment;
import com.savegoals.savegoals.dialog.ErrorDateDialog;
import com.savegoals.savegoals.formularios.CustomAdapter;
import com.savegoals.savegoals.formularios.CustomItem;

import java.util.ArrayList;
import java.util.Date;

public class EditEntradasActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ErrorDateDialog.ErrorDateDialogListener {

    Spinner spinnerCategoria;
    ArrayList<CustomItem> customList;
    Button btnGuardar, btnVolverEntradas;
    EditText etNombre, etFecha, etCantidad;
    TextView tvErrorCategoria, tvErrorFecha;
    Switch swRestar;
    AppDatabase db;
    SharedPreferences settingssp;
    int idO, idE;
    FloatingActionButton btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entradas);

        db = AppDatabase.getDatabase(this);

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        setDayNight();

        idO = getIntent().getIntExtra("idO", 0);
        idE = getIntent().getIntExtra("idE", 0);

        Entradas entradas = db.entradasDao().findByIds(idO, idE);

        etNombre = findViewById(R.id.etEditNombreEntradas);
        etFecha = findViewById(R.id.etEditFechaEntradas);
        etCantidad = findViewById(R.id.etEditCantidadEntradas);

        tvErrorCategoria = findViewById(R.id.tvEditErrorCategoriaEntradas);
        tvErrorFecha = findViewById(R.id.tvEditErrorFechaEntradas);

        swRestar = findViewById(R.id.swRestarEdit);

        btnGuardar = findViewById(R.id.btnSaveEntrada);
        btnVolverEntradas = findViewById(R.id.btnVolverEntradasFragment);

        spinnerCategoria = findViewById(R.id.spinnerEditCategoriaEntradas);

        btnEliminar = findViewById(R.id.btnEliminarEntrada);

        etFecha.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnVolverEntradas.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);

        customList = getCustomList();
        CustomAdapter adapter = new CustomAdapter(this, customList);
        if (spinnerCategoria != null) {
            spinnerCategoria.setAdapter(adapter);
            spinnerCategoria.setOnItemSelectedListener(this);
        }

        etNombre.setText(entradas.getNombre());
        etFecha.setText(entradas.getFecha());

        boolean negativo = String.valueOf(entradas.getCantidad()).contains("-");
        if (negativo) {
            swRestar.setChecked(true);
            String[] cantidad = String.valueOf(entradas.getCantidad()).split("-");
            etCantidad.setText(cantidad[1]);
        } else {
            swRestar.setChecked(false);
            etCantidad.setText(String.valueOf(entradas.getCantidad()));
        }
        spinnerCategoria.setSelection(entradas.getCategoria());
    }

    private ArrayList<CustomItem> getCustomList() {
        customList = new ArrayList<>();
        customList.add(new CustomItem(getString(R.string.categoria_seleccionar), 0));
        customList.add(new CustomItem(getString(R.string.categoria_cartera), R.drawable.cartera));
        customList.add(new CustomItem(getString(R.string.categoria_hucha), R.drawable.hucha));
        customList.add(new CustomItem(getString(R.string.categoria_trabajo), R.drawable.martillo));
        customList.add(new CustomItem(getString(R.string.categoria_regalo), R.drawable.regalo));
        customList.add(new CustomItem(getString(R.string.categoria_compras), R.drawable.carrito));
        customList.add(new CustomItem(getString(R.string.categoria_clase), R.drawable.clase));
        customList.add(new CustomItem(getString(R.string.categoria_otros), R.drawable.otros));
        return customList;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == etFecha.getId()) {
            showDatePickerDialog();
        } else if (v.getId() == btnGuardar.getId()) {
            // Guardar en la base de datos los cambios
            String fechaObjetivos = db.objetivosDao().findById(idO).getFecha();

            Date fecha = new Date();
            if (etFecha.getText().toString().length() != 0) {
                String[] fechaSeparada = etFecha.getText().toString().split("/");
                fecha.setYear(Integer.parseInt(fechaSeparada[2]) - 1900);
                fecha.setMonth(Integer.parseInt(fechaSeparada[1]) - 1);
                fecha.setDate(Integer.parseInt(fechaSeparada[0]));
            }

            Date objetivos = new Date();
            if (etFecha.getText().toString().length() != 0) {
                String[] fechaSeparada = fechaObjetivos.split("/");
                objetivos.setYear(Integer.parseInt(fechaSeparada[2]) - 1900);
                objetivos.setMonth(Integer.parseInt(fechaSeparada[1]) - 1);
                objetivos.setDate(Integer.parseInt(fechaSeparada[0]));
                if (objetivos.before(fecha)) {
                    // Dialogo fecha objetivo termianda
                    ErrorDateDialog errorDateDialog = new ErrorDateDialog();
                    errorDateDialog.show(getSupportFragmentManager(), "error date dialog");
                } else {
                    guardarBaseDeDatos();
                }
            } else {
                guardarBaseDeDatos();
            }

        } else if (v.getId() == btnVolverEntradas.getId()) {
            // volver a la pantalla de entradas
            finish();
        } else if (v.getId() == btnEliminar.getId()) {
            // Eliminar entrada
            eliminarEntrada();
        }
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

    private void guardarBaseDeDatos() {
        // Guardar en la base de datos
        if (spinnerCategoria.getSelectedItemId() != 0) {
            tvErrorCategoria.setVisibility(View.GONE);
            if (leerString(etNombre.getText().toString(), etNombre)) {
                if (leerNumero(etCantidad.getText().toString(), etCantidad)) {
                    if (leerFecha(etFecha.getText().toString(), tvErrorFecha)) {
                        // DATOS CORRECTOS
                        int i = 0;
                        Entradas entradas = db.entradasDao().findByIds(idO, idE);
                        Objetivos objetivos = db.objetivosDao().findById(idO);
                        if (spinnerCategoria.getSelectedItemId() != entradas.getCategoria()) {
                            // Cambiar categoria
                            entradas.setCategoria((int) spinnerCategoria.getSelectedItemId());
                            i++;
                        }

                        if (!entradas.getNombre().equals(etNombre.getText().toString())) {
                            // Cambiar nombre
                            entradas.setNombre(etNombre.getText().toString());
                            i++;
                        }

                        if (entradas.getCantidad() != Float.parseFloat(etCantidad.getText().toString())) {
                            boolean negativo = String.valueOf(entradas.getCantidad()).contains("-");
                            if (negativo && swRestar.isChecked()) {
                                // No cambia nada
                            } else {
                                // Cambiar cantidad
                                if (entradas.getCantidad() > Float.parseFloat(etCantidad.getText().toString())) {
                                    // Restar cantidad
                                    float ahorrado = objetivos.getAhorrado() - (entradas.getCantidad() - Float.parseFloat(etCantidad.getText().toString()));
                                    objetivos.setAhorrado(ahorrado);
                                } else {
                                    // Sumar cantidad
                                    float ahorrado = objetivos.getAhorrado() + (Float.parseFloat(etCantidad.getText().toString()) - entradas.getCantidad());
                                    objetivos.setAhorrado(ahorrado);
                                }

                                // Objetivo Completado
                                if (objetivos.getAhorrado() >= objetivos.getCantidad()) {
                                    objetivos.setCompletado(true);
                                } else {
                                    objetivos.setCompletado(false);
                                }

                                entradas.setCantidad(Float.parseFloat(etCantidad.getText().toString()));
                                i++;
                            }
                        } else if (swRestar.isChecked()) {
                            // Cambiar cantidad
                            float cantidad = 0;
                            cantidad -= Float.parseFloat(etCantidad.getText().toString());
                            if (entradas.getCantidad() > cantidad) {
                                // Restar cantidad
                                float ahorrado = objetivos.getAhorrado() - (entradas.getCantidad() - cantidad);
                                objetivos.setAhorrado(ahorrado);
                            } else {
                                // Sumar cantidad
                                float ahorrado = objetivos.getAhorrado() + (cantidad - entradas.getCantidad());
                                objetivos.setAhorrado(ahorrado);
                            }

                            // Objetivo Completado
                            if (objetivos.getAhorrado() >= objetivos.getCantidad()) {
                                objetivos.setCompletado(true);
                            } else {
                                objetivos.setCompletado(false);
                            }

                            entradas.setCantidad(cantidad);
                            i++;
                        }

                        if (!entradas.getFecha().equals(etFecha.getText().toString())) {
                            // Cambiar fecha
                            entradas.setFecha(etFecha.getText().toString());
                            i++;
                        }

                        if (i > 0) {
                            db.entradasDao().update(entradas.getIdObjetivos(), entradas.getIdEntrada(), entradas.getCategoria(),entradas.getFecha(), entradas.getNombre(), entradas.getCantidad());
                            db.objetivosDao().update(objetivos.getId(), objetivos.getCategoria(), objetivos.getNombre(), objetivos.getFecha(), objetivos.getCantidad(), objetivos.getAhorrado(), objetivos.getCompletado());
                        } else {
                            // Alerta de que no cambia nada
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
        } else if (fecha.getDay() == today.getDay() && fecha.getMonth() == today.getMonth() && fecha.getYear() == today.getYear()) {
            text.setVisibility(View.GONE);
            return true;
        } else {
            text.setVisibility(View.GONE);
            return true;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
            guardarBaseDeDatos();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // No hacer nada
    }

    private void eliminarEntrada() {
        Entradas entradas = db.entradasDao().findByIds(idO, idE);
        Objetivos objetivos = db.objetivosDao().findById(idO);

        db.objetivosDao().updateAhorrado(objetivos.getId(), (objetivos.getAhorrado() - entradas.getCantidad()));

        objetivos = db.objetivosDao().findById(idO);

        if (objetivos.getAhorrado() >= objetivos.getCantidad()) {
            db.objetivosDao().updateCompletado(objetivos.getId(), true);
        } else {
            db.objetivosDao().updateCompletado(objetivos.getId(), false);
        }

        db.entradasDao().deleteByIds(idO, idE);
        finish();
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