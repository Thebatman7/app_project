package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class GetFollowingHandler extends GeneralHandler implements RequestHandler<FollowingRequest, FollowingResponse> {

    @Override
    public FollowingResponse handleRequest(FollowingRequest request, Context context) {
        FollowService service = new FollowService(getFactory());
        return service.getFollowees(request);
    }
    //private DaoFactory getFactory() { return new DynamoDbDAO(); }
}
