package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

/**
 For the login view there is only one thing the user can do, which is login.
 For every different kind of input the user can give in the view there is a corresponding method on the presenter.
*/
public class LoginPresenter extends AuthenticationPresenter {

    public LoginPresenter(View view) {
        super((AuthenticationView)view);
    }

    public void login(String alias, String password) {
        //We clear all messages
        getAuthenticationView().clearErrorMessage();
        getAuthenticationView().clearInfoMessage();
        /*
        After a user clicks on the login button, we validate the input. This method was in the view
        so in order to use it in the fragment we must move the logic to this class as well.
        We modified the method so it returns a message instead of throwing an exception.
        */
        String message = validateLogin(alias, password);
        //We add logic to know if there is a problem or not
        if (message == null) {
            getAuthenticationView().displayInfoMessage("Logging in...");
            LoginRequest loginRequest = new LoginRequest(alias, password);
            /*
            We call the service because it is the one that can call the server.
            We need to pass an observer to get information back.
            We can call service method this way or we can have a UserService variable.
            */
            new UserService().login(loginRequest, new LoginUserObserver());
        } else {
            getAuthenticationView().displayErrorMessage("Login failed: " + message);
        }
    }

    /*
    We move this method that was originally in the view and we modify it so it can work in the presenter.
    We use an alias, so we pass it an alias. We use a password, we pass it a password.
    Instead of throwing exceptions we return messages
    */
    private String validateLogin(String alias, String password) {
        //We make sure the alias has an @ in it
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        //We make sure it is at least two characters
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        //We make sure the password is not empty
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    private class LoginUserObserver extends AuthenticationObserver implements UserService.LoginObserver {}
}
