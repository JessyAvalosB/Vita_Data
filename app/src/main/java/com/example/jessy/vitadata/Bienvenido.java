package com.example.jessy.vitadata;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Bienvenido extends AppCompatActivity {

    //Declaramos las variables de los elementos de nuestro layout
    //--------------------------
    Button Welcome;
    //--------------------------

    //Elementos necesarios para el servicio de coneccion
    //---------------------
    BluetoothAdapter BT;
    //---------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido);

        BT = BluetoothAdapter.getDefaultAdapter();

        //Enlazamos los elementos de nuestro layout
        //-----------------------------------------------
        Welcome = (Button)findViewById(R.id.btnWelcome);
        //-----------------------------------------------

        //Metodo OnClick para ir a la clase Menu
        //-----------------------------------------------------------------------------
        Welcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu = new Intent(Bienvenido.this , Menu.class);
                startActivity(menu);
                finish();
            }
        });
        //------------------------------------------------------------------------------
    }
}
