package com.example.translator.Chat;

public class Chat {
    String senderid;
    String receiverph;
    String msg;
    String receiverimg;
    String receivername;
    String Tmsg;
    //String id;
    //String name;

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public Chat(String s, String r, String msg, String receiverimg, String sendername, String receivername,String msg2) {
        this.senderid = s;
        this.receiverph = r;
        this.msg = msg;
        this.Tmsg=msg2;
        this.receiverimg = receiverimg;
        this.receivername = receivername;
    }

    public String getTmsg() {
        return Tmsg;
    }

    public void setTmsg(String tmsg) {
        Tmsg = tmsg;
    }

    //    public Chat(String s, String r,String sendername,String receivername){
//        this.senderid=s;
//        this.receiverid=r;
//        this.sendername=sendername;
//        this.receivername=receivername;
//    }
//    public Chat(String s, String r, String msg, String receiverimg,String sendername,String receivername,String id){
//        this.senderid=s;
//        this.receiverid=r;
//        this.msg=msg;
//        this.receiverimg=receiverimg;
//        this.sendername=sendername;
//        this.receivername=receivername;
//    }
    public Chat() {
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReceiverimg() {
        return receiverimg;
    }

    public void setReceiverimg(String receiverimg) {
        this.receiverimg = receiverimg;
    }

    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }

    public String getReceiverph() {
        return receiverph;
    }
    public void setReceiverph(String receiverph) {
        this.receiverph = receiverph;
    }
}
