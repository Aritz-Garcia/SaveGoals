package com.savegoals.savegoals.data.datos;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class ColoresHighLight {

    public static List<Integer> coloresGet_VORDIPLOM_COLORS() {
        List<Integer> colores = new ArrayList<>();
        colores.add(Color.GREEN);
        colores.add(Color.YELLOW);
        colores.add(Color.rgb(255, 165, 0));
        colores.add(Color.BLUE);
        colores.add(Color.RED);
        return colores;
    }
}
