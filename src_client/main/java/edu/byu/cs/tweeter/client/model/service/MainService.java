package edu.byu.cs.tweeter.client.model.service;

import android.os.Looper;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LogoutHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PostStatusHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.UnfollowHandler;
import edu.byu.cs.tweeter.client.model.service.observer.CountNotificationServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.IsFollowerNotificationServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;

/*
Chaching the result is responsibility of the presenter or the service.
Service: they need to run backgound tasks to call the server. Every time we call the server
we have to execute background threat, that is where we run the task, that makes all asynchronous.
We have all this callbacks when the task is completed. When the UserService run the task,
the user service has to pass a handler to the task. This allows the LoginTask to call back to
the UserService to tell it what the result was, success, failure, exception. Handlers are just observers.
Service in turn notifies the Presenter, we use an observer for this.
*/
public class MainService {

    public interface GetFollowersCountObserver extends CountNotificationServiceObserver { }
    /*
    This method requires an observer so we can notify when it is done and we call the appropriate method when needed.
    GetFollowersCountHandler are examples of observer pattern because when the brackground tasks are done
    they notify Service classes.
    */
    public void getFollowersCount(AuthToken authToken, User selectedUser, GetFollowersCountObserver observer) {
        FollowersCountRequest followersCountRequest =  new FollowersCountRequest(authToken, selectedUser.getAlias());
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(followersCountRequest,
                new GetFollowersCountHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(followersCountTask);
    }

    public interface GetFollowingCountObserver extends CountNotificationServiceObserver { }
    /*
    This method requires an observer so we can notify when it is done and we call the appropriate method when needed.
    GetFollowingCountHandler are examples of observer pattern because when the brackground tasks are done
    they notify Service classes.
    */
    public void getFollowingCount(AuthToken authToken, User selectedUser, GetFollowingCountObserver observer) {
        FollowingCountRequest followingCountRequest =  new FollowingCountRequest(authToken, selectedUser.getAlias());
        GetFollowingCountTask getFollowingCountTask = new GetFollowingCountTask(followingCountRequest,
                new GetFollowingCountHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(getFollowingCountTask);
    }


    public interface IsFollowerObserver extends IsFollowerNotificationServiceObserver { }
    public void isFollower(IsFollowerRequest request, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(request, new IsFollowerHandler(observer));
        //This method contains the executor that executes isFollowerHandler which is a runnable
        BackgroundTaskUtils.runTask(isFollowerTask);
    }


    public interface UnfollowObserver extends SimpleNotificationServiceObserver { }
    public void unfollow(UnfollowRequest request, UnfollowObserver observer) {
        //We get the class that extends abstract BackgroundTask which implements the Runnable class
        UnfollowTask unfollowTask = new UnfollowTask(request, new UnfollowHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(unfollowTask);
    }


    public interface FollowObserver extends SimpleNotificationServiceObserver { }
    public void follow(FollowRequest request, FollowObserver observer) {
        //We get the class that extends abstract BackgroundTask which implements the Runnable class
        FollowTask followTask = new FollowTask(request, new FollowHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(followTask);
    }


    public interface LogoutObserver extends SimpleNotificationServiceObserver { }
    /*
    This method requires an observer so we can notify when it is done and
    we call the appropriate method when needed. LogoutHandler are examples of observer pattern
    because when the brackground tasks are done they notify Service classes.
    */
    public void logout(LogoutRequest request, LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(request, new LogoutHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(logoutTask);
    }


    public interface StatusObserver extends SimpleNotificationServiceObserver { }
    /*
    This method requires an observer so we can notify when it is done and
    we call the appropriate method when needed. PostStatusHandler are examples of observer pattern
    because when the brackground tasks are done they notify Service classes.
    */
    public void postStatus(PostStatusRequest request, StatusObserver observer) {
        //Service and presenter
        PostStatusTask statusTask = new PostStatusTask(request, new PostStatusHandler(observer));
        //This method contains the executor that executes statusTask which is a runnable
        BackgroundTaskUtils.runTask(statusTask);
    }
}

