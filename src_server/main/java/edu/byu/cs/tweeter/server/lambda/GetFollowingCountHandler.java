package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingCountHandler extends GeneralHandler implements RequestHandler<FollowingCountRequest, FollowingCountResponse> {

    @Override
    public FollowingCountResponse handleRequest(FollowingCountRequest request, Context context) {
        FollowService service = new FollowService(getFactory());
        return service.getFollowingCount(request);
    }
    //private DaoFactory getFactory() { return new DynamoDbDAO(); }
}
