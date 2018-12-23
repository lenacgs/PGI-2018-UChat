package com.pgi.uchat;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
    private Button loginbutton;
    private EditText user,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginbutton = (Button) findViewById(R.id.submit);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               user = (EditText) findViewById(R.id.username);
               pass = (EditText) findViewById(R.id.password);
                if (validate()) {
                    Intent intent = new Intent(view.getContext(), AdminActivity.class);
                    startActivity(intent);
                }
            }

            private boolean validate(){
                boolean temp=true;
                String username = user.getText().toString();
                String password = pass.getText().toString();
                if(!username.equals("pgiteste")){
                    Toast.makeText(LoginActivity.this,"Invalid Username",Toast.LENGTH_SHORT).show();
                    temp=false;
                }
                else if(!password.equals("teste")){
                    Toast.makeText(LoginActivity.this,"Invalid password",Toast.LENGTH_SHORT).show();
                    temp=false;
                }
                return temp;
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
