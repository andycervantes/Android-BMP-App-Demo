package ptc.hw6;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy Cervantes on 11/23/2016.
 * heavily influenced by the google api/developers documentation/examples
 */
public class ServiceServer extends Service {

    // better to use this method when refactoring code
    private static final String TAG = ServiceServer.class.getSimpleName();
//    public STATUS serviceStatus = new MainStatus(MainStatus.STATUS.STOPPED);
    // serviceStatus of the service
    public MainStatus serviceStatus = MainStatus.STOPPED;
    // list for the audio clips
    public List<String> audioList;

    // initialize the audio player backend
    public AudioPlayer audioPlayer = null;

    // keep track of what audio clip is currently playing
    public int currentSelection = -1;

    // default onCreate function called for when service is initialized
    // takes no arguments because its a service
    @Override
    public void onCreate() {
        super.onCreate();

        if (audioPlayer == null)
            audioPlayer = AudioPlayer.create(ServiceServer.this, AudioClip.AUDIOCLIP[0]);

        audioList = new ArrayList<>();
        audioList.add(0,"1. Test 1");
        audioList.add(1,"2. Test 2");
        audioList.add(2,"3. Test 3");
        audioList.add(3,"4. Test 4");
    }

    // need to clear out the service since its a resource hog
    @Override
    public void onDestroy() {
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.release();
            audioPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return audioServiceBinder;
    }

    // call the interface api from the aidl file
//    private final AudioServer.Stub mBinder = new MainInterface().Stub();
//    @Override
    private final AudioService.Stub audioServiceBinder = new AudioService.Stub() {
        @Override
        public boolean stop() throws RemoteException {

            try {
                switch (audioPlayer.currentStatus()) {
                    case PAUSED:
                    case PREPARED:
                    case STARTED:
                    case STOPPED:
                        audioPlayer.stop();
                        serviceStatus = MainStatus.STOPPED;
                    default:
                        audioPlayer.reset();
                        if (currentSelection < 0)
                            audioPlayer.setDataSource(0);
                        else
                            audioPlayer.setDataSource(currentSelection);

                        audioPlayer.prepare();
                        audioPlayer.stop();
                        serviceStatus = MainStatus.STOPPED;
                }
            }
            // interesting....
            catch (Throwable rekt) {
                rekt.printStackTrace();
            }

            return false;
        }

        @Override
        public boolean play(int indexSelection) throws RemoteException {
            // case where we are playing a clip already while choosing another clip
            if (audioPlayer != null) {
                if (audioPlayer.isPlaying())
                    audioPlayer.stop();
            }

            // try to play the audio clip
            try {
                // handle case where we try to select a different song if it audio player serviceStatus was paused
                if ( currentSelection != AudioClip.AUDIOCLIP[indexSelection] ) {
                    audioPlayer.reset();
                    audioPlayer.release();
                    audioPlayer = AudioPlayer.create(ServiceServer.this, AudioClip.AUDIOCLIP[indexSelection]);
                    currentSelection = indexSelection;
                }
                else {
                    try {
                        if (audioPlayer.currentStatus() == AudioStatus.PREPARED) {
                            audioPlayer.start();
                            serviceStatus = MainStatus.PLAYING;
                        }
                        else if (audioPlayer.currentStatus() == AudioStatus.STARTED) {
                            audioPlayer.seekTo(0);
                            serviceStatus = MainStatus.PLAYING;
                        }
                        else if (audioPlayer.currentStatus() == AudioStatus.PAUSED) {
                            audioPlayer.start();
                            serviceStatus = MainStatus.PLAYING;
                        }
                        else if (audioPlayer.currentStatus() == AudioStatus.STOPPED) {
                            audioPlayer.prepare();
                            audioPlayer.seekTo(0);
                            audioPlayer.start();
                            serviceStatus = MainStatus.PLAYING;
                        }
                        else {
                            audioPlayer.reset();
                            audioPlayer.setDataSource(indexSelection);
                            audioPlayer.prepare();
                            audioPlayer.start();
                            serviceStatus = MainStatus.PLAYING;
                        }
                    }
                    catch (IOException rekt) {
                        rekt.printStackTrace();
                    }
                }
            }
            catch (NullPointerException rekt) {
                rekt.printStackTrace();
            }

            return false;
        }

        @Override
        public boolean idle() throws RemoteException {
            return false;
        }

        @Override
        public boolean pause() throws RemoteException {
            return false;
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };
}
