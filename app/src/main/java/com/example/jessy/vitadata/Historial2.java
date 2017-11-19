package com.example.jessy.vitadata;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.jessy.vitadata.Objetos.FirebaseReferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Historial2 extends AppCompatActivity {
    String TAG = "Historial";
    //Declaramos nuestra variables del layout
    //--------------------------------------------
    ListView lvTemp;
    ListView lvFC;
    ListView lvOS;

    Button back;
    //---------------------------------------------

    //Recursos necesarios para el servicio Firebase
    //----------------------------------------------------------
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //----------------------------------------------------------

    //ArrayAdapters necesarios para cada signo vital
    //----------------------------------------------------------
    ArrayAdapter arrayAdapterTemp;
    ArrayAdapter arrayAdapterFC;
    ArrayAdapter arrayAdapterOS;
    //----------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial2);

        //Aqui obligamos que el layout inicie como landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Realizamos la coneccion con los elementos del layout
        //----------------------------------------------
        lvTemp = (ListView)findViewById(R.id.Id_lv_T);
        lvFC = (ListView)findViewById(R.id.Id_lv_FC);
        lvOS = (ListView)findViewById(R.id.Id_lv_OS);

        back = (Button)findViewById(R.id.Id_btn_back);
        //----------------------------------------------

        //Metodo OnClick para regresar al menu
        //--------------------------------------------------------------------------
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Historial2.this, Menu.class);
                startActivity(back);
                finish();
            }
        });
        //--------------------------------------------------------------------------
    }

    @Override
    protected void onResume() {
        super.onResume();


        arrayAdapterTemp = new ArrayAdapter(this,R.layout.nombre_dispositivos);
        arrayAdapterFC = new ArrayAdapter(this,R.layout.nombre_dispositivos);
        arrayAdapterOS = new ArrayAdapter(this,R.layout.nombre_dispositivos);

        //Referencias de la Firebase
        //-------------------------------------------------------------------------
        DatabaseReference Temp = database.getReference(FirebaseReferences.Temp);
        DatabaseReference FC = database.getReference(FirebaseReferences.FC);
        DatabaseReference OS = database.getReference(FirebaseReferences.OS);
        //-------------------------------------------------------------------------

        //Mandamos a llamar los datos y los mostramos en la listview
        //---------------------------------------------------------------
        Temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String valorTemp = dataSnapshot.getValue().toString();
                lvTemp.setAdapter(arrayAdapterTemp);
                arrayAdapterTemp.add(valorTemp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error ", databaseError.getMessage());
            }
        });

        FC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String valorFC = dataSnapshot.getValue().toString();
                lvFC.setAdapter(arrayAdapterFC);
                arrayAdapterFC.add(valorFC);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error: ", databaseError.getMessage());
            }
        });

        OS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String valorOS = dataSnapshot.getValue().toString();
                lvOS.setAdapter(arrayAdapterOS);
                arrayAdapterOS.add(valorOS);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error: ", databaseError.getMessage());
            }
        });
        //--------------------------------------------------------------
    }
}
