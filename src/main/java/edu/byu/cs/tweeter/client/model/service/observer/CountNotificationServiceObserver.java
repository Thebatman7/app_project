package edu.byu.cs.tweeter.client.model.service.observer;

public interface CountNotificationServiceObserver extends ServiceObserver {
    void handleSuccess(int count);
}
