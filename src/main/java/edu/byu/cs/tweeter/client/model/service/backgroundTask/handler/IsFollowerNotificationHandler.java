package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.observer.IsFollowerNotificationServiceObserver;

public abstract class IsFollowerNotificationHandler<T extends IsFollowerNotificationServiceObserver> extends BackgroundTaskHandler<T> {

    public IsFollowerNotificationHandler(T observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(T observer, Bundle bundle) {
        boolean isFollower = bundle.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}
