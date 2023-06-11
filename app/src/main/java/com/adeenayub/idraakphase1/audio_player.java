package com.adeenayub.idraakphase1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class audio_player extends AppCompatActivity {
Button cancel;
ImageView pause,play,rewind,forward;
TextView playerPosition, playerDuration;
SeekBar seekBar;

MediaPlayer mediaPlayer;
Handler handler = new Handler();
Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        cancel = findViewById(R.id.audio_button);
        pause = findViewById(R.id.bt_pause);
        play = findViewById(R.id.bt_play);
        rewind = findViewById(R.id.bt_rewind);
        forward = findViewById(R.id.bt_forward);
        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        seekBar = findViewById(R.id.seekBar);

        mediaPlayer = MediaPlayer.create(this,R.raw.nightcore);
        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this,500); //delay 0.5s
            }
        };
        //get duration of media player
        int duration = mediaPlayer.getDuration();
        //convert duration to minutes and seconds
        String sDuration = convertFormat(duration);
        //set duration to text view
        playerDuration.setText(sDuration);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide play button
                play.setVisibility(View.GONE);
                //show pause button
                pause.setVisibility(View.VISIBLE);
                //start media player
                mediaPlayer.start();
                //set max value of seek bar
                seekBar.setMax(mediaPlayer.getDuration());
                //start handler
                handler.postDelayed(runnable,0);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide pause button
                pause.setVisibility(View.GONE);
                //show play button
                play.setVisibility(View.VISIBLE);
                //pause media player
                mediaPlayer.pause();
                //stop handler
                handler.removeCallbacks(runnable);
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get current position
                int currentPosition = mediaPlayer.getCurrentPosition();
                //get duration
                int duration = mediaPlayer.getDuration();
                if(mediaPlayer.isPlaying() && duration != currentPosition){
                    //fast forward 5 seconds
                    currentPosition = currentPosition + 5000;
                    //set current position on text view
                    playerPosition.setText(convertFormat(currentPosition));
                    //set progress on seek bar
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get current position
                int currentPosition = mediaPlayer.getCurrentPosition();
                if(mediaPlayer.isPlaying() && currentPosition > 5000){
                    //fast rewind 5 seconds
                    currentPosition = currentPosition - 5000;
                    //set current position on text view
                    playerPosition.setText(convertFormat(currentPosition));
                    //set progress on seek bar
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
                playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //hide pause button
                pause.setVisibility(View.GONE);
                //show play button
                play.setVisibility(View.VISIBLE);
                //set media player to start position
                mediaPlayer.seekTo(0);
                //stop handler
                handler.removeCallbacks(runnable);
                //set progress to 0
                seekBar.setProgress(0);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent end= new Intent(audio_player.this,RecordingScreen.class);
                startActivity(end);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}