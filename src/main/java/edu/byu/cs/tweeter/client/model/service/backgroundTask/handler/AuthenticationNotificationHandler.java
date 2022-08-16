package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.AuthenticationTask;
import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationNotificationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticationNotificationHandler<T extends AuthenticationNotificationServiceObserver> extends BackgroundTaskHandler<T> {

    public AuthenticationNotificationHandler(T observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(T observer, Bundle bundle) {
        User theUser = (User) bundle.getSerializable(AuthenticationTask.USER_KEY);
        AuthToken authToken = (AuthToken) bundle.getSerializable(AuthenticationTask.AUTH_TOKEN_KEY);

        //We cache user session information
        Cache.getInstance().setCurrUser(theUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.handleSuccess(authToken, theUser);
    }
}
