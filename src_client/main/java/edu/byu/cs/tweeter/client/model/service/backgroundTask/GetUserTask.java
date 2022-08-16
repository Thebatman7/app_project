package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthorizedTask {
    private static final String LOG_TAG = "GetUserTask";

    public static final String USER_KEY = "user";

    private ServerFacade serverFacade;
    //Alias (or handle) for user whose profile is being retrieved.
    private GetUserRequest request;
    private User user;


    public GetUserTask(GetUserRequest request, Handler messageHandler) {
        super(messageHandler, request.getAuthToken());
        this.request = request;
    }

    //The new bundle loader is loaded with what we want and is unique to this task
    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
    }

    @Override
    protected void runTask() {
        try {
            GetUserResponse response = getResponse(request);
            user = response.getUser();

            BackgroundTaskUtils.loadImage(user);
            sendSuccessMessage();//added TODO
        }
        catch(Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            sendExceptionMessage(e);
        }

    }

    public GetUserResponse getResponse(GetUserRequest request) throws Exception {
        return getServerFacade().getUser(request, "/getuser");
    }

    public ServerFacade getServerFacade() {
        if (serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }


    /*@Override//Fix me, delete teh whole method
    public void runTask() {
        try {
            User user = getUser();//FIX ME, delete
            sendSuccessMessage(user);

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }*/
    //private FakeData getFakeData() { return new FakeData(); }
}
