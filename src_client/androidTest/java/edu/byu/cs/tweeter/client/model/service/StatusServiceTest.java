package edu.byu.cs.tweeter.client.model.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.server.util.FakeData;

public class StatusServiceTest {
    /*
    Service that returns a user's story pages (i.e., StatusService).
    The Service should have an operation that creates a background task to retrieve a user's story
    from the server and notifies the Server's observer of the operation's outcome.
    Your test should verify that the Service's observer is notified in the case of a successful story retrieval.
     */

    private StoryRequest storyRequest;

    private CountDownLatch countDownLatch;

    private StoryService.GetStoryObserver observerSpy;

    private List<String> urls = new ArrayList<>();
    private List<String> mentions = new ArrayList<>();

    @Before
    public void setUp() {
        observerSpy = Mockito.spy(StoryPresenter.GetStoryObserver.class);
        User currentUser = new User("FirstName", "LastName", null);
        FakeData fakeData = new FakeData();
        AuthToken authToken = fakeData.getAuthToken();
        storyRequest = new StoryRequest(authToken, currentUser.getAlias(), 3, null);

        resetCountDownLatch();

        Answer<Void> storyRetrievedAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //invocation.callRealMethod();
                decrementCountDownLatch();
                return null;
            }
        };

        Mockito.doAnswer(storyRetrievedAnswer).when(observerSpy)// mocked object
                .handleSuccess(Mockito.anyList(), Mockito.anyBoolean());

    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void decrementCountDownLatch() { countDownLatch.countDown(); }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    public void testGetStoryTest() throws InterruptedException {

        StoryService storyService = new StoryService();

        storyService.getStory(storyRequest, observerSpy);

        awaitCountDownLatch();

        Mockito.verify(observerSpy).handleSuccess(Mockito.anyList(), Mockito.anyBoolean());
    }

   /* private class StoryServiceObserver implements StoryService.GetStoryObserver {
        private boolean success;
        private String message;
        private List<Status> story;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.story = items;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            this.success = false;
            this.message = message;
            this.story = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }
        public String getMessage() {
            return message;
        }
        public List<Status> getStory() {
            return story;
        }
        public boolean getHasMorePages() {
            return hasMorePages;
        }
        public Exception getException() {
            return exception;
        }
    }*/
}
