package edu.byu.cs.tweeter.server.lambda.sqs;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.lambda.GeneralHandler;
import edu.byu.cs.tweeter.server.service.FollowService;

public class Q2QHandler extends GeneralHandler implements RequestHandler<SQSEvent, Void> {

    private FollowService service;
    public Q2QHandler() {
        System.out.println("Q2Q: Code got here, we have deserialized object and about to create follow service.");
        service = new FollowService(getFactory());
        System.out.println("Code got here: We have initialized service and about to call call method to get follower aliases.");
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        int num_calls = 0;
        Gson gson = new Gson();
        for(SQSEvent.SQSMessage msg : event.getRecords()) {
            String jsonStringMessage = msg.getBody();
            /*
            Deserialization in the context of Gson means converting a JSON string to equivalent Java object.
            In order to do the deserialization, we need a Gson object and call the function fromJson()
            and pass two parameters i.e. JSON string and expected java type after parsing is finished.
            See Note below.
            */
            PostStatusRequest postStatusRequest = gson.fromJson(jsonStringMessage, PostStatusRequest.class);
            //FollowService service = new FollowService(getFactory());
            ++num_calls;
            System.out.println("Q2Q handler: " + num_calls + " Code is about to call getFollowerAliases " );
            service.getFollowerAliases(postStatusRequest);
        }
        return null;
    }
}
/*
Note: Do we need to implement Serializable for model classes while using
GSON(serialization/deserialization library)?
Depends on what we want to do with them, but most likely we don't need to.
Serializable is one way to serialize data within Java that's sort of the default Java way.
JSON serialization is another. Parcelable is a third that's Android specific.
The only time we need to use Serializable is if we want to pass it to an API that
takes a Serializable as a parameter. If we don't need to do that then using GSON to serialize
and not implementing Serializable is just fine.
The difference between those 3 methods is the format of the data they output to.
The different formats have different pros and cons, but they all get the job done.
*/
