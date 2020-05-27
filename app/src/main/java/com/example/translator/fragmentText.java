package com.example.translator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener;
import com.skydoves.powerspinner.PowerSpinnerView;

public class fragmentText extends Fragment{

    private EditText gText,tText;
    private PowerSpinnerView s2;
    private String target;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.text_fragment, container, false);
        gText= v.findViewById(R.id.editText);
        tText= v.findViewById(R.id.editText2);
        s2= v.findViewById(R.id.lang);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.lang, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Button Translate= v.findViewById(R.id.Translate);
        Translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        s2.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(View view, MotionEvent motionEvent) {
                s2.showOrDismiss();
            }
        });
        return v;
    }

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
}
