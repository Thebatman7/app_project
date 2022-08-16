package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.server.util.Pair;


/**
 * Background task that retrieves a page of other users being followed by a specified user.
 * This calls will extend from the abstract class GetbackgroundTask which implements Runnable to run
 * task in the background.
 */
public class GetFollowingTask extends PagedTask<User> {
    private static final String LOG_TAG = "GetFollowingTask";

    /*
     The user whose following is being retrieved.
     (This can be any user, not just the currently logged-in user.)
     */
    //private User targetUser;


    public GetFollowingTask(FollowingRequest request, Handler messageHandler) {
        //We pass this to the abstract class so it sets its handler and sends back messages
        super(messageHandler, request);
    }

    @Override
    public PagedResponse getResponse(PagedRequest request) throws Exception {
        return getServerFacade().getFollowees((FollowingRequest) request, "/following");
    }
    /*@Override
    protected Pair<List<User>, Boolean> getItems() {
        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), targetUser);
    }*/

    @Override
    protected List<User> convertItemsToUsers(List<User> items) {
        return items;
    }

}
