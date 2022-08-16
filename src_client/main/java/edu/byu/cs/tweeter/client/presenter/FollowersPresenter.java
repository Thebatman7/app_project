package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;

/**
 In the view for the followers tab all we can do is scroll through the followers or
 click on one follower to navigate to that person.
 There is one presenter per view.
 */
public class FollowersPresenter extends PagedPresenter<User> {

    //Constructor
    public FollowersPresenter(PagedView view, AuthToken authToken, User targetUser) {
        super(view, authToken, targetUser);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {

        FollowersRequest followersRequest;
        if(lastItem == null) {
            followersRequest = new FollowersRequest(authToken, targetUser.getAlias(), pageSize, null);
        }
        else {
            followersRequest = new FollowersRequest(authToken, targetUser.getAlias(), pageSize, lastItem.getAlias());
        }
        /*
        We call the service because it is the one that can call the server.
        We need to pass an observer, since this class is implementing the observer we can pass this class as an observer.
        We can call service method this way or we can have a FollowService variable.
        */
        new FollowService().getFollowers(followersRequest, new GetFollowersObserver());
    }

    /*
    GetItemsObserver already contains handleSuccess and handleFailure methods we extend them.
    GetFollowersObserver implements GetFollowersObserver that is in FollowService.
    */
    private class GetFollowersObserver extends GetItemsObserver implements FollowService.GetFollowersObserver {
    }
}
