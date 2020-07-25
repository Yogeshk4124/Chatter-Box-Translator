package com.example.translator.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ifragmentchat extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    ListView l;
    View v;
    FirebaseUser my;
    static ArrayList<String> phone2=new ArrayList<>();
    static ArrayList<String> names2=new ArrayList<>();
    static ArrayList<String> img2=new ArrayList<>();
    static ArrayList<String> lang2=new ArrayList<>();
    public ifragmentchat() {
        // Required empty public constructor
    }

    public static ifragmentchat newInstance(String param1, String param2) {
        ifragmentchat fragment = new ifragmentchat();
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_ichat, container, false);
        l=v.findViewById(R.id.chatlist);
        my= FirebaseAuth.getInstance().getCurrentUser();
        final ArrayList phone=new ArrayList<>();
        final ArrayList name=new ArrayList<>();
        final ArrayList img=new ArrayList<>();
        final ArrayList lang=new ArrayList<>();
        Query Q= FirebaseDatabase.getInstance().getReference("Chatlist").child(Objects.requireNonNull(my.getPhoneNumber()));
//         Toast.makeText(getContext(),"GOt it:"+my.getPhoneNumber(),Toast.LENGTH_LONG).show();
        Q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
//                    Toast.makeText(getContext(),""+ds.child(my.getPhoneNumber()).getChildrenCount(),Toast.LENGTH_LONG).show();
                    img.add(ds.child("Img").getValue(String.class));
                    name.add(ds.child("Name").getValue(String.class));
                    phone.add(ds.child("Phone").getValue(String.class));
                    lang.add(ds.child("Language").getValue(String.class));
               //     Toast.makeText(getContext(),"I am searching\n"+ds.child("Phone").getValue(String.class)+"\n"+ds.child("Name").getValue(String.class)+"\n"+ds.child("Img").getValue(String.class)+"\nLang:"+ds.child("Lang").getValue(String.class),Toast.LENGTH_LONG).show();

                }
//                Toast.makeText(getContext(),"GOt it",Toast.LENGTH_LONG).show();
                phone2=phone;
                names2=name;
                img2=img;
                lang2=lang;

                Adapter2 adapter = new Adapter2(getContext(),names2, phone2, img2);
                l.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"Some Error",Toast.LENGTH_LONG).show();
            }
        });
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(),Message.class);
                intent.putExtra("Phone",phone2.get(position));
                intent.putExtra("Name",names2.get(position));
                intent.putExtra("Img",img2.get(position));
                intent.putExtra("Lang",lang2.get(position));
//                Toast.makeText(getContext(),"rlang:"+names2.get(position),Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        return v;
    }

    static class Adapter2 extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> rTitle;
        ArrayList<String> rDescription;
        ArrayList<String> rImgs;

        Adapter2 (Context c, ArrayList<String> title, ArrayList<String> description, ArrayList<String> imgs) {
//            super(c, R.layout.contact_view, R.id.name, title);
            super(c,R.layout.contact_view,R.id.name,description);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            View row = layoutInflater.inflate(R.layout.contact_view, parent, false);
            ImageView images = row.findViewById(R.id.profile_pic);
            TextView myTitle = row.findViewById(R.id.name);
            TextView myDescription = row.findViewById(R.id.phone);

            // now set our resources on views
            myTitle.setText(rTitle.get(position));
            myDescription.setText(rDescription.get(position));
            Picasso.get().load(rImgs.get(position)).into(images);

            return row;
        }
    }
}
