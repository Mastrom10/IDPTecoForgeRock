package ar.com.telecom.iot.idptecoforgerock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationServiceConfiguration;

import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.Logger;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.exception.AuthenticationRequiredException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NodeListener<FRUser> {


    private TextView status;
    private Button loginWithBrowserButton;
    private Button logoutButton;
    String TAG;

    private TextView TokenInfoTitle;
    private TextView TokenInfo;
    private TextView IdTokenTitle;
    private TextView IdToken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.textViewStatus);
        logoutButton = findViewById(R.id.btnLogOut);
        loginWithBrowserButton = findViewById(R.id.btnLoginWeb);

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



    }

    private void updateStatus() {
        runOnUiThread(() -> {
            if (FRUser.getCurrentUser() == null) {
                status.setText("User is not authenticated.");
                status.setTextColor(Color.RED);
                logoutButton.setEnabled(false);
                loginWithBrowserButton.setEnabled(true);
                TokenInfoTitle.setVisibility(View.GONE);
                TokenInfo.setVisibility(View.GONE);
                IdTokenTitle.setVisibility(View.GONE);
                IdToken.setVisibility(View.GONE);
            } else {
                status.setText("User is authenticated.");
                //set statuc color to GREEN
                status.setTextColor(Color.GREEN);
                logoutButton.setEnabled(true);
                loginWithBrowserButton.setEnabled(false);
                TokenInfoTitle.setVisibility(View.VISIBLE);
                TokenInfo.setVisibility(View.VISIBLE);
                IdTokenTitle.setVisibility(View.VISIBLE);
                IdToken.setVisibility(View.VISIBLE);

                try {
                    String token = FRUser.getCurrentUser().getAccessToken().toJson();


                    JSONObject jsonObject = new JSONObject(token);
                    TokenInfo.setText(jsonObject.toString(4));

                    String idToken = jsonObject.getString("idToken");

                    String[] chunks = idToken.split("\\.");
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