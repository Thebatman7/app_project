package edu.byu.cs.tweeter.client.model.service.observer;

public interface IsFollowerNotificationServiceObserver extends ServiceObserver{
    void handleSuccess(boolean isFollower);
}
