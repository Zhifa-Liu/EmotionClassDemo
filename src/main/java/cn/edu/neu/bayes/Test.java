package cn.edu.neu.bayes;

/**
 * @author 32098
 *
 * cn.edu.neu.bayes: 情感分类效果不行，略过即可
 */
public class Test {
    public static void main(String[] args) {
        WeiboCommentClassifier.init();
        System.out.println(WeiboCommentClassifier.getScore("你真好"));
        System.out.println(WeiboCommentClassifier.getScore("去死吧"));
        System.out.println(WeiboCommentClassifier.getScore("哇哦今年的春夏季衣服不错诶"));
    }
}
