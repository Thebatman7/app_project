package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.CountTask;
import edu.byu.cs.tweeter.client.model.service.observer.CountNotificationServiceObserver;

public abstract class CountNotificationHandler<T extends CountNotificationServiceObserver> extends BackgroundTaskHandler<T> {
    public CountNotificationHandler(T observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(T observer, Bundle bundle) {
        int count = bundle.getInt(CountTask.COUNT_KEY);
        observer.handleSuccess(count);
    }
}
