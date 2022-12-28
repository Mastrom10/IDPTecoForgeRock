package ar.com.telecom.iot.idptecoforgerock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.forgerock.android.auth.FRUser;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainMenu extends AppCompatActivity {

    String idTokenstring;
    TextView LoginStatusTextView;
    Button btnLogOut;
    Button btnGetUserInfo;
    Button btnGetDevices;
    Button btnPairDeviceViaQR;
    String resultData;
    TextView lblTextInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        idTokenstring = getIntent().getStringExtra("idTokenstring");
        LoginStatusTextView = findViewById(R.id.LoginStatusText);
        CheckLogin();

        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(v -> {
            FRUser.getCurrentUser().logout();
            finish();

        });

        lblTextInfo = findViewById(R.id.lblTextInfo);
        btnGetUserInfo = findViewById(R.id.btnGetUserInfo);

        btnGetUserInfo.setOnClickListener(v -> {
            String userInfoUrl = "https://nmtuya.apps.k8s.cablevision-labs.com.ar/tuya/apps/user";
            makeRequest(userInfoUrl, callbackMensajePantalla);
        });

        btnGetDevices = findViewById(R.id.btnGetDevices);
        btnGetDevices.setOnClickListener(v -> {
            String devicesUrl = "https://nmtuya.apps.k8s.cablevision-labs.com.ar/tuya/users/devices";
            makeRequest(devicesUrl, callbackMensajePantalla);
        });

        btnPairDeviceViaQR = findViewById(R.id.btnPairDeviceViaQR);
        btnPairDeviceViaQR.setOnClickListener(v -> {
            IntentIntegrator qrScan = new IntentIntegrator(this);
            qrScan.setPrompt("Scan a QR to Pair device");
            qrScan.setOrientationLocked(true);

            qrScan.initiateScan();


        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Error al escanear el código QR", Toast.LENGTH_SHORT).show();
            } else {
                resultData = result.getContents();
                lblTextInfo.setText(resultData);
                //json string with qrcode = resultData, asset_id = 1570448563990822, time_zone_id = "America/Argentina/Buenos_Aires"
                String jsonBody  = "{\"qrcode\":\"" + resultData + "\",\"asset_id\":\"1570448563990822\",\"time_zone_id\":\"America/Argentina/Buenos_Aires\"}";
                String pairDeviceUrl = "https://nmtuya.apps.k8s.cablevision-labs.com.ar/devices/pairViaQR";
                RequestBody body = RequestBody.create(jsonBody, okhttp3.MediaType.parse("application/json; charset=utf-8"));
                makePostRequest(pairDeviceUrl, body, callbackMensajePantalla);

            }
        }
    }

    void makeRequest(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + idTokenstring)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

    void makePostRequest(String url, RequestBody requestBody, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + idTokenstring)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }



    //makePostRequest


    Callback callbackMensajePantalla = new okhttp3.Callback() {
         @Override
         public void onFailure(okhttp3.Call call, java.io.IOException e) {
             e.printStackTrace();
         }

         @Override
         public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
             if (response.isSuccessful()) {
                 final String myResponse = response.body().string();
                 MainMenu.this.runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         resultData = myResponse;
                         lblTextInfo.setText(myResponse);
                     }
                 });
             }
         }
     };

    void CheckLogin(){
        if (FRUser.getCurrentUser() != null) {
            LoginStatusTextView.setText("Logged in");
            LoginStatusTextView.setTextColor(Color.GREEN);
        } else {
            LoginStatusTextView.setText("Not logged in");
            LoginStatusTextView.setTextColor(Color.RED);
        }
    }

    //onscreenorientationchange
    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        CheckLogin();
        lblTextInfo.setText(resultData);
    }
}