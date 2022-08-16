package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.observer.PagedNotificationServiceObserver;

public abstract class PagedNotificationHandler<T extends PagedNotificationServiceObserver, I> extends BackgroundTaskHandler<T> {

    public PagedNotificationHandler(T observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(T observer, Bundle bundle) {
        List<I> items = (List<I>) bundle.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = bundle.getBoolean(PagedTask.MORE_PAGES_KEY);
        //PagedNotificationServiceObserver method, void handleSuccess();
        observer.handleSuccess(items, hasMorePages);
    }
}
