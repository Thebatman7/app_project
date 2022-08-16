package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.authtoken.AuthTokenDaoInterface;
import edu.byu.cs.tweeter.server.dao.feed.FeedDaoInterface;
import edu.byu.cs.tweeter.server.dao.follow.FollowDaoInterface;
import edu.byu.cs.tweeter.server.dao.story.StoryDaoInterface;
import edu.byu.cs.tweeter.server.dao.user.UserDaoInterface;

public interface DaoFactory {
    public AuthTokenDaoInterface createAuthTokenDAO();
    public FeedDaoInterface createFeedDAO();
    public FollowDaoInterface createFollowDAO();
    public StoryDaoInterface createStoryDAO();
    public UserDaoInterface createUserDAO();
}
