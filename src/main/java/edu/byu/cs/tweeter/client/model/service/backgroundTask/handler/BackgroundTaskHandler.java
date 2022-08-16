package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;

/*
Message handler (i.e., observer) for background tasks. Handlers are needed anytime we call one of the background tasks.
The handler receives messages with data from the task, which is the result of the task.
This is sort of the output of the background task.
We use generic type here because the different services implemented different types of observers and
we use type bound because we need the observers extend the abstract ServiceObserver class we created.
 */
public abstract class BackgroundTaskHandler<T extends ServiceObserver> extends Handler {

    private final T observer;

    public BackgroundTaskHandler(T observer) {
        //Added
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        //We use BackgroundTask instead of a specific task like FollowTask or LogoutTask ???
        boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);

        if (success) {
            handleSuccessMessage(observer, msg.getData());
        } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
            String message = getFailedMessagePrefix() + ": " +msg.getData().getString(BackgroundTask.MESSAGE_KEY);
            observer.handleFailure(message);
        } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
            String message = getFailedMessagePrefix() + " because of exception: " + ex.getMessage();
            observer.handleFailure(message);
        }
    }

    //Method for messages that are not simple and contain data to be passed
    protected abstract void handleSuccessMessage(T observer, Bundle bundle);
    protected abstract String getFailedMessagePrefix();
}
