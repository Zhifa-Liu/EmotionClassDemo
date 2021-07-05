package cn.edu.neu.hanlp.cons;

import java.io.File;

/**
 * @author 32098
 */
public class ClassifierConstant {
    public static String WEIBO_MODEL_PATH = null;
    public static String HOTEL_MODEL_PATH = null;
    public static String SOUGOU_MODEL_PATH = null;
    public static String DATASET_WEIBO_PATH = null;
    public static String DATASET_HOTEL_PATH = null;
    public static String DATASET_SOUGOU_PATH = null;

    static {
        WEIBO_MODEL_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/model/nb-classifier-for-weibo.ser".replace("/", File.separator);
        HOTEL_MODEL_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/model/nb-classifier-for-hotel.ser".replace("/", File.separator);
        SOUGOU_MODEL_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/model/nb-classifier-for-sougou.ser".replace("/", File.separator);
        DATASET_WEIBO_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/data/weibo_senti_100k.csv".replace("/", File.separator);
        DATASET_HOTEL_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/data/ChnSentiCorp情感分析酒店评论".replace("/", File.separator);
        DATASET_SOUGOU_PATH = System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/data/搜狗文本分类语料库迷你版".replace("/", File.separator);
    }
}



