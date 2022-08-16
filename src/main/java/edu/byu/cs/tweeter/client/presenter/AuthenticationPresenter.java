package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationNotificationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticationPresenter extends NavigateToUserPresenter {

    public AuthenticationPresenter(AuthenticationView view) {
        super(view);
    }

    public interface AuthenticationView extends NavigateToUserView {
        void displayErrorMessage(String message);
        //When we need to clear the error message
        void clearErrorMessage();
        //When a message other than error message needs to be displayed in the view
        void displayInfoMessage(String message);
        //When we need to clear messages
        void clearInfoMessage();
    }

    protected AuthenticationView getAuthenticationView() {
        return (AuthenticationView) getView();
    }

    protected class AuthenticationObserver implements AuthenticationNotificationServiceObserver {

        @Override
        public void handleSuccess(AuthToken authToken, User theUser) {
            //This logic was taken from RegisterHandler that was in the view
            getAuthenticationView().navigateToUser(theUser);
            //getAuthenticationView().clearErrorMessage();
            getAuthenticationView().displayInfoMessage("Hello " + theUser.getName());
        }

        @Override
        public void handleFailure(String message) {
            getAuthenticationView().displayErrorMessage(message);
        }
    }
}
