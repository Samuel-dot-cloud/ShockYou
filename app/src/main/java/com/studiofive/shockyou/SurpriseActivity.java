package com.studiofive.shockyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SurpriseActivity extends AppCompatActivity {
    @BindView(R.id.imageView)
    ImageView imageView;

    Uri photoUri;
    Uri soundUri;

    TextToSpeech tts;
    MediaPlayer mediaPlayer;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise);
        unbinder = ButterKnife.bind(this);

        photoUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + File.separator + getPackageName() +"/drawable/man_1");
        soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + File.separator + getPackageName() +"/raw/scream2");

        showImage();
        playSoundClip();
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
}