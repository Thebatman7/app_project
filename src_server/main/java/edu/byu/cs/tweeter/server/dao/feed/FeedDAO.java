package edu.byu.cs.tweeter.server.dao.feed;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

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
import edu.byu.cs.tweeter.server.dao.GeneralDAO;
import edu.byu.cs.tweeter.server.dao.util.DaoUtils;
import edu.byu.cs.tweeter.server.util.Pair;

public class FeedDAO extends GeneralDAO implements FeedDaoInterface {
    private static final String FEED = "feed";

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
                //String alias = request.getItemAlias();
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
                throw new RuntimeException("[Bad Request] Getting feed: Problem converting string date to integer.");
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
    public void postStatus(String ownerAlias, PostStatusRequest request) {
        int postTime;
        try {
            String format = request.getStatus().datetime;
            postTime = DaoUtils.stringToTime(format);
        }
        catch(ParseException parseException) {
            throw new RuntimeException("[Bad Request] Posting Status: Problem converting string date to integer.");
        }
        try {
            Table feedTable = dynamoDB.getTable(FEED);

            PutItemOutcome outcome = feedTable.putItem(new Item()
                    .withPrimaryKey("owner_alias", ownerAlias, "post_time", postTime)
                    .withString("post", request.getStatus().post)
                    .withString("datetime", request.getStatus().datetime)
                    .withString("author_alias", request.getStatus().getUser().getAlias()));
        }
        catch (Exception exception) {
            throw new RuntimeException("[Internal Server Error]: Unable to connect to AWS DynamoDB. "
                    + exception.toString());
        }
    }

    @Override
    public void postStatusBatch(SqsPostStatusRequest request) {
        int postTime;
        try {
            String format = request.getStatus().datetime;
            postTime = DaoUtils.stringToTime(format);
        }
        catch(ParseException parseException) {
            throw new RuntimeException("[Bad Request] Posting Status: Problem converting string date to integer.");
        }
        String ownerAlias;
        //Constructor for TableWriteItems takes the name of the table, which is stored in FEED
        TableWriteItems items = new TableWriteItems(FEED);
        System.out.println("Inside feedDAO batch method: The size of list of followers is " + request.getFollowers().size());
        for (int i = 0; i < request.getFollowers().size(); ++i) {
            ownerAlias = request.getFollowers().get(i);
            Item item = new Item()
                    .withPrimaryKey("owner_alias", ownerAlias, "post_time", postTime)
                    .withString("post", request.getStatus().post)
                    .withString("datetime", request.getStatus().datetime)
                    .withString("author_alias", request.getStatus().getUser().getAlias());
            items.addItemToPut(item);
            //25 is the maximum number of items allowed in a single batch write.
            //Attempting to write more than 25 items will result in an exception being thrown
            if(items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items, "Wrote Feed Batch");
                items = new TableWriteItems(FEED);
            }
        }
    }
}
