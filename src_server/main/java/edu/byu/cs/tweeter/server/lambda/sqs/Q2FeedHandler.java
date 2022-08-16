package edu.byu.cs.tweeter.server.lambda.sqs;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.net.request.SqsPostStatusRequest;
import edu.byu.cs.tweeter.server.lambda.GeneralHandler;
import edu.byu.cs.tweeter.server.service.FollowService;

public class Q2FeedHandler extends GeneralHandler implements RequestHandler<SQSEvent, Void> {

    private FollowService service;
    public Q2FeedHandler() {
        service = new FollowService(getFactory());
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        Gson gson = new Gson();
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            String jsonStringMessage = msg.getBody();
            SqsPostStatusRequest request = gson.fromJson(jsonStringMessage, SqsPostStatusRequest.class);
            //FollowService service = new FollowService(getFactory());
            System.out.println("Q2Feed handler: The size of followers inside request is " + request.getFollowers().size());
            service.postFeedBatch(request);
        }
        return null;
    }
}
