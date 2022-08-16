package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.client.presenter.StatusPagedPresenter.StatusPagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;

/**
 In the view for the feed tab all a user can do is scroll through the user stories or
 navigate to that person or click on a url link or mention.
 Stories are sorted from newest to oldest. There is one presenter per view.
*/
public class StoryPresenter extends StatusPagedPresenter {

    //Constructor
    public StoryPresenter(View view, AuthToken authToken, User targetUser) {
        super((StatusPagedView)view, authToken, targetUser);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        StoryRequest storyRequest = new StoryRequest(authToken, targetUser.getAlias(), pageSize, lastItem);
        new StoryService().getStory(storyRequest, new GetStoryObserver());
    }

    public class GetStoryObserver extends GetItemsObserver implements StoryService.GetStoryObserver { }
}
