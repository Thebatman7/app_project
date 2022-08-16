package edu.byu.cs.tweeter.server.dao.user;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.GeneralDAO;
import edu.byu.cs.tweeter.server.dao.util.DaoUtils;

public class UserDAO extends GeneralDAO implements UserDaoInterface {
    public static final String USER = "user";
    public static final String AUTHTOKEN = "authtoken";
    public static final String BUCKET = "cs340project";

    @Override
    public User login(LoginRequest request) {
        try {
            Table userTable = dynamoDB.getTable(USER);
            Item item = userTable.getItem("alias", request.getAlias());

            if(item != null) {
                String alias = item.getString("alias");
                String securedPassword = item.getString("secured_password");
                String salt = item.getString("salt");

                if (alias != null && securedPassword != null && salt != null) {
                    //We verify that user's alias and password match with data in database.
                    if (alias.equals(request.getAlias()) &&
                            verifyPassword(securedPassword, request.getPassword(), salt)) {
                        String firstName = item.get("first_name").toString();
                        String lastName = item.get("last_name").toString();
                        String imageUrl = item.get("image_url").toString();
                        User user = new User(firstName, lastName, alias, imageUrl);
                        return user;
                    }
                    else {
                        throw new RuntimeException("[Bad Request] Alias or password does not match.");
                    }
                }
                return null;
            }
            else {
                throw new RuntimeException("[Bad Request] The alias is not found.");
            }
        }
        catch(Exception exception) {
            throw new RuntimeException("[Internal Server Error] Unable to connect to AWS DynamoDB "
                    + exception.toString());
        }
    }

    @Override
    public User register(RegisterRequest request) {
        //Creates AmazonS3 object for doing S3 operations.
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-2").build();
        String imageName = request.getAlias();
        String imageString = request.getImageBytes();
        String imageUrl;

        String salt;
        String securedPassword;

        salt = DaoUtils.getSalt();
        securedPassword = DaoUtils.getSecurePassword(request.getPassword(), salt);

        try {
            Table userTable = dynamoDB.getTable(USER);
            //We upload an object in public S3 bucket and we make it public. We get the image URL.
            uploadImageToS3(s3, imageString, imageName);
            imageUrl = retrieveURL(s3, imageName);

            //We check that user is not already in database.
            Item item = userTable.getItem("alias", request.getAlias());
            if (item == null && imageUrl != null) {
                //User is not in database: we upload one to user table.
                PutItemOutcome outcome = userTable.putItem(new Item()
                        .withPrimaryKey("alias", request.getAlias())
                        .withString("first_name", request.getFirstName())
                        .withString("last_name", request.getLastName())
                        .withString("salt", salt)
                        .withString("secured_password", securedPassword)
                        .withString("image_url", imageUrl));
                User user = new User(request.getFirstName(), request.getLastName(), request.getAlias(), imageUrl);
                return user;
            } else {
                return null;
            }
        } catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error] Unable to connect to AWS S3 or DynamoDB. "
                    + exception.toString());
        }
    }

    @Override
    public boolean logout(LogoutRequest request) {
        AuthToken token = request.getAuthToken();
        String authToken = token.getAuthToken();
        if (authToken != null) {
            try {
                Table authTokenTable = dynamoDB.getTable(AUTHTOKEN);
                authTokenTable.deleteItem("authtoken", authToken);
                return true;
            }
            catch (Exception exception) {
                throw new RuntimeException("[Internal Server Error] Unable to connect to DynamoDB. "
                        + exception.toString());
            }
        }
        return false;
    }

    public User getUser(GetUserRequest request) {
        try {
            Table userTable = dynamoDB.getTable(USER);

            Item item = userTable.getItem("alias", request.getAlias());
            String alias = item.getString("alias");

            if (alias != null) {
                String firstName = item.get("first_name").toString();
                String lastName = item.get("last_name").toString();
                String imageUrl = item.get("image_url").toString();
                return new User(firstName, lastName, alias, imageUrl);
            }
            else {
                return null;
            }
        } catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }

    private boolean verifyPassword(String securedPassword, String password, String salt) {
        //We generate the secured password with the password in request object and the salt in database
        String generatedPassword = DaoUtils.getSecurePassword(password, salt);
        if(generatedPassword != null){
            //We check if generated secured password is the same as the one saved in database
            return generatedPassword.equals(securedPassword);
        }
        return false;
    }

    public void uploadImageToS3(AmazonS3 s3, String imageString, String imageName) {
        //Note we have to use the same Base64 for client and backend
        byte[] imageBytes = Base64.getDecoder().decode(imageString);

        InputStream stream = new ByteArrayInputStream(imageBytes);

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(imageBytes.length);
        meta.setContentType("image/jpeg");//JPEG,PNG, WEBP

        //uploading image to s3 and make it public
        s3.putObject(new PutObjectRequest(BUCKET, imageName, stream, meta)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
    public String retrieveURL(AmazonS3 s3, String imageName){
        String imageURL;
        imageURL = s3.getUrl(BUCKET, imageName).toString();
        if (imageURL!= null) { return imageURL; }
        return null;
    }


    //Method to add a batch of users
    public void addUsersBatch(List<User> users) {
        String salt = "1SKyCT9ufNxGmTsRN3z/7w==";
        //password: 123
        String securedPassword = "63471851adcd397abf3f6931031b31d106a669a9d0cee358d724fdfc3bdc148c";

        //Constructor for TableWriteItems takes the name of the table
        TableWriteItems items = new TableWriteItems(USER);

        //Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item()
                    .withPrimaryKey("alias", user.getAlias())
                    .withString("first_name", user.getName())
                    .withString("image_url", user.getImageUrl())
                    .withString("last_name", user.getLastName())
                    .withString("salt", salt)
                    .withString("secured_password", securedPassword);
            items.addItemToPut(item);

            //25 is the maximum number of items allowed in a single batch write.
            //Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items, "Wrote User Batch");//Method in parent class
                items = new TableWriteItems(USER);
            }
        }
    }
}
