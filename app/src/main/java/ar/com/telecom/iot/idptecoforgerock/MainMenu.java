package ar.com.telecom.iot.idptecoforgerock;

import static ar.com.telecom.iot.idptecoforgerock.RequestMaker.baseurl;

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

    TextView LoginStatusTextView;
    Button btnLogOut;
    Button btnGetUserInfo;
    Button btnGetDevices;
    Button btnPairDeviceViaQR;
    Button btnPairDeviceViaMiniSDKApMode;

    String resultData;
    TextView lblTextInfo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        RequestMaker.idTokenstring = getIntent().getStringExtra("idTokenstring");
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
            String userInfoUrl = baseurl + "/tuya/apps/user";
            RequestMaker.makeRequest(userInfoUrl, callbackMensajePantalla);
        });

        btnGetDevices = findViewById(R.id.btnGetDevices);
        btnGetDevices.setOnClickListener(v -> {
            String devicesUrl = baseurl + "/tuya/users/devices";
            RequestMaker.makeRequest(devicesUrl, callbackDeviceNewActivity);
        });

        btnPairDeviceViaQR = findViewById(R.id.btnPairDeviceViaQR);
        btnPairDeviceViaQR.setOnClickListener(v -> {
            IntentIntegrator qrScan = new IntentIntegrator(this);
            qrScan.setPrompt("Scan a QR to Pair device");
            qrScan.setOrientationLocked(true);

            qrScan.initiateScan();


        });

        btnPairDeviceViaMiniSDKApMode = findViewById(R.id.btnPairDeviceViaMiniSDKApMode);
        btnPairDeviceViaMiniSDKApMode.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, MiniSDKPairing.class);
            startActivity(intent);
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
                String pairDeviceUrl = baseurl + "/devices/pairViaQR";
                RequestBody body = RequestBody.create(jsonBody, okhttp3.MediaType.parse("application/json; charset=utf-8"));
                RequestMaker.makePostRequest(pairDeviceUrl, body, callbackMensajePantalla);

            }
        }
    }


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
                         // Catch Attempt to invoke interface method 'java.util.Iterator java.util.List.iterator()' on a null object reference

                         resultData = myResponse;
                         //console log response
                         System.out.println(resultData);
                         lblTextInfo.setText(myResponse);
                     }
                 });
             }
         }
     };


    Callback callbackDeviceNewActivity = new okhttp3.Callback() {
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
                        //start user devices activity, and send response as extra
                        Intent intent = new Intent(MainMenu.this, userDevices.class);
                        intent.putExtra("devices", resultData);
                        startActivity(intent);
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