package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DaoFactory;
import edu.byu.cs.tweeter.server.dao.authtoken.AuthTokenDaoInterface;


public class UserService {
    public UserService(DaoFactory factory) {
        System.out.println("Line of code before factory is assigned");
        daoFactory = factory;
        authTokenDAO = daoFactory.createAuthTokenDAO();
        /*System.out.println("Line of code before userDAO is assigned");
        userDAO = daoFactory.createUserDAO();
        System.out.println("Line of code before followDAO is assigned");
        followDAO = daoFactory.createFollowDAO();
        System.out.println("Line of code before storyDAO is assigned");
        storyDAO = daoFactory.createStoryDAO();
        System.out.println("Line of code before feedDAO is assigned");
        feedDAO = daoFactory.createFeedDAO();
        */
    }
    private DaoFactory daoFactory;
    private AuthTokenDaoInterface authTokenDAO;
    /*
    private FollowDaoInterface followDAO;
    private StoryDaoInterface storyDAO;
    private FeedDaoInterface feedDAO;*/


    /**
     * We check user table and verify that the user trying to login exist in our database. We create
     * a new authToken and save it in our database.
     * @param request contains alias and password.
     * @return LoginResponse object which contains the User object and AuthToken object.
     */
    public LoginResponse login(LoginRequest request) {
        if(request.getPassword() == null || request.getUsername() == null) {
            throw new RuntimeException("[Bad Request] Request missing username or password "
                    + request.getUsername().toString() + " " + request.getPassword().toString());
        }
        User signedInUser = daoFactory.createUserDAO().login(request);
        //User signedInUser = userDAO.login(request);
        if (signedInUser != null) {
            //AuthToken authToken = daoFactory.createAuthTokenDAO().create(signedInUser.getAlias());
            AuthToken authToken = authTokenDAO.create(signedInUser.getAlias());
            System.out.println("Login: This is the authtoken " + authToken);
            if(authToken != null) {
                return new LoginResponse(signedInUser, authToken);
            }
            else {
                String message = "Unable to authorize user.";
                return new LoginResponse(message);
            }
        }
        else {
            String message = "User alias or password doesn't match.";
            return new LoginResponse(message);
        }
    }

    /**
     * We enter the user trying to register in our user table. We create a salt and we secure password.
     * We change string to array of bytes and save to S3 the provided image.
     * Once user is in database and image uploaded to AWS we crate an authtoken for registered user.
     * @param request object contains information for getting a register response.
     * @return RegisterResponse object which contains User object and AuthToken object.
     */
    public RegisterResponse register(RegisterRequest request) {
        if(request.getFirstName() == null || request.getLastName() == null ||
                request.getAlias() == null || request.getPassword() == null ||
                request.getImageBytes() == null) {
            throw new RuntimeException("[Bad Request] Request missing first name or last name or alias or password or imageBytes.");
        }
        User registeredUser = daoFactory.createUserDAO().register(request);
        //User registeredUser = userDAO.register(request);
        if(registeredUser != null) {
            //AuthToken authToken = daoFactory.createAuthTokenDAO().create(registeredUser.getAlias());
            AuthToken authToken = authTokenDAO.create(registeredUser.getAlias());
            System.out.println("Register: This is the authtoken " + authToken);
            if(authToken != null) {
                return new RegisterResponse(registeredUser, authToken);
            }
            else {
                String message = "Unable to authorize user.";
                return new RegisterResponse(message);
            }
        }
        else {
            String message = "User is already registered. Please sign in.";
            return new RegisterResponse(message);
        }
    }

    /**
     * A signed in user, which was given an AuthToken, will be able to log out.
     * @param request object which contains the AuthToken that will be deleted from database.
     * @return LogoutResponse object.
     */
    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken.");
        }
        if(daoFactory.createUserDAO().logout(request)) {
        //if (userDAO.logout(request)){
            return new LogoutResponse();
        }
        else {
            String message = "User not authorized to logout.";
            return new LogoutResponse(message);
        }
    }

    /**
     * We verify that authtoken of signed in user is not expired. We get the signed in user alias
     * from authtoken table using the authtoken. We get the alias from the request object.
     * We add a new entry in the follow table.
     * @param request object contains AuthToken object and alias of user we wish to follow.
     * @return FollowResponse object.
     */
    public FollowResponse follow(FollowRequest request){
        if(request.getAuthToken() == null || request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken or selected user alias");
        }
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        IsFollowerRequest isFollowerRequest = new IsFollowerRequest(authToken, authToken.getUserOwner(), request.getSelectedUserAlias());
        IsFollowerResponse response = isFollower(isFollowerRequest);
        if(response.isFollower()) {
            throw new RuntimeException("[Bad Request] User " + authToken.getUserOwner()
                    + " is already following " + request.getSelectedUserAlias());
        }
        System.out.println("Follow: This is the authtoken " + authToken);
        if(authToken != null) {
            if(daoFactory.createFollowDAO().follow(authToken.getUserOwner(), request.getSelectedUserAlias())){
            //if (followDAO.follow(authToken.getUserOwner(), request.getSelectedUserAlias())) {
                return new FollowResponse();
            }
            else {
                String message = "Unable to perform follow task. " + authToken.getUserOwner() +
                        " could not follow " + request.getSelectedUserAlias() + ".";
                return new FollowResponse(message);
            }
        }
        else {
            String message = "User not authorized to perform follow task. Please login again.";
            return new FollowResponse(message);
        }
    }

    /**
     * We verify the authtoken is not expired. We use follower alias(signed in user) and followee
     * alias to delete an entry in follow table.
     * @param request object contains AuthToken object and alias of user we wish to follow.
     * @return UnfollowResponse object.
     */
    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getAuthToken() == null || request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken or selected user alias");
        }
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        System.out.println("Unfollow: This is the authtoken " + authToken);
        if(authToken != null) {
            if(daoFactory.createFollowDAO().unfollow(authToken.getUserOwner(), request.getSelectedUserAlias())) {
            //if (followDAO.unfollow(authToken.getUserOwner(), request.getSelectedUserAlias())) {
                return new UnfollowResponse();
            }
            else {
                String message = "Unable to perform unfollow task. " + authToken.getUserOwner() +
                        " could not unfollow " + request.getSelectedUserAlias() + ".";
                return new UnfollowResponse(message);
            }
        }
        else {
            String message = "User not authorized to perform unfollow task. Please login again.";
            return new UnfollowResponse(message);
        }
    }

    /**
     * We verify that authtoken is not expired and we retrieve data from follow table to check if
     * current user is a follower of the selected user.
     * @param request contains AuthToken object, signed in user's alias, and selected user's alias.
     * @return IsFollowerResponse object.
     */
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getAuthToken() == null
                || request.getCurrentUserAlias() == null
                || request.getSelectedUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken or " +
                    "signed in user alias or selected user alias");
        }
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        System.out.println("isFollower: This is the authtoken " + authToken);
        if(authToken != null) {
            if(daoFactory.createFollowDAO().isFollower(authToken.getUserOwner(), request.getSelectedUserAlias())){
            //if(followDAO.isFollower(authToken.getUserOwner(), request.getSelectedUserAlias()))
                System.out.println("Code worked up until here. Isfollower is true.");
                return new IsFollowerResponse(true);
            }
            else {
                System.out.println("Code worked up until here. Isfollower is false.");
                return new IsFollowerResponse(false);
            }
        }
        else {
            throw new RuntimeException("[Bad Request] Not authorized to perform is follower task.");
        }
    }

    /**
     * We verify that authtoken is not expired. We retrieve the user from database.
     * @param request contains AuthToken object and alias.
     * @return GetUserResponse object.
     */
    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getAuthToken() == null || request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken or user alias");
        }
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        System.out.println("Get user: This is the authtoken " + authToken);
        if (authToken != null) {
            //IsFollowerRequest isFollowerRequest = new IsFollowerRequest(authToken, authToken.getUserOwner(), request.getAlias());
            //IsFollowerResponse response = isFollower(isFollowerRequest);
            //response.
            User user = daoFactory.createUserDAO().getUser(request);
            //User user = userDAO.getUser(request);
            if (user != null) {
                return new GetUserResponse(user);
            }
            else {
                String message = "Getting user failed.";
                return new GetUserResponse(message);
            }
        }
        else {
            String message = "User not authorized to perform task. Please login again.";
            return new GetUserResponse(message);
        }
    }

    /*public PostStatusResponse postStatus2(PostStatusRequest request) {
        if(request.getAuthToken() == null || request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken or status");
        }
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        if (authToken != null) {
            storyDAO.postStatus(request);

            FollowService followService = new FollowService(daoFactory);
            String lastFollower = null;
            boolean hasMore = true;
            do {
                FollowersRequest followersRequest = new FollowersRequest(authToken, authToken.getUserOwner(), 10, lastFollower);
                FollowersResponse response = followService.getFollowers(followersRequest);
                if(response.getFollowers().size() != 0) {
                    for(int i = 0; i < response.getFollowers().size(); ++i) {
                        lastFollower = response.getFollowers().get(i).getAlias();
                        feedDAO.postStatus(lastFollower, request);
                    }
                    hasMore = response.getHasMorePages();
                }
                else {
                    hasMore = false;
                }
            } while(hasMore);

            return new PostStatusResponse();
        }
        else {
            throw new RuntimeException("[Bad Request] User not authorized to perform task. Please login again.");
        }
    }*/

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if(request.getAuthToken() == null || request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request missing authToken or status");
        }
        //AuthToken authToken = daoFactory.createAuthTokenDAO().read(request.getAuthToken());
        AuthToken authToken = authTokenDAO.read(request.getAuthToken());
        System.out.println("Post status: This is the authtoken " + authToken);
        if (authToken != null) {
            System.out.println("Code got here: before posting status in Story table.");
            daoFactory.createStoryDAO().postStatus(request);
            //storyDAO.postStatus(request);

            System.out.println("Code got here: before calling method to send message to Q2Q.");
            if(sendMessageToQ2Q(request)) return new PostStatusResponse();
            else throw new RuntimeException("[Bad Request] Failed to post status.");
        }
        else {
            throw new RuntimeException("[Bad Request] User not authorized to perform task. Please login again.");
        }
    }

    public boolean sendMessageToQ2Q(PostStatusRequest request){
        String queueUrl = "https://sqs.us-east-2.amazonaws.com/182452362445/quickQueue";
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        /*
        Using gson 'com.google.code.gson:gson:2.8.5' dependency to serialize/deserialize.
        Serialization in the context of Gson means converting a Java object to its JSON representation.
        In order to do the serialization, we need a Gson object, which handles the conversion.
        Next, we need to call the function toJson() and pass the object we wish to serialize.
        */
        Gson gson = new Gson();
        String jsonStringMessage = gson.toJson(request);

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(jsonStringMessage);

        System.out.println("Code got here: before sending the message with serialized request object ");
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

        if (send_msg_result.getMessageId() != null) { return true; }
        return false;
    }
}
