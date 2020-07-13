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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise);
        unbinder = ButterKnife.bind(this);

        photoUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + File.separator + getPackageName() +"/drawable/man_1");
        soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + File.separator + getPackageName() +"/raw/scream2");

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
        mediaPlayer.start();
    }

    private void userTriggeredActions(){
        if (!acceptingTouches){
            return;
        }
        acceptingTouches = false;

        showImage();
        playSoundClip();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        userTriggeredActions();
        return super.onTouchEvent(event);
    }
}