package edu.byu.cs.tweeter.server.dao.fakedaos;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.server.dao.GeneralDAO;
import edu.byu.cs.tweeter.server.dao.follow.FollowDaoInterface;
import edu.byu.cs.tweeter.server.dao.util.DaoUtils;
import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;

public class FakeDataFollow extends GeneralDAO implements FollowDaoInterface {
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
                if(item != null) { return true; }
                else { return false; }
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
       /* HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#f", "follower");
        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":a", request.getUserAlias());*/

        //More secured way tro retrieve information from database
        AttributeValue attributeValue = new AttributeValue(request.getUserAlias());
        HashMap<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":a", attributeValue);
        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#f", "follower");

        try {
            /*QuerySpec query = new QuerySpec().withScanIndexForward(true)
                    .withKeyConditionExpression("#f = :a").withNameMap(nameMap).withValueMap(valueMap);*/


            QueryRequest queryRequest = new QueryRequest().withTableName(FOLLOW)
                    .withKeyConditionExpression("#f = :a")
                    .withExpressionAttributeNames(nameMap)
                    .withExpressionAttributeValues(valueMap);

            QueryResult queryResult = awsDynamoDB.query(queryRequest);

            List<Map<String,AttributeValue>> attributeValues = queryResult.getItems();

            /*System.out.println(" This is what we get with this method found online: "
                    + attributeValues.size());*/

            /*
            "An ItemCollection object maintains a cursor pointing to its current pages of data.
             Initially the cursor is positioned before the first page.
             The next method moves the cursor to the next row, and because it returns false when
             there are no more rows in the ItemCollection object, it can be used in a while loop
             to iterate through the collection. Network calls can be triggered when the collection
             is iterated across page boundaries."
             */
            //ItemCollection<QueryOutcome> items = null;
            /*
            Table followTable = dynamoDB.getTable(FOLLOW);
            items = followTable.query(query);
            Iterator<Item> iterator = null;
            Item item = null;
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                System.out.println(item.getString("follower") + ": " + item.getString("followee"));
            }
            if (items != null) { return items.getAccumulatedItemCount(); }
            else { return -1; }
            */

            if (attributeValues != null) {
                return attributeValues.size();
            }
            else {
                return -1;
            }
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }

    @Override
    public int getFollowersCount(FollowersCountRequest request) {
       /* HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#f", "followee");
        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":a", request.getUserAlias());*/

        AttributeValue attributeValue = new AttributeValue(request.getUserAlias());
        HashMap<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":a", attributeValue);
        HashMap<String, String> nameMap = new HashMap<>();
        nameMap.put("#f", "followee");

        try {
            /*QuerySpec query = new QuerySpec().withScanIndexForward(true)
                    .withKeyConditionExpression("#f = :a").withNameMap(nameMap).withValueMap(valueMap);
            ItemCollection<QueryOutcome> items = null;

            Table followTable = dynamoDB.getTable(FOLLOW);
            Index followIndexTable = followTable.getIndex(FOLLOW_INDEX);
            items = followIndexTable.query(query);

            //This is just for testing
            Iterator<Item> iterator = null;
            Item item = null;
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                System.out.println(item.getString("followee") + ": " + item.getString("follower"));
            }
            if (items != null) { return items.getAccumulatedItemCount(); }
            else { return -1; }
            */
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

        if(DaoUtils.isNonEmptyString(request.getLastItem())) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("followee", new AttributeValue().withS(request.getItemAlias()));
            startKey.put("follower", new AttributeValue().withS(request.getLastItem()));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        try {
            QueryResult queryResult = awsDynamoDB.query(queryRequest);
            List<Map<String, AttributeValue>> items = queryResult.getItems();

            if (items != null) {
                for(Map<String, AttributeValue> item: items) {
                    String alias = item.get("follower").getS();
                    followerAliases.add(alias);
                }
                //There are more items
                if(items.size() == request.getLimit()) { return new Pair<>(true, followerAliases); }
                else { return new Pair<>(false, followerAliases); } //There are no more items
            }
            else { return null; }
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }



    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {
        int followeesIndex = 0;
        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }
        return followeesIndex;
    }
    private int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {
        int followersIndex = 0;
        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }
        return followersIndex;
    }

    //Method to add a batch of users
    public void addFollowersBatch(List<String> followers, String followee) {}

    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }
    List<User> getDummyFollowers() {
        return getFakeData().getFakeUsers();
    }
    FakeData getFakeData() {
        return new FakeData();
    }
}
