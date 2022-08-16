package edu.byu.cs.tweeter.server.dao.user;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public interface UserDaoInterface {

    public User login(LoginRequest request);

    public User register(RegisterRequest request);

    public boolean logout(LogoutRequest request);

    public User getUser(GetUserRequest request);

    public void addUsersBatch(List<User> users);
}
