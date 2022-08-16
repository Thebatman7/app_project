package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthorizedTask {
    private static final String LOG_TAG = "UnfollowTask";


    private ServerFacade serverFacade;

    private UnfollowRequest request;
    public UnfollowTask(UnfollowRequest request, Handler messageHandler) {
        super(messageHandler, request.getAuthToken());
        this.request = request;
    }

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle = null;
    }

    @Override
    public void runTask() {
        try {
            UnfollowResponse response = getResponse(request);
            sendSuccessMessage();//added TODO
        }
        catch(Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    public UnfollowResponse getResponse(UnfollowRequest request) throws Exception {
        return getServerFacade().unfollow(request, "/unfollow");
    }
    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
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
}
