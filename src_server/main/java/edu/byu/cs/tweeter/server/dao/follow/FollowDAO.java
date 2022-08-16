package edu.byu.cs.tweeter.server.dao.follow;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.server.dao.GeneralDAO;
import edu.byu.cs.tweeter.server.dao.util.DaoUtils;
import edu.byu.cs.tweeter.server.util.Pair;

public class FollowDAO extends GeneralDAO implements FollowDaoInterface {
    public static final String FOLLOW = "follow";
    public static final String FOLLOW_INDEX = "follow_index";

    @Override
    public boolean follow(String follower, String followee) {
        if (follower != null && followee!= null && !follower.equals(followee)) {
            try {
                Table followTable = dynamoDB.getTable(FOLLOW);

                PutItemOutcome outcome = followTable.putItem(new Item()
                        .withPrimaryKey("follower", follower, "followee", followee));
                return true;
            }
            catch (Exception exception) {
                throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                        + exception.toString());
            }
        }
        return false;
    }

    @Override
    public boolean unfollow(String follower, String followee) {
        if (follower != null && followee!= null && !follower.equals(followee)) {
            try {
                Table followTable = dynamoDB.getTable(FOLLOW);
                followTable.deleteItem("follower", follower, "followee", followee);
                return true;
            }
            catch (Exception exception) {
                throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                        + exception.toString());
            }
        }
        return false;
    }

    @Override
    public boolean isFollower(String follower, String followee) {
        if (!follower.equals(followee)) {
            try {
                Table followTable = dynamoDB.getTable(FOLLOW);
                Item item = followTable.getItem("follower", follower, "followee", followee);
                if(item != null) {
                    System.out.println("Code worked up until here. Item is not null. " + follower + " is follower of " + followee);
                    return true;
                }
                else {
                    System.out.println("Code worked up until here. Item is null. " + follower + " is NOT follower of " + followee);
                    return false;
                }
            }
            catch (Exception exception) {
                throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                        + exception.toString());
            }
        }
        else {
            throw new RuntimeException("[Bad Request] Failed to verify if " + follower + " if following "
                    + followee + ".");
        }
    }

    @Override
    public int getFolloweeCount(FollowingCountRequest request) {
        //More secured way tro retrieve information from database
        AttributeValue attributeValue = new AttributeValue(request.getUserAlias());
        HashMap<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":a", attributeValue);
        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#f", "follower");

        try {
            QueryRequest queryRequest = new QueryRequest().withTableName(FOLLOW)
                    .withKeyConditionExpression("#f = :a")
                    .withExpressionAttributeNames(nameMap)
                    .withExpressionAttributeValues(valueMap);

            QueryResult queryResult = awsDynamoDB.query(queryRequest);

            List<Map<String,AttributeValue>> attributeValues = queryResult.getItems();
            if (attributeValues != null) { return attributeValues.size(); }
            else { return -1; }
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }

    @Override
    public int getFollowersCount(FollowersCountRequest request) {
        AttributeValue attributeValue = new AttributeValue(request.getUserAlias());
        HashMap<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":a", attributeValue);
        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#f", "followee");

        try {
            QueryRequest queryRequest = new QueryRequest().withTableName(FOLLOW)
                    .withIndexName(FOLLOW_INDEX)
                    .withKeyConditionExpression("#f = :a")
                    .withExpressionAttributeNames(nameMap)
                    .withExpressionAttributeValues(valueMap);

            QueryResult queryResult = awsDynamoDB.query(queryRequest);

            List<Map<String,AttributeValue>> attributeValues = queryResult.getItems();

            if (attributeValues != null) { return attributeValues.size(); }
            else { return -1; }
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }

    @Override
    public Pair<Boolean, List<String>> getFollowees(FollowingRequest request) {
        List<String> followeeAliases = new ArrayList<>();

        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#f", "follower");
        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":a", new AttributeValue().withS(request.getItemAlias()));

        QueryRequest queryRequest = new QueryRequest().withTableName(FOLLOW)
                .withKeyConditionExpression("#f = :a")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(request.getLimit());

        if(DaoUtils.isNonEmptyString(request.getLastItem())) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("follower", new AttributeValue().withS(request.getItemAlias()));
            startKey.put("followee", new AttributeValue().withS(request.getLastItem()));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        try {
            QueryResult queryResult = awsDynamoDB.query(queryRequest);
            List<Map<String, AttributeValue>> items = queryResult.getItems();


            if (items != null) {
                for(Map<String, AttributeValue> item: items) {
                    String alias = item.get("followee").getS();
                    followeeAliases.add(alias);
                }
                //There are more items
                if(items.size() == request.getLimit()) { return new Pair<>(true, followeeAliases); }
                else { return new Pair<>(false, followeeAliases); } //There are no more items
            }
            else { return null; }
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }

    @Override
    public Pair<Boolean, List<String>> getFollowers(FollowersRequest request) {
        List<String> followerAliases = new ArrayList<>();
        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#f", "followee");
        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":a", new AttributeValue().withS(request.getItemAlias()));

        QueryRequest queryRequest = new QueryRequest().withTableName(FOLLOW)
                .withIndexName(FOLLOW_INDEX)
                .withKeyConditionExpression("#f = :a")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(request.getLimit());

        if (DaoUtils.isNonEmptyString(request.getLastItem())) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("followee", new AttributeValue().withS(request.getItemAlias()));
            startKey.put("follower", new AttributeValue().withS(request.getLastItem()));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        try {
            QueryResult queryResult = awsDynamoDB.query(queryRequest);
            List<Map<String, AttributeValue>> items = queryResult.getItems();

            if (items != null) {
                for (Map<String, AttributeValue> item : items) {
                    String alias = item.get("follower").getS();
                    followerAliases.add(alias);
                }
                //There are more items
                if (items.size() == request.getLimit()) {
                    return new Pair<>(true, followerAliases);
                } else {
                    return new Pair<>(false, followerAliases);
                } //There are no more items
            } else {
                return null;
            }
        } catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }

    //Method to add a batch of users
    public void addFollowersBatch(List<String> followers, String followee) {
        //Constructor for TableWriteItems takes the name of the table
        TableWriteItems items = new TableWriteItems(FOLLOW);

        //Add each alias into the TableWriteItems object
        for(String follower : followers) {
            Item item = new Item()
                    .withPrimaryKey("follower", follower, "followee", followee);
            items.addItemToPut(item);

            //25 is the maximum number of items allowed in a single batch write.
            //Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items, "Wrote followers Batch");//Method in parent class
                items = new TableWriteItems(FOLLOW);
            }
        }
    }
}
