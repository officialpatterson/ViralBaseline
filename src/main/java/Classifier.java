import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;

import java.util.Random;


public abstract class Classifier {

    protected FilteredClassifier classifier;

    protected Classifier(weka.classifiers.Classifier c){
        classifier = new FilteredClassifier();
        classifier.setClassifier(c);
        classifier.setFilter(buildFilter());
    }
    protected Classifier(){}

    protected void setClassifier(weka.classifiers.Classifier c){
        classifier = new FilteredClassifier();
        classifier.setClassifier(c);
        classifier.setFilter(buildFilter());
    }
    public void train(Instances trainingData) throws Exception {
        classifier.buildClassifier(trainingData);
    }
    public String classify(Instance instance) {
        try {
            return instance.classAttribute().value((int) classifier.classifyInstance(instance));
        }catch(Exception e){
            return null;
        }
    }

    public Evaluation evaluate(Instances instances) {
        Evaluation eval;
        try {
            eval = new Evaluation(instances);
            eval.crossValidateModel(classifier, instances, 10, new Random(1));
        }catch(Exception e){
            System.out.println("Evaluation error: "+e);
            return null;
        }
        return eval;
    }
    abstract Filter buildFilter();
}
