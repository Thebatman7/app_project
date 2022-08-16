package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.service.UserService;

public class PostStatusHandler extends GeneralHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {

    private UserService service;
    public PostStatusHandler() {
        System.out.println("Code is here: Before initializing user service.");
        service = new UserService(getFactory());
        System.out.println("Code is here: After initializing user service.");
    }

    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        //UserService service = new UserService(getFactory());
        return service.postStatus(request);
    }
}
