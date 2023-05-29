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

import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;
import com.savegoals.savegoals.dialog.DatePickerFragment;
import com.savegoals.savegoals.formularios.CustomAdapter;
import com.savegoals.savegoals.formularios.CustomItem;

import java.util.ArrayList;

public class AddObjetivosActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner spinnerCategoria;
    ArrayList<CustomItem> customList;
    Button btnGuardar, btnVolverObjetivos;
    EditText etNombre, etFecha, etCantidad;
    TextView tvErrorCategoria;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_objetivos);

        db = AppDatabase.getDatabase(this);

        etNombre = findViewById(R.id.etNombre);
        etFecha = findViewById(R.id.etFecha);
        etCantidad = findViewById(R.id.etCantidad);

        tvErrorCategoria = findViewById(R.id.tvErrorCategoria);

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
            if (stringIrakurri(etNombre.getText().toString(), etNombre)) {
                if (zenbakiaIrakurri(etCantidad.getText().toString(), etCantidad)) {
                    if (etFecha.getText().toString().length() != 0) {
                        Objetivos objetivo = new Objetivos();
                        objetivo.setCategoria(getCategoria((int) spinnerCategoria.getSelectedItemId()));
                        objetivo.setNombre(etNombre.getText().toString());
                        objetivo.setCantidad(Integer.parseInt(etCantidad.getText().toString()));
                        objetivo.setFecha(etFecha.getText().toString());
                        objetivo.setAhorrado(0);
                        objetivo.setCompletado(false);
                        db.objetivosDao().insertAll(objetivo);

                        finish();
                    } else {
                        etFecha.setError("Campo necesario");
                    }
                }
            }
        } else {
            tvErrorCategoria.setVisibility(View.VISIBLE);
        }
    }

    private boolean stringIrakurri(String textua, EditText text){
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

    private boolean zenbakiaIrakurri(String textua, EditText text){
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
}