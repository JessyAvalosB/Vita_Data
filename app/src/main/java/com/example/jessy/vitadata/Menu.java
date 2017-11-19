package com.example.jessy.vitadata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.jessy.vitadata.Bluetooth.DispositivosBT;

public class Menu extends AppCompatActivity {

    //Declaamos los elementos que usaremos en nuestro layout
    Button Monitor, Historial,Salir;

    String TAG = "Menu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Realizamos la coneccion con los elementos del layout
        //---------------------------------------------------
        Monitor = (Button)findViewById(R.id.btnMonitor);
        Historial = (Button)findViewById(R.id.btnHistorial);
        Salir = (Button) findViewById(R.id.btnSalir);
        //---------------------------------------------------


        //Con este metodo pasaremos de la activity_menu a la acivity_monitor
        Monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent m = new Intent(Menu.this , DispositivosBT.class);
                    startActivity(m);
                    finish();

            }
        });


        //Con este metodo pasaremos de la activity_menu a la activity_historial
        Historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Historial = new Intent(Menu.this, Historial2.class);
                startActivity(Historial);

            }
        });

        //Con este metodo salimos de la aplicacion
        Salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
