package util;

import bean.DataObjectBean;
import java.util.ArrayList;

public class KMeans {
    private int k;// 分成多少簇
    private int m;// 迭代次数
    private int trainSetLength;// 数据集元素个数，即数据集的长度
    private ArrayList<DataObjectBean> trainSet;
    private ArrayList<ArrayList<DataObjectBean>> cluster;
    private ArrayList<DataObjectBean> center;
    private ArrayList<Double> jc;// 误差平方和，k越接近numericSetLength，误差越小
    private int[] randoms;

    public int getK() {
        return k;
    }

    public ArrayList<DataObjectBean> getCenter() {
        return center;
    }

    public ArrayList<ArrayList<DataObjectBean>> getCluster() {
        return cluster;
    }

    public int[] getRandoms() {
        return randoms;
    }

    public KMeans(int k, ArrayList<DataObjectBean> trainSet, int[] randoms){
        this.k = k;
        this.trainSet = trainSet;
        this.randoms = randoms;
    }

    private ArrayList<DataObjectBean> initCenters(){
        ArrayList<DataObjectBean> center = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            center.add(trainSet.get(randoms[i]));// 生成初始化中心链表
        }
        return center;
    }

    private ArrayList<ArrayList<DataObjectBean>> initCluster(){
        ArrayList<ArrayList<DataObjectBean>> cluster = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            cluster.add(new ArrayList<>());
        }
        return cluster;
    }

    private void init(){

        trainSetLength = trainSet.size();
        m = 0;
        if (k > trainSetLength) {
            k = trainSetLength;
        }
        center = initCenters();
        cluster = initCluster();
        jc = new ArrayList<Double>();
    }

    private int minDistance(float[] distance) {
        float minDistance = distance[0];
        int minLocation = 0;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i] < minDistance) {
                minDistance = distance[i];
                minLocation = i;
            }
        }

        return minLocation;
    }
    private float distance(float[] element, float[] center) {
        float distance = 0.0f;
        for (int i = 0; i < element.length; i++) {
            distance = distance + (element[i] - center[i]) * (element[i] - center[i]);
        }
        distance = (float) Math.sqrt(distance);
        return distance;
    }
    private void clusterSet() {
        float[] distance = new float[k];
        for (int i = 0; i < trainSetLength; i++) {
            //计算numeric距离
            for (int j = 0; j < k; j++) {
                float[] numericCenter = center.get(j).getNumericData();
                float[] numericData = trainSet.get(i).getNumericData();
                distance[j] = distance(numericData, numericCenter);
            }
            int minLocation = minDistance(distance);
            cluster.get(minLocation).add(trainSet.get(i));// 将当前元素放到最小距离中心相关的簇中
        }
    }
    private void countRule() {
        double jcF = 0.0f;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                jcF = jcF + distance(cluster.get(i).get(j).getNumericData(), center.get(i).getNumericData());
            }
        }
        jc.add(jcF);
    }
    private void setNewCenter() {
        for (int l = 0; l < k; l++) {
            int n = cluster.get(l).size();
            if (n != 0) {
                DataObjectBean newCenter = new DataObjectBean();
                float[] newNumericCenter = new float[center.get(l).getNumericData().length];
                for (int j = 0; j < newNumericCenter.length; j++) {
                    newNumericCenter[j] = 0.0f;
                }
                //更新numeric
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < newNumericCenter.length; j++) {
                        newNumericCenter[j] += cluster.get(l).get(i).getNumericData()[j];
                    }
                }
                // 设置一个平均值
                for (int j = 0; j < newNumericCenter.length; j++) {
                    newNumericCenter[j] /= n;
                }
                newCenter.setNumericData(newNumericCenter);
                center.set(l, newCenter);
            }
        }
    }
    private void kmeans() {
        init();
        while (true) {
            clusterSet();
            countRule();
            if (m != 0) {
//                System.out.println((m) + "次:" + jc.get(m));
                if (Math.abs(jc.get(m) - jc.get(m - 1)) <= 0.0001f) {
                    break;
                }
            }
            setNewCenter();
            cluster.clear();
            cluster = initCluster();
            m++;
        }
//        System.out.println("迭代次数："+m);
    }

    /**
     * 执行算法
     */
    public void execute() {
        long startTime = System.currentTimeMillis();

        System.out.println("K-means begins");
        kmeans();
        long endTime = System.currentTimeMillis();
        System.out.println("K-means running time=" + (endTime - startTime)/1000.0f
                + "s");
        System.out.println("K-means ends");
    }


}
