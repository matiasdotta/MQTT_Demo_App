package com.example.DemoMQTT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MqttAndroidClient client;
    private String TAG = "MainActivity";
    private String clientid = "";
    private Timer myTimer;
    //Callback when bottom navigation item is selected

    private String estado1="0";
    private String estado2="0";
    private String estado3="0";
    private String pubtopic="";
    private String msg="";
    private String potencia="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button botonEncender1 = findViewById(R.id.boton_encender_Frame1);
        Button botonEncender2 = findViewById(R.id.boton_encender_Frame2);
        Button botonEncender3 = findViewById(R.id.boton_encender_Frame3);
        Button botonEncender4 = findViewById(R.id.boton_encender_Frame4);
        Button botonApagar1 = findViewById(R.id.boton_apagar_Frame1);
        Button botonApagar2 = findViewById(R.id.boton_apagar_Frame2);
        Button botonApagar3 = findViewById(R.id.boton_apagar_Frame3);
        Button botonApagar4 = findViewById(R.id.boton_apagar_Frame4);
        Button botonConectar = findViewById(R.id.conectar);
        Button botonConfig = findViewById(R.id.boton_Configurar);
        botonEncender1.setOnClickListener((View.OnClickListener) this);
        botonEncender2.setOnClickListener((View.OnClickListener) this);
        botonEncender3.setOnClickListener((View.OnClickListener) this);
        botonEncender4.setOnClickListener((View.OnClickListener) this);
        botonApagar1.setOnClickListener((View.OnClickListener) this);
        botonApagar2.setOnClickListener((View.OnClickListener) this);
        botonApagar3.setOnClickListener((View.OnClickListener) this);
        botonApagar4.setOnClickListener((View.OnClickListener) this);
        botonConectar.setOnClickListener((View.OnClickListener) this);
        botonConfig.setOnClickListener((View.OnClickListener) this);

        clientid = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.1.35:1883",clientid);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                TextView tvMessage  = (TextView) findViewById(R.id.cnxStatus);
                String msg_new="";
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Si MQTT Conecto
                    Toast.makeText(MainActivity.this,"MQTT Conectado",Toast.LENGTH_LONG).show();
                    msg_new = "Conectado\r\n";
                    tvMessage.setTextColor(0xFF00FF00); //Green if connected
                    tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tvMessage.setText(msg_new);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Si fallo conexion mqtt
                    Toast.makeText(MainActivity.this,"MQTT Error de conexion",Toast.LENGTH_LONG).show();
                    msg_new = "Desconectado\r\n";
                    tvMessage.setTextColor(0xFFFF0000); //Red if not connected
                    tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tvMessage.setText(msg_new);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        EditText potenciaI1 = (EditText) findViewById(R.id.editTextNumber_Frame1);
        EditText potenciaI2 = (EditText) findViewById(R.id.editTextNumber_Frame2);
        EditText potenciaI3 = (EditText) findViewById(R.id.editTextNumber_Frame3);

        Intent intent = this.getIntent();
        Boolean incluir1 = intent.getBooleanExtra("state1",false);
        Boolean incluir2 = intent.getBooleanExtra("state2",false);
        Boolean incluir3 = intent.getBooleanExtra("state3",false);




        switch (v.getId()) {

            case R.id.conectar:

                EditText etBroker = (EditText) findViewById(R.id.urlBroker);
                String msg_new = "";
                //--- Set Connection Parameters ---
                String urlBroker = etBroker.getText().toString().trim();


                Random r = new Random();        //Unique Client ID for connection
                int i1 = r.nextInt(5000 - 1) + 1;
                clientid = "mqtt" + i1;

                if (client.isConnected()) {
                    //Disconnect and Reconnect to  Broker
                    try {
                        //Disconnect from Broker
                        client.disconnect();
                        //Connect to Broker
                        client = new MqttAndroidClient(getApplicationContext(), urlBroker,clientid);
                        //Set Mqtt Message Callback
                        mqttCallback();
                    } catch (MqttException e) {
                    }
                } else {
                    //Connect to Broker
                    client = new MqttAndroidClient(getApplicationContext(), urlBroker,clientid);
                    //Set Mqtt Message Callback
                    mqttCallback();
                }
                break;
            case R.id.boton_encender_Frame1:
                potenciaI1 = (EditText) findViewById(R.id.editTextNumber_Frame1);
                potencia = potenciaI1.getText().toString();
                estado1 = Encender("1", estado1, potencia);
                break;
            case R.id.boton_encender_Frame2:
                potenciaI2 = (EditText) findViewById(R.id.editTextNumber_Frame2);
                potencia = potenciaI2.getText().toString();
                estado2 = Encender("2", estado2, potencia);
                break;
            case R.id.boton_encender_Frame3:
                potenciaI3 = (EditText) findViewById(R.id.editTextNumber_Frame3);
                potencia = potenciaI3.getText().toString();
                estado3 = Encender("3", estado3, potencia);
                break;
            case R.id.boton_encender_Frame4:
                if (incluir1){
                    potencia = potenciaI1.getText().toString();
                    estado1 = Encender("1", estado1, potencia);
                }
                if (incluir2){
                    potencia = potenciaI2.getText().toString();
                    estado2 = Encender("2", estado2, potencia);
                }
                if (incluir3){
                    potencia = potenciaI3.getText().toString();
                    estado3 = Encender("3", estado3, potencia);
                }
                break;
            case R.id.boton_apagar_Frame1:
                //Check if connected to broker
                potencia = potenciaI1.getText().toString();
                estado1 = Apagar("1", estado1, potencia);
                break;
            case R.id.boton_apagar_Frame2:
                potencia = potenciaI2.getText().toString();
                estado2 = Apagar("2", estado2, potencia);
                break;
            case R.id.boton_apagar_Frame3:
                potencia = potenciaI3.getText().toString();
                estado3 = Apagar("3", estado3, potencia);
                break;
            case R.id.boton_apagar_Frame4:
                if (incluir1){
                    potencia = potenciaI1.getText().toString();
                    estado1 = Apagar("1", estado1, potencia);
                }
                if (incluir2){
                    potencia = potenciaI2.getText().toString();
                    estado2 = Apagar("2", estado2, potencia);
                }
                if (incluir3){
                    potencia = potenciaI3.getText().toString();
                    estado3 = Apagar("3", estado3, potencia);
                }
                break;
            case R.id.boton_Configurar:
                //Settings();
                Intent i = new Intent(this,Settings.class);
                startActivity(i);
                break;


            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private void ScheduleTasks()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(RunScheduledTasks);
    }


    private Runnable RunScheduledTasks = new Runnable() {
        public void run() {
            //This method runs in the same thread as the UI.

            //Check MQTT Connection Status
            TextView tvMessage  = (TextView) findViewById(R.id.cnxStatus);
            String msg_new="";

            if(client.isConnected() ) {
                msg_new = "Conectado\r\n";
                tvMessage.setTextColor(0xFF00FF00); //Green if connected
                tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }
            else {
                msg_new = "Desconectado\r\n";
                tvMessage.setTextColor(0xFFFF0000); //Red if not connected
                tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }
            tvMessage.setText(msg_new);
        }
    };

    //-----------------------------------Funciones-----------------------------------

    public void publishMessage(@NonNull MqttAndroidClient client, @NonNull String msg, int qos, @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        encodedPayload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setId(320);
        message.setRetained(true);
        message.setQos(qos);
        client.publish(topic, message);
    }
    protected void mqttCallback() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                //msg("Connection lost...");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //TextView tvMessage = (TextView) findViewById(R.id.subscribedMsg);
                if(topic.equals("mycustomtopic1")) {
                    //Add custom message handling here (if topic = "mycustomtopic1")
                }
                else if(topic.equals("mycustomtopic2")) {
                    //Add custom message handling here (if topic = "mycustomtopic2")
                }
                else {
                    String msg = "topic: " + topic + "\r\nMessage: " + message.toString() + "\r\n";
                    //tvMessage.append( msg);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    protected String Encender (String nInterfaz, String estado, String potencia)
    {
        pubtopic = "luz"+nInterfaz;
        msg      = "0";
        if(!client.isConnected() ) {
            return estado;
        }


            try {
                publishMessage(client, msg, 1, pubtopic);
                estado="1";
                LogEntry("Encender","I"+nInterfaz,potencia);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        return estado;
    }

//    public void Settings (){
//        Intent intent = new Intent(this, setting.class);
//        startActivity(intent);
//    }

    protected String Apagar (String nInterfaz, String estado, String potencia)
    {
        pubtopic = "luz"+nInterfaz;
        msg      = "1";
        if(!client.isConnected() ) {
            return estado;
        }


            try {
                publishMessage(client, msg, 1, pubtopic);
                estado="0";
                LogEntry("Appagar","I"+nInterfaz,potencia);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        g+
        return estado;
    }

    protected void LogEntry (String accion, String interfaz, String potencia){
        FileOutputStream log;
        try {
            String data= accion+";"+interfaz+";"+potencia+";"+ Calendar.getInstance().getTime().toString()+"\n";
            log = openFileOutput("log.csv", Context.MODE_APPEND);
            log.write(data.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}