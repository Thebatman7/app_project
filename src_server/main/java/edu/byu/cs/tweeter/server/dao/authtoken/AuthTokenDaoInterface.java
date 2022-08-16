package edu.byu.cs.tweeter.server.dao.authtoken;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface AuthTokenDaoInterface {
    public AuthToken create(String alias);
    public AuthToken read(AuthToken token);
    public void update();
    public void delete();
}
