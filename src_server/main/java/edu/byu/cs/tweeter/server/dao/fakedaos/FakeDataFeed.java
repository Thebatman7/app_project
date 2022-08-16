package edu.byu.cs.tweeter.server.dao.fakedaos;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.SqsPostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.dao.GeneralDAO;
import edu.byu.cs.tweeter.server.dao.feed.FeedDaoInterface;
import edu.byu.cs.tweeter.server.dao.util.DaoUtils;
import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;

public class FakeDataFeed extends GeneralDAO implements FeedDaoInterface {
    private static final String FEED = "feed";

    /*@Override
    public Pair<Boolean, List<Status>> getFeed(FeedRequest request) {
        List<Status> statuses = new ArrayList<>();
        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#o", "owner_alias");
        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":u", new AttributeValue().withS(request.getItemAlias()));

        QueryRequest queryRequest = new QueryRequest().withTableName(FEED)
                .withKeyConditionExpression("#o = :u")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(request.getLimit());

        System.out.println("3. Code works up until here");
        try {
            if (request.getLastItem() != null) {
                int time = DaoUtils.stringToTime(request.getLastItem().datetime);
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("owner_alias", new AttributeValue().withS(request.getItemAlias()));
                startKey.put("post_time", new AttributeValue().withN(String.valueOf(time)));//Might take a different value not the sort key

                queryRequest = queryRequest.withExclusiveStartKey(startKey);
            }
        }
        catch(ParseException parseException){
            throw new RuntimeException("[Bad Request] Problem converting string date to integer.");
        }

        System.out.println("4. Code works up until here");
        try {
            QueryResult queryResult = awsDynamoDB.query(queryRequest);
            List<Map<String, AttributeValue>> items = queryResult.getItems();

            System.out.println("5. Code works up until here");
            if (items != null) {
                List<String> urls = new ArrayList<>();
                List<String> mentions = new ArrayList<>();
                for (Map<String, AttributeValue> item : items) {
                    //String post, User user, String datetime, List<String> urls, List<String> mentions
                    String post = item.get("post").getS();
                    String datetime = item.get("datetime").getS();
                    String authorAlias = item.get("author_alias").getS();
                    User user = new User();//The user is the user who created the post or the owner of post
                    user.setAlias(authorAlias);
                    urls = DaoUtils.parseURLs(post);
                    mentions = DaoUtils.parseMentions(post);
                    Status status = new Status(post, user, datetime, urls, mentions);
                    statuses.add(status);
                }
                System.out.println("6. Code works up until here");
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
    public Pair<Boolean, List<Status>> getFeed(FeedRequest request) {
        List<Status> statuses = new ArrayList<>();

        System.out.println("3. Code works up until here.");

        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#o", "owner_alias");
        Map<String, Object> attrValues = new HashMap<>();
        attrValues.put(":u", request.getItemAlias());

        System.out.println("3. Code works up until here.");
        QuerySpec query;
        if(request.getLastItem() == null) {
            System.out.println("Last item is null");
            query = new QuerySpec().withKeyConditionExpression("#o = :u")
                    .withNameMap(attrNames)
                    .withValueMap(attrValues)
                    .withMaxResultSize(request.getLimit())
                    .withScanIndexForward(false);
        }
        else {
            System.out.println("Last item is not null");
            try {
               String alias = request.getItemAlias();
               System.out.println("This is the string time will be converting: " + request.getLastItem().datetime);
               int time = DaoUtils.stringToTime(request.getLastItem().datetime);
               System.out.println("Feed: This is the time we get " + time);
               PrimaryKey lastPrimaryKey = new PrimaryKey("owner_alias", request.getItemAlias(),
                       "post_time", time);
               query = new QuerySpec().withKeyConditionExpression("#o = :u")
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
        System.out.println("4. Code works up until here.");
        try {
            Table feedTable = dynamoDB.getTable(FEED);

            ItemCollection<QueryOutcome> items = feedTable.query(query);
            Iterator<Item> iterator = null;
            Item item = null;

            System.out.println("5. Code works up until here.");
            System.out.println("This is what we get for items: " +  items);
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
                    String datetime = item.getString("datetime");
                    String authorAlias = item.getString("author_alias");
                    User user = new User();
                    user.setAlias(authorAlias);
                    Status status = new Status(post, user, datetime, urls, mentions);
                    statuses.add(status);
                }
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
    public void postStatus(String ownerAlias, PostStatusRequest request) {
        int postTime;
        try {
            String newFormat = DaoUtils.convertTimeType(request.getStatus().datetime);
            postTime = DaoUtils.stringToTime(newFormat);
            System.out.println("Feed: This is the post time we get " + postTime);
        }
        catch(ParseException parseException) {
            throw new RuntimeException("[Bad Request] Problem converting string date to integer.");
        }
        try {
            Table feedTable = dynamoDB.getTable(FEED);
            //AuthToken authToken, Status status
            PutItemOutcome outcome = feedTable.putItem(new Item()
                    .withPrimaryKey("owner_alias", ownerAlias, "post_time", postTime)
                    .withString("post", request.getStatus().post).withString("datetime", request.getStatus().datetime)
                    .withString("author_alias", request.getStatus().getUser().getAlias()));
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                + exception.toString());
        }
    }

    @Override
    public void postStatusBatch(SqsPostStatusRequest request) {}


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

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}
