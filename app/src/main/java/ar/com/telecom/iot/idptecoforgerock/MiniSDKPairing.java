package ar.com.telecom.iot.idptecoforgerock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.forgerock.android.auth.FRUser;

import okhttp3.Callback;

import com.tuya.smart.config.TuyaConfig;

public class MiniSDKPairing extends AppCompatActivity {


    TextView LoginStatusTextView;
    TextView lblPairingtokenValue;
    TextView lblPairingStatusValue;
    Button btnLogOut;
    Button btnObtenerSSID;
    Button btnGetToken;
    Button btnCheckStatus;
    Button btnPairDevice;

    EditText txtssid;
    EditText txtPassWord;

    String LastPairToken;
    String LastAuthToken;


    private static final int REQUEST_FINE_LOCATION = 1;
    private static String[] PERMISSIONS_FINE_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_sdkpairing);

        LoginStatusTextView = findViewById(R.id.loginStatusText);
        CheckLogin();

        btnLogOut = findViewById(R.id.btnLogOut2);
        btnLogOut.setOnClickListener(v -> {
            FRUser.getCurrentUser().logout();
            finish();

        });

        txtssid = findViewById(R.id.txtssid);
        txtPassWord = findViewById(R.id.txtPassWord);
        lblPairingtokenValue = findViewById(R.id.lblPairingtokenValue);


        btnObtenerSSID = findViewById(R.id.btnObtenerSSID);

        btnObtenerSSID.setOnClickListener(v -> {
            ObtenerSSID();
        });


        btnGetToken = findViewById(R.id.btnGetToken);

        btnGetToken.setOnClickListener(v -> {
            GetPairingToken();
        });

        lblPairingStatusValue = findViewById(R.id.lblPairingStatusValue);
        btnCheckStatus = findViewById(R.id.btnCheckStatus);

        //CheckStatus()
        btnCheckStatus.setOnClickListener(v -> {
            CheckStatus();
        });

        btnPairDevice = findViewById(R.id.btnPairDevice);
        btnPairDevice.setOnClickListener(v -> {
            PairDevice();
        });



    }


    public void GetPairingToken(){
        String url = RequestMaker.baseurl + "/tuya/device/paring/token";
        RequestMaker.makeRequest(url, callbackGetToken);
    }

    Callback callbackGetToken = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, java.io.IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
            if (response.isSuccessful()) {
                final String myResponse = response.body().string();
                MiniSDKPairing.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Parsear la respuesta a un objeto JsonObject
                        JsonObject  json = JsonParser.parseString(myResponse).getAsJsonObject();

                        // Obtener el objeto result
                        JsonObject result = json.get("result").getAsJsonObject();

                        // Obtener los valores deseados
                        String token = result.get("token").getAsString();
                        String secret = result.get("secret").getAsString();
                        String region = result.get("region").getAsString();

                        String authToken = region + token + secret;
                        LastPairToken = token;
                        LastAuthToken = authToken;
                        lblPairingtokenValue.setText(token);
                        lblPairingStatusValue.setText("Token Obtained");
                    }
                });
            }
        }
    };

    private static final int REQUEST_CODE_CHANGE_NETWORK_STATE = 2;
    private static String[] PERMISSIONS_CHANGE_NETWORK_STATE = {Manifest.permission.CHANGE_NETWORK_STATE};

    void PairDevice(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso
            ActivityCompat.requestPermissions(this, PERMISSIONS_CHANGE_NETWORK_STATE, REQUEST_CODE_CHANGE_NETWORK_STATE);
        } else {
            String ssid = txtssid.getText().toString();
            String password = txtPassWord.getText().toString();

            TuyaConfig.getAPInstance().stopConfig();
            TuyaConfig.getAPInstance().startConfig(MiniSDKPairing.this,ssid,password,LastAuthToken);
            //TuyaConfig.getEZInstance().stopConfig();
            //TuyaConfig.getEZInstance().startConfig(ssid,password,LastAuthToken);
            lblPairingStatusValue.setText("Pairing...");
        }



    }

    void CheckStatus(){

        String url = RequestMaker.baseurl + "/devices/tokens/" + LastPairToken;
        RequestMaker.makeRequest(url, callbackCheckStatus);
    }

    Callback callbackCheckStatus = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, java.io.IOException e) {

            e.printStackTrace();
        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
            if (response.isSuccessful()) {
                final String myResponse = response.body().string();
                MiniSDKPairing.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lblPairingStatusValue.setText(myResponse);

                        Toast.makeText(MiniSDKPairing.this, myResponse, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };




    void ObtenerSSID(){
        // Verificar si se tienen permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso
            ActivityCompat.requestPermissions(this, PERMISSIONS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        } else {
            ActualizarSSID();
        }
    }

    void ActualizarSSID(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssidWithQuotes = wifiInfo.getSSID();
        String ssid = ssidWithQuotes.substring(1, ssidWithQuotes.length() - 1);

        txtssid.setText(ssid);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                ActualizarSSID();
            } else {
                // Permiso denegado
                Toast.makeText(this, "Se requiere acceder a la ubicacion para obtener el SSID", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CODE_CHANGE_NETWORK_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                String ssid = txtssid.getText().toString();
                String password = txtPassWord.getText().toString();

                TuyaConfig.getAPInstance().startConfig(MiniSDKPairing.this,ssid,password,LastAuthToken);
                lblPairingStatusValue.setText("Pairing...");
            } else {
                // Permiso denegado
                Toast.makeText(this, "Se requieren permiso de red  para hacer pairing de dispositivos.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    void CheckLogin(){
        if (FRUser.getCurrentUser() != null) {
            LoginStatusTextView.setText("Logged in");
            LoginStatusTextView.setTextColor(Color.GREEN);
        } else {
            LoginStatusTextView.setText("Not logged in");
            LoginStatusTextView.setTextColor(Color.RED);
        }
    }
}