package cn.edu.neu.alink;

import cn.edu.neu.alink.cons.ClassifierConstant;
import com.alibaba.alink.operator.batch.BatchOperator;
import com.alibaba.alink.operator.batch.source.CsvSourceBatchOp;
import com.alibaba.alink.operator.batch.source.TextSourceBatchOp;
import com.alibaba.alink.pipeline.LocalPredictor;
import com.alibaba.alink.pipeline.Pipeline;
import com.alibaba.alink.pipeline.PipelineModel;
import com.alibaba.alink.pipeline.classification.LogisticRegression;
import com.alibaba.alink.pipeline.classification.NaiveBayesTextClassifier;
import com.alibaba.alink.pipeline.dataproc.Imputer;
import com.alibaba.alink.pipeline.nlp.DocCountVectorizer;
import com.alibaba.alink.pipeline.nlp.Segment;
import com.alibaba.alink.pipeline.nlp.StopWordsRemover;
import org.apache.flink.types.Row;

import java.io.File;
import java.util.List;

/**
 * @author 32098
 */
public class CommentClassifier {
    private static PipelineModel pipelineModel;

    public static void initNaiveBayesModel(){
        pipelineModel = PipelineModel.load(ClassifierConstant.WEIBO_LR_MODEL_PATH);
        if(pipelineModel==null){
            System.err.println("载入模型失败...");
            System.out.println("开始构建模型...");
            BatchOperator<?> sourceBatchOp = getCommentSourceOp();
            Pipeline pipeline = new Pipeline(
                    // 缺失值填充：null
                    new Imputer().setSelectedCols("review").setOutputCols("featureText").setStrategy("value").setFillValue("null"),
                    // 分词操作
                    new Segment().setSelectedCol("featureText"),
                    // 去除停用词
                    new StopWordsRemover().setSelectedCol("featureText"),
                    /*
                     * TF, Term Frequency: 词频，生成特征向量的类型
                     * https://www.yuque.com/pinshu/alink_doc/7a529b8564228c01c31f2fa58c43f782
                     */
                    new DocCountVectorizer().setFeatureType("TF").setSelectedCol("featureText").setOutputCol("featureVector"),
                    new NaiveBayesTextClassifier().setVectorCol("featureVector").setLabelCol("label").setPredictionCol("pred")
            );
            pipelineModel = pipeline.fit(sourceBatchOp);
            pipelineModel.save(ClassifierConstant.WEIBO_NB_MODEL_PATH);
            try {
                // save 方法是将模型连接到了 sink 组件，还需要等到 BatchOperator.execute()，才会真正写出模型
                BatchOperator.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("模型构建成功！");
    }

    public static void initLogisticRegressionModel(){
        pipelineModel = PipelineModel.load(ClassifierConstant.WEIBO_LR_MODEL_PATH);
        if(pipelineModel==null){
            System.err.println("载入模型失败...");
            System.out.println("开始构建模型...");
            BatchOperator<?> sourceBatchOp = getCommentSourceOp();
            Pipeline pipeline = new Pipeline(
                    // 缺失值填充：null
                    new Imputer().setSelectedCols("review").setOutputCols("featureText").setStrategy("value").setFillValue("null"),
                    // 分词操作
                    new Segment().setSelectedCol("featureText"),
                    // 去除停用词
                    new StopWordsRemover().setSelectedCol("featureText"),
                    /*
                     * TF, Term Frequency: 词频，生成特征向量的类型
                     * https://www.yuque.com/pinshu/alink_doc/7a529b8564228c01c31f2fa58c43f782
                     */
                    new DocCountVectorizer().setFeatureType("TF").setSelectedCol("featureText").setOutputCol("featureVector"),
                    new LogisticRegression().setVectorCol("featureVector").setLabelCol("label").setPredictionCol("pred")
            );
            pipelineModel = pipeline.fit(sourceBatchOp);
            pipelineModel.save(ClassifierConstant.WEIBO_NB_MODEL_PATH);
            try {
                // save 方法是将模型连接到了 sink 组件，还需要等到 BatchOperator.execute()，才会真正写出模型
                BatchOperator.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("模型构建成功！");
    }

    private static BatchOperator<?> getCommentSourceOp(){
        return new CsvSourceBatchOp()
                .setFilePath(ClassifierConstant.DATASET_WEIBO_PATH)
                .setSchemaStr("label int, review string")
                .setIgnoreFirstLine(true);
    }

    public static String getClassification(String text){
        if(pipelineModel==null){
            System.err.println("As you didn't call initNaiveBayesModel() or initLogisticRegressionModel() before using getClassification(String text),\n" +
                    "we will call initNaiveBayesModel() to set value for our inner attribute (pipelineModel) to get your text's Classification");
            initNaiveBayesModel();
        }
        try {
            // https://blog.csdn.net/Alink1024/article/details/107813310
            LocalPredictor localPredictor = pipelineModel.collectLocalPredictor("review string");
            // System.out.print(localPredictor.getOutputSchema());
            Row row = Row.of(text);
            return String.valueOf(localPredictor.map(row).getField(3));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        // Can't, we will use LocalPredictor
//        initNaiveBayesModel();
//        System.out.println("------------------------------");
//        TextSourceBatchOp textSourceBatchOp1 = new TextSourceBatchOp()
//                .setFilePath(System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/data/neg.txt".replace("/", File.separator))
//                .setTextCol("review");
//        pipelineModel.transform(textSourceBatchOp1).select(new String[]{"label", "pred", "review"}).sampleWithSize(20).print();
//
//        initLogisticRegressionModel();
//        System.out.println("------------------------------");
//        TextSourceBatchOp textSourceBatchOp2 = new TextSourceBatchOp()
//                .setFilePath(System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/data/pos.txt".replace("/", File.separator))
//                .setTextCol("review");
//        pipelineModel.transform(textSourceBatchOp2).select(new String[]{"label", "pred", "review"}).sampleWithSize(20).print();

        // except to be 1(pos)
        System.out.println(CommentClassifier.getClassification("你真好"));
        // except to be 1(pos)
        System.out.println(CommentClassifier.getClassification("哇哦今年的春夏季衣服不错诶"));
        // except to be 0(neg)
        System.out.println(CommentClassifier.getClassification("去死吧"));


//        TextSourceBatchOp textSourceBatchOp1 = new TextSourceBatchOp()
//                .setFilePath(System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/data/neg.txt".replace("/", File.separator))
//                .setTextCol("review");
//        TextSourceBatchOp textSourceBatchOp2 = new TextSourceBatchOp()
//                .setFilePath(System.getProperty("user.dir")+"/src/main/java/cn/edu/neu/zoom/data/pos.txt".replace("/", File.separator))
//                .setTextCol("review");
//        List<Row> negRows = textSourceBatchOp1.getDataSet().collect();
//        List<Row> posRows = textSourceBatchOp2.getDataSet().collect();

//        int acc = 0;
//        for (Row negRow : negRows) {
//            // except to be 0
//            String text = getClassification((String) negRow.getField(0));
//            System.out.println(text);
//            if("0".equals(text)){
//                acc+=1;
//            }
//        }
//        for (Row posRow : posRows) {
//            // except to be 1
//            String text = getClassification((String) posRow.getField(0));
//            System.out.println(text);
//            if("0".equals(text)){
//                acc+=1;
//            }
//        }
//        System.out.println("Acc: "+(double) acc/(negRows.size()+posRows.size()));
    }
}




