package com.pgi.uchat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rivescript.Config;
import com.rivescript.RiveScript;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private String TAG = "MainActivity";
    private ListView campoMensagens;
    private EditText messageToSend;
    private MessageAdapter messageAdapter;
    private String randomColor;
    private RiveScript bot;
    private Boolean notExist = false;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;
    private String deviceToken;
    private StorageReference mStorageRef;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion >= Build.VERSION_CODES.M) {

            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                continueProgram();
            }
        }
        else
        {
            continueProgram();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean allGranted=true;

        for (int i = 0, len = permissions.length; i < len; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                allGranted = false;
            }
        }
        if(allGranted) {

            Toast.makeText(getApplicationContext(),getString(R.string.permission_allgranted),
                    Toast.LENGTH_SHORT).show();
            continueProgram();
        }
        else {
            displayTwoButtonsDialog(DialogTwoButtons.TYPE_CONFIRM_PERMISSIONS,
                    R.string.attention, R.string.permission_message,
                    R.string.settings, R.string.btn_exit);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void displayTwoButtonsDialog(int type, int title, int message,
                                         int buttonPos, int buttonNeg) {
        DialogFragment dFragment = DialogTwoButtons.newInstance(type, title,
                message, buttonPos, buttonNeg);
        dFragment.setCancelable(false);
        dFragment.show(getSupportFragmentManager(), "twoButtonsDialog");
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void continueProgram(){
        if(isOnline()) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            deviceToken = FirebaseInstanceId.getInstance().getToken();
            mStorageRef = FirebaseStorage.getInstance().getReference();

            Log.e(TAG, "deviceToken = " + deviceToken);

            File outFile = new File(getExternalFilesDir(null) + "/Documents");
            if (!outFile.exists()) {
                notExist = true;
                outFile.mkdirs();
            }

            StorageReference riversRef = mStorageRef.child("bot/salvador.rive");
            File downloadFile = new File(getExternalFilesDir(null) + "/Documents/salvador.rive");


            riversRef.getFile(downloadFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.e(TAG, "SUCCESS");
                            chargeBoot();
                            // Successfully downloaded data to local file
                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "Error");
                    if (notExist) {
                        Log.v(TAG, "Carrega ficheiro inicial");
                        copyAssets();
                        chargeBoot();
                    } else {
                        Log.v(TAG, "Aproveita ultimo ficheiro");
                        chargeBoot();
                    }

                }
            });
        }
        else {
            showDialog();
        }
    }

    public void chargeBoot(){
        randomColor = "#1a70c5";

        File rootDataDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        //File rootDataDir = this.getFilesDir();

        Log.e(TAG, rootDataDir.toString());

        //Inicializar bot para responder ao utilizador
        bot = new RiveScript(Config.newBuilder().utf8(true).unicodePunctuation("[.,!?;:]").build());
        //bot.loadDirectory("com/pgi/uchat/"); //Diretoria não encontrada não importa o que eu ponha ??
        //bot.loadDirectory(rootDataDir);
        bot.loadFile(rootDataDir + "/salvador.rive");
        bot = Start(bot);
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
                String s = Normalizer.normalize(auxMes, Normalizer.Form.NFD);
                s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                String reply = bot.reply("Salvador", s);
                if (reply.contains("#")) reply = Checker(reply);
                newMessage(reply, false);
                if (reply.contains(":)"))
                    sendMessagetoDatabase(auxMes);
            }
        });

        newMessage("Para começares uma nova conversa envia: Ola", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void sendMessagetoDatabase(String question){
        Long tsLong = System.currentTimeMillis()/1000;
        //String ts = tsLong.toString();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(tsLong * 1000L);
        String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();

        String key = mDatabase.child("posts").push().getKey();

        Map<String, Object> result = new HashMap<>();
        result.put("Data",date);
        result.put("Pergunta",question);
        result.put("Resposta","");
        result.put("user", deviceToken);

        mDatabase.child("posts").child(key).setValue(result);

        /*
        Map<String, Object> result = new HashMap<>();
        result.put("Data",ts);
        result.put("Pergunta",question);
        result.put("Resposta","");
        result.put("user", "");
        */

       // Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/Perguntas/" + "1", result);

        //mDatabase.updateChildren(childUpdates);
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
    protected RiveScript Start(RiveScript bot) {
        bot.setSubstitution("oi","ola" );
        bot.setSubstitution("olá","ola" );
        bot.sortReplies();
        return bot;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.item1):
                intent = new Intent(this, PerguntasActivity.class);
                startActivity(intent);
                return true;
            case (R.id.about):
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String filename = "salvador.rive";

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            File outFile = new File(getExternalFilesDir(null)+"/Documents");
            if(!outFile.exists()) outFile.mkdirs();
            outFile = new File(getExternalFilesDir(null)+"/Documents", filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + filename, e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }
    }

    protected String Checker(String msg) {

        if (msg.compareTo("#01") == 0)
            msg = "https://www.youtube.com/watch?v=Hf0lmtOqKeQ";
        else if (msg.compareTo("#02") == 0)
            msg = "Beijo para ti também sexy <3";
        return msg;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ligar WIFI ou Sair")
                .setCancelable(false)
                .setPositiveButton("Ligar WIFI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
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
