package cn.edu.neu.alink;

/**
 * @author 32098
 *
 */
public class Test {
    public static void main(String[] args) {
        // except to be 1(pos)
        System.out.println(CommentClassifier.getClassification("你真好"));
        // except to be 1(pos)
        System.out.println(CommentClassifier.getClassification("哇哦今年的春夏季衣服不错诶"));
        // except to be 0(neg)
        System.out.println(CommentClassifier.getClassification("去死吧"));
    }
}
