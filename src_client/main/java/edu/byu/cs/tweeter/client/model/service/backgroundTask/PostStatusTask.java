package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthorizedTask {
    private static final String LOG_TAG = "PostStatusTask";

    /*
    The new status being sent. Contains all properties of the status,
    including the identity of the user sending the status.
    */
    private Status status;

    private ServerFacade serverFacade;

    private PostStatusRequest request;
    public PostStatusTask(PostStatusRequest request, Handler messageHandler) {
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
            PostStatusResponse response = getResponse(request);
            sendSuccessMessage();//added TODO
        }
        catch(Exception ex){
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    public PostStatusResponse getResponse(PostStatusRequest request) throws Exception {
        return getServerFacade().postStatus(request, "/poststatus");
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
