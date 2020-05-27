package com.example.translator.Chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.translator.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.squareup.picasso.Picasso;

import java.util.List;

//public class MessageViewHolder extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.left_msg,parent,false);
//        return new ViewHolder(view);
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//        }
//    }
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//}

public class MessageViewHolder extends RecyclerView.Adapter<MessageViewHolder.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    int checker;
    FirebaseUser fuser;

    public MessageViewHolder(Context mContext, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageViewHolder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.right_msg, parent, false);
            return new MessageViewHolder.ViewHolder(view,2);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.left_msg, parent, false);
            return new MessageViewHolder.ViewHolder(view,1);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(checker==1)
        {
            Chat chat = mChat.get(position);

            holder.show_message.setText(chat.getTmsg());

            if (imageurl.equals("default")){
                holder.profile_image.setImageResource(R.mipmap.ic_launcher);
            }
            else {
                Picasso.get().load(chat.getReceiverimg()).into(holder.profile_image);
            }
        }
        else
        {
            Chat chat = mChat.get(position);
            holder.show_message.setText(chat.getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;

        public ViewHolder(View itemView,int u) {
            super(itemView);

            if(u==1) {
                show_message = itemView.findViewById(R.id.lmsg);
                profile_image = itemView.findViewById(R.id.luser);
                checker=1;
            }
            else{
                show_message = itemView.findViewById(R.id.rmsg);
                checker=2;
            }
            }
    }
    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSenderid().equals(fuser.getPhoneNumber())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}