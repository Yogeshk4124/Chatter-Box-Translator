package com.example.translator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setting extends Fragment {
    TextView Name,Language,Phone;
    CircleImageView profilepic;
    String id;
    FirebaseAuth fa;
    Button logout;
    AnimatedCircleLoadingView animatedCircleLoadingView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_setting,container, false);
        Name=v.findViewById(R.id.name);
        Phone=v.findViewById(R.id.phone);
        Language=v.findViewById(R.id.language);
        profilepic=v.findViewById(R.id.profile_pic);
        logout=v.findViewById(R.id.button);
        fa= FirebaseAuth.getInstance();
        FirebaseUser u=fa.getCurrentUser();
        id=u.getUid();
        animatedCircleLoadingView=v.findViewById(R.id.circle_loading_view);
        animatedCircleLoadingView.setVisibility(View.VISIBLE);
        animatedCircleLoadingView.startDeterminate();
        animatedCircleLoadingView.setAnimationListener(new AnimatedCircleLoadingView.AnimationListener() {
            @Override
            public void onAnimationEnd(boolean success) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                animatedCircleLoadingView.setVisibility(View.INVISIBLE);
            }
        });
        Query Q= FirebaseDatabase.getInstance().getReference("Users").child(id);
        Q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fimage=dataSnapshot.child("Photo").getValue(String.class);
                String fname= dataSnapshot.child("Name").getValue(String.class);
                String fphone=dataSnapshot.child("Phone").getValue(String.class);
                String flanguage=dataSnapshot.child("Language").getValue(String.class);
                animatedCircleLoadingView.setPercent(0);
                Name.setText(fname);
                animatedCircleLoadingView.setPercent(25);
                Phone.setText(fphone);
                animatedCircleLoadingView.setPercent(50);
                Language.setText(flanguage);
                animatedCircleLoadingView.setPercent(75);
                Picasso.get().load(fimage).into(profilepic);
                animatedCircleLoadingView.setPercent(100);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(getContext(), Signup.class));
            }
        });
        return v;

    }
}
