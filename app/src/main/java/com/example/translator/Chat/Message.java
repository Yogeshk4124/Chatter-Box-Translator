package com.example.translator.Chat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.translator.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Message extends AppCompatActivity {
    static TextView receiverName;
    ImageView receiverImg,send;
    static String rPhone,rImg,rName,rlang,mylang,gText,trans;
    private static String tText="";
    FirebaseUser my;
    EditText msg;
    RecyclerView chatview;
    List<Chat> mchat;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg);
        my= FirebaseAuth.getInstance().getCurrentUser();
        String sp;
        getMyLang();
        receiverName=findViewById(R.id.receiverph);
        receiverImg=findViewById(R.id.receiverimg);
        rPhone=getIntent().getStringExtra("Phone");
        rImg=getIntent().getStringExtra("Img");
        rName=getIntent().getStringExtra("Name");
        rlang=getIntent().getStringExtra("Lang");
        receiverName.setText(rName);
        Picasso.get().load(rImg).into(receiverImg);
        msg=findViewById(R.id.msgsend);
        send=findViewById(R.id.send);
        chatview=findViewById(R.id.chatmsg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatview.setLayoutManager(linearLayoutManager);
        Toast.makeText(Message.this," "+my.getUid()+"\n"+my.getPhoneNumber(),Toast.LENGTH_LONG).show();
        read();
//        Toast.makeText(Message.this,"rlang2:"+rlang,Toast.LENGTH_LONG).show();
        // identify();


    }
    public void uploadTask(View view) {
//        Toast.makeText(this, "button clicked...", Toast.LENGTH_SHORT).show();

//        UploadTask uploadTask = new UploadTask();
//        uploadTask.execute();

        new UploadTask().execute("This is the string passaed.");

    }

    private void getMyLang() {
        Query query=FirebaseDatabase.getInstance().getReference("Users").child(my.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    if(ds.child("Phone").equals(my.getPhoneNumber()))
                        mylang=ds.child("Language").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void read()
    {
        mchat = new ArrayList<>();

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Chats");
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if ((chat.getSenderid().equals(my.getPhoneNumber()) && chat.getReceiverph().equals(rPhone)) ||
                            (chat.getReceiverph().equals(my.getPhoneNumber()) && chat.getSenderid().equals(rPhone))){
                        //   Toast.makeText(Message.this,"Showing",Toast.LENGTH_LONG).show();
                        mchat.add(chat);
                    }
                    MessageViewHolder messageAdapter = new MessageViewHolder(Message.this, mchat, chat.getReceiverimg());
                    chatview.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    void identify()
    {
        gText=msg.getText().toString();
        if(msg.getText().toString().equals(""))
            return;
//        Log.d("rlang:",rlang);
//        Toast.makeText(Message.this,"start t:"+rlang,Toast.LENGTH_LONG).show();

        final FirebaseLanguageIdentification identifier= FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        identifier.identifyLanguage(gText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.equals("und"))
                {
//                   Toast.makeText(getContext(),"Lang not found",Toast.LENGTH_LONG).show();

                    tText="Language not found and cannot be translated";
                }
                else{
                    Log.d("My Language Detected :",s);
                    getLanguageCode(s);
                }
            }
        });
    }
    void getLanguageCode(String lang){
        int langCode=0;
        langCode=FirebaseTranslateLanguage.languageForLanguageCode(lang);
//        switch (lang){
//            case"hi":langCode= FirebaseTranslateLanguage.HI;
//                break;
//            case"ko":langCode= FirebaseTranslateLanguage.KO;
//                break;
//            case"ja":langCode= FirebaseTranslateLanguage.JA;
//                break;
//            case"en":langCode= FirebaseTranslateLanguage.EN;
//                break;
//            case"fr":langCode= FirebaseTranslateLanguage.FR;
//                break;
//        }

        translateText(langCode);

    }
    void translateText(int langCode){

        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.KO).build();

        switch (rlang) {
            case "Korean":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.KO).build();
                break;
            case "English":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.EN).build();
                break;
            case "Hindi":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.HI).build();
                break;
            case "Japanese":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.JA).build();
                break;
            case "French":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.FR).build();
                break;
            case "Chinese":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.ZH).build();
                break;
            case "Germany":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.DE).build();
                break;
            case "Italian":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.IT).build();
                break;
            case "Russian":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.RU).build();
                break;
        }

        final FirebaseTranslator translator=FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions=new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                translator.translate(gText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
//                        m.setText(s);
                        tText=s;
                    }
                });
            }
        });
    }
    class UploadTask extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute: " + Thread.currentThread().getName());

//            m.setText("uploading data...");
            send.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... strings) {

            identify();
            for (int i=0; i<10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            Log.i(TAG, "onProgressUpdate: " + Thread.currentThread().getName());
            Log.i(TAG, "onProgressUpdate: " + values[0]+1);
        }
        @Override
        protected void onPostExecute(String string) {

            Log.i(TAG, "onPostExecute: " + tText);
            Toast.makeText(Message.this,"2here Tmsg:"+tText,Toast.LENGTH_LONG).show();
            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(my.getPhoneNumber())
                    .child(rPhone);

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()){
                        chatRef.child("Phone").setValue(rPhone);
                        chatRef.child("Name").setValue(rName);
                        chatRef.child("Img").setValue(rImg);
                        chatRef.child("Lang").setValue(rlang);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(rPhone).
                            child(my.getPhoneNumber());

            chatRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        chatRef2.child("Phone").setValue(my.getPhoneNumber());
                        chatRef2.child("Name").setValue(my.getDisplayName());
                        chatRef2.child("Img").setValue(my.getPhotoUrl());
                        chatRef2.child("Lang").setValue(mylang);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Chats");
            HashMap<String,Object> hm=new HashMap<>();
            hm.put("senderid",my.getPhoneNumber());
            hm.put("receiverph",rPhone);
            hm.put("msg",msg.getText().toString());
            hm.put("receiverimg",rImg);
            hm.put("receivername",rName);
            hm.put("tmsg",tText);
            dref.push().setValue(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                        Toast.makeText(Message.this,"msg Published",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Message.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
            read();
            send.setEnabled(true);
        }
    }
}

