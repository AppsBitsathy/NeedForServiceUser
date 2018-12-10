package in.bittechpro.needforserviceuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Fragment fragment = null;

    Button btn_register,btn_select;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        SharedPreferences sharedpreferences = getSharedPreferences(SPrefManager.PREF_NAME, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(SPrefManager.LOGGED)){
            if (sharedpreferences.getInt(SPrefManager.LOGGED, 0) == 1) {
                finish();
                startActivity(new Intent(LoginActivity.this, UserActivity.class));
            }
        }

        btn_register = findViewById(R.id.btn_register);
        btn_select = findViewById(R.id.btn_select);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new RegisterFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                ft.replace(R.id.content_frame, fragment,"Register");
                ft.commit();
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new SelectFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                ft.replace(R.id.content_frame, fragment,"Select");
                ft.commit();
            }
        });



    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
    }*/
}
