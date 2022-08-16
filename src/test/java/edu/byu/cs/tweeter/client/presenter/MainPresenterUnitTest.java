package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.MainService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;


/*
 We want to test a class and methods but these have dependencies. The code interacts with its dependencies
 in certain ways. What we do is confirm in our test that it actually interacted with those dependencies
 in the correct way.

 */

public class MainPresenterUnitTest {

    private MainPresenter.View mockView;
    private MainService mockMainService;

    private Cache mockCache;

    private String post = "cool post, @ryanreynolds" +
            "https://twitter.com/VancityReynolds?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor";
    private Status status = new Status();
    private AuthToken authToken;

    //We spy this method
    private MainPresenter mainPresenterSpy;

    //We spy the classes we are testing we mock the classes we are not testing
    @BeforeEach
    void setup() {
        //Mock dependencies creation
        mockView = Mockito.mock(MainPresenter.View.class);
        mockMainService = Mockito.mock(MainService.class);

        //mockCache = Mockito.mock(Cache.class);

        //Mockito.doReturn(mockCache).when(Cache.getInstance());

        //Creation of presenter and give the mocks created
        MainPresenter presenter = new MainPresenter(mockView);
        /*
        Creation of spy to modify a method. We could also use new MainPresenter(mockView) to avoid
        calling presenter by accident. We spy the whole class that contains the method we are testing.

         */

        mainPresenterSpy = Mockito.spy(presenter);


       /* We use spy to take over methods for testing. We take over the method getMainService.
        When our spy presenter calls its method getMainService instead of returning the variable inside
        of the MainPresenter class it will return the mock we created, that is mockMainService.
        Alternative syntax:*/
        Mockito.when(mainPresenterSpy.getMainService()).thenReturn(mockMainService);

        Mockito.doReturn(mockMainService).when(mainPresenterSpy).getMainService();
    }

    @Test
    void testPostStatusSuccess() {
        /*
        With answers we put code inside of our mocks. A successful post doesn't return anything.
        When this code gets executed the answer method will be called which gives an instance of an
        invocation object that allows us to grab a parameter

         */

        Answer<Void> successAnswer = new Answer<Void>() {
            //We bypass the background thread and simply call the method on the observer to use handleSuccess
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //We create an instance of the observer we will invoke and get a hold of
                MainService.StatusObserver observer = invocation.getArgument(2, MainService.StatusObserver.class);
                observer.handleSuccess();
                return null;
            }
        };

        /*
        We tell the mock service to execute the answer when statusPosted method is called.
        When we don't care about the parameter and we just want the answer to be executed
        we use Mockito.any as the parameter.

         */

        Mockito.doAnswer(successAnswer).when(mockMainService).postStatus(Mockito.any(), Mockito.any());

        mainPresenterSpy.postStatus(authToken, post);

        //All parameters passed by the Presenter to the Service's "post status" operation are correct.
        ArgumentCaptor<Status> capturedStatus = ArgumentCaptor.forClass(Status.class);
        ArgumentCaptor<AuthToken> argument1 = ArgumentCaptor.forClass(AuthToken.class);
        ArgumentCaptor<MainService.StatusObserver> argument3 = ArgumentCaptor.forClass(MainService.StatusObserver.class);
        //Status statusCaptured = capturedStatus.getValue();



        /*Assertions.assertEquals("cool post, @ryanreynolds" +
                "https://twitter.com/VancityReynolds?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor", statusCaptured.getPost());

         */
        //Mockito.verify(mockMainService).postStatus(argument1.capture(), capturedStatus.capture());


        //We expect this actions to be executed when we call statusPosted method and it succeeds
        Mockito.verify(mockView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockView).clearInfoMessage();
        Mockito.verify(mockView).displayInfoMessage("Successfully Posted!");
    }

    @Test
    void testPostStatusFailure() {
        Answer<Void> failureAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainService.StatusObserver observer = invocation.getArgument(2, MainService.StatusObserver.class);
                /*
                Because the service decides what string to pass and we aren't testing the service
                we can pass any string that we want.*/



                observer.handleFailure("Posting status failed");
                return null;
            }
        };

        //Mockito.doAnswer(failureAnswer).when(mockMainService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        mainPresenterSpy.postStatus(authToken, post);

        Mockito.verify(mockView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockView).clearInfoMessage();
        //We verify that method is not called and that we don't have a bug
        Mockito.verify(mockView, Mockito.times(0)).displayInfoMessage("Successfully Posted!");
        //We verify that the right method is called with the string the service passes
        Mockito.verify(mockView).displayErrorMessage("Posting status failed");
    }
}

