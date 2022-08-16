package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class GetUserMessageHandler extends GetUserNotificationHandler{

    public GetUserMessageHandler(UserService.GetUserObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to get user's profile"; }
}
