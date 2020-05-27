package com.example.translator;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;



import static android.app.Activity.RESULT_OK;

public class fragmentVoice extends Fragment {
    final static int REQUEST_CODE_SPEECH=1000;
    TextToSpeech mTTS;
    ImageView listen;
    ImageView talk;
    TextView mresult,d;
    SeekBar mSeekBarPitch;
    SeekBar mSeekBarSpeed;
    Button b;
    View v;
    PowerSpinnerView spinner2;
    String target="",code="",source="",ans;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.voice_fragment,container,false);
        listen=v.findViewById(R.id.mic);
        mresult=v.findViewById(R.id.translatedText);
        b=v.findViewById(R.id.translate);
        mSeekBarPitch = v.findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = v.findViewById(R.id.seek_bar_speed);
//        getDefault();
//        Toast.makeText(this,"locale:"+Locale.getDefault(),Toast.LENGTH_LONG).show();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.lang, android.R.layout.simple_spinner_item);
        spinner2=v.findViewById(R.id.lang);
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCodes();
                identify();
            }
        });
        talk=v.findViewById(R.id.imageView2);
        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talk();
            }
        });
        spinner2.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(View view, MotionEvent motionEvent) {
                spinner2.showOrDismiss();
            }
        });
        return v;
    }

    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    void talk(){
        mTTS = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {
                Log.d("My array", Arrays.toString(Locale.getAvailableLocales()));
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.KOREAN);
                    if(target.equals("Korean"))
                        result = mTTS.setLanguage(Locale.KOREAN);
                    else if(target.equals("English"))
                        result = mTTS.setLanguage(Locale.ENGLISH);
                    else if(target.equals("Hindi"))
                        result = mTTS.setLanguage(Locale.forLanguageTag("hi"));
                    else if(target.equals("Japanese"))
                        result = mTTS.setLanguage(Locale.JAPANESE);
                    else if(target.equals("French"))
                        result = mTTS.setLanguage(Locale.FRENCH);
                    else if(target.equals("Chinese"))
                        result = mTTS.setLanguage(Locale.CHINESE);
                    else if(target.equals("Germany"))
                        result = mTTS.setLanguage(Locale.GERMANY);
                    else if(target.equals("Italian"))
                        result = mTTS.setLanguage(Locale.ITALIAN);
                    else if(target.equals("Russian"))
                        result = mTTS.setLanguage(Locale.forLanguageTag("ru_RU"));

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        talk.setEnabled(true);
                        speaker();
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
    }
    private void speaker() {
        String text = mresult.getText().toString();
        float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
    void identify()
    {
        final FirebaseLanguageIdentification identifier= FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        identifier.identifyLanguage(ans).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.equals("und"))
                {
//                   Toast.makeText(MainActivity.this,"Lang not found",Toast.LENGTH_LONG).show();
                }
                else{
                    Log.d("My Language Detected :",s);
                    getLanguageCode(s);
                }
            }
        });
    }
    void getLanguageCode(String lang){
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
//            case "ch":langCode=FirebaseTranslateLanguage.ZH;
//        }
        translateText(langCode);
    }
    void translateText(int langCode){
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.KO).build();;
        switch (target) {
            case "Korean":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.KO).build();
                break;
            case "English":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.EN).build();
                break;
            case "Hindi":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.HI).build();
                break;
            case "Japanese":
                options = new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(langCode).setTargetLanguage(FirebaseTranslateLanguage.JA).build();
                break;
            case "French":
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
                translator.translate(ans).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mresult.setText(s);
                    }
                });
            }
        });
    }

    //    void getDefault()
//    {
//        switch(Locale.getDefault().toString()){
//            case "en_IN":code+="en-";
//                source="en";
//                break;
//            case "hi_IN":code+="hi-";
//                source="hi";
//                break;
//            case "ja_JA":code+="ja-";
//                source="ja";
//                break;
//        }
//    }
    void speak(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        // intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "hi say something!!!");
        try{
            startActivityForResult(intent,REQUEST_CODE_SPEECH);
        }
        catch(Exception e) {
            Log.d("My Error",code);
            Toast.makeText(getContext(), "" + e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_CODE_SPEECH:{
                if(resultCode==RESULT_OK && data!=null) {
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ans=(result.get(0));
                }
                break;
            }
        }
    }
    void getCodes()
    {
        String text =spinner2.getText().toString();
        Log.d("Value:",text);
        //  Toast.makeText(this,text,Toast.LENGTH_LONG).show();
        switch(text){
            case "English":
                target="English";
//                code=source+"-"+"en";
                break;
            case "Hindi":
                target="Hindi";
//                code=source+"-"+"hi";
                break;
            case "French":
//                code=source+"-"+"fr";
                target="French";
                break;
            case "Korean":
//                code=source+"-"+"ko";
                target="Korean";
                break;
            case "Japanese":
                target="Japanese";
//                code=source+"-"+"ja";
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
            case "Afrikaans":
                target="Afrikaans";
                break;
        }
    }
}
