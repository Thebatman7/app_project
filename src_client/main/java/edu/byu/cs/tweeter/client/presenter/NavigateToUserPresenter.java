package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public abstract class NavigateToUserPresenter extends Presenter<NavigateToUserPresenter.NavigateToUserView> {

    public NavigateToUserPresenter(NavigateToUserView view) {
        super(view);
    }

    public interface NavigateToUserView extends Presenter.View {
        //When user was clicked on and we got user's data we tell the view the user that needs to be displayed
        void navigateToUser(User user);
    }
}
