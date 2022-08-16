package edu.byu.cs.tweeter.server.dao.authtoken;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.GeneralDAO;
import edu.byu.cs.tweeter.server.dao.util.DaoUtils;

public class AuthTokenDAO extends GeneralDAO implements AuthTokenDaoInterface {
    private static final String AUTHTOKEN = "authtoken";

    @Override
    public AuthToken create(String alias) {
        Table authTokenTable = dynamoDB.getTable(AUTHTOKEN);
        //We generate a new unique random authToken string
        String authToken = DaoUtils.generateNewToken();
        System.out.println("Inside AuthtokenDAO create method: This is the authtoken string " + authToken);
        int expirationTime = DaoUtils.getExpiration();
        System.out.println("Inside AuthtokenDAO create method: This is the expiration time " + expirationTime);
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
        System.out.println("Inside AuthtokenDAO read method: This is the authtoken against table authtoken " + authToken);
        try {
            Table authTokenTable = dynamoDB.getTable(AUTHTOKEN);
            Item item = authTokenTable.getItem("authtoken", authToken);

            int expiration = item.getInt("expiration");
            System.out.println("Inside AuthtokenDAO read method: This is the expiration string " + expiration);

            //If the expiration date is not expired we get alias
            if(!DaoUtils.isExpired(expiration)) {
                String alias = item.getString("alias");
                System.out.println("Inside AuthtokenDAO read method: This is the alias of authtoken " + alias);
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
    public void update() {

    }

    @Override
    public void delete() {

    }
}
