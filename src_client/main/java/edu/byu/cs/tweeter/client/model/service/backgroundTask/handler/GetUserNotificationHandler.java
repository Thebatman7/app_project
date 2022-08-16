package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.observer.GetUserNotificationServiceObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class GetUserNotificationHandler<T extends GetUserNotificationServiceObserver> extends BackgroundTaskHandler<T> {

    public GetUserNotificationHandler(T observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(T observer, Bundle bundle) {
        User user = (User) bundle.getSerializable(GetUserTask.USER_KEY);
        //
        observer.handleSuccess(user);
    }
}
