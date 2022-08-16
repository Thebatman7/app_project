package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;

/**
 In the view for the feed tab all a user can do is scroll through the followees posts or
 click on one followee to navigate to that person or click on a url link or mention.
 A signed in user can view all statuses of all of his or her followers merged into one list,
 sorted from newest to oldest. We call this list of statuses the user’s feed and should be the default
 view of the application for a signed in user.
 There is one presenter per view.
 */
public class FeedPresenter extends StatusPagedPresenter {

    //Constructor
    public FeedPresenter(View view, AuthToken authToken, User targetUser) {
        super((StatusPagedView) view, authToken, targetUser);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        FeedRequest feedRequest = new FeedRequest(authToken, targetUser.getAlias(), pageSize, lastItem);
        new FeedService().getFeed(feedRequest, new GetFeedObserver());
    }

    /*
    GetItemsObserver already contains handleSuccess and handleFailure methods we extend them.
    GetFollowersObserver implements GetFollowersObserver that is in FollowService.
    */
    private class GetFeedObserver extends GetItemsObserver implements FeedService.GetFeedObserver { }
}
