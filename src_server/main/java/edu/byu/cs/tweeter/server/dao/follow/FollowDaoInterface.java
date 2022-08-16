package edu.byu.cs.tweeter.server.dao.follow;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.server.util.Pair;

public interface FollowDaoInterface {
    public boolean isFollower(String follower, String followee);

    public boolean follow(String follower, String followee);

    public boolean unfollow(String follower, String followee);

    public int getFolloweeCount(FollowingCountRequest request);

    public int getFollowersCount(FollowersCountRequest request);

    public Pair<Boolean, List<String>> getFollowees(FollowingRequest request);

    public Pair<Boolean, List<String>> getFollowers(FollowersRequest request);

    public void addFollowersBatch(List<String> followers, String followee);
}
