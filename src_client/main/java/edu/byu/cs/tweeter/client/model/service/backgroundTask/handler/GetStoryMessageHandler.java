package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.model.service.StoryService;


public class GetStoryMessageHandler extends PagedNotificationHandler {

    public GetStoryMessageHandler(StoryService.GetStoryObserver observer) {
        super(observer);
    }

    @Override
    protected String getFailedMessagePrefix() { return "Failed to get story"; }
}
