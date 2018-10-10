package com.pgi.uchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Main Activity";
    private ListView campoMensagens;
    private EditText messageToSend;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageAdapter = new MessageAdapter(this);
        campoMensagens = (ListView) findViewById(R.id.ScrollMensagens);
        campoMensagens.setAdapter(messageAdapter);

        messageToSend = (EditText) findViewById(R.id.MessageToSend);
        ImageButton sendButton = (ImageButton) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String auxMes = messageToSend.getText().toString();
                messageToSend.getText().clear();
                newMessage(auxMes, true);
            }
        });
    }

    protected void newMessage(String msg, Boolean user) {
        MemberData data;
        if(user){
            data = new MemberData();
        }
        else{
            data = new MemberData("UChat",getRandomColor());
        }

        Message message = new Message(msg,user,data);
        messageAdapter.add(message);
        campoMensagens.setSelection(campoMensagens.getCount() -1);
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }
}

class MemberData {
    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData() {
        name = "";
        color = "";
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
}
