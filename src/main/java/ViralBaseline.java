import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import preprocessing.TweetPreprocessor;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.Null;
import weka.core.Instance;
import weka.core.Instances;

import java.io.*;
import java.util.List;

/**
 * Created by apatterson on 26/11/2015.
 * The current classifer just uses TFIDF and the users followers
 * MUST DO: remove duplicates from the training set as I think that it is affecting the weightings of the terms
 */
public class ViralBaseline {
    public static void main(String[] args){

        //this method Connects to the database and makes an arff file
        ViralBaseline.createARFF();

        Classifier classifier = new BayesClassifier();
        Instances dataSet = null;
        try {

            //load dataset
            dataSet = new Instances(new BufferedReader(new FileReader("tweets.arff")));


            dataSet.setClassIndex(dataSet.numAttributes() - 2); //last attribute is the class


            //split the set into training angd testing sets
            int trainSize = dataSet.numInstances()/2;

            int testSize = dataSet.numInstances() - trainSize;
            Instances train = new Instances(dataSet, 0, trainSize);
            Instances test = new Instances(dataSet, trainSize, dataSet.numInstances()/2);




            //Evaluation e = classifier.evaluate(trainingSet);


            //System.out.println(e.toSummaryString());

            System.out.println("Now running baseline using split set");

            classifier.train(train);

            System.out.println(("Size of Test Set: "+test.numInstances()));
            for(int i=0; i<test.numInstances(); i++){
                String label = classifier.classify(test.instance(i));
                if(label == null){
                    throw new Exception("Label is null");
                }

                if(Integer.parseInt(label) >= 3) {
                    System.out.println("Tweet is viral");
                    System.out.println("\t" +   test.instance(i).stringValue(dataSet.numAttributes() - 1));


                }

            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void createARFF(){
        //create new file
        File file = new File("tweets.arff");

        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile());

        BufferedWriter bw = new BufferedWriter(fw);


        //write header information
        bw.write("@RELATION tweets \n\n");
        bw.write("@ATTRIBUTE follower_count NUMERIC\n");
        bw.write("@ATTRIBUTE retweet_class {0, 1, 2, 3, 4}\n");
        bw.write("@ATTRIBUTE text string\n");
        bw.write("@DATA\n");

        //now for each tweet, create new data entry
        CouchDbClient dbClient;
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName("tweets")
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost("localhost")
                .setPort(5984);

        dbClient = new CouchDbClient(properties);

        List<JsonObject> allDocs = dbClient.view("_all_docs").includeDocs(true).query(JsonObject.class);

            System.out.println("Number of documents matching query: "+allDocs.size());
            for(JsonObject json: allDocs){

                //if document is retweet then discard
                if(json.getAsJsonObject("retweeted_status") == null) {
                    //System.out.println("Found retweet, ignoring...");
                    continue;
                }
                if(json.get("text").getAsString().contains("RT")){
                    //System.out.println("Found text retweet, ignoring...");
                    continue;
                }
                Integer retweets = json.get("retweet_count").getAsInt();

                String text = TweetPreprocessor.removeNewLines(json.get("text").getAsString());
                text = TweetPreprocessor.removeQuotes(text);
                text = "\""+text+"\"";
                String bracket = "0";
                if(retweets == 0)
                    bracket = "1";
                if(retweets > 100)
                    bracket = "2";
                if(retweets > 1000)
                    bracket = "3";
                if(retweets > 1000)
                    bracket = "4";


                bw.write(json.getAsJsonObject("user").get("followers_count")+" "+bracket+" "+text+"\n");
                System.out.println("Creted ARFF file");
            }
        bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(NullPointerException np){
            //don nothing. exception cause by incorrect document format.
        }

    }
}
