package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.FeedService;

public class GetFeedMessageHandler extends PagedNotificationHandler {

    public GetFeedMessageHandler(FeedService.GetFeedObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to get feed"; }
}
