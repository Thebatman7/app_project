package edu.byu.cs.tweeter.client.model.service.observer;

import java.util.List;

//We can get User or Status variables that is why we use generic type
public interface PagedNotificationServiceObserver<I> extends ServiceObserver {
    void handleSuccess(List<I> items, boolean hasMorePages);
}
