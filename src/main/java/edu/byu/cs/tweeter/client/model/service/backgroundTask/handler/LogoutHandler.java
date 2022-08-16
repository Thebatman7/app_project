package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.MainService;

public class LogoutHandler extends SimpleNotificationHandler {
    //This handler must have pointer to the observer so we can notify classes above.
    public LogoutHandler(MainService.LogoutObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to logout"; }
}
