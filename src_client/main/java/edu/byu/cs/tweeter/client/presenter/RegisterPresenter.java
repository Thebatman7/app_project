package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;

/**
 For the register view there is only one thing the user can do, which is register.
 For every different kind of input the user can give in the view there is a corresponding method on the presenter.
 */
public class RegisterPresenter extends AuthenticationPresenter {
    //Constructor
    public RegisterPresenter(View view) {
        super ((AuthenticationView) view);
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytes) {
        //We clear all messages
        getAuthenticationView().clearErrorMessage();
        getAuthenticationView().clearInfoMessage();


        /*
        After a user clicks on the register button, we validate the input. This method was in the view
        so in order to use it in the fragment we must move the logic to this class as well.
        We modified the method so it returns a message instead of throwing an exception.
        */
        String message = validateRegistration(firstName, lastName, alias, password);
        //We add logic to know if there is a problem or not
        if (message == null) {
            getAuthenticationView().displayInfoMessage("Registering...");
            RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, alias, password, imageBytes);
            /*
            We call the service because it is the one that can call the server.
            We need to pass an observer, since this class is implementing the
            observer we can pass this class as an observer.
            We can call service method this way or we can have a UserService variable.
            */
            new UserService().register(registerRequest, new RegisterUserObserver());
        } else {
            getAuthenticationView().displayErrorMessage("Register failed: " + message);
        }
    }

    /*
    We move this method that was originally in the view and we modify it so it can work in the presenter.
    We use an alias, so we pass it an alias. We use a password, we pass it a password.
    Instead of throwing exceptions we return messages
    */
    public String validateRegistration(String firstName, String lastName, String alias, String password) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (alias.length() == 0) {
           return "Alias cannot be empty.";
        }
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    private class RegisterUserObserver extends AuthenticationObserver implements UserService.RegisterObserver {}
}
