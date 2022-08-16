package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;

public class FollowingPresenter extends PagedPresenter<User> {


    public FollowingPresenter(View view, AuthToken authToken, User targetUser) {
        super((PagedView) view, authToken, targetUser);
    }


    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        FollowingRequest followingRequest;
        if (lastItem == null) {
           followingRequest = new FollowingRequest(authToken, targetUser.getAlias(), pageSize, null);
        }
        else{
            followingRequest = new FollowingRequest(authToken, targetUser.getAlias(), pageSize, lastItem.getAlias());
        }
        new FollowService().getFollowing(followingRequest, new GetFollowingObserver());
    }

    /*
    GetItemsObserver already contains handleSuccess and handleFailure methods we extend them.
    GetFollowersObserver implements GetFollowersObserver that is in FollowService.
    */
    private class GetFollowingObserver extends GetItemsObserver implements FollowService.GetFollowingObserver { }
}
