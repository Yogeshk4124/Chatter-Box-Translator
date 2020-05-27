package com.example.translator;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class signDetails extends AppCompatActivity {
    Button Done;
    EditText name;
    ImageView pic;
    Uri images_uri=null;
    PowerSpinnerView lang;
    String phonenumber,Downloaduri="",language="";
    private FirebaseAuth mAuth;
    DatabaseReference reference;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int IMAGE_CHOOSE_CODE = 1002;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        phonenumber=getIntent().getStringExtra("Phone");
        Done=findViewById(R.id.done);
        name=findViewById(R.id.name);
        pic=findViewById(R.id.profile_pic);
        lang=findViewById(R.id.lang);
        mAuth = FirebaseAuth.getInstance();
        fetch();
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        imagefrom();
                    }
                }
                else {
                    imagefrom();
                }
            }
        });
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language=lang.getText().toString();
                if(name.getText().toString().equals(""))
                    name.setError("Username cannot be empty!");
                else if(language.equals("")) {
                    lang.setError("Select Language");
                    Toast.makeText(signDetails.this,lang.getText().toString(),Toast.LENGTH_LONG).show();
                }
                else
                    setUpDetails();
            }
        });
    }
    void fetch(){
        Query Q=FirebaseDatabase.getInstance().getReference("Users").orderByChild("Phone");
        Q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    String fphone = ds.child("Phone").getValue(String.class);
                    if(fphone.equals(phonenumber)) {

                        Toast.makeText(signDetails.this,"found:",Toast.LENGTH_SHORT).show();
                        name.setText(ds.child("Name").getValue(String.class));
                        Picasso.get().load(ds.child("Photo").getValue(String.class)).into(pic);
                        language=ds.child("Language").getValue(String.class);
                        assert language != null;
                        switch (language) {
                            case "English":
                                lang.selectItemByIndex(0);
                                break;
                            case "Korean":
                                lang.selectItemByIndex(1);
                                break;
                            case "Hindi":
                                lang.selectItemByIndex(3);
                                break;
                            case "Japanese":
                                lang.selectItemByIndex(4);
                                break;
                            case "French":
                                lang.selectItemByIndex(5);
                                break;
                        }
                        break;
                    }
                }
                Toast.makeText(signDetails.this,"ans2:",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void imagefrom(){
//        Toast.makeText(SignUpActivity.this, "I have done my work111", Toast.LENGTH_LONG).show();
        String[] options={"Gallery","Camera"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Choose Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(SignUpActivity.this, "I have done my work", Toast.LENGTH_LONG).show();
                if(i==1)
                    takephoto();
                else
                    choosePhoto();

            }

        });
        builder.create().show();

    }
    public void choosePhoto(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_CHOOSE_CODE);

    }
    public void takephoto() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "FROM THE CAMERA");
        images_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, images_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
 //       Toast.makeText(SignUpActivity.this,"I am checking for code"+requestCode,Toast.LENGTH_LONG).show();
        if(resultCode==RESULT_OK){
            if (requestCode == IMAGE_CAPTURE_CODE) {
                pic.setImageURI(images_uri);
            }
            else if(requestCode == IMAGE_CHOOSE_CODE)
            {
                images_uri=data.getData();
                pic.setImageURI(images_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setUpDetails() {
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        assert firebaseUser!=null;
        String userid=firebaseUser.getUid();

        if(images_uri==null)
            Downloaduri="https://firebasestorage.googleapis.com/v0/b/translator-1587635247787.appspot.com/o/nopic.png?alt=media&token=56ef1b3a-79eb-4a27-874e-de5f8460f569";
        else
        store();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("id",userid);
        hashMap.put("Phone",phonenumber);
        hashMap.put("Name",name.getText().toString());
        hashMap.put("Language",language);
        hashMap.put("Photo",Downloaduri);
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //    pd.dismiss();
                    Intent intent = new Intent(signDetails.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    //      pd.dismiss();
                    Toast.makeText(signDetails.this,"failed to create",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void store()
    {
        final String timespan=String.valueOf(System.currentTimeMillis());
        String address="Profile/"+"profile_"+timespan;
        StorageReference ref= FirebaseStorage.getInstance().getReference().child(address);
        ref.putFile(Uri.parse(images_uri.toString())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Downloaduri=uriTask.getResult().toString();
                if(uriTask.isSuccessful())
                {
                    Toast.makeText(signDetails.this,"Done uploading",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
