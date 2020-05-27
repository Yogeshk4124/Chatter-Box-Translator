package com.example.translator;

import org.json.JSONException;

public interface IOCRCallBack{

    void getOCRCallBackResult(String response)throws JSONException;
}
