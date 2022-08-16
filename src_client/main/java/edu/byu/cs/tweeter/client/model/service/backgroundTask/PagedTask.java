package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PagedRequest;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;


//Generic implementation
public abstract class PagedTask<T> extends AuthorizedTask {
    private static final String LOG_TAG = "PagedTasK";

    public static final String ITEMS_KEY = "items";
    public static final String MORE_PAGES_KEY = "more-pages";

    //Maximum number of items to return (i.e., page size).
    //private int limit;


    /*
     The last item returned in the previous page of results (can be null).
     This allows the new page to begin where the previous page ended.
     */
    private T lastItem;

    //Users and statuses
    private List<T> items;
    private boolean hasMorePages;

    private ServerFacade serverFacade;

    private PagedRequest request;

    protected PagedTask(Handler messageHandler, PagedRequest request) {
        super(messageHandler, request.getAuthToken());
        this.request = request;

    }

    //We need these methods for getItems method so subclasses can have access to the variables used in it
    /*public T getLastItem() {
        return request.getLastItem();
    }
    */
    public int getLimit() {
        return request.getLimit();
    }

    //protected abstract Pair<List<T>, Boolean> getItems();

    //The new bundle loader is loaded with what we want and is unique to this task
    @Override
    protected void loadMessageBundle(Bundle msgBundle) {
        msgBundle.putSerializable(ITEMS_KEY, (Serializable) items);
        msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
    }

    @Override
    protected void runTask() {
        try {
            PagedResponse response = getResponse(request);
            this.items = response.getItems();
            this.hasMorePages = response.getHasMorePages();


            loadImages(items);
            sendSuccessMessage();//added TODO
        }
        catch(Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    public abstract PagedResponse getResponse(PagedRequest request) throws Exception;


    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

    /*
    We need to convert this list of T into a list of users, this is different for the tasks that use
    statuses instead of users. That is why we need the abstract method convertItemsToUsers.
    */
    private void loadImages(List<T> items) throws IOException {
        for (User u : convertItemsToUsers(items)) {
            BackgroundTaskUtils.loadImage(u);
        }
    }
    protected abstract List<User> convertItemsToUsers(List<T> items);

}
