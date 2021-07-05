package cn.edu.neu.hanlp;

import cn.edu.neu.hanlp.cons.ClassifierConstant;
import com.hankcs.hanlp.classification.classifiers.AbstractClassifier;
import com.hankcs.hanlp.classification.classifiers.NaiveBayesClassifier;
import com.hankcs.hanlp.classification.corpus.FileDataSet;
import com.hankcs.hanlp.classification.corpus.IDataSet;
import com.hankcs.hanlp.classification.models.AbstractModel;
import com.hankcs.hanlp.classification.models.NaiveBayesModel;
import com.hankcs.hanlp.classification.tokenizers.HanLPTokenizer;

import java.io.*;
import java.util.Map;

/**
 * @author 32098
 */
public class HanLpClassifier {
    private static AbstractClassifier classifier = null;

    /**
     *
     * @param dataPath 数据路径
     * @param modelPath 模型路径
     */
    public static void initClassifier(String dataPath, String modelPath){
        AbstractModel model = loadModel(modelPath);
        if(model==null){
            System.out.println("No model find, begin train model!");
            IDataSet dataSet = null;
            try {
                System.out.println(dataPath);

                File f = new File(dataPath);
                if(f.isFile()){
                    BufferedReader reader = new BufferedReader(new FileReader(dataPath));
                    String str;
                    dataSet = new FileDataSet().setTokenizer(new HanLPTokenizer());
                    System.out.println("Prepare dataset!");
                    // ignore first line
                    str = reader.readLine();
                    while ((str=reader.readLine())!=null){
                        dataSet.add(str.substring(0,1), str.substring(2));
                    }
                }else{
                    dataSet = new FileDataSet().setTokenizer(new HanLPTokenizer()).load(dataPath, "UTF-8");
                }
                System.out.println("Dataset prepared!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            classifier = new NaiveBayesClassifier();
            classifier.train(dataSet);
            model = classifier.getModel();
            saveModel(modelPath, model);
        }else{
            System.out.println("NaiveBayesModel init succeeded!");
            classifier = new NaiveBayesClassifier((NaiveBayesModel) model);
        }
    }

    private static void saveModel(String modelPath, AbstractModel model){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath))) {
            oos.writeObject(model);
            System.out.println("Save NaiveBayesModel Succeeded!");
        } catch (Exception e) {
            System.err.println("Save NaiveBayesModel Failed!");
            System.err.println(e.getMessage());
        }
    }

    private static AbstractModel loadModel(String modelPath){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelPath))) {
            Object o = ois.readObject();
            return (AbstractModel) o;
        } catch (FileNotFoundException e) {
            System.err.println("Load NaiveBayesModel Failed(NaiveBayesModel file：" + modelPath+" not Found!)");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static Double getScoreOfWeiboComment(String sentence){
        if(classifier==null){
            System.err.println("Classifier is null, default using weibo comment data to init classifier");
            System.out.println("If you want to use different data to init classifier, call initClassifier first");
            initClassifier(ClassifierConstant.DATASET_WEIBO_PATH, ClassifierConstant.WEIBO_MODEL_PATH);
        }
        Map<String, Double> map = classifier.predict(sentence);
        return map.get("1") - map.get("0");
    }

    public static String getClassification(String sentence) {
        if(classifier==null){
            System.err.println("Classifier is null, default using weibo comment data to init classifier");
            System.out.println("If you want to use different data to init classifier, call initClassifier first");
            initClassifier(ClassifierConstant.DATASET_WEIBO_PATH, ClassifierConstant.WEIBO_MODEL_PATH);
        }
        Map<String, Double> map = classifier.predict(sentence);
        // System.out.println(map);
        return classifier.classify(sentence);
    }
}


