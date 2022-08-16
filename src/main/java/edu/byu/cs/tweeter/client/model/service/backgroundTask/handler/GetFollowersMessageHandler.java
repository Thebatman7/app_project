package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.FollowService;

public class GetFollowersMessageHandler extends PagedNotificationHandler {

    public GetFollowersMessageHandler(FollowService.GetFollowersObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to get followers"; }
}
