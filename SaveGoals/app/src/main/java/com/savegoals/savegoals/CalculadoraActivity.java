package com.savegoals.savegoals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalculadoraActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvResultado, tvTextCrearCalc;
    EditText etCantidad, etDias, etCantidadLlegar;
    Button btnCalcular, btnCrearCalc;
    String dateResultado;

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

        btnCalcular.setOnClickListener(this);
        btnCrearCalc.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCalcular) {
            // Calcular el resultado
            calcular();
        } else if (v.getId() == btnCrearCalc.getId()) {
            // Crear el objetivo con los datos
            mandarObjetivo();
        }
    }

    private void calcular() {
        if (leerNumero(etCantidad.getText().toString(), etCantidad)) {
            if (leerNumero(etDias.getText().toString(), etDias)) {
                if (leerNumero(etCantidadLlegar.getText().toString(), etCantidadLlegar)) {
                    float cantidad = Float.parseFloat(etCantidad.getText().toString());
                    int dias = Integer.parseInt(etDias.getText().toString());
                    float cantidadLlegar = Float.parseFloat(etCantidadLlegar.getText().toString());
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
        Intent intent = new Intent(this, AddObjetivosActivity.class);
        intent.putExtra("rDatos", true);
        intent.putExtra("fecha", dateResultado);
        intent.putExtra("cantidad", etCantidadLlegar.getText().toString());
        startActivity(intent);
    }
}