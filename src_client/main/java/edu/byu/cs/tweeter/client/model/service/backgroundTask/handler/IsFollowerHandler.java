package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.MainService;

public class IsFollowerHandler extends IsFollowerNotificationHandler {

    public IsFollowerHandler(MainService.IsFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to determine following relationship"; }
}
