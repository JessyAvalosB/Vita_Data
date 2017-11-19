package com.example.jessy.vitadata;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jessy.vitadata.Bluetooth.DispositivosBT;
import com.example.jessy.vitadata.Objetos.FirebaseReferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Monitor extends AppCompatActivity {

    //Declaramos nuestra variables del layout
    //----------------------------------------------
    Button IdRegresar;

    TextView IdT, IdOS, IdFC, IdEstado,IdRecibir;
    //----------------------------------------------

    //Declaramos los elementos necesarios para el servicio de Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    //Declaramos los elementos necesarios para el servicio de coneccion BT
    //-------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    //Identificador unico de servidor - spp UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //String para la direccion MAC
    private static String address = null;
    //--------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        //Realizamos la coneccion con los elementos del layout
        //----------------------------------------------------
        IdRegresar = (Button) findViewById(R.id.IdRegresar);

        IdT = (TextView) findViewById(R.id.IdT);
        IdOS = (TextView) findViewById(R.id.IdOS);
        IdFC = (TextView) findViewById(R.id.IdFC);
        IdEstado = (TextView) findViewById(R.id.IdEstado);
        IdRecibir = (TextView) findViewById(R.id.IdRecibir);
        //-----------------------------------------------------

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //Relacionamos los campos de la Firebase con sus respectivas variables
        //-----------------------------------------------------------------------------
        final DatabaseReference Temp = database.getReference(FirebaseReferences.Temp);
        final DatabaseReference FC = database.getReference(FirebaseReferences.FC);
        final DatabaseReference OS = database.getReference(FirebaseReferences.OS);
        //------------------------------------------------------------------------------

        //Metodo para recibir informacion por BT
        //----------------------------------------------------------------------------
        bluetoothIn = new Handler(){
            public void handleMessage(android.os.Message msg){
                if (msg.what == handlerState){
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("~");

                    if (endOfLineIndex > 0){
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex + 1);
                        IdRecibir.setText("Recibiendo datos: "+dataInPrint);
                        int dataLength = dataInPrint.length();

                        if (DataStringIN.charAt(0) == '#'){
                            String ST = DataStringIN.substring(1,3);
                            String SOS = DataStringIN.substring(4,6);
                            String SFC = DataStringIN.substring(7,9);

                            IdT.setText(ST);
                            IdOS.setText(SOS);
                            IdFC.setText(SFC);

                            Temp.setValue(ST);
                            FC.setValue(SFC);
                            OS.setValue(SOS);
                        }
                        DataStringIN.delete(0, DataStringIN.length());
                        dataInPrint = " ";
                    }
                }
            }
        };
        //----------------------------------------------------------------------------

        //Metodo para Regresar al Menu
        IdRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Monitor.this, Menu.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //crea una conexion de salida segura para el dispositivo usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Consigue la direccion MAC desde DeviceListActivity via Intent
        Intent intent = getIntent();

        //consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);

        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try{
            btSocket = createBluetoothSocket(device);
        }catch (IOException e){
            Toast.makeText(getBaseContext(), "La creacion del Socket fallo", Toast.LENGTH_SHORT).show();
        }
        //establece la conexion con el socket Bluetooth
        try{
            btSocket.connect();
            IdEstado.setText("Conectado");
        }catch (IOException e){
            try{
                btSocket.close();
            }catch (IOException e2){

            }
        }

        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();

    }

    @Override
    protected void onPause() {
        super.onPause();

        try{
            //Cuado se sale de la app esta parte permite que el socket se cierre
            btSocket.close();
        }catch (IOException e2){

        }
    }

    //crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread{
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }catch (IOException e){

            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[256];
            int bytes;

            //Se mantiene en modo escucha para determinar el ingreso de datos
            while (true){
                try{
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer,0,bytes);
                    //Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState,bytes,-1,readMessage).sendToTarget();
                }catch (IOException e){
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input){
            try{
                mmOutStream.write(input.getBytes());
            }catch (IOException e){
                //si no es posible enviar los datos cierra la conexion
                Toast.makeText(getBaseContext(), "La Conexion fallo", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
