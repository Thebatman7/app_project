# App Project
This repository contains code for a simple android app project. This social media app allows users to follow each other, post comments, updates, and be able to see followees posts.

The application is based on the following domain concepts: • User of the system has the following attributes o Name: the user’s real name (eg. Rembrand Pardo). o Alias: the user’s login ID or handle (eg. thebatman). o Photo: a picture of the user. • A status (ie message) is a publicly readable character limited length string, which can contain o User mentions which is a user alias preceded by the “@” symbol (eg, @thebat) o URLs • A user’s story is all of the statuses posted by that user. • Follows is an asymmetrical relationship between users, meaning user A can follow user B without user B following user A. • A user’s feed is all of the statuses posted by users he or she follows, sorted from newest to oldest. A detail to note here is that a status is only included in user’s feed if the status was posted at the time the user was following the author.

User interface The application views:

All of the main views are paginated lists of statuses or users. By paginated we mean that the content is loaded a “page” at a time. The application adds an additional page of content when the user of the app reaches the bottom of the page or using something like a “more” button in the user interface.
All displays of statuses turn user mentions (@...) and URLs into clickable links. A user mention links to a user story view (which page should facilitate access to that user’s statuses, followers and following).
All displays of statuses include the profile image of the author of the status
Across all views, actions that are impossible should be disabled or not included in the user interface at all. For example, follow or unfollow functionality only make sense when a user is logged in and depends on whether the currently logged in user is (or is not) already following a given user.
Errors should be handled and (when appropriate) communicated to users in a consistent way. Requirements
The application satisfies the following user and session management specifications:

A new user can sign up for the service, specifying their name, alias, and password, and providing an image to upload (i.e., their profile photo). After signing up a user is automatically “signed in” and is redirected to their (at this point) blank feed.
User passwords are stored as hashes
User profile pictures are stored on AWS S3.
A user that has previously signed up for the service, can sign in (ie. authenticate) by supplying their alias and password. If the alias and password are correct the user is redirected to her or his feed.
Authentication tokens expire after N minutes of inactivity.
A signed in user can sign out (ie log off) of the service.
And status related specifications:

A signed in user can post a status. The system adds that newly created status to the feeds of all of the author’s followers.
A signed in user can view all statuses of all of his or her followers merged into one list, sorted from newest to oldest. This list of statuses is called the user’s feed and is the default view of the application for a signed in user.
A user can view all statuses posted by a given user, sorted from newest to oldest. This list of statuses IS CALLED a user’s story and can be reached by clicking on a “mention” link (@…).
And following/followers related specifications:

A user can view all of the users followed by a particular user. Naturally this includes the ability for a signed in user to see all of the users she or he follows.
A user can view all of the users following a particular user. Again, this includes the ability for a signed in user to see all users following him or her.
A user can see a count of all of his or her followers and followees.
A signed in user can follow an unlimited number of other users, though a user cannot follow herself or himself or someone he or she is already following.
A signed in user can only unfollow users they are following. After user A unfollows user B, user B’s new statuses are no longer added to A’s feed, however all of B’s statuses that had previously been added to A’s feed remain in A’s feed.
A user can see a count of how many users are followed by a particular user. The same goes for the count of how many users are following that particular user. Naturally, these counts changes when users select to follow or unfollow another user.
