package cn.edu.neu.bayes.cons;

import java.io.File;

/**
 * @author 32098
 */
public class ClassifierConstant {
    public static String WEIBO_MODEL_PATH = null;
    public static String DATASET_WEIBO_PATH = null;

    static {
        WEIBO_MODEL_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/model/self-nb-classifier-for-weibo.ser".replace("/", File.separator);
        DATASET_WEIBO_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/data/weibo_senti_100k.csv".replace("/", File.separator);
    }
}



