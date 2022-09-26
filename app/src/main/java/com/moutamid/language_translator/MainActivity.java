package com.moutamid.language_translator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText text_et;
    TextView text_tv;
    Button btn_translate;
    ImageView speak;
    ImageView copy;
    ImageView speach;
    ImageView copy2;
    ImageView retry;
    ImageView retry2;

    private TranslatorOptions translatorOptions;
    private Translator translator;
    private ProgressDialog progressDialog;

    private static final String TAG = "MAIN_TAG";

    private String sourceLangaugeCode = "en";
    private String destinantionLangaugeCode = "fr";

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    TextToSpeech textToSpeech;

    TextView privacy_text;
    Button thumbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_et = findViewById(R.id.text_et);
        text_tv = findViewById(R.id.text_tv);
        btn_translate = findViewById(R.id.btn_translate);
        speak = findViewById(R.id.speak);
        copy = findViewById(R.id.copy);
        speach = findViewById(R.id.speech);
        copy2 = findViewById(R.id.copy2);
        retry = findViewById(R.id.retry);
        retry2 = findViewById(R.id.retry2);

        thumbs = findViewById(R.id.btn_thumbs);
        privacy_text = findViewById(R.id.privacy_policy);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                }
                catch (Exception e) {
                    Toast
                            .makeText(MainActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text;
                text = text_et.getText().toString().trim();
                if (text.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter Text...", Toast.LENGTH_SHORT).show();
                }
                else {
                    myClip = ClipData.newPlainText("text", text);
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text;
                text = text_tv.getText().toString().trim();
                if (text.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Translated Text is Empty...", Toast.LENGTH_SHORT).show();
                }
                else {
                    myClip = ClipData.newPlainText("text", text);
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_et.setText("");
            }
        });

        retry2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_tv.setText("");
            }
        });

        speach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(text_tv.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        privacy_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/e/2PACX-1vRKT3jR0CmTe-s8nBbpruhE3Sglqcuiu1En8HqL0nPfSPA17tYB8X812M4j8yU7rvfn71F3aubdpbe5/pub"));
                startActivity(browserIntent);
            }
        });

        thumbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , DonateActivity.class);
                startActivity(intent);
            }
        });

        btn_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                text_et.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }

    private String sourceLangText = "";

    private void validateData() {
        sourceLangText = text_et.getText().toString().trim();
        if (sourceLangText.isEmpty()){
            Toast.makeText(this, "Enter Text to Translate...", Toast.LENGTH_SHORT).show();
        }
        else {
            startTranslation();
        }
    }

    private void startTranslation() {
        progressDialog.setMessage("Processing Language Mode...");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder().setSourceLanguage(sourceLangaugeCode).setTargetLanguage(destinantionLangaugeCode).build();
        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder().requireWifi().build();
        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.setMessage("Translating...");
                        translator.translate(sourceLangText).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                progressDialog.dismiss();
                                text_tv.setText(s);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Failed to Translate due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failed to ready modal due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}