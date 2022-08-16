package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.FollowService;

public class GetFollowingMessageHandler extends PagedNotificationHandler {

    public GetFollowingMessageHandler(FollowService.GetFollowingObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to get following"; }
}
