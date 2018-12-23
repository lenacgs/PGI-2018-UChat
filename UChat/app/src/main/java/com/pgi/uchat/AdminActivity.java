package com.pgi.uchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AdminActivity extends AppCompatActivity {
    private String TAG = "AdminActivity";
    private int index = 0;
    Button Ignorebutton, Passbutton, Submitbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        SetQuestion();

        Ignorebutton = (Button) findViewById(R.id.ignore);
        Ignorebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
            public void next(){
                TextView pergunta = (TextView)findViewById(R.id.pergunta);
                TextView resposta = (TextView)findViewById(R.id.resposta);
                TextView field1 = (TextView)findViewById(R.id.plus_tag);
                TextView field2 = (TextView)findViewById(R.id.minus_tag);

                pergunta.setText("Pergunta nº" + index);
                resposta.setText("");
                field1.setText("+");
                field2.setText("-");

                index++;

                Toast.makeText(AdminActivity.this,"Pergunta eliminada",Toast.LENGTH_SHORT).show();
            }
        });

        Passbutton = (Button) findViewById(R.id.pass);
        Passbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
            public void next(){
                TextView pergunta = (TextView)findViewById(R.id.pergunta);
                TextView resposta = (TextView)findViewById(R.id.resposta);
                TextView field1 = (TextView)findViewById(R.id.plus_tag);
                TextView field2 = (TextView)findViewById(R.id.minus_tag);

                pergunta.setText("Pergunta nº" + index);
                resposta.setText("");
                field1.setText("+");
                field2.setText("-");

                index++;
            }
        });

        Submitbutton = (Button) findViewById(R.id.submit);
        Submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
            public void next(){
                TextView pergunta = (TextView)findViewById(R.id.pergunta);
                TextView resposta = (TextView)findViewById(R.id.resposta);
                TextView field1 = (TextView)findViewById(R.id.plus_tag);
                TextView field2 = (TextView)findViewById(R.id.minus_tag);

                pergunta.setText("Pergunta nº" + index);
                resposta.setText("");
                field1.setText("+");
                field2.setText("-");

                index++;

                Toast.makeText(AdminActivity.this,"Pergunta respondida",Toast.LENGTH_SHORT).show();
            }
        });
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

    public void SetQuestion(){
        TextView pergunta = (TextView)findViewById(R.id.pergunta);
        pergunta.setText("Pergunta nº" + index);
        index++;
    }
}
