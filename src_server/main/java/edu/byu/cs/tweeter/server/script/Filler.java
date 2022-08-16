package edu.byu.cs.tweeter.server.script;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DaoFactory;
import edu.byu.cs.tweeter.server.dao.follow.FollowDaoInterface;
import edu.byu.cs.tweeter.server.dao.user.UserDaoInterface;

public class Filler {
    //Number of follower users to add
    private final static int NUM_USERS = 2000;
    // The alias of the user to be followed by each user created
    private final static String FOLLOW_TARGET = "@batman";
    private final static String IMAGE_URL = "https://cs340project.s3.us-east-2.amazonaws.com/user+simple.png";
    private final static String F_IMAGE_URL = "https://cs340project.s3.us-east-2.amazonaws.com/blonde+user.png";

    private DaoFactory daoFactory;
    private static UserDaoInterface userDAO;
    private static FollowDaoInterface followDAO;

    public Filler(DaoFactory factory) {
        daoFactory = factory;
        userDAO = daoFactory.createUserDAO();
        followDAO = daoFactory.createFollowDAO();
    }

    public void fillDatabase() {
        List<String> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {
        //for (char c = 'A'; c <= 'Z'; ++c) {//This is just for test
            String name = "User " + i;
            String alias = "@user" + i;

            //Attributes added in the order of user table
            User user = new User();
            user.setAlias(alias);
            user.setFirstName(name);
            user.setImageUrl(IMAGE_URL);
            user.setLastName(name);
            users.add(user);

            //To represent a follows relationship, only the aliases of the two users are needed
            followers.add(alias);
        }

        //Call the DAOs for the database logic
        if(users.size() > 0) {
            userDAO.addUsersBatch(users);
        }
        if(followers.size() > 0) {
            followDAO.addFollowersBatch(followers, FOLLOW_TARGET);
        }
    }
}
