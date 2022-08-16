package edu.byu.cs.tweeter.client;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import com.google.common.base.Verify;

import org.apache.tools.ant.types.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

/*
Using JUnit and Mockito, write an automated integration test to verify that when a user sends a status,
the status is correctly appended to the user's story. Your test should do the following:
1. Login a user.
2. Post a status from the user to the server by calling the "post status" operation on the relevant Presenter.
3. Verify that the "Successfully Posted!" message was displayed to the user.
4. Retrieve the user's story from the server to verify that the new status was correctly
appended to the user's story, and that all status details are correct.
 */
public class EndToEndTest {
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private ServerFacade serverFacade;
    private PostStatusRequest postStatusRequest;
    private CountDownLatch countDownLatch;
    private ViewTest viewSpy;
    private AuthToken authToken;
    private MainPresenter presenter;
    private MainPresenter.View view;
    private StoryRequest storyRequest;//
    private StoryResponse storyResponse;
    private List<Status> statuses;

    @Before
    public void setup() {
        view = Mockito.mock(MainPresenter.View.class);
        //viewSpy = Mockito.mock(ViewTest.class);
        //ViewTest view = new ViewTest();
        //viewSpy = spy(view);
        //view = spy(ViewTest.class);
        serverFacade = new ServerFacade();
        loginRequest = new LoginRequest("@batman", "Iambatman10");
        try {
            loginResponse = serverFacade.login(loginRequest, "/login");
        }
        catch (Exception exception) {
            System.out.println(exception.toString());
        }

        authToken = loginResponse.getAuthToken();
        //AuthToken authToken, String userAlias, int limit, Status lastStatus
        storyRequest = new StoryRequest(authToken, "@batman", 7, null);
        //String firstName, String lastName, String alias, String imageURL
        //User theUser = new User("Rembrand", "Pardo", "@batman", "https://cs340project.s3.us-east-2.amazonaws.com/theBat.png");
        //Status status = new Status("End to end test.", theUser, "Tue Dec 07 10:55:23 MST 2021", null, null);


        //Prepare the countdown latch
        resetCountDownLatch();
    }
    //serverFacade to get the authtoken
    //Mock the main view(posting status) for presenter.. spy call decrement countdown
    //await count in the handle success
    //Post status
    //ServerFace.getStory();

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }


    private class ViewTest implements MainPresenter.View {

        @Override
        public void setFollowerCount(int count) {}

        @Override
        public void setFollowingCount(int count) {}

        @Override
        public void setIsFollower(boolean isFollower) {}

        @Override
        public void setFollowButton(boolean update) {}

        @Override
        public void setEnabled(boolean enabled) {}

        @Override
        public void logoutUser() {}

        @Override
        public void displayInfoMessage(String message) {
            if(message == "Successfully Posted!") { countDownLatch.countDown();}
        }

        @Override
        public void displayErrorMessage(String message) {}

        @Override
        public void clearInfoMessage() {}
    }

    public void setUpPostStatus(){
        //viewSpy = Mockito.spy(ViewTest.class);
        presenter = new MainPresenter(view);
    }
    public void setUpTestRetrieveStory() throws IOException, TweeterRemoteException {
        storyResponse = serverFacade.getStory(storyRequest, "/story");
        statuses = storyResponse.getStatuses();
    }


    @Test
    public void testGetStory() throws Exception {
        setUpPostStatus();
        presenter.postStatus(authToken, "This is batman posting! ");
        //Mockito.verify(view).displayInfoMessage(Mockito.anyString());
        Mockito.verify(view).displayInfoMessage("Posting Status...");
        Thread.sleep(3000);
        //awaitCountDownLatch();
        Mockito.verify(view).displayInfoMessage("Successfully Posted!");
        //countDownLatch.countDown();
        //when(view.displayInfoMessage("Successfully Posted!")).thenReturn(countDownLatch.countDown())
        //Mockito.verify(view, Mockito.atLeast(1)).displayInfoMessage("Successfully Posted!");
        setUpTestRetrieveStory();
        Status status = statuses.get(0);
        Assert.assertEquals("This is batman posting! ", status.post);

        //setup
        //call post status with authtoken and post
        //call await
        //verify that display message with

    }
}
