/**
 * Created by apatterson on 26/11/2015.
 */

import weka.classifiers.bayes.NaiveBayes;
import weka.core.stemmers.NullStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;


public class BayesClassifier extends Classifier {


    public BayesClassifier(){
        super(new NaiveBayes());

    }
    protected Filter buildFilter() {
        StringToWordVector filter = new StringToWordVector();
        //lower the document text case


        //use TFIDF vectors and keep the 100,000 most frequent words
        filter.setLowerCaseTokens(true);
        filter.setIDFTransform(true);
        filter.setTFTransform(true);
        filter.setWordsToKeep(1000000);


        //Set Tokenizer
        WordTokenizer tokenizer = new WordTokenizer();

        tokenizer.setDelimiters(".,;:\'\"()0123456789*-+`/@&}\\?!#");
        filter.setTokenizer(tokenizer);



        //Set Stemmer
        Stemmer stemmer = new NullStemmer();
        filter.setStemmer(stemmer);

        //Set Stopword rules


        filter.setUseStoplist(true);
        System.out.println("Stopwords: "+filter.stopwordsTipText());


        //only keep terms that occur in at least 2 documents
        filter.setWordsToKeep(2);

        return filter;
    }

}
