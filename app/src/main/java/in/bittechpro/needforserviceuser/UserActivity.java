package in.bittechpro.needforserviceuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {



    HashMap<String, String> params;

    TextView txt_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle("");
        }

        SharedPreferences sharedpreferences = getSharedPreferences(SPrefManager.PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if (sharedpreferences.contains(SPrefManager.LOGGED)){
            if (sharedpreferences.getInt(SPrefManager.LOGGED, 0) == 0) {
                finish();
                startActivity(new Intent(UserActivity.this, LoginActivity.class));
                Log.d("pppp", "logged ");
            }
        }


        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        if (sharedpreferences.contains(SPrefManager.DEVICE_ID)) {
            String id = sharedpreferences.getString(SPrefManager.DEVICE_ID, "OOPS");
            if(!Objects.requireNonNull(id).equals("OOPS")){
                params= new HashMap<>();
                params.put("device_id",id);

                asyncTask(params,this,R.id.user_Pbar);
            }else {
                Toast.makeText(this, "Some error occured /n Please register again", Toast.LENGTH_LONG).show();
                editor.clear();
                editor.apply();
            }
        }
        else{
            Toast.makeText(this, "Some error occured /n Please register again", Toast.LENGTH_LONG).show();
            editor.clear();
            editor.apply();
            finish();
        }

        if (sharedpreferences.contains(SPrefManager.EMP_ID)){
            editor.remove(SPrefManager.EMP_ID);
            editor.remove(SPrefManager.TEMP_BUTTONS);
            editor.commit();
        }

        txt_info = findViewById(R.id.txt_info);
        String info = "DEVICE NAME : "+sharedpreferences.getString(SPrefManager.DEVICE_NAME,"")+"\nSUPERVISOR : "+sharedpreferences.getString(SPrefManager.SUPERVISOR,"");
        txt_info.setText(info);

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
                return requestHandler.sendPostRequest(UrlManager.GET_DEVICE_STATE, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("oooooo",s);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject result = new JSONObject(s);
                    if(result.getInt("status")==0){

                        JSONObject button = result.getJSONObject("button");
                        int [] state = new int[button.length()];

                        JSONObject names = result.getJSONObject("name");
                        String[] name = new String[names.length()];

                        for (int i = 0; i<button.length();i++) {
                                state[i] = button.getInt(String.valueOf(i+1));
                                name[i] = names.getString(String.valueOf(i+1));
                        }

                        GridView gridView;
                        gridView = activity.findViewById(R.id.gridUser);
                        gridView.setAdapter(new UserAdapter(activity,name,state));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error in connecting to network \n Try refresh", Toast.LENGTH_LONG).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.finish();
                            activity.startActivity(new Intent(context,UserActivity.class));
                        }
                    }, 5000);
                }
            }
        }

        RegisterDevice registerDevice = new RegisterDevice(params,activity,bar);
        registerDevice.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_emp:
                setEmp("");
                return true;
            case R.id.menu_logout:
                logout();
                return true;
            case R.id.menu_refresh:
                finish();
                startActivity(new Intent(UserActivity.this,UserActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to Logout ?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = getSharedPreferences(SPrefManager.PREF_NAME,MODE_PRIVATE).edit();
                editor.putInt(SPrefManager.LOGGED,0);
                editor.apply();
                Intent intent = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

    }

    private void setEmp(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("ViewHolder") View alertView = inflater.inflate(R.layout.view_employee, null);
        final EditText emp_id = alertView.findViewById(R.id.emp_no_txt);
        TextView error = (TextView)alertView.findViewById(R.id.textViewError);
        //Button enter = alertView.findViewById(R.id.but_emp_enter);
        error.setText(s);
        builder.setView(alertView);
        builder.setTitle("Enter Employee ID / Number");
        builder.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String emp = emp_id.getText().toString().trim();

                SharedPreferences sharedPreferences = getSharedPreferences(SPrefManager.PREF_NAME,MODE_PRIVATE);

                params = new HashMap<>();
                params.put("emp_id",emp);
                params.put("device_id", sharedPreferences.getString(SPrefManager.DEVICE_ID,"NULL"));

                asyncTaskTwo(params,UserActivity.this,R.id.user_Pbar);
                Toast.makeText(UserActivity.this, "Please wait..", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();



                /*String emp = emp_id.getText().toString().trim();

                SharedPreferences sharedPreferences = getSharedPreferences(SPrefManager.PREF_NAME,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if(!emp.isEmpty()){
                    editor.putString(SPrefManager.EMP_ID,emp);
                    editor.apply();
                }*/



    }

    private void asyncTaskTwo(HashMap<String, String> params, Activity activity, int bar) {

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
                return requestHandler.sendPostRequest(UrlManager.EMP_CHECK, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("oooooo",s);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject result = new JSONObject(s);
                    if(result.getInt("status")==0){
                        if (result.getInt("assign")>0){
                            SharedPreferences sharedPreferences = activity.getSharedPreferences(SPrefManager.PREF_NAME,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SPrefManager.EMP_ID, params.get("emp_id"));
                            editor.putString(SPrefManager.TEMP_BUTTONS,result.getString("button"));
                            editor.apply();
                            Toast.makeText(context, "Autheticated Success", Toast.LENGTH_SHORT).show();
                        }
                        else setEmp("You are not authorized!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error in connecting to network \n Try refresh", Toast.LENGTH_LONG).show();
                }
            }
        }

        RegisterDevice registerDevice = new RegisterDevice(params,activity,bar);
        registerDevice.execute();
    }
}
