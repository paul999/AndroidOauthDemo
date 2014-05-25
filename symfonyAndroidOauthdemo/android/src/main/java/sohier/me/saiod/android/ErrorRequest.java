package sohier.me.saiod.android;

import com.android.volley.NetworkResponse;

public class ErrorRequest extends com.android.volley.VolleyError {

    private NetworkResponse response;

    public ErrorRequest(NetworkResponse response)
    {
        this.response = response;
    }

    public NetworkResponse getResponse() {
        return response;
    }

    public void setResponse(NetworkResponse response) {
        this.response = response;
    }
}
