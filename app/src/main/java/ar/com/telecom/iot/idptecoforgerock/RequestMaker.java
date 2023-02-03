package ar.com.telecom.iot.idptecoforgerock;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestMaker {

    public static String idTokenstring;

    public static void makeRequest(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + idTokenstring)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

    public static void makePostRequest(String url, RequestBody requestBody, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + idTokenstring)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

}
