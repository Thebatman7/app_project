package edu.byu.cs.tweeter.server.dao.fakedaos;

import edu.byu.cs.tweeter.server.dao.DaoFactory;
import edu.byu.cs.tweeter.server.dao.authtoken.AuthTokenDaoInterface;
import edu.byu.cs.tweeter.server.dao.fakedaos.FakeDataAuthToken;
import edu.byu.cs.tweeter.server.dao.fakedaos.FakeDataFeed;
import edu.byu.cs.tweeter.server.dao.fakedaos.FakeDataFollow;
import edu.byu.cs.tweeter.server.dao.fakedaos.FakeDataStory;
import edu.byu.cs.tweeter.server.dao.fakedaos.FakeDataUser;
import edu.byu.cs.tweeter.server.dao.feed.FeedDaoInterface;
import edu.byu.cs.tweeter.server.dao.follow.FollowDaoInterface;
import edu.byu.cs.tweeter.server.dao.story.StoryDaoInterface;
import edu.byu.cs.tweeter.server.dao.user.UserDaoInterface;

public class FakeDataFactory implements DaoFactory {
    @Override
    public AuthTokenDaoInterface createAuthTokenDAO() {
        return new FakeDataAuthToken();
    }

    @Override
    public FeedDaoInterface createFeedDAO() {
        return new FakeDataFeed();
    }

    @Override
    public FollowDaoInterface createFollowDAO() {
        return new FakeDataFollow();
    }

    @Override
    public StoryDaoInterface createStoryDAO() {
        return new FakeDataStory();
    }

    @Override
    public UserDaoInterface createUserDAO() {
        return new FakeDataUser();
    }
}
