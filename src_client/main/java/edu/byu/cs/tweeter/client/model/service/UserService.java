package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserMessageHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginMessageHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationNotificationServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.GetUserNotificationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;

/*
Chaching the result is responsibility of the presenter or the service.
Service: they need to run backgound tasks to call the server. Every time we call the server
we have to execute background threat, that is where we run the task, that makes all asynchronous.
We have all this callbacks when the task is completed. When the UserService run the task,
the user service has to pass a handler to the task. This allows the LoginTask to call back to
the UserService to tell it what the result was, success, failure, exception. Handlers are just observers.
Service in turn notifies the Presenter, we use an observer for this.
*/
public class UserService {

    public interface GetUserObserver extends GetUserNotificationServiceObserver { }
    public void getUser(GetUserRequest request, GetUserObserver observer) {
        //We get the class that extends abstract BackgroundTask which implements the Runnable class
        GetUserTask getUserTask = new GetUserTask(request, new GetUserMessageHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(getUserTask);
    }

    public interface LoginObserver extends AuthenticationNotificationServiceObserver { }
    public void login(LoginRequest request, LoginObserver observer) {
        LoginTask loginTask = new LoginTask(request,  new LoginMessageHandler(observer));
        //This method contains the executor that executes getFollowingTask which is a runnable
        BackgroundTaskUtils.runTask(loginTask);
    }


    public interface RegisterObserver extends AuthenticationNotificationServiceObserver { }
    public void register(RegisterRequest request, RegisterObserver observer) {
        RegisterTask registerTask = new RegisterTask(request, new RegisterHandler(observer));
        //This method contains the executor that executes registerTask which is a runnable
        BackgroundTaskUtils.runTask(registerTask);
    }
}
