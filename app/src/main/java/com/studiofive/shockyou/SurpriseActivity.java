package com.studiofive.shockyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class SurpriseActivity extends AppCompatActivity {
    @BindView(R.id.imageView)
    ImageView imageView;

    Uri photoUri;
    Uri soundUri;

    TextToSpeech tts;
    MediaPlayer mediaPlayer;
    private Unbinder unbinder;

    boolean acceptingTouches = true;

    AudioModel audioModel;
    ImageModel imageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise);
        unbinder = ButterKnife.bind(this);

        audioModel = new AudioStorer(this).getSelectedAudio();
        imageModel = new ImageStorer(this).getSelectedImage();

        //Images
        if (imageModel.isAsset()){
            photoUri = ShockUtils.getDrawableUri(this, imageModel.getImgFilename());
        }else{
            photoUri = Uri.fromFile(new File(imageModel.getImgFilename()));
        }

        //Audio
        if (!audioModel.isTTS()){
            if (audioModel.isAsset()){
                soundUri = ShockUtils.getRawUri(this, audioModel.getAudioFileName());
            }else {
                soundUri = Uri.fromFile(new File(audioModel.getAudioFileName()));
            }
        }

        Toasty.success(this, "Ready!", Toast.LENGTH_SHORT, true).show();


    }

    private void showImage(){
        Glide.with(this)
                .load(photoUri)
                .into(imageView);
        imageView.setVisibility(View.VISIBLE);
    }

    private void playSoundClip(){
        mediaPlayer = MediaPlayer.create(this, soundUri);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
        mediaPlayer.start();
    }

    private void handleTTS(){
        final String toSpeak = audioModel.getDescriptionMessage();
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    HashMap<String, String> params = new HashMap<>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");

                    if (status == TextToSpeech.SUCCESS){
                        tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                            @Override
                            public void onUtteranceCompleted(String utteranceId) {
                                finish();
                            }
                        });
                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, params);
                    }else{
                        finish();
                    }
                }
            }
        });
    }

    private void userTriggeredActions(){
        if (!acceptingTouches){
            return;
        }
        acceptingTouches = false;

        showImage();

        if (audioModel.isTTS()){
            handleTTS();
        }else{
            playSoundClip();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        userTriggeredActions();
        return super.onTouchEvent(event);
    }
}