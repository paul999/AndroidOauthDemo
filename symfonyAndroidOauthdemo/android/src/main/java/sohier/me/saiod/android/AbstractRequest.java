package sohier.me.saiod.android;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.api.client.auth.oauth2.Credential;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequest<T> extends Request<T> {

    private final Map<String, String> headers;
    private final Response.Listener<T> listener;

    protected Object bodyObject = null;

    /**
     * Create the URL for calling the API.
     * @param HOST Hostname
     * @param cred Credentials for the oAuth api.
     * @param url The path for the API.
     * @return full URL.
     */
    private static String makeUrl(String HOST, Credential cred, String url) {
        url = HOST + url;
        url += "?access_token=";
        url += cred.getAccessToken();

        Log.d("AbstractRequest", "URL: " + url);

        return url;
    }

    /**
     *
     * @param method HTTP method
     * @param path Path of the call
     * @param host hostname
     * @param creds Credentials
     * @param headers Headers for the HTTP request
     * @param listener Response listener
     * @param errorListener error Listener
     */
    public AbstractRequest(int method, String path, String host, Credential creds, Map<String, String> headers, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, makeUrl(host, creds, path), errorListener);

        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put("Accept", "application/json");

        this.headers = headers;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        if (bodyObject == null)
        {
            return super.getBody();
        }
        Gson gson = new Gson();

        try {
            Log.d("saiod", new String(gson.toJson(bodyObject).getBytes("utf-8")));

            byte[] bt = gson.toJson(bodyObject).getBytes("utf-8");
            return bt;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }
}

