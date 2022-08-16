package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.net.request.AuthenticationRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticationTask {
    //private static final String LOG_TAG = "RegisterTask";

    public RegisterTask(RegisterRequest request, Handler messageHandler) {
        //We pass this to the abstract class so it sets its handler and sends back messages
        super(messageHandler, request);
    }

    @Override
    public AuthenticationResponse getResponse(AuthenticationRequest request) throws Exception {
        return getServerFacade().register((RegisterRequest)request, "/register");
    }
}
