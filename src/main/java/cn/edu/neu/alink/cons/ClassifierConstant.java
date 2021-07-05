package cn.edu.neu.alink.cons;

import java.io.File;

/**
 * @author 32098
 */
public class ClassifierConstant {
    public static String WEIBO_LR_MODEL_PATH = null;
    public static String WEIBO_NB_MODEL_PATH = null;
    public static String DATASET_WEIBO_PATH = null;

    static {
        WEIBO_LR_MODEL_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/model/alink-lr-classifier-for-weibo".replace("/", File.separator);
        WEIBO_NB_MODEL_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/model/alink-nb-classifier-for-weibo".replace("/", File.separator);
        DATASET_WEIBO_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/data/weibo_senti_100k.csv".replace("/", File.separator);
    }
}



