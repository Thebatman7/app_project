package edu.byu.cs.tweeter.server.dao.story;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.util.Pair;

public interface StoryDaoInterface {

    public void postStatus(PostStatusRequest request);

    public Pair<Boolean, List<Status>> getStories(StoryRequest request);
}
