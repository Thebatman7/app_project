package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.SqsPostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.server.dao.DaoFactory;
import edu.byu.cs.tweeter.server.dao.authtoken.AuthTokenDaoInterface;
import edu.byu.cs.tweeter.server.dao.feed.FeedDaoInterface;
import edu.byu.cs.tweeter.server.dao.follow.FollowDaoInterface;
import edu.byu.cs.tweeter.server.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    public FollowService(DaoFactory factory) {
        daoFactory = factory;
        authTokenDAO = daoFactory.createAuthTokenDAO();
        /*followDAO = daoFactory.createFollowDAO();
        feedDAO = daoFactory.createFeedDAO();*/
    }
    private DaoFactory daoFactory;
    private AuthTokenDaoInterface authTokenDAO;
    /*private FollowDaoInterface followDAO;
    private FeedDaoInterface feedDAO;*/


    /**
     * We verify alias provided matches alias in AuthToken object. We verify authtoken is not expired.
     * We retrieve the number of followees. If number of following is less than zero we return response with failure message.
     * @param request object contains AuthToken object and alias.
     * @return FollowingResponse object.
     */
    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        if(request.getAuthToken() == null || request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken or user alias");
        }
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        System.out.println("getFollowingCount: This is the authtoken " + authToken);
        if(authToken != null) {
            int followingCount = daoFactory.createFollowDAO().getFolloweeCount(request);
            //int followingCount = followDAO.getFolloweeCount(request);
            if(followingCount >= 0) { return new FollowingCountResponse(followingCount); }
            else {
                String message = "Failed to get following count.";
                return new FollowingCountResponse(message);
            }
        }
        else {
            throw new RuntimeException("[Bad Request] User not authorized to perform getFollowingCount. Please login again.");
        }
    }

    /**
     * We verify alias provided matches alias in AuthToken object. We verify authtoken is not expired.
     * We retrieve the number of followers. If number of followers is less than zero we return response with failure message.
     * @param request object contains AuthToken object and alias.
     * @return FollowersResponse object.
     */
    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        if(request.getAuthToken() == null || request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken or user alias");
        }
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        System.out.println("getFollowerCount: This is the authtoken " + authToken);
        if(authToken != null) {
            int followerCount = daoFactory.createFollowDAO().getFollowersCount(request);
            //int followerCount = followDAO.getFollowersCount(request);
            if(followerCount >= 0) { return new FollowersCountResponse(followerCount); }
            else {
                String message = "Failed to get follower count.";
                return new FollowersCountResponse(message);
            }
        }
        else {
            throw new RuntimeException("[Bad Request] User not authorized to perform getFollowersCount. Please login again.");
        }
    }

    /**
     * We verify that authtoken is not expired. We get a list of followee aliases from followDAO and
     * we check if there are more followees.
     * We get the users by calling getUser method in UserService.
     * @param request contains AuthToken, follower alias, limit of number of items, and last followee.
     * @return FollowingResponse object.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getAuthToken() == null || request.getItemAlias() == null || request.getLimit() < 0) {
            throw new RuntimeException("[Bad Request] Request missing authToken or user alias or " +
                    "limit is less than zero");
        }
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        System.out.println("getFollowees: This is the authtoken " + authToken);
        if(authToken != null) {
            Pair<Boolean, List<String>> followeesInf = daoFactory.createFollowDAO().getFollowees(request);
            //Pair<Boolean, List<String>> followeesInf = followDAO.getFollowees(request);
            List<String> followeeAliases = new ArrayList<>();
            List<User> followees = new ArrayList<>();
            if(followeesInf == null) { return new FollowingResponse("[Bad Request] Failed to get followees."); }
            followeeAliases = followeesInf.getSecond();
            if(followeeAliases.size() != 0) {
                followees = getUsers(followeeAliases, authToken);
            }
            if(followeesInf.getFirst()) { return new FollowingResponse(followees, true); }
            return new FollowingResponse(followees, false);
        }
        else {
            throw new RuntimeException("[Bad Request] User not authorized to perform getFollowees. Please login again.");
        }
    }

    /**
     * We verify that authtoken is not expired. We get a list of follower aliases from followDAO and
     * we check if there are more followers.
     * @param request contains AuthToken, followee alias, limit of number of items, and last follower.
     * @return FollowersResponse object.
     */
    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getAuthToken() == null || request.getItemAlias() == null || request.getLimit() < 0) {
            throw new RuntimeException("[Bad Request] Request missing authToken or user alias or " +
                    "limit is less than zero");
        }
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        System.out.println("get followers: This is the authtoken " + authToken);
        if(authToken != null) {
            Pair<Boolean, List<String>> followersInf = daoFactory.createFollowDAO().getFollowers(request);
            //Pair<Boolean, List<String>> followersInf = followDAO.getFollowers(request);
            List<String> followerAliases = new ArrayList<>();
            List<User> followers = new ArrayList<>();
            if(followersInf == null) { return new FollowersResponse("[Bad Request] Failed to get followers."); }
            followerAliases = followersInf.getSecond();
            if(followerAliases.size() != 0) {
                followers = getUsers(followerAliases, authToken);
            }
            if(followersInf.getFirst()) { return new FollowersResponse(followers, true); }
            return new FollowersResponse(followers, false);
        }
        else {
            throw new RuntimeException("[Bad Request] User not authorized to perform getFollowers. Please login again.");
        }
    }
    //Method to retrieve a list of users by calling a method in UserService
    public List<User> getUsers(List<String> userAliases, AuthToken authToken){
        UserService userService = new UserService(daoFactory);
        List<User> followees = new ArrayList<>();
        for(int i = 0; i < userAliases.size(); ++i) {
            GetUserRequest getUserRequest = new GetUserRequest(authToken, userAliases.get(i));
            GetUserResponse getUserResponse = userService.getUser(getUserRequest);
            followees.add(getUserResponse.getUser());
        }
        return followees;
    }

    /**
     * Method to get the followers aliases and call a SQS queue
     */
    public void getFollowerAliases(PostStatusRequest request) {
        if(request.getAuthToken() == null || request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken or status");
        }
        System.out.println("Code got here: Before verifying authtoken is not expired " + "authtoken is: " + request.getAuthToken().getAuthToken());
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        System.out.println("get followers aliases: This is the authtoken " + authToken);

        if (authToken != null) {
            String lastFollower = null;
            boolean hasMore = true;
            FollowDaoInterface followDAO = daoFactory.createFollowDAO();
            do {
                FollowersRequest followersRequest = new FollowersRequest(authToken, authToken.getUserOwner(), 1000, lastFollower);
                //Pair<Boolean, List<String>> followersInf = daoFactory.createFollowDAO().getFollowers(followersRequest);
                Pair<Boolean, List<String>> followersInf = followDAO.getFollowers(followersRequest);
                if (followersInf.getSecond().size() != 0) {
                    lastFollower = followersInf.getSecond().get(followersInf.getSecond().size() - 1);//This would get the last follower
                    hasMore = followersInf.getFirst();//getFirst() returns the hasMore boolean
                } else { hasMore = false; }

                System.out.println("Code got here: size of list is: " + followersInf.getSecond().size()
                        + " last follower is: " + lastFollower + " hasmore is: " + hasMore);

                //We create a new object that we will pass to the send message containing everything we need
                SqsPostStatusRequest sqsRequest = new SqsPostStatusRequest(request.getAuthToken(), request.getStatus(), followersInf.getSecond());
                //System.out.println("Code got here: We about to pass object to send message method.");
                if(!sendMessageToQ2Feed(sqsRequest)) {
                    throw new RuntimeException("[Bad Request] Failed to get follower aliases for posting a status.");
                }
            } while (hasMore);
        }
        else {
            throw new RuntimeException("[Bad Request] User not authorized to perform task. Please login again.");
        }
    }
    public boolean sendMessageToQ2Feed(SqsPostStatusRequest request){
        String queueUrl = "https://sqs.us-east-2.amazonaws.com/182452362445/slowQueue";
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        /*
        Using gson 'com.google.code.gson:gson:2.8.5' dependency to serialize/deserialize.
        Serialization in the context of Gson means converting a Java object to its JSON representation.
        In order to do the serialization, we need a Gson object, which handles the conversion.
        To serialize a list we just pass the list as a parameter.
        */
        System.out.println("Message to Q2Feed: Size of the followers to be serialized is " + request.getFollowers().size());
        Gson gson = new Gson();
        String jsonStringMessage = gson.toJson(request);
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(jsonStringMessage);

        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
        if(send_msg_result.getMessageId() != null) { return true; }
        return false;
    }

    public void postStatusInFeed(SqsPostStatusRequest request) {
        String follower;
        PostStatusRequest postStatusRequest = new PostStatusRequest(request.getAuthToken(), request.getStatus());
        FeedDaoInterface feedDAO = daoFactory.createFeedDAO();
        System.out.println("Code is here: We have created request object and about to post in feed table. " +
                "The size of the list of followers is " + request.getFollowers().size());
        for (int i = 0; i < request.getFollowers().size(); ++i) {//TODO Looping over 1000 people about 10 times would take a lot of time
            follower = request.getFollowers().get(i);
            //daoFactory.createFeedDAO().postStatus(follower, postStatusRequest);
            feedDAO.postStatus(follower, postStatusRequest);
        }
    }

    public void postFeedBatch(SqsPostStatusRequest request) {
        System.out.println("Code is here: Before calling post batch method in feedDAO " +
                "the size of the list of followers is " + request.getFollowers().size());
        daoFactory.createFeedDAO().postStatusBatch(request);
    }

}
