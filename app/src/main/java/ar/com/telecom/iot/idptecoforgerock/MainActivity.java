package ar.com.telecom.iot.idptecoforgerock;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.Logger;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.exception.AuthenticationRequiredException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NodeListener<FRUser> {


    private TextView status;
    private Button loginWithBrowserButton;
    private Button logoutButton;
    Button btnGoMenu;
    String TAG;

    private TextView TokenInfoTitle;
    private TextView TokenInfo;
    private TextView IdTokenTitle;
    private TextView IdToken;
    String idTokenstring;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.textViewStatus);
        logoutButton = findViewById(R.id.btnLogOut);
        loginWithBrowserButton = findViewById(R.id.btnLoginWeb);

        //btnGoMenu
        btnGoMenu = findViewById(R.id.btnGoMenu);

        TokenInfoTitle = findViewById(R.id.textViewTokenInfoTitle);
        TokenInfo = findViewById(R.id.textViewTokenInfo);
        IdTokenTitle = findViewById(R.id.textViewIdTokenTitle);
        IdToken = findViewById(R.id.textViewidToken);

        Logger.set(Logger.Level.DEBUG);
        FRAuth.start(this);

        TAG = MainActivity.class.getName();
        updateStatus();

        logoutButton.setOnClickListener(view -> {
            FRUser.getCurrentUser().logout();
            updateStatus();
        });


        loginWithBrowserButton.setOnClickListener(view -> FRUser.browser().appAuthConfigurer().authorizationRequest(r -> {
            //additionalParameters
            HashMap<String, String> additionalParameters = new HashMap<>();
            additionalParameters.put("acr_values", "Login2");
            r.setAdditionalParameters(additionalParameters);
            r.setLoginHint("testidpperf125@fakemail.com");

        }).done().login(this, this));

        btnGoMenu.setOnClickListener(view -> {

            //start activity if user is logged in
            if (FRUser.getCurrentUser() != null) {
                //Go to Menu, passing the idTokenstring
                Intent intent = new Intent(this, MainMenu.class);
                intent.putExtra("idTokenstring", idTokenstring);
                startActivity(intent);
            } else {
                //show error mensaje
                Toast.makeText(this, "You must be logged in to access this menu", Toast.LENGTH_SHORT).show();
                updateStatus();

            }
        });


    }

    //on resume update status
    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }

    private void updateStatus() {
        runOnUiThread(() -> {
            if (FRUser.getCurrentUser() == null) {
                status.setText("User is not authenticated.");
                status.setTextColor(Color.RED);
                logoutButton.setEnabled(false);
                loginWithBrowserButton.setEnabled(true);
                loginWithBrowserButton.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.GONE);
                TokenInfoTitle.setVisibility(View.GONE);
                TokenInfo.setVisibility(View.GONE);
                IdTokenTitle.setVisibility(View.GONE);
                IdToken.setVisibility(View.GONE);
                btnGoMenu.setVisibility(View.GONE);
            } else {
                status.setText("User is authenticated.");
                //set statuc color to GREEN
                status.setTextColor(Color.GREEN);
                logoutButton.setEnabled(true);
                logoutButton.setVisibility(View.VISIBLE);
                loginWithBrowserButton.setEnabled(false);
                loginWithBrowserButton.setVisibility(View.GONE);
                TokenInfoTitle.setVisibility(View.VISIBLE);
                TokenInfo.setVisibility(View.VISIBLE);
                IdTokenTitle.setVisibility(View.VISIBLE);
                IdToken.setVisibility(View.VISIBLE);
                btnGoMenu.setVisibility(View.VISIBLE);

                try {
                    String token = FRUser.getCurrentUser().getAccessToken().toJson();


                    JSONObject jsonObject = new JSONObject(token);
                    TokenInfo.setText(jsonObject.toString(4));

                    idTokenstring= jsonObject.getString("idToken");

                    String[] chunks = idTokenstring.split("\\.");
                    String payload = chunks[1];
                    String payloadDecoded = new String(android.util.Base64.decode(payload, android.util.Base64.DEFAULT));
                    String header = chunks[0];
                    String headerDecoded = new String(android.util.Base64.decode(header, android.util.Base64.DEFAULT));
                    String signature = chunks[2];


                    IdToken.setText("Header: \n" +
                            new JSONObject(headerDecoded).toString(4)+
                            "\n\nPayload: \n" +
                            new JSONObject(payloadDecoded).toString(4)+
                            "\n\nSignature: \n" +
                            signature
                    );









                } catch (AuthenticationRequiredException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onCallbackReceived(Node node) {
        Logger.debug(TAG,  "onCallbackReceived: " + node.toString());
    }

    @Override
    public void onSuccess(FRUser result) {
        updateStatus();
        Logger.debug(TAG, "Login success");
    }

    @Override
    public void onException(Exception e) {
        Logger.debug(TAG, "Login failed", e);
    }
}