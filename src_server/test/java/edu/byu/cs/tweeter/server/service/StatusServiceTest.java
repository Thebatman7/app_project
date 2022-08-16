package edu.byu.cs.tweeter.server.service;

import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class StatusServiceTest {
    /*
    Service that returns a user's story pages (i.e., StatusService).
    The Service should have an operation that creates a background task to retrieve a user's story
    from the server and notifies the Server's observer of the operation's outcome.
    Your test should verify that the Service's observer is notified in the case of a successful story retrieval.
     */

    private StoryRequest storyRequest;

    private StoryResponse storyResponse;

    private CountDownLatch countDownLatch;






    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    //private class StoryServiceObserver implements PagedPresenter
}
