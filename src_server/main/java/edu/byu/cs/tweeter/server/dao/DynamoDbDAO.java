package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.authtoken.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.authtoken.AuthTokenDaoInterface;
import edu.byu.cs.tweeter.server.dao.feed.FeedDAO;
import edu.byu.cs.tweeter.server.dao.feed.FeedDaoInterface;
import edu.byu.cs.tweeter.server.dao.follow.FollowDAO;
import edu.byu.cs.tweeter.server.dao.follow.FollowDaoInterface;
import edu.byu.cs.tweeter.server.dao.story.StoryDAO;
import edu.byu.cs.tweeter.server.dao.story.StoryDaoInterface;
import edu.byu.cs.tweeter.server.dao.user.UserDAO;
import edu.byu.cs.tweeter.server.dao.user.UserDaoInterface;

public class DynamoDbDAO implements DaoFactory {
    @Override
    public AuthTokenDaoInterface createAuthTokenDAO() {
        return new AuthTokenDAO();
    }

    @Override
    public FeedDaoInterface createFeedDAO() {
        return new FeedDAO();
    }

    @Override
    public FollowDaoInterface createFollowDAO() {
        return new FollowDAO();
    }

    @Override
    public StoryDaoInterface createStoryDAO() { return new StoryDAO(); }

    @Override
    public UserDaoInterface createUserDAO() {
        return new UserDAO();
    }
}
