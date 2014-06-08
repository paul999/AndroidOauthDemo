package sohier.me.saiod.android;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.api.client.auth.oauth2.Credential;

import java.util.Map;

public class PostRequest extends AbstractRequest<String> {

    public PostRequest(String host, Credential creds, String url, Map<String, String> headers,
                       Response.Listener<String> listener, Response.ErrorListener errorListener, Object send) {
        super(Method.POST, url, host, creds, headers, listener, errorListener);

        this.bodyObject = send;
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            if (response.statusCode == 201) {
                return Response.success("", HttpHeaderParser.parseCacheHeaders(response));
            } else if (response.statusCode == 400) {
                Log.e("oauth/postrequest", "Something was wrong with the request.");
                Log.e("oauth/response", new String(
                        response.data, HttpHeaderParser.parseCharset(response.headers)));
                return Response.error(new ErrorRequest(response));
            } else {
                return Response.error(new ParseError());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }
}
