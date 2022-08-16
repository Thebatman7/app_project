package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFeedMessageHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedNotificationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;

/*
Service: They need to run backgound tasks to call the server. Every time we call the server
we have to execute background threat, that is where we run the task, that makes all asynchronous.
We have all this callbacks when the task is completed. When the FeedService run the task,
the follow service has to pass a handler to the task. This allows the GetFeedTask to call back to
the FeedService to tell it what the result was, success, failure, exception. Handlers are just observers.
Service in turn notifies the Presenter, we use an observer for this.
*/
public class FeedService {

    public interface GetFeedObserver extends PagedNotificationServiceObserver<Status> { }

    public void getFeed(FeedRequest request, GetFeedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(request, new GetFeedMessageHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(getFeedTask);
    }
}
