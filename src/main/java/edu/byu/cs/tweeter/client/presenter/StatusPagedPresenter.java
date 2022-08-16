package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class StatusPagedPresenter extends PagedPresenter<Status> {
    public StatusPagedPresenter(StatusPagedView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
    }

    //navigateToURL method is unique for feed and story
    public interface StatusPagedView extends PagedPresenter.PagedView<Status> {
        //When a URL was clicked on
        void navigateToUrl(String clickable);
    }

    protected StatusPagedView getStatusPagedView() {
        return (StatusPagedView) getView();
    }

    public void clickDecider(String clickable) {
        if (clickable.contains("http")) {
            getStatusPagedView().navigateToUrl(clickable);
        } else {
            goToUser(clickable);
        }
    }
}
