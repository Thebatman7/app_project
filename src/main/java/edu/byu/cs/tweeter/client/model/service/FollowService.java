package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersMessageHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingMessageHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedNotificationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;

/*
Service: They need to run backgound tasks to call the server. Every time we call the server
we have to execute background threat, that is where we run the task, that makes all asynchronous.
We have all this callbacks when the task is completed. When the FollowService run the task,
the follow service has to pass a handler to the task. This allows the GetFollowingTask to call back to
the FollowService to tell it what the result was, success, failure, exception. Handlers are just observers.
Service in turn notifies the Presenter, we use an observer for this.
In the view for the following tab all we can do is scroll through the followees or click on one
followee to navigate to that person.
*/
public class FollowService {

    public interface GetFollowingObserver extends PagedNotificationServiceObserver<User> { }

    public void getFollowing(FollowingRequest request, GetFollowingObserver observer ) {
        //We get the class that extends abstract BackgroundTask which implements the Runnable class
        GetFollowingTask getFollowingTask = new GetFollowingTask(request, new GetFollowingMessageHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(getFollowingTask);
    }


    public interface GetFollowersObserver extends PagedNotificationServiceObserver<User> { }

    public void getFollowers(FollowersRequest request, GetFollowersObserver observer) {
        //We get the class that extends abstract BackgroundTask which implements the Runnable class
        GetFollowersTask getFollowersTask = new GetFollowersTask(request, new GetFollowersMessageHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(getFollowersTask);
    }
}
