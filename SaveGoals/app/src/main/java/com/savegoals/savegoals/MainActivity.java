package com.savegoals.savegoals;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.savegoals.savegoals.controlador.estadisticas.PagerController;
import com.savegoals.savegoals.data.entities.Entradas;
import com.savegoals.savegoals.data.entities.Objetivos;
import com.savegoals.savegoals.db.AppDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tab1, tab2, tab3;
    PagerController pagerAdapter;
    Toolbar toolbar;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int id = getIntent().getIntExtra("id", 0);

        db = AppDatabase.getDatabase(this);

        Objetivos objetivo = db.objetivosDao().findById(id);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(objetivo.getNombre());
        setSupportActionBar(toolbar);
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.objetivos_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.editarBtn) {
                    Intent intent = new Intent(MainActivity.this, EditObjetivosActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    return true;
                } else if (menuItem.getItemId() == R.id.retirarBtn) {
                    Intent intent = new Intent(MainActivity.this, AddEntradasActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("restar", true);
                    startActivity(intent);
                    return true;
                } else if (menuItem.getItemId() == R.id.eliminarBtn) {
                    eliminarObjetivo();
                    finish();
                    return true;
                }
                return false;
            }

            private void eliminarObjetivo() {
                List<Entradas> entradas = db.entradasDao().findByIdObj(id);
                if (entradas.size() != 0) {
                    for (int i = 0; i < entradas.size(); i++) {
                        db.entradasDao().deleteByIds(id, entradas.get(i).getIdEntrada());
                    }
                }
                db.objetivosDao().deleteById(id);
            }
        });

        tab1 = findViewById(R.id.tabResumen);
        tab2 = findViewById(R.id.tabEstadisticas);
        tab3 = findViewById(R.id.tabEntradas);

        pagerAdapter = new PagerController(getSupportFragmentManager(), tabLayout.getTabCount(), id);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0 || tab.getPosition() == 1 || tab.getPosition() == 2) {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            // @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            // @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}