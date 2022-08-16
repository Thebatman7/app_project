package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.MainService;

public class GetFollowingCountHandler extends CountNotificationHandler {

    public GetFollowingCountHandler(MainService.GetFollowingCountObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to get following count"; }
}
