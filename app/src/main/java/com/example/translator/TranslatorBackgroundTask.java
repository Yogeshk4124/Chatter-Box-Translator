package com.example.translator;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TranslatorBackgroundTask extends AsyncTask<String, Void, String> {
    //Declare Context
    Context ctx;
    public static String mainResult="";
    //Set Context
    TranslatorBackgroundTask(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        //String variables
        String textToBeTranslated = params[0];
        String languagePair = params[1];

        String jsonString;

        try {
            //Set up the translation call URL
            String yandexKey = "trnsl.1.1.20200423T095045Z.c260904fe069b92b.5ea8b987529925cf390dfcc82b53cece858c4ace";
            String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                    + "&text=" + textToBeTranslated + "&lang=" + languagePair;
            URL yandexTranslateURL = new URL(yandexUrl);

            //Set Http Conncection, Input Stream, and Buffered Reader
            HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
            InputStream inputStream = httpJsonConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //Set string builder and insert retrieved JSON result into it
            StringBuilder jsonStringBuilder = new StringBuilder();
            while ((jsonString = bufferedReader.readLine()) != null) {
                jsonStringBuilder.append(jsonString + "\n");
            }

            //Close and disconnect
            bufferedReader.close();
            inputStream.close();
            httpJsonConnection.disconnect();

            //Making result human readable
            String resultString = jsonStringBuilder.toString().trim();
            //Getting the characters between [ and ]
            resultString = resultString.substring(resultString.indexOf('[')+1);
            resultString = resultString.substring(0,resultString.indexOf("]"));
            //Getting the characters between " and "
            resultString = resultString.substring(resultString.indexOf("\"")+1);
            resultString = resultString.substring(0,resultString.indexOf("\""));

            Log.d("Translation Result:", resultString);
            mainResult=resultString;
            return resultString;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}