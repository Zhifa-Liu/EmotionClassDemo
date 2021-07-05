package cn.edu.neu.bayes;

import cn.edu.neu.bayes.cons.ClassifierConstant;
import cn.edu.neu.bayes.nb.NaiveBayesModel;

import java.io.*;

/**
 * @author origin marwincn https://github.com/marwincn/pubsenti-finder
 * @author modify: 320983
 */
public class WeiboCommentClassifier {
    private static NaiveBayesModel naiveBayesModel;

    /**
     * 初始化模型
     *
     */
    public static void init(){
        naiveBayesModel = loadModel(ClassifierConstant.WEIBO_MODEL_PATH);
        if (naiveBayesModel == null) {
            System.err.println("模型文件不存在，模型加载失败!");
            System.out.println("开始训练模型");
            naiveBayesModel = NaiveBayesModelForWeibo.getModel();
            saveModel(naiveBayesModel, ClassifierConstant.WEIBO_MODEL_PATH);
        }
        System.out.println("分类器初始化完成!!!");
    }

    /**
     * 评分
     * @return score=0:中立; score>0: pos; score<负: neg;
     *
     */
    public static double getScore(String text) {
        return naiveBayesModel.nb(text);
    }

    /**
     * 保存模型
     */
    private static void saveModel(Object model, String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(model);
        } catch (Exception e) {
            System.err.println("保存模型失败！");
            System.err.println(e.getMessage());
        }
    }

    /**
     * 载入模型
     */
    private static NaiveBayesModel loadModel(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Object o = ois.readObject();
            System.out.println(o);
            return (NaiveBayesModel) o;
        } catch (FileNotFoundException e) {
            System.err.println("载入模型失败！");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}

