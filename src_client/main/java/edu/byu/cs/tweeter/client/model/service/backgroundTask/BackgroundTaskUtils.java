package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.util.ByteArrayUtils;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * BackgroundTaskUtils contains utility methods needed by background tasks.
 */
public class BackgroundTaskUtils {

    private static final String LOG_TAG = "BackgroundTaskUtils";
    /**
     * Loads the profile image for the user.
     *
     * @param user the user whose profile image is to be loaded.
     */
    public static void loadImage(User user) throws IOException {
        try {
            byte[] bytes = ByteArrayUtils.bytesFromUrl(user.getImageUrl());
            user.setImageBytes(bytes);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString(), e);
            throw e;
        }
    }

    /*
    In java we have the executor service class used to run a backgournd task. We need to give it
    a handler. Handler is an android thing, handler is just an object that we can call when the background
    task is finished. We can pass it whatever data we want. Task calls the handler when it is finished.
    Handler classes should be in Service classes. Services call the Tasks. All this is asynchronous.
    */
    public static void runTask(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }


}
