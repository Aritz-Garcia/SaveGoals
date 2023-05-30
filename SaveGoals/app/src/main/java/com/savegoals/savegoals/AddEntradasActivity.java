package com.savegoals.savegoals;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;
import com.savegoals.savegoals.dialog.DatePickerFragment;
import com.savegoals.savegoals.formularios.CustomAdapter;
import com.savegoals.savegoals.formularios.CustomItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddEntradasActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Spinner spinnerCategoria;
    ArrayList<CustomItem> customList;
    Button btnGuardar, btnVolverEntradas;
    EditText etNombre, etFecha, etCantidad;
    TextView tvErrorCategoria, tvErrorFecha;
    AppDatabase db;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entradas);

        db = AppDatabase.getDatabase(this);

        id = getIntent().getIntExtra("id", 0);

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

        customList = getCustomList();
        CustomAdapter adapter = new CustomAdapter(this, customList);
        if (spinnerCategoria != null) {
            spinnerCategoria.setAdapter(adapter);
            spinnerCategoria.setOnItemSelectedListener(this);
        }

    }

    private ArrayList<CustomItem> getCustomList() {
        customList = new ArrayList<>();
        customList.add(new CustomItem("Seleccionar", 0));
        customList.add(new CustomItem("Cartera", R.drawable.cartera));
        customList.add(new CustomItem("Hucha", R.drawable.hucha));
        customList.add(new CustomItem("Trabajo", R.drawable.martillo));
        customList.add(new CustomItem("Regalo", R.drawable.regalo));
        customList.add(new CustomItem("Compras", R.drawable.carrito));
        customList.add(new CustomItem("Clase", R.drawable.clase));
        customList.add(new CustomItem("Otros", R.drawable.otros));
        return customList;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnGuardar.getId()) {
            // Guardar en la base de datos
            guardarBaseDeDatos();
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
                        Objetivos objetivos = db.objetivosDao().findById(id);
                        float ahorrado = objetivos.getAhorrado();
                        float cantidad = objetivos.getCantidad();

                        ahorrado += Float.parseFloat(etCantidad.getText().toString());
                        db.objetivosDao().updateAhorrado(id, ahorrado);
                        if (ahorrado >= cantidad) {
                            db.objetivosDao().updateCompletado(id, true);
                        }

                        Entradas entradas = new Entradas();
                        entradas.setIdObjetivos(id);
                        entradas.setIdEntrada(entradasList.size()+1);
                        entradas.setCategoria(getCategoria((int) spinnerCategoria.getSelectedItemId()));
                        entradas.setNombre(etNombre.getText().toString());
                        entradas.setCantidad(Integer.parseInt(etCantidad.getText().toString()));
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
        }else if(Integer.parseInt(textua)<0) {
            text.setError("No puede ser un numero negativo");
            return false;
        }else if((!textua.matches("[0-9]+\\.?")) ){
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
            text.setVisibility(View.GONE);
            return true;
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
                return "Cartera";
            case 2:
                return "Hucha";
            case 3:
                return "Trabajo";
            case 4:
                return "Regalo";
            case 5:
                return "Compras";
            case 6:
                return "Clase";
            case 7:
                return "Otros";
            default:
                return "Seleccionar";
        }
    }

}