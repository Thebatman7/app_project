package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedNotificationServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;

//Item is generic type
public abstract class PagedPresenter<I> extends Presenter<PagedPresenter.PagedView> {
    private static final int PAGE_SIZE = 10;


    private User targetUser;
    private AuthToken authToken;

    private I lastItem;//User or Status
    private boolean hasMorePages = true;
    private boolean isLoading;//a flag to not making calls to the server when we are getting data

    public interface PagedView<I> extends NavigateToUserPresenter.NavigateToUserView {
        /*
         When we tell view to call the method that removes the dummy user from the list of users so
         the RecyclerView will stop displaying the loading footer at the bottom of the list.
         */
        void removeLoadingFooter();
        /*
         When we tell view to call the method that adds a dummy user to the list of users so
         the RecyclerView will display a view (the loading footer view) at the bottom of the list.
         */
        void addLoadingFooter();
        //When view needs more users to display and we got them we call this method so they can be displayed
        void displayMoreItems(List<I> items, boolean hasMorePages);
    }

    public PagedPresenter(PagedView view, AuthToken authToken, User targetUser) {
        super(view);
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    protected PagedView getPagedView() {
        return (PagedView) getView();
    }

    public boolean isLoading() { return isLoading; }

    //Template method
    public void loadMoreItems() {
        //If we are already loading we don't need to do it again. We also check if there are more pages
        if (!isLoading && hasMorePages) { //This guard is important for avoiding a race condition in the scrolling code.
            //We record that we are loading
            isLoading = true;

            //We notify the view that we are loading
            getPagedView().addLoadingFooter();

            //Subclasses implement this method, each will use the appropriate service class
            getItems(authToken, targetUser, PAGE_SIZE, lastItem);
        }
    }

    //Abstract method for the template method
    protected abstract void getItems(AuthToken authToken, User targetUser, int pageSize, I lastItem);

    /*
    When the a user is clicked on we navigate to that user.
    alias parameter tells us what user was clicked on.
    */
    public void goToUser(String alias) {
        getView().clearInfoMessage();
        GetUserRequest request = new GetUserRequest(authToken, alias);
        //We call the background task in service. We use an anonymous class for the observer
        new UserService().getUser(request, new UserService.GetUserObserver() {
            @Override
            public void handleSuccess(User user) {
                String message = "Getting user's profile...";
                getView().navigateToUser(user);
                getView().displayInfoMessage(message);
                getView().navigateToUser(user);
            }
            @Override
            public void handleFailure(String message) { getView().displayErrorMessage(message); }

        });
    }


    protected class GetItemsObserver implements PagedNotificationServiceObserver<I> {
        @Override
        public void handleSuccess(List<I> items, boolean hasMorePages) {
            PagedPresenter.this.lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            PagedPresenter.this.hasMorePages = hasMorePages;
            isLoading = false;
            getView().removeLoadingFooter();
            getView().displayMoreItems(items, hasMorePages);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            getView().removeLoadingFooter();
            getView().displayErrorMessage(message);
        }
    }//Tue Nov 16 01:47:53 UTC 2021

}
