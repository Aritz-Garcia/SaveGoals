package com.savegoals.savegoals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalculadoraActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvResultado, tvTextCrearCalc;
    EditText etCantidad, etDias, etCantidadLlegar, etCantidadInicial;
    Button btnCalcular, btnCrearCalc;
    String dateResultado;
    Switch swInicio;
    SharedPreferences settingssp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculadora);

        tvTextCrearCalc = findViewById(R.id.tvTextCrearCalc);
        tvResultado = findViewById(R.id.tvResultadoCalc);
        etCantidad = findViewById(R.id.etCantidadCalc);
        etDias = findViewById(R.id.etDias);
        etCantidadLlegar = findViewById(R.id.etCantidadLlegar);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnCrearCalc = findViewById(R.id.btnCrearCalc);
        swInicio = findViewById(R.id.swInicioCalc);
        etCantidadInicial = findViewById(R.id.etCantidadInicialCalc);

        btnCalcular.setOnClickListener(this);
        btnCrearCalc.setOnClickListener(this);
        swInicio.setOnClickListener(this);

        settingssp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        setDayNight();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCalcular) {
            // Calcular el resultado
            comporbar();
        } else if (v.getId() == btnCrearCalc.getId()) {
            // Crear el objetivo con los datos
            mandarObjetivo();
        } else if (v.getId() == swInicio.getId()) {
            // Mostrar el campo de cantidad inicial
            if (swInicio.isChecked()) {
                etCantidadInicial.setVisibility(View.VISIBLE);
            } else {
                etCantidadInicial.setVisibility(View.GONE);
            }
        }
    }

    private void comporbar() {
        if (leerNumero(etCantidad.getText().toString(), etCantidad)) {
            if (leerNumero(etDias.getText().toString(), etDias)) {
                if (leerNumero(etCantidadLlegar.getText().toString(), etCantidadLlegar)) {
                    if (swInicio.isChecked()) {
                        if (leerNumero(etCantidadInicial.getText().toString(), etCantidadInicial)) {
                            float cantidad = Float.parseFloat(etCantidad.getText().toString());
                            int dias = Integer.parseInt(etDias.getText().toString());
                            float cantidadLlegar = Float.parseFloat(etCantidadLlegar.getText().toString());
                            float cantidadInicial = Float.parseFloat(etCantidadInicial.getText().toString());
                            calcular(cantidad, dias, cantidadLlegar, cantidadInicial);
                        } else {
                            tvResultado.setVisibility(View.GONE);
                            tvTextCrearCalc.setVisibility(View.GONE);
                            btnCrearCalc.setVisibility(View.GONE);
                        }
                    } else {
                        float cantidad = Float.parseFloat(etCantidad.getText().toString());
                        int dias = Integer.parseInt(etDias.getText().toString());
                        float cantidadLlegar = Float.parseFloat(etCantidadLlegar.getText().toString());
                        calcular(cantidad, dias, cantidadLlegar, 0);
                    }
                } else {
                    tvResultado.setVisibility(View.GONE);
                    tvTextCrearCalc.setVisibility(View.GONE);
                    btnCrearCalc.setVisibility(View.GONE);
                }
            } else {
                tvResultado.setVisibility(View.GONE);
                tvTextCrearCalc.setVisibility(View.GONE);
                btnCrearCalc.setVisibility(View.GONE);
            }
        } else {
            tvResultado.setVisibility(View.GONE);
            tvTextCrearCalc.setVisibility(View.GONE);
            btnCrearCalc.setVisibility(View.GONE);
        }
    }

    private void calcular(float cantidad, int dias, float cantidadLlegar, float cantidadInicial) {
        if (cantidadInicial == 0) {
            if (cantidadLlegar < cantidad) {
                etCantidadLlegar.setError(getString(R.string.cantidad_menor));
            } else {
                float resultado = cantidadLlegar / cantidad;
                BigDecimal bd = new BigDecimal(resultado);
                bd = bd.setScale(0, BigDecimal.ROUND_CEILING);
                int porCanto = bd.intValue();
                int diasSuma = porCanto * dias;

                LocalDate localDate;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    localDate = LocalDate.now();
                    localDate = localDate.plusDays(diasSuma);
                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    dateResultado = localDate.format(formato);
                    tvResultado.setText(getString(R.string.calculadora_text) + dateResultado);
                    tvResultado.setVisibility(View.VISIBLE);
                    tvTextCrearCalc.setVisibility(View.VISIBLE);
                    btnCrearCalc.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (cantidadInicial > cantidadLlegar) {
                etCantidadInicial.setError(getString(R.string.cantidad_menor));
            } else {
                cantidadLlegar = cantidadLlegar - cantidadInicial;
                if (cantidadLlegar < cantidad) {
                    etCantidadLlegar.setError(getString(R.string.cantidad_menor));
                } else {
                    float resultado = cantidadLlegar / cantidad;
                    BigDecimal bd = new BigDecimal(resultado);
                    bd = bd.setScale(0, BigDecimal.ROUND_CEILING);
                    int porCanto = bd.intValue();
                    int diasSuma = porCanto * dias;

                    LocalDate localDate;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        localDate = LocalDate.now();
                        localDate = localDate.plusDays(diasSuma);
                        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        dateResultado = localDate.format(formato);
                        tvResultado.setText(getString(R.string.calculadora_text) + dateResultado);
                        tvResultado.setVisibility(View.VISIBLE);
                        tvTextCrearCalc.setVisibility(View.VISIBLE);
                        btnCrearCalc.setVisibility(View.VISIBLE);
                    }
                }
            }
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

    private void mandarObjetivo() {
        if (swInicio.isChecked()) {
            float cantidadInicial = Float.parseFloat(etCantidadInicial.getText().toString());
            float cantidadLlegar = Float.parseFloat(etCantidadLlegar.getText().toString());
            float cantidad = cantidadLlegar- cantidadInicial;
            Intent intent = new Intent(this, AddObjetivosActivity.class);
            intent.putExtra("rDatos", true);
            intent.putExtra("fecha", dateResultado);
            intent.putExtra("cantidad", cantidad+"");
            startActivity(intent);
        } else{
            Intent intent = new Intent(this, AddObjetivosActivity.class);
            intent.putExtra("rDatos", true);
            intent.putExtra("fecha", dateResultado);
            intent.putExtra("cantidad", etCantidadLlegar.getText().toString());
            startActivity(intent);
        }
    }

    private void setDayNight() {
        boolean oscuro = settingssp.getBoolean("oscuro", false);
        if (oscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btnCalcular.setTextColor(Color.WHITE);
            btnCrearCalc.setTextColor(Color.WHITE);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btnCalcular.setTextColor(Color.WHITE);
            btnCrearCalc.setTextColor(Color.WHITE);
        }
    }
}