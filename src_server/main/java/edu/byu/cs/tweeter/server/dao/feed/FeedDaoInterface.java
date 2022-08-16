package edu.byu.cs.tweeter.server.dao.feed;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.SqsPostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.util.Pair;

public interface FeedDaoInterface {
    public Pair<Boolean, List<Status>> getFeed(FeedRequest request);

    public void postStatus(String ownerAlias, PostStatusRequest request);

    public void postStatusBatch(SqsPostStatusRequest request);
}
