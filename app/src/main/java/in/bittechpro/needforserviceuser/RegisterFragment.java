package in.bittechpro.needforserviceuser;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    View view;
    static FloatingActionButton btn_register;
    HashMap<String, String> params;
    String d_name,sup_name;
    EditText d_txt,sup_txt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);

        d_txt = view.findViewById(R.id.device_name);
        sup_txt = view.findViewById(R.id.sup_name);

        SharedPreferences sharedpreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SPrefManager.PREF_NAME, Context.MODE_PRIVATE);

        if (sharedpreferences.contains("LOGGED")){
            if (sharedpreferences.getInt(SPrefManager.LOGGED, 1) == 0) {
                d_txt.setText(sharedpreferences.getString(SPrefManager.DEVICE_NAME,"NULL"));
                sup_txt.setText(sharedpreferences.getString(SPrefManager.SUPERVISOR,"NULL"));
            }
        }

        btn_register = view.findViewById(R.id.submit_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
        btn_register.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(getContext(),UserActivity.class));
                return true;
            }
        });

        return view;
    }

    private void validate() {
        d_name = d_txt.getText().toString().trim().toUpperCase();
        sup_name = sup_txt.getText().toString().trim();

        if(!d_name.isEmpty() && !sup_name.isEmpty() && sup_name.length()==10){

            String a_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

            params = new HashMap<>();

            params.put("d_name",d_name);
            params.put("sup_name",sup_name);
            params.put("a_id",a_id);

            btn_register.hide();

            asyncTask(params,getActivity(),R.id.progressBarRegister);

        } else {
            Toast.makeText(getActivity(), "Oops! Please check the info ", Toast.LENGTH_SHORT).show();
        }

    }

    private static void asyncTask(HashMap<String, String> params, Activity activity, int bar) {

        class RegisterDevice extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;
            private HashMap<String, String> params;
            private Activity activity;
            private int bar;
            private Context context;

            private RegisterDevice(HashMap<String, String> params, Activity activity, int bar) {
                this.params = params;
                this.activity = activity;
                this.bar = bar;
                this.context = activity.getApplicationContext();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(bar!=0) {
                    progressBar = activity.findViewById(bar);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                return requestHandler.sendPostRequest(UrlManager.SET_DEVICE, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                progressBar.setVisibility(View.GONE);
                btn_register.show();
                try {
                    JSONObject result = new JSONObject(s);
                    if(result.getInt("status")==0){
                        SharedPreferences.Editor editor = activity.getSharedPreferences(SPrefManager.PREF_NAME, MODE_PRIVATE).edit();
                        editor.putString(SPrefManager.DEVICE_NAME,params.get("d_name"));
                        editor.putInt(SPrefManager.LOGGED, 1);
                        editor.putString(SPrefManager.SUPERVISOR,params.get("sup_name"));
                        editor.putString(SPrefManager.DEVICE_ID,params.get("a_id"));
                        editor.apply();
                        activity.finish();
                        activity.startActivity(new Intent(context,UserActivity.class));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error in connecting to network \n Try after sometime"+e, Toast.LENGTH_LONG).show();
                }
            }
        }

        RegisterDevice registerDevice = new RegisterDevice(params,activity,bar);
        registerDevice.execute();
    }

}
