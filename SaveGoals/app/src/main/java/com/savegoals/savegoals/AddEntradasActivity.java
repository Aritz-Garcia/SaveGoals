package com.savegoals.savegoals;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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
import androidx.fragment.app.DialogFragment;

import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;
import com.savegoals.savegoals.dialog.DatePickerFragment;
import com.savegoals.savegoals.dialog.ErrorDateDialog;
import com.savegoals.savegoals.formularios.CustomAdapter;
import com.savegoals.savegoals.formularios.CustomItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddEntradasActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ErrorDateDialog.ErrorDateDialogListener {

    Spinner spinnerCategoria;
    ArrayList<CustomItem> customList;
    Button btnGuardar, btnVolverEntradas;
    EditText etNombre, etFecha, etCantidad;
    TextView tvErrorCategoria, tvErrorFecha;
    AppDatabase db;
    SharedPreferences settingssp;
    int id;
    boolean restar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entradas);

        db = AppDatabase.getDatabase(this);

        id = getIntent().getIntExtra("id", 0);
        restar = getIntent().getBooleanExtra("restar", false);

        etNombre = findViewById(R.id.etNombreEntradas);
        etFecha = findViewById(R.id.etFechaEntradas);
        etCantidad = findViewById(R.id.etCantidadEntradas);

        tvErrorCategoria = findViewById(R.id.tvErrorCategoriaEntradas);
        tvErrorFecha = findViewById(R.id.tvErrorFechaEntradas);

        btnGuardar = findViewById(R.id.btnAddEntradas);
        btnVolverEntradas = findViewById(R.id.btnVolverEntradas);

        spinnerCategoria = findViewById(R.id.spinnerCategoriaEntradas);

        etFecha.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnVolverEntradas.setOnClickListener(this);

        if (restar) {
            btnGuardar.setText(getString(R.string.restar));
        }

        customList = getCustomList();
        CustomAdapter adapter = new CustomAdapter(this, customList);
        if (spinnerCategoria != null) {
            spinnerCategoria.setAdapter(adapter);
            spinnerCategoria.setOnItemSelectedListener(this);
        }

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        setDayNight();

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
        if (v.getId() == btnGuardar.getId()) {
            // Guardar en la base de datos
            String fechaObjetivos = db.objetivosDao().findById(id).getFecha();

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
            // Volver a la pantalla de entradas
            finish();
        } else if (v.getId() == etFecha.getId()) {
            showDatePickerDialog();
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

                        List<Entradas> entradasList = db.entradasDao().findByIdObj(id);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            entradasList.sort((o1, o2) -> o1.getIdEntrada() - o2.getIdEntrada());
                        }

                        Objetivos objetivos = db.objetivosDao().findById(id);
                        float ahorrado = objetivos.getAhorrado();
                        float cantidad = objetivos.getCantidad();

                        if (restar) {
                            ahorrado -= Float.parseFloat(etCantidad.getText().toString());
                        } else {
                            ahorrado += Float.parseFloat(etCantidad.getText().toString());
                        }
                        db.objetivosDao().updateAhorrado(id, ahorrado);
                        if (ahorrado >= cantidad) {
                            db.objetivosDao().updateCompletado(id, true);
                        }

                        Entradas entradas = new Entradas();
                        entradas.setIdObjetivos(id);

                        if (entradasList.size() == 0) {
                            entradas.setIdEntrada(1);
                        } else {
                            for (int i = 0; i < entradasList.size(); i++) {
                                if (i == entradasList.size() - 1) {
                                    entradas.setIdEntrada(entradasList.get(i).getIdEntrada() + 1);
                                }
                            }
                        }

                        entradas.setCategoria((int) spinnerCategoria.getSelectedItemId());
                        entradas.setNombre(etNombre.getText().toString());
                        if (restar) {
                            float restado = 0;
                            restado -= Float.parseFloat(etCantidad.getText().toString());
                            entradas.setCantidad(restado);
                        } else {
                            entradas.setCantidad(Float.parseFloat(etCantidad.getText().toString()));
                        }
                        entradas.setFecha(etFecha.getText().toString());

                        db.entradasDao().insertAll(entradas);

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
        } else if (textua.length()>30) {
            text.setError(getString(R.string.largo));
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

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btnVolverEntradas.setTextColor(Color.WHITE);
            btnGuardar.setTextColor(Color.WHITE);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btnVolverEntradas.setTextColor(Color.WHITE);
            btnGuardar.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        guardarBaseDeDatos();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // No hace nada
    }
}