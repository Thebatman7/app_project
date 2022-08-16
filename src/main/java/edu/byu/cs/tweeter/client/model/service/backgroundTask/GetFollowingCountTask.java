package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends CountTask {
    //private static final String LOG_TAG = "LogoutTask";


    public GetFollowingCountTask(FollowingCountRequest request, Handler messageHandler) {
        super(messageHandler, request);
    }

    @Override
    protected CountResponse getResponse(CountRequest request) throws Exception {
        return getServerFacade().getFollowingCount((FollowingCountRequest) request, "/getfollowingcount");
    }


    /*private void sendSuccessMessage(int count) {
        sendSuccessMessage(new BundleLoader() {
            @Override
            public void load(Bundle msgBundle) {
                msgBundle.putInt(COUNT_KEY, count);
            }
        });
    }*/
}
