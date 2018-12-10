package in.bittechpro.needforserviceuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserAdapter extends ArrayAdapter<String> {

        private Activity activity;

        GetImg getImg;

        private int[] state;
        private String[] name;
        private HashMap<String, String> params;
        private SharedPreferences sharedPreferences;

        UserAdapter(Activity c, String[] name,int[] state){
            super(c, R.layout.adapter_user, name);
            this.state = state;
            this.name = name;
            this.activity = c;
        }

        @NonNull
        public View getView(final int position, View view, @NonNull final ViewGroup parent) {
            LayoutInflater inflater = activity.getLayoutInflater();

            @SuppressLint("ViewHolder") View spez = inflater.inflate(R.layout.adapter_user, null, true);

            ImageView img = spez.findViewById(R.id.img_spez);
            TextView txt = spez.findViewById(R.id.txt_spez_name);

            getImg = new GetImg();
            img.setImageResource(getImg.getImg(name[position]));
            txt.setText(name[position]);

            CardView card = spez.findViewById(R.id.card_spez);

            if(state[position]==0){
                card.setCardBackgroundColor(Color.parseColor("#00e676"));
            }else {
                card.setCardBackgroundColor(Color.parseColor("#f44336"));
            }
            sharedPreferences = activity.getSharedPreferences(SPrefManager.PREF_NAME,Context.MODE_PRIVATE);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(state[position]==0){
                        alert(position);
                    } else {
                        if (sharedPreferences.contains(SPrefManager.EMP_ID)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Complaint @ "+name[position]);

                            if(sharedPreferences.getString(SPrefManager.TEMP_BUTTONS,"").contains(":"+String.valueOf(position+1)+":")) {

                                builder.setMessage("Is the complaint with " + name[position] + " solved");


                                builder.setPositiveButton("Yes, Solved", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        String id = sharedPreferences.getString(SPrefManager.DEVICE_ID, "NULL");
                                        String emp = sharedPreferences.getString(SPrefManager.EMP_ID, "NULL");
                                        params = new HashMap<>();
                                        params.put("device_id", id);
                                        params.put("button", String.valueOf(position + 1));
                                        params.put("state", "0");
                                        params.put("emp", emp);

                                        asyncTaskTwo(params, activity, R.id.user_Pbar);
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                            }else{
                                builder.setMessage("You are not assigned for this Complaint");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                            }


                            AlertDialog dialog = builder.create();
                            dialog.setCancelable(false);
                            dialog.show();
                        }else {
                            Toast.makeText(getContext(), "Issue with "+name[position]+" is will be cleared shortly", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            return spez;
        }

    private void alert(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String id = sharedPreferences.getString(SPrefManager.DEVICE_ID,"NULL");

                params = new HashMap<>();
                params.put("device_id",id);
                params.put("button",String.valueOf(position+1));
                params.put("state","1");

                asyncTask(params,activity,R.id.user_Pbar);
            }
        });
        builder.setTitle("Do you have any issue with "+name[position]+" ?");
        //builder.setMessage("If you have any complaint / issues with "+name[position]+", you can proceed by clicking Yes.");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private static void asyncTask(HashMap<String, String> params, Activity activity, int bar) {

        class TaskAsync extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;
            private HashMap<String, String> params;
            private Activity activity;
            private int bar;
            private Context context;

            private TaskAsync(HashMap<String, String> params, Activity activity, int bar) {
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
                return requestHandler.sendPostRequest(UrlManager.UPDATE_STATE, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("oooooo",s);
                if(bar!=0) {
                    progressBar.setVisibility(View.GONE);
                }
                try {
                    JSONObject result = new JSONObject(s);
                    if(result.getInt("status")==0){
                        Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error in connecting to network \n Try refresh ", Toast.LENGTH_LONG).show();
                }
                activity.finish();
                activity.startActivity(new Intent(context,UserActivity.class));
            }
        }

        TaskAsync registerDevice = new TaskAsync(params,activity,bar);
        registerDevice.execute();
    }

    private static void asyncTaskTwo(HashMap<String, String> params, Activity activity, int bar) {

        class TaskAsync extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;
            private HashMap<String, String> params;
            private Activity activity;
            private int bar;
            private Context context;

            private TaskAsync(HashMap<String, String> params, Activity activity, int bar) {
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
                return requestHandler.sendPostRequest(UrlManager.UPDATE_STATE, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("oooooo",s);
                if(bar!=0) {
                    progressBar.setVisibility(View.GONE);
                }
                try {
                    JSONObject result = new JSONObject(s);
                    if(result.getInt("status")==0){
                        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error in connecting to network \n Try refresh", Toast.LENGTH_LONG).show();
                }
                activity.finish();
                activity.startActivity(new Intent(context,UserActivity.class));
            }
        }

        TaskAsync registerDevice = new TaskAsync(params,activity,bar);
        registerDevice.execute();
    }
}

