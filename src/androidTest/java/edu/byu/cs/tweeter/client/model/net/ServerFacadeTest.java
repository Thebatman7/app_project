package edu.byu.cs.tweeter.client.model.net;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class ServerFacadeTest {
    private ServerFacade serverFacade;

    private RegisterRequest registerRequest;
    private FollowersRequest followersRequest;
    private FollowersCountRequest countRequest;

    private RegisterResponse registerResponse;
    private FollowersResponse followersResponse;
    private FollowersCountResponse countResponse;

    @Before
    public void setUp() {
        serverFacade = new ServerFacade();

        String imageURL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
        registerRequest = new RegisterRequest("Allen", "Anderson",
                "@allen", "dummypassword", imageURL);

        AuthToken authToken = new AuthToken();
        followersRequest = new FollowersRequest(authToken, "@allen", 10, "@elizabeth");

        countRequest = new FollowersCountRequest(authToken, "@allen");
    }

    @Test
    public void registerTest() throws IOException, TweeterRemoteException {
        registerResponse = serverFacade.register(registerRequest, "/register");
        Assert.assertNotNull(registerResponse);

    }

    @Test
    public void getFollowersTest() throws IOException, TweeterRemoteException {
        followersResponse = serverFacade.getFollowers(followersRequest, "/followers");
        Assert.assertNotNull(followersResponse);
    }

    @Test
    public void getFollowersCountTest() throws IOException, TweeterRemoteException {
        countResponse = serverFacade.getFollowersCount(countRequest, "/getfollowerscount");
        Assert.assertNotNull(countResponse);
    }
}

