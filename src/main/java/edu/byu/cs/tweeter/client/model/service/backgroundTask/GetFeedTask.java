package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.server.util.Pair;


/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedTask<Status> {
    //private static final String LOG_TAG = "GetFeedTask";


    public GetFeedTask(FeedRequest request, Handler messageHandler) {
        //We pass this to the abstract class so it sets its handler and sends back messages
        super(messageHandler, request);
    }

    @Override
    public PagedResponse getResponse(PagedRequest request) throws Exception {
        return getServerFacade().getFeed((FeedRequest) request, "/feed");
    }


    @Override
    protected List<User> convertItemsToUsers(List<Status> statuses) {
        /*
        This is functional programming. We are essentially copying all the user values of the
        statuses. This is as if we are iterating through the statuses, getting the user value out of them
        and collecting them into a list that we return.
        */
        return statuses.stream().map(x -> x.user).collect(Collectors.toList());
    }
}
