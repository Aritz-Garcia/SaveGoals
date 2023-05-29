package com.savegoals.savegoals;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.savegoals.savegoals.formularios.CustomAdapter;
import com.savegoals.savegoals.formularios.CustomItem;

import java.util.ArrayList;

public class AddObjetivosActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerCategoria;
    ArrayList<CustomItem> customList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_objetivos);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
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
        CustomItem item = (CustomItem) parent.getSelectedItem();
        Toast.makeText(this, item.getSpinnerItemName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}