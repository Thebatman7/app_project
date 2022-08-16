package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.AuthenticationRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;

/**
 * Background task that logs in a user (i.e., starts a session).
 * This calls will extend from the abstract class GetbackgroundTask which implements Runnable to run
 * task in the background.
 */
public class LoginTask extends AuthenticationTask {
    private static final String LOG_TAG = "LoginTask";

    public LoginTask(LoginRequest request, Handler messageHandler) {
        super(messageHandler, request);
    }

    @Override
    public AuthenticationResponse getResponse(AuthenticationRequest request) throws Exception {
        return getServerFacade().login((LoginRequest) request, "/login");
    }
}
