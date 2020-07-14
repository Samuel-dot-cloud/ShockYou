package com.studiofive.shockyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.prankSurface)
    ConstraintLayout mPrankSurface;


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
                            downloadImage(url);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).create();
        dialog.show();
    }

    private void downloadImage(String url) {
    }

    public void createNotification(){
        String notificationMessage = "Tap to shock friends";

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