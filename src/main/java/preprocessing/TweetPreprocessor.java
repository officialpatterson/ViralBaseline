package preprocessing;

/**
 * Created by apatterson on 04/06/15. class provides static functions for removing tweet-unique attributes within the tweet-text
 */
public final class TweetPreprocessor {
//http.*?\s |
    public static String removeURLS(String s){
        String regex = "\\s*http\\S*";
        return s.replaceAll(regex, "");
    }
    public static String removeHashtags(String s){
        String regex = "#.*?\\s";
        return s.replaceAll(regex, "");
    }
    public static String removeMentions(String s){
        String regex = "@.*?\\s|[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\\s";
        return s.replaceAll(regex, "");
    }
    public static String removeNewLines(String s){
        String regex = "\\n|\\r";
        return s.replaceAll(regex, "");

    }
    public static String removeQuotes(String s){
        String regex = "'|\"";
        return s.replaceAll(regex, "");

    }
    public static void main(String[] args){

        String exampleTweet;
        System.out.println("Simple Smoke Testing of TweetPreprocessor behaviour.\nShows the normal input data but does not show \nextraordinary input data.\n");


        exampleTweet = "go to http://tblu.co for the best deals on #Kony2012 child soldiers or contact @Kony or user@grapevine.com for more http://t.co/DwC4UWfd";

        System.out.println("Example tweet: "+exampleTweet);

        System.out.println("Post-processing: "+ TweetPreprocessor.removeMentions(exampleTweet));

        System.out.println("Post-processing: "+ TweetPreprocessor.removeURLS(exampleTweet));
    }
}
