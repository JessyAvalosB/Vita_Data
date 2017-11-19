package com.example.jessy.vitadata.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jessy.vitadata.Monitor;
import com.example.jessy.vitadata.R;

import java.util.Set;

public class DispositivosBT extends AppCompatActivity {

    //Depuracion de LOGCAT
    private static final String TAG = "DispositivosBT";

    //Declaramos los elementos del layout
    //-----------------
    ListView IdLista;
    //-----------------

    //Variable para enviar la direccion MAC a la actividad Monitor
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    //Declaracion de elementos necesarios para el servicio de coneccion BT
    private BluetoothAdapter mBtAdapter;

    //ArrayAdapter para mostrar los dispositivos vinculados
    private ArrayAdapter mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_bt);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Verificamos que el dispositivo BT esta encendido y que el dispositivo movil soporte el servicio
        //------------------------
        VerificarEstadoBT();
        //------------------------

        //Inicializa la array que contendra la lista de los dispositivos bluetooth vinculados
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.nombre_dispositivos);

        //presenta los dispositivos vinculados en la ListView
        IdLista = (ListView) findViewById(R.id.Id_Lista);
        IdLista.setAdapter(mPairedDevicesArrayAdapter);

        //Metodo para iniciar el proceso de seleeccion de coneccion
        IdLista.setOnItemClickListener(mDeviceClickListener);

        //Obtiene el adaptador local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        //Obtiene un conjunto de dispositivos actualmente emparejados y agrega a 'pairedDevices'
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        //Adiciona un dispositivo previo emparejado al array
        if (pairedDevices.size()>0){
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    //Configura un (on-Click) para la lista
    //----------------------------------------------------------------------------------------------
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            //Obtener la direccion MAC del dispositivo, que son los ultimos 17 caracteres en la vista
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length()-17);

            //Realiza un Intent para iniciar la siguiente actividad mientras toma un EXTRA_DEVICE_ADDRESS que es la direccion MAC
            Intent intent = new Intent(DispositivosBT.this, Monitor.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(intent);
            finish();
        }
    };
    //----------------------------------------------------------------------------------------------

    //Metodo para que el dispositivo BT esta encendido y que el dispositivo movil soporte el servicio
    private void VerificarEstadoBT(){
        //Comprueba que el dispositivo tiene Bluetooth y que esta encendido
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null){
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        }else{
            if (mBtAdapter.isEnabled()){
                Toast.makeText(getBaseContext(), "...Bluetooth Activado...", Toast.LENGTH_SHORT).show();
            }else{
                //Solicita al usuario que active Bluetooth
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent,1);
            }
        }
    }
}
