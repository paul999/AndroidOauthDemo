package sohier.me.saiod.android;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.api.client.auth.oauth2.Credential;

import java.util.Map;

public class DeleteRequest extends AbstractRequest<String> {

    /**
     * @param path          Path of the call
     * @param host          hostname
     * @param creds         Credentials
     * @param headers       Headers for the HTTP request
     * @param listener      Response listener
     * @param errorListener error Listener
     */
    public DeleteRequest(String path, String host, Credential creds, Map<String, String> headers, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.DELETE, path, host, creds, headers, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            if (response.statusCode == 204) {
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
