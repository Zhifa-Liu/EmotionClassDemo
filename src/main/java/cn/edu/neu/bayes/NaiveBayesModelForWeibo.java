package cn.edu.neu.bayes;

import cn.edu.neu.bayes.cons.ClassifierConstant;
import cn.edu.neu.bayes.nb.NaiveBayesModel;
import cn.edu.neu.bayes.util.SegmentUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * @author 32098
 *
 */
public class NaiveBayesModelForWeibo {
    public static ArrayList<String> posDocs = new ArrayList<>();
    public static ArrayList<String> negDocs = new ArrayList<>();

    // 文档中每个词，及在文档中出现的次数
    public static HashMap<String, Integer> posWordsMap = new HashMap<>();
    public static HashMap<String, Integer> negWordsMap = new HashMap<>();

    // 从词中选出的特征及其卡方值
    public static HashMap<String, BigDecimal> featureMap = new HashMap<>();

    /**
     * 获取模型
     */
    public static NaiveBayesModel getModel() {
        try {
            loadDataSet();
            segmentDocs();
            extractFeatures();
            return new NaiveBayesModel(posDocs.size(), negDocs.size(), posWordsMap, negWordsMap, featureMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载微博数据集
     *
     */
    private static void loadDataSet() throws IOException {
        long start = System.currentTimeMillis();

        FileReader fileReader = new FileReader(ClassifierConstant.DATASET_WEIBO_PATH);
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        // ignore first line
        line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                if("1".equals(line.substring(0,1))){
                    posDocs.add(line.substring(2));
                }else{
                    negDocs.add(line.substring(2));
                }
            }
        }
        System.out.println("PosDocs size: "+posDocs.size());
        System.out.println("NegDocs size: "+posDocs.size());

        System.out.println("微博数据集加载成功，加载耗时：" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 对每个文档进行分词，计算词在所有文档里出现的次数，忽略单个文档里的词频
     *
     */
    private static void segmentDocs() {
        long start = System.currentTimeMillis();

        posDocs.stream().map(SegmentUtil::segment).forEach(words -> {
            for (String word: words) {
                if (posWordsMap.containsKey(word)) {
                    // 忽略了同步问题
                    posWordsMap.put(word, posWordsMap.get(word) + 1);
                } else {
                    posWordsMap.put(word, 1);
                }
            }
        });

        negDocs.stream().map(SegmentUtil::segment).forEach(words -> {
            for (String word: words) {
                if (negWordsMap.containsKey(word)) {
                    // 忽略了同步问题
                    negWordsMap.put(word, negWordsMap.get(word) + 1);
                } else {
                    negWordsMap.put(word, 1);
                }
            }
        });

        System.out.println("文档分词完成，耗时：" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 计算每个词的卡方值，确定特征词
     *
     */
    private static void extractFeatures() {
        long start = System.currentTimeMillis();

        // pos文档数
        int posDocsSize = posDocs.size();
        // neg文档数
        int negDocsSize = negDocs.size();

        Set<String> posWords = posWordsMap.keySet();
        Set<String> negWords = negWordsMap.keySet();

        // 取pos和neg词语集合的并集，得到语料库里所有的非停用词
        Set<String> words = new HashSet<>();
        words.addAll(posWords);
        words.addAll(negWords);

        words.forEach(word -> {
            // 包含word的pos文档数
            double posTimes = posWordsMap.getOrDefault(word, 0);
            // 包含word的neg文档数
            double negTimes = negWordsMap.getOrDefault(word, 0);

            // 计算2x2表格的卡方值
            // todo: improve this arithmetic
            BigDecimal a = new BigDecimal(posTimes);
            BigDecimal b = new BigDecimal(negTimes);
            BigDecimal c = new BigDecimal(posDocsSize - posTimes);
            BigDecimal d = new BigDecimal(negDocsSize - negTimes);

            BigDecimal x = a.multiply(d).subtract(b.multiply(c)).pow(2).multiply(new BigDecimal(posDocsSize + negDocsSize));
            BigDecimal y = a.add(b).multiply(c.add(d)).multiply(a.add(c)).multiply(b.add(d));
            BigDecimal chi = x.divide(y, 3, BigDecimal.ROUND_HALF_UP);

            // 选择95%置信度下的特征词
            if (chi.compareTo(new BigDecimal("3.84")) > 0) {
                featureMap.put(word, chi);
            }
        });

        System.out.println("计算卡方值成功，耗时：" + (System.currentTimeMillis() - start) + "ms");
    }
}



