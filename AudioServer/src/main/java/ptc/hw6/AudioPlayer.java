package ptc.hw6;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Andy Cervantes on 11/23/2016.
 */
public class AudioPlayer extends MediaPlayer {
    private static final String TAG = AudioPlayer.class.getSimpleName();
    public Context context;
    public MediaPlayer mediaPlayer;
    public AudioStatus status;

    // default constructor
    public AudioPlayer(Context context, int selectionIndex) {
        mediaPlayer = MediaPlayer.create(context, selectionIndex);
        this.context = context;
        this.status = AudioStatus.IDLE;
    }

    // default object creator
    public static AudioPlayer create(Context context, int selectionIndex) {
        AudioPlayer audioPlayer = new AudioPlayer(context, selectionIndex);
        return audioPlayer;
    }

    // return the current serviceStatus of the audio player
    public AudioStatus currentStatus() {
        return this.status;
    }

//    @Override
    public void onReset() {
        try {
            this.mediaPlayer.reset();
            this.status = AudioStatus.IDLE;
        }
        catch (NullPointerException rekt) {
            rekt.printStackTrace();
        }
//        super.onReset();
    }

//    @Override
    public void onPrepare() {
        try {
            this.mediaPlayer.prepare();
            this.status = AudioStatus.PREPARED;
        }
        catch (IOException rekt) {
            rekt.printStackTrace();
        }
//        super.onPrepare();
    }

//    @Override
    public void onPause() {
        try {
            this.mediaPlayer.pause();
            this.status = AudioStatus.PAUSED;
        }
        catch (NullPointerException rekt) {
            rekt.printStackTrace();
        }
//        super.onPause();
    }

//    @Override
    public void onStart() {
        try {
            this.mediaPlayer.start();
            this.status = AudioStatus.STARTED;
        }
        catch (NullPointerException rekt) {
            rekt.printStackTrace();
        }
//        super.onStart();
    }

//    @Override
    public void onStop() {
        try {
            this.mediaPlayer.stop();
            this.status = AudioStatus.STOPPED;
        }
        catch (NullPointerException rekt) {
            rekt.printStackTrace();
        }
//        super.onStop();
    }

//    @Override
    public void onRelease() {
        try {
            this.mediaPlayer.release();
            this.status = AudioStatus.END;
        }
        catch (NullPointerException rekt) {
            rekt.printStackTrace();
        }
//        super.onStop();
    }

    // handle all the cases of dealing with the source of the audio files
    @Override
    public void setDataSource(AssetFileDescriptor afd) throws IOException, IllegalArgumentException, IllegalStateException {
        super.setDataSource(afd);
    }

    @Override
    public void setDataSource(MediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        super.setDataSource(dataSource);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        super.setDataSource(fd);
        mediaPlayer.setDataSource(fd);
        status = AudioStatus.INITIALIZED;
    }

//    @Override
    public void setDataSource(int selectionIndex) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
//        super.setDataSource(selectionIndex);

        AssetFileDescriptor assetFileDescriptor = context.getResources().openRawResourceFd(selectionIndex);

        // kept crashing because it was null
        if (assetFileDescriptor == null) {
            Log.d(TAG, "setDataSource: NULL");
            return;
        }

        mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());
        assetFileDescriptor.close();
        status = AudioStatus.INITIALIZED;
    }


}
