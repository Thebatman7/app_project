package edu.byu.cs.tweeter.server.dao.fakedaos;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.GeneralDAO;
import edu.byu.cs.tweeter.server.dao.authtoken.AuthTokenDaoInterface;
import edu.byu.cs.tweeter.server.dao.util.DaoUtils;

public class FakeDataAuthToken extends GeneralDAO implements AuthTokenDaoInterface {
    private static final String AUTHTOKEN = "authtoken";

    @Override
    public AuthToken create(String alias) {
        Table authTokenTable = dynamoDB.getTable(AUTHTOKEN);
        //We generate a new unique random authToken string
        String authToken = DaoUtils.generateNewToken();

        int expirationTime = DaoUtils.getExpiration();
        try{
            PutItemOutcome outcome = authTokenTable.putItem(new Item()
                    .withPrimaryKey("authtoken", authToken)
                    .withString("alias", alias).withInt("expiration", expirationTime));
            return new AuthToken(authToken, expirationTime, alias);
        }
        catch(Exception exception) {
            throw new RuntimeException("[Internal Server Error] Unable to connect to AWS DynamoDB "
                    + exception.toString());
        }
    }

    @Override
    public AuthToken read(AuthToken token) {
        String authToken = token.getAuthToken();

        System.out.println("This is the authtoken: " + authToken);

        try {
            Table authTokenTable = dynamoDB.getTable(AUTHTOKEN);
            Item item = authTokenTable.getItem("authtoken", authToken);

            int expiration = item.getInt("expiration");

            //If the expiration date is not expired we get alias
            if(!DaoUtils.isExpired(expiration)) {
                String alias = item.getString("alias");
                //String authToken, int expiration, String userOwner
                return new AuthToken(authToken, expiration, alias);
            }
            else {
                return null;
            }
        }
        catch(Exception exception) {
            throw new RuntimeException("[Internal Server Error] Unable to connect to AWS DynamoDB "
                    + exception.toString());
        }
    }

    @Override
    public void update() {}

    @Override
    public void delete() {}
}
