package sohier.me.saiod.android;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static PlaceholderFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            fragment = new PlaceholderFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
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

        public ArrayAdapter<Demo> adapter;
        public Handler handler = new Handler() {};
        public DemoDataSource datasource;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ArrayList<String> data = new ArrayList<String>();

            datasource = new DemoDataSource(this.getActivity());
            datasource.open();

            List<Demo> values = datasource.getAllDemos();

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
    public static class CreateDemoDialog extends DialogFragment {
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


                            final Demo d = fragment.datasource.createDemo(title.getText().toString(), desc.getText().toString());
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    fragment.adapter.add(d);
                                    fragment.adapter.notifyDataSetChanged();
                                    Log.d("saiod", "changed.");
                                }
                            };
                            fragment.handler.post(r);
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
}
