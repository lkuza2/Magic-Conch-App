package com.darkprograms.magicconch;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.VideoView;

public class MainActivity extends Activity implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private boolean spoken;
    private boolean shellSpoken;
    private boolean playing;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        findViewById(R.id.conch_button).setOnClickListener(this);
        Spinner spinner = (Spinner) findViewById(R.id.sound_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sound_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        VideoView video = (VideoView) findViewById(R.id.main_video_view);
        video.setOnPreparedListener(this);
        video.setOnCompletionListener(this);

    }

    @Override
    public void onClick(View view) {
        if (!playing) {
            playing = true;
            findViewById(R.id.pull_string_check_box).setEnabled(false);
            findViewById(R.id.shell_has_spoken_check_box).setEnabled(false);
            findViewById(R.id.sound_spinner).setEnabled(false);
            if (((CheckBox) findViewById(R.id.pull_string_check_box)).isChecked()) {
                view.setVisibility(View.INVISIBLE);
                VideoView video = (VideoView) findViewById(R.id.main_video_view);
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.string_pull);

                video.setVisibility(View.VISIBLE);
                video.requestFocus();
                video.setOnPreparedListener(this);
                video.setOnCompletionListener(this);
                video.setVideoURI(uri);
            } else {
                MediaPlayer player = MediaPlayer.create(this, getMP3ToPlay());
                player.setOnCompletionListener(this);
                player.start();
            }
        }
    }

    private void reset() {
        findViewById(R.id.main_video_view).setVisibility(View.INVISIBLE);
        findViewById(R.id.conch_button).setVisibility(View.VISIBLE);
        findViewById(R.id.pull_string_check_box).setEnabled(true);
        findViewById(R.id.shell_has_spoken_check_box).setEnabled(true);
        findViewById(R.id.sound_spinner).setEnabled(true);
        shellSpoken = false;
        spoken = false;
        playing = false;
    }

    private int getMP3ToPlay() {
        int selected = ((Spinner) findViewById(R.id.sound_spinner)).getSelectedItemPosition();
        switch (selected) {
            case 0:
                return R.raw.nothing;
            case 1:
                return R.raw.no;
            case 2:
                return R.raw.no_sarcastic;
            case 3:
                return R.raw.try_asking_again;
            default:
                return R.raw.nothing;
        }
    }

    private void playShellSpoken() {
        VideoView video = (VideoView) findViewById(R.id.main_video_view);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.shell_spoken);
        video.requestFocus();
        video.setVideoURI(uri);
        video.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (((CheckBox) findViewById(R.id.pull_string_check_box)).isChecked()) {

            if (!spoken) {
                MediaPlayer player = MediaPlayer.create(this, getMP3ToPlay());
                player.setOnCompletionListener(this);
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    Log.e("Magic Conch", Log.getStackTraceString(e));
                }
                player.start();
                spoken = true;
            } else {
                if (((CheckBox) findViewById(R.id.shell_has_spoken_check_box)).isChecked() && !shellSpoken) {
                    playShellSpoken();
                    shellSpoken = true;
                } else {
                    reset();
                }
            }
        } else {
            if (((CheckBox) findViewById(R.id.shell_has_spoken_check_box)).isChecked() && !shellSpoken) {
                findViewById(R.id.conch_button).setVisibility(View.INVISIBLE);
                playShellSpoken();
                shellSpoken = true;
            } else {
                reset();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
