package com.pgi.uchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class PerguntasActivity extends AppCompatActivity {
    private String TAG = "PerguntasActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;
    private String deviceToken;
    private String randomColor = "#1a70c5";
    private ArrayList<DuvidasObject> duvidas = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private ListView campoMensagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perguntas);

        messageAdapter = new MessageAdapter(this);
        campoMensagens = (ListView) findViewById(R.id.listaPerguntas);
        campoMensagens.setAdapter(messageAdapter);

        //newMessage("OLA" , true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        deviceToken = FirebaseInstanceId.getInstance().getToken();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference posts = mDatabase.child("posts");

        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                duvidas = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userText = "", bootText = "", date = "", user ="";

                    try {
                        String response = ds.getValue().toString();

                        String[] aux = response.split("Resposta=");
                        aux = aux[1].split(", Data=");
                        bootText = aux[0];

                        aux = aux[1].split(", user=");
                        date = aux[0];

                        aux = aux[1].split(", Pergunta=");
                        user = aux[0];

                        aux[0] = aux[1].substring(0,aux[1].length()-1);
                        userText = aux[0];

                        if(user.contains(deviceToken)){
                            duvidas.add(new DuvidasObject(userText, bootText, date));
                            /*
                            newMessage(userText, true);
                                if(!bootText.isEmpty())
                                    newMessage(bootText, false);
                            */
                        }

                    } catch (NullPointerException error) {
                        Log.e(TAG,"ERROR: " + error.toString());
                    }
                }
                createList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void createList() {
        for(int i=duvidas.size() - 1; i >= 0; i--){
            newMessage(duvidas.get(i).getTextUser(), true);
            if(!duvidas.get(i).getTextBoot().isEmpty()){
                newMessage(duvidas.get(i).getTextBoot(), false);
            }
        }
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

    protected void newMessage(String msg, Boolean user) {
        MemberData data;
        if(user){
            data = new MemberData();
        }
        else{
            data = new MemberData("Salvador",randomColor);
        }

        Message message = new Message(msg,user,data);
        messageAdapter.add(message);
        campoMensagens.setSelection(0);
    }
}
