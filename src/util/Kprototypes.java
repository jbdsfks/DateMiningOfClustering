package util;

import java.util.ArrayList;
import java.util.Random;

/**
 * K-prototypes聚类算法
 */
public class Kprototypes {
    private int k;// 分成多少簇
    private int a; //权重值
    private int m;// 迭代次数
    private int trainSetLength;// 数据集元素个数，即数据集的长度
    private ArrayList<DataObject> trainSet;
    private ArrayList<DataObject> center;// 中心链表
    private ArrayList<ArrayList<DataObject>> cluster; // 簇
    private ArrayList<Float> jc;// 误差平方和，k越接近numericSetLength，误差越小
    private Random random;

    public void setTrainSet(ArrayList<DataObject> trainSet) {
        this.trainSet = trainSet;
    }

    public Kprototypes(int k, int a) {
        if (k <= 0) {
            k = 1;
        }
        this.k = k;
        this.a = a;
    }

    /**
     * 初始化
     */
    private void init() {
        m = 0;
        random = new Random();
        trainSetLength = trainSet.size();
        if (k > trainSetLength) {
            k = trainSetLength;
        }
        center = initCenters();
        cluster = initCluster();
        jc = new ArrayList<Float>();
    }

    /**
     * 初始化中心数据链表，分成多少簇就有多少个中心点
     *
     * @return 中心点集
     */
    private ArrayList<DataObject> initCenters() {
        ArrayList<DataObject> center = new ArrayList<>();
        int[] randoms = new int[k];
        boolean flag;
        int temp = random.nextInt(trainSetLength);
        randoms[0] = temp;
        for (int i = 1; i < k; i++) {
            flag = true;
            while (flag) {
                temp = random.nextInt(trainSetLength);
                int j = 0;
                while (j < i) {
                    if (temp == randoms[j]) {
                        break;
                    }
                    j++;
                }
                if (j == i) {
                    flag = false;
                }
            }
            randoms[i] = temp;
        }
        for (int i = 0; i < k; i++) {
            center.add(trainSet.get(randoms[i]));// 生成初始化中心链表
        }
        return center;
    }

    /**
     * 初始化簇集合
     *
     * @return 一个分为k簇的空数据的簇集合
     */
    private ArrayList<ArrayList<DataObject>> initCluster() {
        ArrayList<ArrayList<DataObject>> cluster = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            cluster.add(new ArrayList<>());
        }
        return cluster;
    }

    /**
     * 计算两个点之间的欧式距离
     *
     * @param element 点1
     * @param center  点2
     * @return 距离
     */
    private float distance(float[] element, float[] center) {
        float distance = 0.0f;
        for (int i = 0; i < element.length; i++) {
            distance = distance + element[i] * element[i] + center[i] * center[i];
        }
        distance = (float) Math.sqrt(distance);
        return distance;
    }

    /**
     * 获取距离集合中最小距离的位置
     *
     * @param distance 距离数组
     * @return 最小距离在距离数组中的位置
     */
    private int minDistance(float[] distance) {
        float minDistance = distance[0];
        int minLocation = 0;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i] < minDistance) {
                minDistance = distance[i];
                minLocation = i;
            } else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
            {
                if (random.nextInt(10) < 5) {
                    minLocation = i;
                }
            }
        }

        return minLocation;
    }

    /**
     * 将当前元素放到最小距离中心相关的簇中
     */
    private void clusterSet() {
        float[] distance = new float[k];
        for (int i = 0; i < trainSetLength; i++) {
            //计算numeric距离
            for (int j = 0; j < k; j++) {
                float[] numericCenter = center.get(j).getNumericData();
                float[] numericData = trainSet.get(i).getNumericData();
                distance[j] = distance(numericData, numericCenter);
            }
            //计算nominal相异度


            int minLocation = minDistance(distance);
            cluster.get(minLocation).add(trainSet.get(i));// 将当前元素放到最小距离中心相关的簇中
        }
    }

    /**
     * 求两点误差平方的方法
     *
     * @param element 点1
     * @param center  点2
     * @return 误差平方
     */
    private float errorSquare(float[] element, float[] center) {
        float distance = 0.0f;
        for (int i = 0; i < element.length; i++) {
            distance = distance + element[i] * element[i] + center[i] * center[i];
        }
        return distance;
    }

    private float difference(int[] element, int[] center){
        return 0;
    }

    /**
     * 计算误差平方和准则函数方法
     */
    private void countRule() {
        float jcF = 0.0f;
        float numericF;
        float nominalF;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                numericF=errorSquare(cluster.get(i).get(j).getNumericData(), center.get(i).getNumericData());
                nominalF = difference(cluster.get(i).get(j).getNominalData(),center.get(i).getNominalData());
                jcF =jcF+ numericF+a*nominalF;
            }
        }
        jc.add(jcF);
    }

    /**
     * 设置新的簇中心方法
     */
    private void setNewCenter() {
        for (int l = 0; l < k; l++) {
            int n = cluster.get(l).size();
            if (n != 0) {
                DataObject newCenter = new DataObject();
                float[] newNumericCenter = new float[center.get(l).getNumericData().length];
                int[] newNominalCenter = new int[center.get(l).getNominalData().length];
                for (int j = 0; j < newNumericCenter.length; j++) {
                    newNumericCenter[j] = 0.0f;
                }
                for (int j = 0; j < newNominalCenter.length; j++) {
                    newNominalCenter[j] = 0;
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

                //更新nominal


                newCenter.setNumericData(newNumericCenter);
                newCenter.setNominalData(newNominalCenter);
                center.set(l, newCenter);
            }
        }
    }

    /**
     * 打印数据，测试用
     *
     * @param dataArray     数据集
     * @param dataArrayName 数据集名称
     */
    public void printDataArray(ArrayList<float[]> dataArray,
                               String dataArrayName) {
        for (int i = 0; i < dataArray.size(); i++) {
            System.out.println("print:" + dataArrayName + "[" + i + "]={"
                    + dataArray.get(i)[0] + "," + dataArray.get(i)[1] + "}");
        }
        System.out.println("===================================");
    }

    /**
     * K-prototypes核心算法过程
     */
    private void KPrototypes() {
        init();
        // 循环分组，直到误差不变为止
        while (true) {
            clusterSet();
            countRule();

            // 误差不变了，分组完成
            if (m != 0) {
                if (jc.get(m) - jc.get(m - 1) == 0) {
                    break;
                }
            }

            setNewCenter();

            m++;
            cluster.clear();
            cluster = initCluster();
        }

    }

    /**
     * 执行算法
     */
    public void execute() {
        long startTime = System.currentTimeMillis();
        System.out.println("K-prototypes begins");
        KPrototypes();
        long endTime = System.currentTimeMillis();
        System.out.println("K-prototypes running time=" + (endTime - startTime)
                + "ms");
        System.out.println("K-prototypes ends");
    }
}