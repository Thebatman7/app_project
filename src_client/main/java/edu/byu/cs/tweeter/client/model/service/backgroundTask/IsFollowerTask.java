package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthorizedTask {
    private static final String LOG_TAG = "IsFollowerTask";

    public static final String IS_FOLLOWER_KEY = "is-follower";


    boolean isFollower;

    private ServerFacade serverFacade;
    private IsFollowerRequest request;

    public IsFollowerTask(IsFollowerRequest request, Handler messageHandler) {
        super(messageHandler, request.getAuthToken());
        this.request = request;
    }

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }

    @Override
    public void runTask() {
        try {
            IsFollowerResponse response = getResponse(request);
            isFollower = response.isFollower();
            //isFollower = new Random().nextInt() > 0;
            sendSuccessMessage();//added TODO
        }
        catch(Exception ex){
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }


    public IsFollowerResponse getResponse(IsFollowerRequest request) throws Exception {
        return  getServerFacade().isFollower(request, "/isfollower");
    }
    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    /*private void sendSuccessMessage(boolean isFollower) {
        sendSuccessMessage(new BundleLoader() {
            @Override
            public void load(Bundle msgBundle) {
                msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
            }
        });
    }*/

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
}