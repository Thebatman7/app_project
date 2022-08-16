package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.MainService;

public class GetFollowersCountHandler extends CountNotificationHandler {

    public GetFollowersCountHandler(MainService.GetFollowersCountObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to get followers count"; }
}
