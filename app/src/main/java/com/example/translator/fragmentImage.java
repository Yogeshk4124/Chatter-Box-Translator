package com.example.translator;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener;
import com.skydoves.powerspinner.PowerSpinnerPreference;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;




import static android.app.Activity.RESULT_OK;

public class fragmentImage extends Fragment implements IOCRCallBack {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int IMAGE_CHOOSE_CODE = 1002;
    private Context context=getContext();

    //
    private IOCRCallBack mIOCRCallBack;
    private String mAPiKey = "498d1eac7088957"; //TODO Add your own Registered API key
    private boolean isOverlayRequired;
    private String mImageUrl="";
    private String mLanguage="";
    private TextView mTxtResult;

    // Now create matcher object.
    //
    private Bitmap images_Bit = null;
    private ImageView photo;
    private TextView gText;
    private EditText tText;
    private String textToBeTranslated,languagePair="";
    private String target = "", recognizedText = "";
    private PowerSpinnerView s1,s2;
    Uri images_uri = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_fragment, container, false);
        photo = v.findViewById(R.id.imageView3);
//        gText= v.findViewById(R.id.textView7);
        s1=v.findViewById(R.id.langp);
        s2=v.findViewById(R.id.lang);
        gText=v.findViewById(R.id.getT);
        tText = v.findViewById(R.id.tran);
        photo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        imagefrom();
                    }
                } else {
                    imagefrom();
                }
            }
        });
        s1.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(View view, MotionEvent motionEvent) {
                s1.showOrDismiss();
            }
        });
        s2.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(View view, MotionEvent motionEvent) {
                s2.showOrDismiss();
            }
        });
//        Button button = v.findViewById(R.id.button2);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Recognize();
//            }
//        });
        mIOCRCallBack = this;
        // Image url to apply OCR API
        //Language
        isOverlayRequired = false;
        Button button2 = v.findViewById(R.id.button3);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                identify();
                getOCRCodes();
//                for (int i=0; i<10; i++) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if(s1.getText().equals(""))
//                    s1.setError("");
//                else{
//                    mLanguage=s1.getText().toString();
//                }
                if(mLanguage.equals(""))
                    s1.setError("Please choose language");
//                if(languagePair.equals(""))
//                    s2.setError("");
                 if(mImageUrl.equals(""))
                    Toast.makeText(getContext(),"Please Select a Photo or Wait for upload",Toast.LENGTH_LONG).show();
                else
                {
                    OCRAsyncTask oCRAsyncTask = new OCRAsyncTask(mAPiKey, isOverlayRequired, mImageUrl, mLanguage, mIOCRCallBack);
                    oCRAsyncTask.execute();
                }
            }
        });
        Button translater=v.findViewById(R.id.Trans);
        translater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCodes();
                if(languagePair.equals(""))
                    Toast.makeText(getContext(),"Please Select Source and Target Language",Toast.LENGTH_LONG).show();
                else
                Translate(gText.getText().toString(),languagePair);
            }
        });
        return v;
    }

    void getOCRCodes() {
        String text =s1.getText().toString();
        Log.d("cjec:",text);
        String f = null,s = null;
        switch(text){
            case "English":
                mLanguage="eng";
                f="en";
                break;
            case "Hindi":
                f="hi";
                mLanguage="hin";
                break;
            case "French":
                f="fr";
                mLanguage="fre";
                break;
            case "Korean":
                f="ko";
                mLanguage = "kor";
                break;
            case "Japanese":
                f="ja";
                mLanguage = "jpn";
                break;
            case "Chinese":
                f="zh";
                mLanguage="chs";
                break;
            case "Germany":
                f="de";
                mLanguage="ger";
                break;
            case "Italian":
                f="it";
                mLanguage = "ita";
                break;
            case "Russian":
                f="ru";
                mLanguage = "rus";
                break;
            default:mLanguage="";
        }
        text =s2.getText().toString();
        Log.d("cjec:",text);
        switch(text){
            case "English":
                s="en";
                break;
            case "Hindi":
                s="hi";
                break;
            case "French":
                s="fr";
                break;
            case "Korean":
                s="ko";
                break;
            case "Japanese":
                s="ja";
                break;
            case "Chinese":
                s="zh";
                break;
            case "Germany":
                s="de";
                break;
            case "Italian":
                s="it";
                break;
            case "Russian":
                s="ru";
                break;
            default:s="";
        }
        languagePair=f+"-"+s;
    }
    //Button Code

    private void getCodes() {
        String text =s2.getText().toString();
        Log.d("cjec:",text);
        switch(text){
            case "English":
                target="en";
                break;
            case "Hindi":
                target="hi";
                break;
            case "French":
                target="fr";
                break;
            case "Korean":
                target="ko";
                break;
            case "Japanese":
                target="ja";
                break;
            case "Chinese":
                target="Chinese";
                break;
            case "Germany":
                target="Germany";
                break;
            case "Italian":
                target="Italian";
                break;
            case "Russian":
                target="Russian";
                break;
        }
    }
    private void imagefrom() {
//        Toast.makeText(getContext(), "I have done my work111", Toast.LENGTH_LONG).show();
        String[] options = {"Gallery", "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getContext(), "I have done my work", Toast.LENGTH_LONG).show();
                if (i == 1) {
                    try {
                        takephoto();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), mImageUrl, Toast.LENGTH_LONG).show();
                    }
                } else
                    choosePhoto();
            }

        });
        builder.create().show();

    }

    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_CHOOSE_CODE);
    }

    private void takephoto() throws MalformedURLException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "FROM THE CAMERA");
        images_uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, images_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

//        Toast.makeText(getContext(), "I am checking for code" + requestCode, Toast.LENGTH_LONG).show();
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_CAPTURE_CODE) {

                photo.setImageURI(images_uri);
                store();
            } else if (requestCode == IMAGE_CHOOSE_CODE) {
                images_uri = data.getData();
                photo.setImageURI(images_uri);
                store();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void store() {
        final String timespan = String.valueOf(System.currentTimeMillis());
        String address = "Image/" + "image_" + timespan;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(address);
        ref.putFile(Uri.parse(images_uri.toString())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                mImageUrl = uriTask.getResult().toString();
                if (uriTask.isSuccessful()) {
                    Toast.makeText(getContext(), "Done uploading", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getOCRCallBackResult(String response) throws JSONException {
        JSONObject jsonObject = null;
        Object level = null;
        JSONObject innerArray3, jsonobject = null;
        Log.d("response:",response);
        try {
            jsonObject = new JSONObject(response);
            Log.d("response:", response);
            JSONArray jsonarray = jsonObject.getJSONArray("ParsedResults");
            jsonobject = jsonarray.getJSONObject(0);
            Log.d("nicce", jsonobject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assert jsonobject != null;
        textToBeTranslated=jsonobject.getString("ParsedText").trim().replaceAll("\r\n"," ");
        gText.setText(textToBeTranslated);
        System.out.println("gggooooodd" + textToBeTranslated);
    }
    void Translate(String textToBeTranslated,String languagePair){
//        TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(context);
//        String a=translatorBackgroundTask.execute(textToBeTranslated,languagePair).toString(); // Returns the translated text as a String
//        for (int i=0; i<10; i++) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        tText.setText(translatorBackgroundTask.mainResult); // Logs the result in Android Monitor
        if(gText.getText().toString().equals(""))
            Toast.makeText(getContext(),"Please Enter Text!",Toast.LENGTH_LONG).show();
        else
        {
            getCodes();
            if(s2.getText().toString().equals(""))
                s2.setError("Select Language!");
            else {
                s2.setError(null);
                identify();
            }
        }

    }

//    private void getCodes() {
//        String text =s2.getText().toString();
//        Log.d("cjec:",text);
//        switch(text){
//            case "English":
//                target="en";
//                break;
//            case "Hindi":
//                target="hi";
//                break;
//            case "French":
//                target="fr";
//                break;
//            case "Korean":
//                target="ko";
//                break;
//            case "Japanese":
//                target="ja";
//                break;
//            case "Chinese":
//                target="Chinese";
//                break;
//            case "Germany":
//                target="Germany";
//                break;
//            case "Italian":
//                target="Italian";
//                break;
//            case "Russian":
//                target="Russian";
//                break;
//        }
//    }

    private void identify()
    {
        if(gText.getText().toString().equals(""))
            return;
        final FirebaseLanguageIdentification identifier= FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        identifier.identifyLanguage(gText.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.equals("und"))
                {
//                   Toast.makeText(getContext(),"Lang not found",Toast.LENGTH_LONG).show();
                    tText.setText("Language not found and cannot be translated");
                }
                else{
                    Log.d("My Language Detected :",s);
                    getLanguageCode(s);
                }
            }
        });
    }
    private void getLanguageCode(String lang){
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
    private void translateText(int langCode){
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.KO).build();
        switch (target) {
            case "ko":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.KO).build();
                break;
            case "en":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.EN).build();
                break;
            case "hi":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.HI).build();
                break;
            case "ja":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.JA).build();
                break;
            case "fr":
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
                translator.translate(gText.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        tText.setText(s);
                    }
                });
            }
        });
    }
    private static class OCRAsyncTask extends AsyncTask {

        private static final String TAG = OCRAsyncTask.class.getName();

        String url = "https://api.ocr.space/parse/image"; // OCR API Endpoints

        private String mApiKey;
        private boolean isOverlayRequired = false;
        private String mImageUrl;
        private String mLanguage;
        private ProgressDialog mProgressDialog;
        private IOCRCallBack mIOCRCallBack;

        OCRAsyncTask(String apiKey, boolean isOverlayRequired, String imageUrl, String language, IOCRCallBack iOCRCallBack) {
            this.mApiKey = apiKey;
            this.isOverlayRequired = isOverlayRequired;
            this.mImageUrl = imageUrl;
            this.mLanguage = language;
            this.mIOCRCallBack = iOCRCallBack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object[] params) {

            try {
                return sendPost(mApiKey, isOverlayRequired, mImageUrl, mLanguage);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String sendPost(String apiKey, boolean isOverlayRequired, String imageUrl, String language) throws Exception {

            URL obj = new URL(url); // OCR API Endpoints
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


            JSONObject postDataParams = new JSONObject();

            postDataParams.put("apikey", apiKey);//TODO Add your Registered API key
            postDataParams.put("isOverlayRequired", isOverlayRequired);
            postDataParams.put("url", imageUrl);
            postDataParams.put("language", language);


            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(getPostDataString(postDataParams));
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //return result
            return String.valueOf(response);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            String response = (String) result;

            try {
                mIOCRCallBack.getOCRCallBackResult(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("CTT", response.toString());

        }

        public String getPostDataString(JSONObject params) throws Exception {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            Iterator<String> itr = params.keys();
            while (itr.hasNext()) {
                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            Log.d("CTT2:", result.toString());
            return result.toString();
        }
    }


}