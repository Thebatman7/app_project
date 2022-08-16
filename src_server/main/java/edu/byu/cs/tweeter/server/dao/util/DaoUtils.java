package edu.byu.cs.tweeter.server.dao.util;

import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

//This contains methods used by the DAOs
public class DaoUtils {
    //Variables for expiration time. After 10 hours token should expire.
    private static final long  EXPIRES_IN = TimeUnit.HOURS.toMillis(10);
    //Variables for generating AuthToken
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    /*Using these methods return negative int. because of int overflow.
    public static int getExpiration() {
        //AuthToken will expire 40 hours from now (current time)
        Timestamp expirationTime = new Timestamp(System.currentTimeMillis() + EXPIRES_IN);
        //We cast the long value to integer (value saved in database) we get from .getTime
        return (int) expirationTime.getTime();
    }
    public static boolean isExpired(int tokenExpirationTime) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        int timeNow = (int) currentTime.getTime();
        if (tokenExpirationTime < timeNow) {
            return true;//AuthToken expired
        }
        return false;//AuthToken didn't expire
    }
    */

    //This method could be improved
    public static int getExpiration() {
        Timestamp timenow = new Timestamp(System.currentTimeMillis());
        System.out.println("This is the we are getting for now: " + timenow.getTime());
        //AuthToken will expire 40 hours from now (current time)
        Timestamp expirationTime = new Timestamp(System.currentTimeMillis() + EXPIRES_IN);
        //We cast the long value to integer (value saved in database) we get from .getTime
        int expiration = (int) (expirationTime.getTime()/10000000);
        System.out.println("This is the expiration we are getting: " + expiration + ". This is with 10 hours more.");
        return expiration;
    }
    public static boolean isExpired(int tokenExpirationTime) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        int timeNow = (int) (currentTime.getTime()/10000000);
        System.out.println("token's expiration time: " + tokenExpirationTime + " current time: " + timeNow);
        if (tokenExpirationTime < timeNow) {
            System.out.println("Authtoken is expired.");
            return true;//AuthToken expired
        }
        System.out.println("Authtoken is not expired.");
        return false;//AuthToken didn't expire
    }

    //Method to get time in milliseconds
    public static int stringToTime(String datetime) throws ParseException {
        Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(datetime);
        int time = (int) (date.getTime()/10000000);
        return time;
    }

    public static int getTime(String datetime) throws ParseException {
        Date date = new SimpleDateFormat("MMM dd yyyy HH:mm aa").parse(datetime);
        int time = (int) date.getTime();
        return time;
    }

    public static String convertTimeType(String dateTime) throws ParseException {
        String dateString = dateTime;
        DateFormat sourceDate = new SimpleDateFormat("MMM dd yyyy HH:mm aa");
        Date date = sourceDate.parse(dateString);

        DateFormat destDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        dateString = destDate.format(date);
        return dateString;
    }

    //Method to generate the authtoken strings
    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
    //Methods to Hash and Salt users passwords
    public static String getSecurePassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
        //return "FAILED TO HASH PASSWORD";
    }
    public static String getSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
        //return "FAILED TO GET SALT";
    }

    public static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public static String generateRandomString() {
        byte[] randomBytes = new byte[12];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }


    public static List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }
    private static int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }
    public static List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }
}
