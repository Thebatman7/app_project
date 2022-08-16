package edu.byu.cs.tweeter.server.dao.fakedaos;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.GeneralDAO;
import edu.byu.cs.tweeter.server.dao.story.StoryDaoInterface;
import edu.byu.cs.tweeter.server.dao.util.DaoUtils;
import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;

public class FakeDataStory extends GeneralDAO implements StoryDaoInterface {
    private static final String STORY = "story";
    //AuthToken authToken, String userAlias, int limit, Status lastStatus
    /*@Override
    public Pair<Boolean, List<Status>> getStories(StoryRequest request) {
        List<Status> statuses = new ArrayList<>();

        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#a", "alias");
        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":u", new AttributeValue().withS(request.getItemAlias()));

        QueryRequest queryRequest = new QueryRequest().withTableName(STORY)
                .withKeyConditionExpression("#a = :u")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(request.getLimit());

        try {
            System.out.println("2. Code worked up until here");

            if (request.getLastItem() != null) {
                int time = DaoUtils.stringToTime(request.getLastItem().datetime);
                System.out.println("This is what we get for time: " + time);
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("alias", new AttributeValue().withS(request.getItemAlias()));
                startKey.put("post", new AttributeValue().withS(request.getLastItem().post));//Might take a different value not the sort key

                queryRequest = queryRequest.withExclusiveStartKey(startKey);
            }
        }
        catch(ParseException parseException){
            throw new RuntimeException("[Bad Request] Problem converting string date to integer.");
        }

        try {
            System.out.println("3. Code worked up until here");
            QueryResult queryResult = awsDynamoDB.query(queryRequest);
            List<Map<String, AttributeValue>> items = queryResult.getItems();

            System.out.println("4. Code worked up until here");
            if (items != null) {
                System.out.println("This is what we get for items: " + items);
                List<String> urls = new ArrayList<>();
                List<String> mentions = new ArrayList<>();
                for (Map<String, AttributeValue> item : items) {
                    //String post, User user, String datetime, List<String> urls, List<String> mentions
                    String post = item.get("post").getS();

                    urls = DaoUtils.parseURLs(post);
                    mentions = DaoUtils.parseMentions(post);
                    User user = new User();
                    String datetime = item.get("datetime").getS();
                    Status status = new Status(post, user, datetime, urls, mentions);
                    statuses.add(status);
                }

                //There are more items
                if(items.size() == request.getLimit()) { return new Pair<>(true, statuses); }
                else { return new Pair<>(false, statuses); } //There are no more items
            }
            else { return null; }
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }*/

    @Override
    public Pair<Boolean, List<Status>> getStories(StoryRequest request) {
        List<Status> statuses = new ArrayList<>();

        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#a", "alias");
        Map<String, Object> attrValues = new HashMap<>();
        attrValues.put(":u", request.getItemAlias());

        QuerySpec query;
        if(request.getLastItem() == null){
            query = new QuerySpec().withKeyConditionExpression("#a = :u")
                    .withNameMap(attrNames)
                    .withValueMap(attrValues)
                    .withMaxResultSize(request.getLimit())
                    .withScanIndexForward(false);
        }
        else {
            try {
                String alias = request.getItemAlias();
                System.out.println("This is the string time will be converting: " + request.getLastItem().datetime);
                int time = DaoUtils.stringToTime(request.getLastItem().datetime);
                System.out.println("Story: This is the time we get " + time);
                System.out.println("This is the time we will convert " + request.getLastItem().datetime);
                PrimaryKey lastPrimaryKey = new PrimaryKey("alias", alias, "post_time", time);
                query = new QuerySpec().withKeyConditionExpression("#a = :u")
                        .withNameMap(attrNames)
                        .withValueMap(attrValues)
                        .withMaxResultSize(request.getLimit())
                        .withExclusiveStartKey(lastPrimaryKey)
                        .withScanIndexForward(false);
            }
            catch(ParseException parseException){
                throw new RuntimeException("[Bad Request] Problem converting string date to integer.");
            }
        }
        try {
            System.out.println("3. Code worked up until here");
            Table storyTable = dynamoDB.getTable(STORY);
            ItemCollection<QueryOutcome> items = storyTable.query(query);
            Iterator<Item> iterator = null;
            Item item = null;

            System.out.println("4. Code worked up until here");
            if (items != null) {
                System.out.println("This is what we get for items: " + items);
                List<String> urls = new ArrayList<>();
                List<String> mentions = new ArrayList<>();

                iterator = items.iterator();

                while(iterator.hasNext()) {
                    item = iterator.next();

                    String post = item.getString("post");

                    urls = DaoUtils.parseURLs(post);
                    mentions = DaoUtils.parseMentions(post);
                    User user = new User();
                    String datetime = item.getString("datetime");
                    Status status = new Status(post, user, datetime, urls, mentions);
                    statuses.add(status);
                }
                //There are more items
                if(items.getAccumulatedItemCount() == request.getLimit()) { return new Pair<>(true, statuses); }
                else { return new Pair<>(false, statuses); } //There are no more items
            }
            else { return null; }
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }


    @Override
    public void postStatus(PostStatusRequest request) {
        int postTime;
        System.out.println("This is the time we will convert " + request.getStatus().datetime);
        try {
            String newFormat = DaoUtils.convertTimeType(request.getStatus().datetime);
            System.out.println("This is the new format: " + newFormat);
            postTime = DaoUtils.stringToTime(newFormat);
            System.out.println("Story: This is the postTime we get " + postTime);
        }
        catch(ParseException parseException) {
            throw new RuntimeException("[Bad Request] Problem converting string date to integer. " + request.getStatus().datetime);
        }

        try{
            Table storyTable = dynamoDB.getTable(STORY);

            //AuthToken authToken, Status status
            PutItemOutcome outcome = storyTable.putItem(new Item()
                    .withPrimaryKey("alias", request.getAuthToken().getUserOwner(), "post_time", postTime)
                    .withString("post", request.getStatus().post)
                    .withString("datetime", request.getStatus().datetime));
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }





    private int getStatusStartingIndex(Status lastStatus, List<Status> allStatuses) {
        int StatusIndex = 0;
        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatus.equals(allStatuses.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    StatusIndex = i + 1;
                    break;
                }
            }
        }
        return StatusIndex;
    }

    List<Status> getDummyStatuses() {return getFakeData().getFakeStatuses(); }
    FakeData getFakeData() {
        return new FakeData();
    }
}
