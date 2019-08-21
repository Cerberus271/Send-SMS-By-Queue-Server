package com.hairol2k.vcoresmsbulk;

import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ActiveMQ";
    private static final String CLIENT_ID = "ExampleAndroidClient";

    private String SERVER_URI;
    private String USERNAME;
    private String PASSWORD;
    private String SUBSCRIBE_TOPIC;

    private MqttAndroidClient clientMQ;
    private MqttConnectOptions connectOptionsMQ;

    private Switch swPowerONOF;
    private TextView txtStatus;
    private LottieAnimationView animation_view;
    private EditText serverName;
    private EditText user;
    private EditText pass;
    private EditText queueName;

    //Expand Configuration
    private NestedScrollView nested_scroll_view;
    private View lyt_expand_save_config;
    private ImageButton bt_toggle_save_config;
    private Button btn_hide_save_config;
    private Button btn_Save_config;

    private  int TimeDelay = 10;
    final Handler handler = new Handler();
    private Spinner spinnerDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_WIFI_STATE,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.RECEIVE_SMS,
                        android.Manifest.permission.READ_SMS,
                        Manifest.permission.SEND_SMS},
                1);

        connectOptionsMQ = new MqttConnectOptions();
        initViews();
        initPreference();

        if(!validateConfig()){
            txtStatus.setText("Configuración\nVacia");
        }
    }

    private void initPreference() {
        SERVER_URI = Preference.getServerName(getApplicationContext());
        USERNAME = Preference.getServerUser(getApplicationContext());
        PASSWORD = Preference.getServerPass(getApplicationContext());
        SUBSCRIBE_TOPIC = Preference.getQueueName(getApplicationContext());
        TimeDelay  = Preference.getDelay(getApplicationContext());
        serverName.setText(SERVER_URI);
        user.setText(USERNAME);
        pass.setText(PASSWORD);
        queueName.setText(SUBSCRIBE_TOPIC);

        if(TimeDelay == 10) spinnerDelay.setSelection(0);
        if(TimeDelay == 15) spinnerDelay.setSelection(1);
        if(TimeDelay == 20) spinnerDelay.setSelection(2);

    }

    private void savePreference() {
        Preference.setServerName(getApplicationContext(), serverName.getText().toString());
        Preference.setServerUser(getApplicationContext(), user.getText().toString());
        Preference.setServerPass(getApplicationContext(), pass.getText().toString());
        Preference.setQueueName(getApplicationContext(), queueName.getText().toString());
        Preference.setDelay(getApplicationContext(), TimeDelay);
    }

    private boolean validateConfig() {

        if (serverName.getText().toString().isEmpty() ||
                user.getText().toString().isEmpty() ||
                pass.getText().toString().isEmpty() ||
                queueName.getText().toString().isEmpty()) {
            return false;
        }

        return true;
    }

    private void connectMQUI() {
        animation_view.setAnimation(R.raw.success);
        animation_view.playAnimation();
        txtStatus.setText("Conectado");
    }

    private void disconnectMQUI(String msg) {
        animation_view.setAnimation(R.raw.error);
        animation_view.playAnimation();
        txtStatus.setText(msg);
        swPowerONOF.setChecked(false);
    }

    private void initViews() {

        serverName = (EditText) findViewById(R.id.txtServerName);
        queueName = (EditText) findViewById(R.id.txtQueueName);
        user = (EditText) findViewById(R.id.txtUser);
        pass = (EditText) findViewById(R.id.txtPass);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        animation_view = (LottieAnimationView) findViewById(R.id.animation_view);

        spinnerDelay =(Spinner)findViewById(R.id.cmbDelayTime);
        spinnerDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0)TimeDelay = 10;
                if(position == 1)TimeDelay = 15;
                if(position == 2)TimeDelay = 20;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        swPowerONOF = (Switch) findViewById(R.id.swPowerONOF);
        disconnectMQUI("Desconectado");

        swPowerONOF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (validateConfig()) {
                        getMqttConnectOptions();
                        connect();
                    } else {
                        swPowerONOF.setChecked(false);
                        Snackbar.make(findViewById(android.R.id.content), "Rellenar valores requeridos en configuración", Snackbar.LENGTH_SHORT).show();
                    }

                } else {
                    if (validateConfig()) {
                        disconnect();
                    } else {
                        swPowerONOF.setChecked(false);
                        Snackbar.make(findViewById(android.R.id.content), "Rellenar valores requeridos en configuración", Snackbar.LENGTH_SHORT).show();

                    }
                }
            }
        });

        bt_toggle_save_config = (ImageButton) findViewById(R.id.bt_toggle_save_config);
        btn_hide_save_config = (Button) findViewById(R.id.btn_hide_config);
        btn_Save_config = (Button) findViewById(R.id.btn_Save_config);
        lyt_expand_save_config = (View) findViewById(R.id.lyt_expand_save_config);
        lyt_expand_save_config.setVisibility(View.GONE);

        bt_toggle_save_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionSaveConfig(bt_toggle_save_config, lyt_expand_save_config);
            }
        });

        btn_hide_save_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionSaveConfig(bt_toggle_save_config, lyt_expand_save_config);
            }
        });

        btn_Save_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreference();
                initPreference();
                toggleSectionSaveConfig(bt_toggle_save_config, lyt_expand_save_config);

                Snackbar.make(findViewById(android.R.id.content), "Se guardó tu configuración", Snackbar.LENGTH_SHORT).show();

            }
        });

        nested_scroll_view = (NestedScrollView) findViewById(R.id.nested_scroll_view);
    }

    private void disconnect() {

        try {

            if (clientMQ != null) {
                clientMQ.disconnect(connectOptionsMQ, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        disconnectMQUI("Desconectado");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                        disconnectMQUI("Error al desconectar al servidor de colas");
                    }
                });
            }

        } catch (MqttException e) {
            disconnectMQUI("Error al desconectar al servidor de colas");
        }
    }

    private void connect() {

        clientMQ = new MqttAndroidClient(this, SERVER_URI, CLIENT_ID);
        try {
            clientMQ.connect(connectOptionsMQ, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    subscribe();
                    connectMQUI();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                    disconnectMQUI("Error al conectar con servidor de colas");

                }
            });
        } catch (MqttException e) {
            disconnectMQUI("Error al conectar con servidor de colas");
        }
    }

    private void getMqttConnectOptions() {
        connectOptionsMQ.setAutomaticReconnect(true);
        connectOptionsMQ.setCleanSession(false);
        connectOptionsMQ.setUserName(USERNAME);
        connectOptionsMQ.setPassword(PASSWORD.toCharArray());
        connectOptionsMQ.setConnectionTimeout(10);
        connectOptionsMQ.setKeepAliveInterval(20);
    }

    private void subscribe() {
        try {
            clientMQ.subscribe(SUBSCRIBE_TOPIC, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(final String topic, final MqttMessage message) throws Exception {

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(!message.toString().isEmpty()){
                                        Gson gson = new Gson();
                                        Message object = gson.fromJson(message.toString(), Message.class);
                                        String SENT = "SMS_SENT";
                                        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
                                        SmsManager.getDefault().sendTextMessage(object.getPhone(), null, object.getMessage(), sentPI, null);
                                        Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this, "El mensaje esta vacio", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }, TimeDelay*1000);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void toggleSectionSaveConfig(View view, final View lyt_expand) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimation.expand(lyt_expand, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    nestedScrollTo(nested_scroll_view, lyt_expand);
                }
            });
        } else {
            ViewAnimation.collapse(lyt_expand);
        }
    }

    public static void nestedScrollTo(final NestedScrollView nested, final View targetView) {
        nested.post(new Runnable() {
            @Override
            public void run() {
                nested.scrollTo(500, targetView.getBottom());
            }
        });
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }
}
