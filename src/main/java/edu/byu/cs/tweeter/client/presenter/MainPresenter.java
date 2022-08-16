package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.MainService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;

/*
This Main view contained a lot of methods so we moved those methods into its presenter
*/
public class MainPresenter {

    /*
    Every presenter has a view interface with methods that the view class needs to implement.
    view and presenter call methods on each other a lot. This interface makes that possible without
    breaking the dependency rule.
    */
    public interface View extends Presenter.View {
        void setFollowerCount(int count);
        void setFollowingCount(int count);
        void setIsFollower(boolean isFollower);
        void setFollowButton(boolean update);
        void setEnabled(boolean enabled);
        void logoutUser();
    }
    private View view;

    //we create this variable to make the code TESTABLE
    private MainService mainService;
    public MainPresenter(View view) {
        this.view = view;
    }

    //We create this method to make the code TESTABLE
    public MainService getMainService() {
        if (mainService == null) { return new MainService(); }
        return mainService;
    }


    //Updates Following and Followers
    public void updateSelectedUserFollowingAndFollowers(AuthToken authToken, User selectedUser){
        // Get count of most recently selected user's followers.
        new MainService().getFollowersCount(authToken, selectedUser, new MainService.GetFollowersCountObserver() {
            @Override
            public void handleSuccess(int count) {
                view.setFollowerCount(count);
            }
            @Override
            public void handleFailure(String message) {
                view.displayErrorMessage(message);
            }
        });

        new MainService().getFollowingCount(authToken, selectedUser, new MainService.GetFollowingCountObserver() {
            @Override
            public void handleSuccess(int count) {
                view.setFollowingCount(count);
            }
            @Override
            public void handleFailure(String message) {
                view.displayErrorMessage(message);
            }
        });
    }

    public void isFollower(AuthToken authToken, User currentUser, User selectedUser) {
        IsFollowerRequest request = new IsFollowerRequest(authToken, currentUser.getAlias(), selectedUser.getAlias());
        new MainService().isFollower(request, new MainService.IsFollowerObserver() {
            @Override
            public void handleSuccess(boolean isFollower) {
                view.setIsFollower(isFollower);
            }
            @Override
            public void handleFailure(String message) {
                view.displayErrorMessage(message);
            }
        });
    }

    public void unfollow(AuthToken authToken, User selectedUser) {
        UnfollowRequest request = new UnfollowRequest(authToken, selectedUser.getAlias());

        new MainService().unfollow(request, new MainService.UnfollowObserver() {
            @Override
            public void handleSuccess() {
                view.setFollowButton(true);
                view.setEnabled(true);
            }
            @Override
            public void handleFailure(String message) {
                view.displayErrorMessage(message);
            }
        });
        //view.setEnabled();
        view.displayInfoMessage("Removing " + selectedUser.getName() + "...");
    }
    public void follow(AuthToken authToken, User selectedUser) {
        FollowRequest request = new FollowRequest(authToken, selectedUser.getAlias());
        new MainService().follow(request, new MainService.FollowObserver() {
            @Override
            public void handleSuccess() {
                view.setFollowButton(false);
                view.setEnabled(true);
            }
            @Override
            public void handleFailure(String message) {
                view.displayErrorMessage(message);
            }
        });
        //view.setEnabled();
        view.displayInfoMessage("Adding " + selectedUser.getName() + "...");
    }

    public void logout(AuthToken authToken) {
        view.clearInfoMessage();
        view.displayInfoMessage("Logging Out...");

        LogoutRequest request = new LogoutRequest(authToken);
        new MainService().logout(request, new MainService.LogoutObserver() {
            @Override
            public void handleSuccess() {
                view.clearInfoMessage();
                view.logoutUser();
            }
            @Override
            public void handleFailure(String message) {
                view.displayErrorMessage(message);
            }
        });
    }

    public void postStatus(AuthToken authToken, String post){
        view.displayInfoMessage("Posting Status...");

        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            System.out.println("This is the post date time format: " + newStatus.getDate());//Wed Nov 17 23:31:24 MST 2021
            PostStatusRequest request = new PostStatusRequest(authToken, newStatus);
            //We modify this method to make it TESTABLE
            getMainService().postStatus(request, new MainService.StatusObserver() {
                @Override
                public void handleSuccess() {
                    view.clearInfoMessage();
                    view.displayInfoMessage("Successfully Posted!");
                }
                @Override
                public void handleFailure(String message) {
                    view.clearInfoMessage();
                    view.displayErrorMessage(message);
                }
            });
        } catch (Exception ex) {
            view.displayErrorMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }
    private String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");//EEE MMM dd HH:mm:ss zzz yyyy
        SimpleDateFormat statusFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }
    private List<String> parseURLs(String post) throws MalformedURLException {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }
    private int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }
    private List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }
}
