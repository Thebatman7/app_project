package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthorizedTask {
    private static final String LOG_TAG = "LogoutTask";

    private ServerFacade serverFacade;

    private LogoutRequest request;
    private boolean success;

    public LogoutTask(LogoutRequest request, Handler messageHandler) {
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
            LogoutResponse response = getResponse(request);
            success = response.isSuccess();
            sendSuccessMessage();//added TODO
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    public LogoutResponse getResponse(LogoutRequest request) throws Exception {
        return getServerFacade().logout(request, "/logout");
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
