package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.AuthenticationRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;



public abstract class AuthenticationTask extends BackgroundTask {
    private static final String LOG_TAG = "AuthenticationTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";


    private User theUser;
    private AuthToken authToken;
    private AuthenticationRequest request;
    private ServerFacade serverFacade;

    protected AuthenticationTask(Handler messageHandler, AuthenticationRequest request) {
        super(messageHandler);
        this.request = request;
    }

    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, theUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }


    @Override
    public void runTask() {
        try {
            AuthenticationResponse response = getResponse(request);

            theUser = response.getUser();
            authToken = response.getAuthToken();

            BackgroundTaskUtils.loadImage(theUser);
            sendSuccessMessage();//added TODO
        }
        catch(Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    public abstract AuthenticationResponse getResponse(AuthenticationRequest request) throws Exception;

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    /*private Pair<User, AuthToken> getResult() {
        User fakeUser = getFakeData().getFirstUser();
        AuthToken fakeAuthToken = getFakeData().getAuthToken();
        return new Pair<>(fakeUser, fakeAuthToken);
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
