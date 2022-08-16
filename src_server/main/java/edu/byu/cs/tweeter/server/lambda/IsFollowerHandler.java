package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.service.UserService;

public class IsFollowerHandler extends GeneralHandler implements RequestHandler<IsFollowerRequest, IsFollowerResponse> {

    @Override
    public IsFollowerResponse handleRequest(IsFollowerRequest request, Context context) {
        UserService service = new UserService(getFactory());
        return service.isFollower(request);
    }
    //private DaoFactory getFactory() { return new DynamoDbDAO(); }
}
