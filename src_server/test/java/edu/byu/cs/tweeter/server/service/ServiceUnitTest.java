package edu.byu.cs.tweeter.server.service;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DaoFactory;
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
import edu.byu.cs.tweeter.server.lambda.GeneralHandler;
import edu.byu.cs.tweeter.server.util.Pair;

/*
The server-side Service that returns user stories should have an operation that
returns the next page of a user's story. This operation should use a DAO to retrieve
the data from the database. Using JUnit and Mockito, write automated UNIT tests to verify
that the Service correctly returns story pages. Use mocking to isolate the Service class from
its dependencies (specifically its dependencies on DAO classes). In this test we mock the dependencies and we spy the class we want
*/
public class ServiceUnitTest {
    private StoryRequest storyRequest;
    private StoryDAO storyDAO;
    private AuthTokenDAO authTokenDAO;
    private UserDAO userDAO;
    private User theUser;
    private List<Status> statuses;
    private GetStoryTest getStoryTest;

    private StatusService service;


    @BeforeEach
    public void setup() {
        authTokenDAO = Mockito.mock(AuthTokenDAO.class);
        AuthToken authToken = new AuthToken("3tiB1sDMitvJSiXa0F5ZqXQLhPeL_EAP",
                163897, "@batman");
        when(authTokenDAO.read(Mockito.any())).thenReturn(authToken);
        //Mockito.doReturn(authToken).when(authTokenDAO.read(Mockito.any()));


        storyDAO = Mockito.mock(StoryDAO.class);
        User emptyUser = new User();
        List<String> emptyUrls = new ArrayList<>();
        List<String> emptyMentions = new ArrayList<>();
        statuses = new ArrayList<>();
        Status statusA = new Status("This is batman posting! ", emptyUser,
                "Thu Dec 02 10:55:23 MST 2021", emptyUrls, emptyMentions);
        Status statusB = new Status("Passoff post ", emptyUser,
                "Wed Dec 01 17:10:59 MST 2021", emptyUrls, emptyMentions);
        Status statusC = new Status("post status", emptyUser,
                "Wed Dec 01 17:09:36 MST 2021", emptyUrls, emptyMentions);
        Status statusD = new Status("positng\n", emptyUser,
                "Wed Dec 01 17:08:04 MST 2021", emptyUrls, emptyMentions);
        Status statusE = new Status("Final Test posting time 4:44", emptyUser,
                "Wed Dec 01 16:44:20 MST 2021", emptyUrls, emptyMentions);
        Status statusF = new Status("Posting 4:35", emptyUser,
                "Wed Dec 01 16:35:06 MST 2021", emptyUrls, emptyMentions);
        Status statusG = new Status("Post status 4:29", emptyUser,
                "Wed Dec 01 16:29:15 MST 2021", emptyUrls, emptyMentions);
        statuses.add(statusA);
        statuses.add(statusB);
        statuses.add(statusC);
        statuses.add(statusD);
        statuses.add(statusE);
        statuses.add(statusF);
        statuses.add(statusG);
        when(storyDAO.getStories(Mockito.any())).thenReturn(new Pair<>(true, statuses));

        userDAO = Mockito.mock(UserDAO.class);
        theUser = new User("Rembrand", "Pardo", "@batman",
                "https://cs340project.s3.us-east-2.amazonaws.com/theBat.png");
        when(userDAO.getUser(Mockito.any())).thenReturn(theUser);

        storyRequest = new StoryRequest(authToken, "@batman", 7, null);
    }

    private class TestDAO implements DaoFactory {
        @Override
        public AuthTokenDaoInterface createAuthTokenDAO() { return authTokenDAO; }
        @Override
        public StoryDaoInterface createStoryDAO() { return storyDAO; }
        @Override
        public UserDaoInterface createUserDAO() { return userDAO; }

        //Not needed for this test
        @Override
        public FeedDaoInterface createFeedDAO() {
            return new FeedDAO();
        }
        @Override
        public FollowDaoInterface createFollowDAO() {
            return new FollowDAO();
        }
    }


    private class GetStoryTest extends GeneralHandler implements RequestHandler<StoryRequest, StoryResponse> {
        @Override
        protected DaoFactory getFactory() { return new TestDAO(); }

        @Override
        public StoryResponse handleRequest(StoryRequest request, Context context) {
            service = spy(new StatusService(getFactory()));
            //StatusService service = new StatusService(getFactory());
            StoryResponse response = service.getStory(request);
            return response;
        }
    }

    private GetStoryTest setupTest(){
        getStoryTest = new GetStoryTest();
        return getStoryTest;
    }

    @Test
    public void testGetStory() {
        StoryResponse response = setupTest().handleRequest(storyRequest, null);
        verify(service, atLeast(1)).getTheUser(Mockito.any(), Mockito.any(), Mockito.any());
        verify(service, atLeast(1)).getStory(Mockito.any());
        verify(service, atLeast(0)).getFeed(Mockito.any());
        verify(service, atLeast(0)).assignUsers(Mockito.any(), Mockito.any());
        Assertions.assertEquals(7, response.getStatuses().size());
    }


    /*
    GetStoryHandler:
    @Override
    public StoryResponse handleRequest(StoryRequest request, Context context) {
        StatusService service = new StatusService(getFactory());
        StoryResponse response = service.getStory(request);
        return  response;
    }
    */
    /*
    Status Service:
    public StoryResponse getStory(StoryRequest request) {
        if(request.getAuthToken() == null || request.getItemAlias() == null ||
                request.getLimit() < 0) {
            throw new RuntimeException("[Bad Request] Request missing authToken or user alias or " +
                    "limit is less than zero");
        }
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        if(authToken != null) {
            Pair<Boolean, List<Status>> statusInf = storyDAO.getStories(request);
            List<Status> statuses = new ArrayList<>();
            if(statusInf == null) {return new StoryResponse("[Bad Request] Failed to get statuses."); }
            statuses = statusInf.getSecond();
            if(statuses.size() != 0) {
                getTheUser(statuses, request.getItemAlias(), authToken);
            }
            if(statusInf.getFirst()) { return new StoryResponse(statuses, true); }
            return new StoryResponse(statuses, false);
        }
        else {
            throw new RuntimeException("[Bad Request] User not authorized to perform task. Please login again.");
        }
    }
    //Method to get the user (owner of story) information
    public void getTheUser(List<Status> statuses, String alias, AuthToken authToken){
        UserService userService = new UserService(daoFactory);
        GetUserRequest getUserRequest = new GetUserRequest(authToken, alias);
        GetUserResponse getUserResponse = userService.getUser(getUserRequest);
        User user = getUserResponse.getUser();
        for(int i = 0; i < statuses.size(); ++i) {
            statuses.get(i).setUser(user);
        }
    }
    */
}
