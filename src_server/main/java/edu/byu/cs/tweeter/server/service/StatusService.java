package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DaoFactory;
import edu.byu.cs.tweeter.server.dao.authtoken.AuthTokenDaoInterface;
import edu.byu.cs.tweeter.server.dao.feed.FeedDaoInterface;
import edu.byu.cs.tweeter.server.dao.story.StoryDaoInterface;
import edu.byu.cs.tweeter.server.util.Pair;

public class StatusService {


    public StatusService(DaoFactory factory) {
        daoFactory = factory;
        authTokenDAO = daoFactory.createAuthTokenDAO();
        storyDAO = daoFactory.createStoryDAO();
        feedDAO = daoFactory.createFeedDAO();
    }
    private DaoFactory daoFactory;
    private AuthTokenDaoInterface authTokenDAO;
    private StoryDaoInterface storyDAO;
    private FeedDaoInterface feedDAO;

    /**
     * We verify that authtoken is not expired. We form the statuses from the information retrieved from database.
     * @param request contains AuthToken object, owner of story alias, limit, and last Status.
     * @return StoryResponse object.
     */
    public StoryResponse getStory(StoryRequest request) {
        if(request.getAuthToken() == null || request.getItemAlias() == null ||
                request.getLimit() < 0) {
            throw new RuntimeException("[Bad Request] Request missing authToken or user alias or " +
                    "limit is less than zero");
        }
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        if(authToken != null) {
            System.out.println("1. Code worked up until here");
            Pair<Boolean, List<Status>> statusInf = storyDAO.getStories(request);
            System.out.println("5. Code worked up until here");

            List<Status> statuses = new ArrayList<>();
            if(statusInf == null) {return new StoryResponse("[Bad Request] Failed to get statuses."); }
            statuses = statusInf.getSecond();
            if(statuses.size() != 0) {
                getTheUser(statuses, request.getItemAlias(), authToken);
            }
            System.out.println("6. Code worked up until here. This is the list size: " + statuses.size());
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

    /**
     * We verify that authtoken is not expired. We form the statuses from the information retrieved from database.
     * @param request contains AuthToken object, owner of story alias, limit, and last Status.
     * @return FeedResponse object.
     */
    public FeedResponse getFeed(FeedRequest request) {
        if(request.getAuthToken() == null || request.getItemAlias() == null ||
                request.getLimit() < 0) {
            throw new RuntimeException("[Bad Request] Request missing authToken or user alias or " +
                    "limit is less than zero");
        }
        System.out.println("1. Code works up until here");
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        if(authToken != null) {
            System.out.println("2. Code works up until here");
            Pair<Boolean, List<Status>> statusInf = feedDAO.getFeed(request);
            List<Status> statuses = new ArrayList<>();
            System.out.println("7. Code works up until here");
            if(statusInf == null) {return new FeedResponse("[Bad Request] Failed to get statuses."); }
            statuses = statusInf.getSecond();
            if(statuses.size() != 0) {
                assignUsers(statuses, authToken);
            }
            System.out.println("8. Code works up until here");
            if(statusInf.getFirst()) { return new FeedResponse(statuses, true); }
            return new FeedResponse(statuses, false);
        }
        else {
            throw new RuntimeException("[Bad Request] User not authorized to perform task. Please login again.");
        }
    }
    //Method to assign the owner of the post to the post
    public void assignUsers(List<Status> statuses,AuthToken authToken){
        UserService userService = new UserService(daoFactory);
        for(int i = 0; i < statuses.size(); ++i){
            GetUserRequest getUserRequest = new GetUserRequest(authToken, statuses.get(i).getUser().getAlias());
            GetUserResponse getUserResponse = userService.getUser(getUserRequest);
            User user = getUserResponse.getUser();
            statuses.get(i).setUser(user);
        }
    }
}
