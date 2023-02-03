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

import org.forgerock.android.auth.FRUser;

import okhttp3.Callback;

public class MiniSDKPairing extends AppCompatActivity {


    TextView LoginStatusTextView;
    Button btnLogOut;
    Button btnObtenerSSID;
    Button btnPairViaAPMode;

    EditText txtssid;
    EditText txtPassWord;

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

        btnObtenerSSID = findViewById(R.id.btnObtenerSSID);

        btnObtenerSSID.setOnClickListener(v -> {
            ObtenerSSID();
        });


        btnPairViaAPMode = findViewById(R.id.btnPairViaAPMode);

        btnPairViaAPMode.setOnClickListener(v -> {
            PairViaAPMode();
        });

    }


    public void PairViaAPMode(){
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