package edu.byu.cs.tweeter.client.model.service;

import android.os.Looper;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetStoryMessageHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedNotificationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;

/*
Service: They need to run backgound tasks to call the server. Every time we call the server
we have to execute background threat, that is where we run the task, that makes all asynchronous.
We have all this callbacks when the task is completed. When the StoryService run the task,
the follow service has to pass a handler to the task. This allows the GetStoryTask to call back to
the StoryService to tell it what the result was, success, failure, exception. Handlers are just observers.
Service in turn notifies the Presenter, we use an observer for this.
*/
public class StoryService {

    public interface GetStoryObserver extends PagedNotificationServiceObserver<Status> { }
    public void getStory (StoryRequest request, GetStoryObserver observer) {
        //GetStoryTask getStoryTask = new GetStoryTask(request, new GetStoryMessageHandler(observer));
        GetStoryTask getStoryTask = createStoryTask(request, observer);
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(getStoryTask);
    }

    //Method to make code more testable
    public GetStoryTask createStoryTask(StoryRequest request, GetStoryObserver observer) {
        return new GetStoryTask(request, new GetStoryMessageHandler(observer));
    }
}
