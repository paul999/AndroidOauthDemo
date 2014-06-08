package sohier.me.saiod.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson.JacksonFactory;
import com.wuman.android.auth.AuthorizationDialogController;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static PlaceholderFragment fragment;
    private static OAuthManager manager;
    private static Credential creds;

    private static final String CLIENT_ID = "4_93m61sebqts8s4os0wc0884w4cw88kks00so84gkcccw00ks8";
    private static final String CLIENT_SECRET = "4toqis1xbg8w0oswg440g40cgo0cg8c084cw0w0s88s8wgs0g8";
    private static final String HOST = "http://ip-6.nl/app_dev.php";
    private static final String API_HOST = "http://api.ip-6.nl/app_dev.php";
    private static final String AUTHORIZE = "/oauth/v2/auth";
    private static final String REQUEST_TOKEN = "/oauth/v2/token";

    private static ArrayAdapter<Demo> adapter;
    private static Handler handler = new Handler() {};
    private static DemoDataSource datasource;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datasource = new DemoDataSource(this);
        datasource.open();

        queue = Volley.newRequestQueue(this);

        OAuthManager.OAuthCallback<Credential> callback = new OAuthManager.OAuthCallback<Credential>() {
            @Override
            public void run(OAuthManager.OAuthFuture<Credential> future) {
                try {
                    creds = future.getResult();

                    Log.d("oauth login", "Login succesfull. Token: "  + creds.getAccessToken() + " Time: " + creds.getExpiresInSeconds());

                    refreshData();

                } catch (IOException e) {
                    Log.e("Oauth error", "IO error during oauth", e);
                }
            }
        };

        getManager().authorizeExplicitly("userId", callback, null);

        fragment = null;
        if (savedInstanceState == null) {
            fragment = new PlaceholderFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (datasource == null)
        {
            datasource = new DemoDataSource(this);
        }
        datasource.open();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        datasource.close();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        datasource.close();
    }

    private void refreshData()
    {
        refreshToken(new CallBackInterface() {
            @Override
            public void call() {
                final Response.Listener<DemoResult> rs = new Response.Listener<DemoResult>(){

                    @Override
                    public void onResponse(DemoResult demoResult) {
                        Log.d("saiod", "Got data...");

                        Log.d("saiod", "size: " + demoResult.demos.length);
                    }
                };
                GsonRequest<DemoResult> rq = new GsonRequest<DemoResult>(Request.Method.GET, API_HOST, creds, "/demos", DemoResult.class, null, rs, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("saiod", "Error during request to the server: " + error);

                        throw new RuntimeException();
                    }
                });
                queue.add(rq);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            CreateDemoDialog cdd = new CreateDemoDialog();
            cdd.show(getFragmentManager(), "");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ArrayList<String> data = new ArrayList<String>();

            List<Demo> values;

            if (creds != null)  {
                values = datasource.getAllDemos();
            }
            else
            {
                values = new ArrayList<Demo>();
            }

            adapter = new ArrayAdapter<Demo>(getActivity(), android.R.layout.simple_list_item_1, values);

            ListView dataList = (ListView) rootView.findViewById(R.id.listView);
            dataList.setAdapter(adapter);

            dataList.setLongClickable(true);

            dataList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    Log.v("long clicked","pos: " + pos);

                    datasource.deleteDemo(adapter.getItem(pos));
                    adapter.remove(adapter.getItem(pos));
                    adapter.notifyDataSetChanged();

                    return true;
                }
            });

            return rootView;
        }
    }
    public class CreateDemoDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View vw = inflater.inflate(R.layout.dialog_add_demo, null);
            final EditText title = (EditText)vw.findViewById(R.id.title);
            final EditText desc = (EditText)vw.findViewById(R.id.description);

            builder.setView(vw)
                    // Add action buttons
                    .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            refreshToken(new CallBackInterface() {
                                @Override
                                public void call() {

                                    Demo data = new Demo();
                                    data.setDescription(desc.getText().toString());
                                    data.setTitle(title.getText().toString());

                                    final Response.Listener<String> rs = new Response.Listener<String>(){
                                        @Override
                                        public void onResponse(String demoResult) {
                                            Log.d("saiod", "Got data..." + demoResult);

                                            // Lets call refreshData :)
                                            refreshData();
                                        }
                                    };
                                    PostRequest rq = new PostRequest(API_HOST, creds, "/demos", null, rs, new Response.ErrorListener(){
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("saiod", "Error during request to the server: " + error);

                                            error.printStackTrace();

                                            throw new RuntimeException();
                                        }
                                    }, data);

                                    queue.add(rq);
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CreateDemoDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }

    /**
     * Create a OAuthManager based on our configuration. The manager is only created once, and after
     * that the same manager is returned.
     *
     * Because we don't use the support fragment manager, we need to use the android build in
     * manager for creating the DialogFragmentController.
     *
     * The getRedirectUri is the URI we used when creating the client.
     *
     * @return OAuthManager manager
     */
    public OAuthManager getManager() {
        if (manager == null) {
            AuthorizationDialogController controller;

            controller = new DialogFragmentController(this.getFragmentManager()) {
                @Override
                public boolean isJavascriptEnabledForWebView() {
                    return true;
                }

                @Override
                public String getRedirectUri() throws IOException {
                    return "http://android.local/";
                }
            };

            SharedPreferencesCredentialStore credentialStore =
                    new SharedPreferencesCredentialStore(this.getApplication(),
                            "saiod", new JacksonFactory());


            Log.d("oauthdebug", "HOST" + HOST + " token " + REQUEST_TOKEN);
            GenericUrl url = new GenericUrl(HOST + REQUEST_TOKEN);

            AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                    BearerToken.authorizationHeaderAccessMethod(),
                    AndroidHttp.newCompatibleTransport(),
                    new JacksonFactory(),
                    url,
                    new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
                    CLIENT_ID,
                    HOST + AUTHORIZE);
            builder.setCredentialStore(credentialStore);
            //builder.setScopes(Arrays.asList("scope1", "scope2"));

            AuthorizationFlow flow = builder.build();

            manager = new OAuthManager(flow, controller);
        }
        return manager;
    }

    /**
     * This is used with refreshToken to make sure the token is still valid, and if it needs to be
     * refreshed that it will be refreshed before making the actual call to the API.
     *
     * Please note that if you want to update UI stuff in the call method, that you need to use a
     * handler because refreshToken runs in a seperate Thread.
     */
    public interface CallBackInterface {
        /**
         * This method is called when there is a valid token.
         */
        public void call();

    }

    /**
     * This methods checks if the token needs to be refreshed, and if so it refreshes the token.
     * After that, it calls the callback that is provided.
     *
     * This method runs in a separate thread, it is not required to start it separately yourself :)
     *
     * @param cb Callback with the actual API call.
     */
    public void refreshToken(final CallBackInterface cb) {
        new Thread() {
            @Override
            public void run() {
                if (creds.getExpiresInSeconds() < 0) {
                    Log.d("refreshToken", "Token expired: " + creds.getExpiresInSeconds());
                    try {
                        boolean rs = creds.refreshToken();
                        Log.d("refreshToken", "Result: " + rs);
                    } catch (IOException e) {
                        Log.e("refreshToken", "Refresh token failed", e);
                        return;
                    }
                } else {
                    Log.d("refreshToken", "Token not expired: " + creds.getExpiresInSeconds());
                }
                cb.call();
            }
        }.start();
    }
}
