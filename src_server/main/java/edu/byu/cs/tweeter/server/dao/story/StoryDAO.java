package edu.byu.cs.tweeter.server.dao.story;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.server.dao.GeneralDAO;
import edu.byu.cs.tweeter.server.dao.util.DaoUtils;
import edu.byu.cs.tweeter.server.util.Pair;

public class StoryDAO extends GeneralDAO implements StoryDaoInterface {
    private static final String STORY = "story";

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
                int time = DaoUtils.stringToTime(request.getLastItem().datetime);
                PrimaryKey lastPrimaryKey = new PrimaryKey("alias", alias, "post_time", time);
                query = new QuerySpec().withKeyConditionExpression("#a = :u")
                        .withNameMap(attrNames)
                        .withValueMap(attrValues)
                        .withMaxResultSize(request.getLimit())
                        .withExclusiveStartKey(lastPrimaryKey)
                        .withScanIndexForward(false);
            }
            catch(ParseException parseException){
                throw new RuntimeException("[Bad Request] Getting stories: Problem converting string date to integer.");
            }
        }
        try {
            Table storyTable = dynamoDB.getTable(STORY);
            ItemCollection<QueryOutcome> items = storyTable.query(query);
            Iterator<Item> iterator = null;
            Item item = null;


            if (items != null) {
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
                else { return new Pair<>(false, statuses); }//There are no more items
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
        try {
            String format = request.getStatus().datetime;
            postTime = DaoUtils.stringToTime(format);
        }
        catch(ParseException parseException) {
            throw new RuntimeException("[Bad Request] Posting status: Problem converting string date to integer. " + request.getStatus().datetime);
        }

        try{
            Table storyTable = dynamoDB.getTable(STORY);

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
}
