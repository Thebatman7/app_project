package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends CountTask {
    public GetFollowersCountTask(FollowersCountRequest request, Handler messageHandler) {
        super(messageHandler, request);
    }

    @Override
    protected CountResponse getResponse(CountRequest request) throws Exception {
        return getServerFacade().getFollowersCount((FollowersCountRequest) request, "/getfollowerscount");
    }

}
