package com.studiofive.shockyou;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.prankSurface)
    ConstraintLayout mPrankSurface;
    @BindView(R.id.scareImageView)
    ImageView imageView;
    @BindView(R.id.audioTextView)
    TextView audioTextView;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    AudioStorer audioStorer;
    ImageStorer imageStorer;

    BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPrankSurface.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
                finish();
            }
        });

        preferences = getSharedPreferences(ShockUtils.SHOCK_SCARED_PREFS, Context.MODE_PRIVATE);
        editor = preferences.edit();

        audioStorer = new AudioStorer(this);
        imageStorer = new ImageStorer(this);

        updateUI();
        findViewById(R.id.audioSurface).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null){
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                AudioPickerDialogFragment dialogFragment = new AudioPickerDialogFragment();
                dialogFragment.setCancelable(true);
                dialogFragment.show(ft, "dialog");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_button){
            final PopupMenu popup = new PopupMenu(this, findViewById(R.id.add_button));
            popup.getMenuInflater().inflate(R.menu.pop_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem popItem) {
                    switch (popItem.getItemId()){
                        case R.id.addImage:
                         addImageDialog();
                         break;

                        case R.id.addAudio:
                            addAudioDialog();
                            break;
                    }
                    return false;
                }
            });
            popup.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addAudioDialog() {
        final EditText soundEditText = new EditText(this);
        soundEditText.setHint(R.string.editText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.audio)
                .setMessage("Enter message for text to speech")
                .setView(soundEditText)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = soundEditText.getText().toString();
                        if (message == null || message.trim().isEmpty()) {
                            Toasty.error(getBaseContext(), "Please provide appropriate input", Toast.LENGTH_SHORT, true).show();
                            return;
                        } else {
                            addTTSAudio(message);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).create();
        dialog.show();
    }

    private void addTTSAudio(String message) {
        int mediaId = preferences.getInt(getString(R.string.key_next_media_id), ShockUtils.STARTING_ID);
        editor.putInt(getString(R.string.key_next_media_id), mediaId + 1);
        editor.commit();

        AudioModel audioModel = new AudioModel(mediaId, message);
        audioStorer.addAudio(audioModel);
    }

    private void addImageDialog() {
        final EditText urlBox = new EditText(this);
        urlBox.setHint("Image URL");

        AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("Image URL")
                .setMessage("Import image from web")
                .setView(urlBox)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = urlBox.getText().toString();
                        if (url == null || url.trim().isEmpty()) {
                            Toasty.error(getBaseContext(), "Please provide appropriate input", Toast.LENGTH_SHORT, true).show();
                            return;
                        } else {
                            downloadImageToFile(url);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).create();
        dialog.show();
    }

    private void downloadImageToFile(String url) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImage(resource);
                    }
                });
    }

    private void saveImage(Bitmap bitmap) {
        FileOutputStream outputStream = null;
        File file = createInternalFile(UUID.randomUUID().toString());
        int mediaId = preferences.getInt(getString(R.string.key_next_media_id), ShockUtils.STARTING_ID);
        editor.putInt(getString(R.string.key_next_media_id), mediaId + 1);
        editor.commit();

        ImageModel imageModel = new ImageModel(mediaId, file.getAbsolutePath(), false);

        try {
            outputStream = new FileOutputStream(new File(imageModel.getImgFilename()));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            imageStorer.addImage(imageModel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private File createInternalFile(String filename){
        File outputDir = getExternalCacheDir();
        File outputFile = new File(outputDir, filename);

        return outputFile;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, new IntentFilter(ShockUtils.MEDIA_UPDATED_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
    }

    private void updateUI(){
        ImageModel imageModel = imageStorer.getSelectedImage();
        Uri imgUri;

        if (imageModel.isAsset()){
            imgUri = ShockUtils.getDrawableUri(this, imageModel.imgFilename);
        }else {
            imgUri = Uri.fromFile(new File(imageModel.getImgFilename()));
        }
        //updates to current selected image.
        Glide.with(this)
                .load(imgUri)
                .into(imageView);

        // Audio text
        AudioModel audio = audioStorer.getSelectedAudio();
        audioTextView.setText(audio.getDescriptionMessage());
    }

    public void createNotification(){
        String notificationMessage = getString(R.string.tap_notification);

        int requestId = (int)System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, SurpriseActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .setContentText(notificationMessage)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "Anything_channel";
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);

        }
        notificationManager.notify(5, builder.build());
    }
}