package com.example.translator.Chat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.translator.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ifragmentContacts extends Fragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    ListView l;
    View v;
    int flag=0;
    ArrayList<String> contacts=new ArrayList<>();
    private ArrayList<String> phone=new ArrayList<>();
    private ArrayList<String> names=new ArrayList<>();
    private ArrayList<String> img=new ArrayList<>();
    private ArrayList<String> lang=new ArrayList<>();
    //    String names[]=new String[10000];
//    String phone[]=new String[10000];
//    String img[]=new String[10000];
    public ifragmentContacts() {


    }
    public static ifragmentContacts newInstance(String param1, String param2) {
        ifragmentContacts fragment = new ifragmentContacts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_icontacts, container, false);
        l=v.findViewById(R.id.contact);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1);
        }
        else
            getContact();
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Query Q= FirebaseDatabase.getInstance().getReference("Users").orderByChild("Phone").equalTo(phone.get(position));
//      final int finalI = i;

                Q.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int s=0;
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            if(ds.child("Phone").getValue(String.class).equals(phone.get(position))){
//                        img[finalI]=ds.child("Photo").getValue(String.class);
                                s=988;

//                                Log.d("FOund",ds.child("Phone").getValue(String.class));
//                                img.add(ds.child("Photo").getValue(String.class));
                                Intent intent=new Intent(getContext(),Message.class);
                                intent.putExtra("Phone",phone.get(position));
                                intent.putExtra("Name",names.get(position));
                                intent.putExtra("Img",img.get(position));
                                intent.putExtra("Lang",lang.get(position));
                                startActivity(intent);
                            }
                        }
                        if(s==0){
                            Toast.makeText(getContext(),"NOT FOUND",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return v;
    }
private void getContact()
{

    Cursor cursor=getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
    assert cursor != null;
    int i=0;
    while (cursor.moveToNext())
    {
//        names.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
//        phone.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//        names[i]=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//        phone[i]=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        names.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        phone.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s",""));

        final String val=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s", "");
        flag =0;
        Query Q= FirebaseDatabase.getInstance().getReference("Users").orderByChild("Phone").equalTo(val);
//      final int finalI = i;
        Q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    if(ds.child("Phone").getValue(String.class).equals(val)){
//                        img[finalI]=ds.child("Photo").getValue(String.class);
                        flag=999;
                  //      Log.d("FOund",ds.child("Phone").getValue(String.class));
                        img.add(ds.child("Photo").getValue(String.class));
                        lang.add(ds.child("Language").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(flag==0) {
         //   img[finalI] = "https://firebasestorage.googleapis.com/v0/b/translator-1587635247787.appspot.com/o/nopic.png?alt=media&token=56ef1b3a-79eb-4a27-874e-de5f8460f569";
        img.add("https://firebasestorage.googleapis.com/v0/b/translator-1587635247787.appspot.com/o/nopic.png?alt=media&token=56ef1b3a-79eb-4a27-874e-de5f8460f569");
        }
     //   contacts.add(name+"\n"+mobile);
//        i++;
    }

//    ArrayAdapter<String>arrayAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,contacts);
    MyAdapter adapter = new MyAdapter(getContext(), names,phone,img);
    l.setAdapter(adapter);
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED);
            {
                getContact();
            }
        }
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
//        String rTitle[];
//        String rDescription[];
//        String rImgs[];
        ArrayList<String> rTitle;
        ArrayList<String> rDescription;
        ArrayList<String> rImgs;

        MyAdapter (Context c, ArrayList<String> title, ArrayList<String> description, ArrayList<String> imgs) {
            super(c, R.layout.contact_view, R.id.name, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.contact_view, parent, false);
            ImageView images = row.findViewById(R.id.profile_pic);
            TextView myTitle = row.findViewById(R.id.name);
            TextView myDescription = row.findViewById(R.id.phone);

            // now set our resources on views
            myTitle.setText(rTitle.get(position));
            myDescription.setText(rDescription.get(position));
            Picasso.get().load(rImgs.get(position)).into(images);
//            Log.d("img:",rImgs.get(position));


            return row;
        }
    }
}
