package com.savegoals.savegoals.controlador.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.R;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.util.List;

public class CalendarioFragment extends Fragment {

    LinearLayout lyCalDatos;

    public CalendarioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        lyCalDatos = view.findViewById(R.id.lyCalDatos);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        AppDatabase db = AppDatabase.getDatabase(getContext());

        List<Objetivos> objetivos = db.objetivosDao().getAll();

    }
}