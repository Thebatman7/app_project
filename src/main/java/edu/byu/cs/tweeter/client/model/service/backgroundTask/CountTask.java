package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

public abstract class CountTask extends AuthorizedTask {
    private static final String LOG_TAG = "CountTask";

    public static final String COUNT_KEY = "count";

    //The user whose count is being retrieved. (This can be any user, not just the currently logged-in user.)
    private User targetUser;

    private int count;//??? what is this variable, we used to get it from the success message , now where does it come from

    private CountRequest request;

    private ServerFacade serverFacade;

    protected CountTask(Handler messageHandler, CountRequest request) {
        super(messageHandler, request.getAuthToken());
        this.request = request;
    }

    protected User getTargetUser() {
        return targetUser;
    }

    //The new bundle loader is loaded with what we want and is unique to this task
    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putInt(COUNT_KEY, count);
    }

    @Override
    public void runTask() {
        try {
            CountResponse response = getResponse(request);
            count = response.getCount();
            sendSuccessMessage();//added TODO
        }
        catch(Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    protected abstract CountResponse getResponse(CountRequest request) throws Exception;

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
