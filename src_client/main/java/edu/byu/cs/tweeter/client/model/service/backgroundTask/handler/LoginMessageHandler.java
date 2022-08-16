package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationNotificationServiceObserver;

public class LoginMessageHandler extends AuthenticationNotificationHandler {

    public LoginMessageHandler(UserService.LoginObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to login"; }
}
