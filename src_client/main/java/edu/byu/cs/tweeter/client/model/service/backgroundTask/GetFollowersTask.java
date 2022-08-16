package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;


/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedTask<User> {
    //private static final String LOG_TAG = "GetFollowersTask";

    public GetFollowersTask(FollowersRequest request, Handler messageHandler) {
        super(messageHandler, request);
    }

    @Override
    public PagedResponse getResponse(PagedRequest request) throws Exception {
        return getServerFacade().getFollowers((FollowersRequest) request, "/followers");
    }

    @Override
    protected List<User> convertItemsToUsers(List<User> items) {
        return items;
    }

}
