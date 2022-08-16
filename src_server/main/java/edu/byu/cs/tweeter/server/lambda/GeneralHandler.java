package edu.byu.cs.tweeter.server.lambda;

import edu.byu.cs.tweeter.server.dao.DaoFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDbDAO;

public class GeneralHandler {
    protected DaoFactory getFactory() { return new DynamoDbDAO(); }

    /*protected UserService userService = new UserService(getFactory());
    protected StatusService statusService = new StatusService(getFactory());
    protected FollowService followService = new FollowService(getFactory());*/
}
