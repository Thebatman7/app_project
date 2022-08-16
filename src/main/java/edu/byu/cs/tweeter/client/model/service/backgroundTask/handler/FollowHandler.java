package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.MainService;

public class FollowHandler extends SimpleNotificationHandler {
    //This handler must have pointer to the observer so we can notify classes above.
    public FollowHandler(MainService.FollowObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to follow"; }
}
