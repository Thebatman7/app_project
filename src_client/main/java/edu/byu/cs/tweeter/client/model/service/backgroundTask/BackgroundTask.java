package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;




public abstract class BackgroundTask implements Runnable {
    private static final String LOG_TAG = "BackgroundTask";


    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    //Message handler to be notified when the task completes
    protected final Handler messageHandler;

    //With protected, only subclasses have access to it
    protected BackgroundTask(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    protected abstract void loadMessageBundle(Bundle msgBundle);

    public void sendSuccessMessage() {
        Bundle msgBundle = createBundle(true);
        loadMessageBundle(msgBundle);
        //Bundle msgBundle = createSuccessBundle();
        sendMessage(msgBundle);
    }

    public void sendFailedMessage(String message) {
        Bundle msgBundle = createBundle(false);
        //Bundle msgBundle = createFailedBundle();
        msgBundle.putString(MESSAGE_KEY, message);
        sendMessage(msgBundle);
    }
    public void sendExceptionMessage(Exception exception) {
        Bundle msgBundle = createBundle(false);
        //Bundle msgBundle = createFailedBundle();
        msgBundle.putSerializable(EXCEPTION_KEY, exception);
        sendMessage(msgBundle);
    }

    /*
     This method adds the part that is common,...putBoolean(SUCCESS_KEY, boolean), to all
     success message, fail messages, and exception messages
     */
    private Bundle createBundle(boolean value){
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, value);
        return msgBundle;
    }

    /*
     This method adds the part that is common,...putBoolean(SUCCESS_KEY, boolean), to all
     success message, fail messages, and exception messages
     */
    private void sendMessage(Bundle msgBundle) {
        Message msg = Message.obtain();
        msg.setData(msgBundle);

        messageHandler.sendMessage(msg);
    }

    //Template Method
    @Override
    public void run() {
        try {
            /*
            We have an abstract method that every subclass will override.
            This overridden method will do the task
            */
            runTask();
            //sendSuccessMessage();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
        //sendSuccessMessage();
    }

    //Every subclass needs to fill in this method
    protected abstract void runTask() throws Exception;
}


