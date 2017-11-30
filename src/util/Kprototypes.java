package util;

import bean.DataObjectBean;

import java.util.ArrayList;
import java.util.Random;

/**
 * K-prototypes聚类算法
 */
public class Kprototypes {
    private int k;// 分成多少簇
    private float a; //权重值
    private int m;// 迭代次数
    private int trainSetLength;// 数据集元素个数，即数据集的长度
    private String[] nominalFeature;
    private String[] numericFeature;
    private ArrayList<DataObjectBean> trainSet;
    private ArrayList<DataObjectBean> center;// 中心链表
    private ArrayList<ArrayList<DataObjectBean>> cluster; // 簇
    private ArrayList<Float> jc;// 误差平方和，k越接近numericSetLength，误差越小
    private Random random;

    public ArrayList<ArrayList<DataObjectBean>> getCluster() {
        return cluster;
    }

    public String[] getNominalFeature() {
        return nominalFeature;
    }

    public void setNominalFeature(String[] nominalFeature) {
        this.nominalFeature = nominalFeature;
    }

    public String[] getNumericFeature() {
        return numericFeature;
    }

    public void setNumericFeature(String[] numericFeature) {
        this.numericFeature = numericFeature;
    }

    public void setTrainSet(ArrayList<DataObjectBean> trainSet) {
        this.trainSet = trainSet;
    }

    public Kprototypes(int k, float a,String[] nominalFeature,String[] numericFeature,ArrayList<DataObjectBean> trainSet) {
        if (k <= 0) {
            k = 1;
        }
        if (a <= 0) {
            a = 1;
        }
        this.k = k;
        this.a = a;
        this.nominalFeature=nominalFeature;
        this.numericFeature=numericFeature;
        this.trainSet=trainSet;
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
    private ArrayList<DataObjectBean> initCenters() {
        ArrayList<DataObjectBean> center = new ArrayList<>();
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
    private ArrayList<ArrayList<DataObjectBean>> initCluster() {
        ArrayList<ArrayList<DataObjectBean>> cluster = new ArrayList<>();
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
     * 计算两个点之间的相异度
     *
     * @param element 点1
     * @param center  点2
     * @return 距离
     */
    private float difference(int[] element, int[] center) {
        float distance = 0.0f;
        for (int i = 0; i < element.length; i++) {
            if (element[i] == center[i])
                distance = distance + 1;
        }
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
            for (int j = 0; j < k; j++) {
                int[] nominalCenter = center.get(j).getNominalData();
                int[] nominalData = trainSet.get(i).getNominalData();
                distance[j] = distance[j] + a * difference(nominalData, nominalCenter);
            }
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


    /**
     * 计算误差平方和准则函数方法
     */
    private void countRule() {
        float jcF = 0.0f;
        float numericF;
        float nominalF;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                numericF = errorSquare(cluster.get(i).get(j).getNumericData(), center.get(i).getNumericData());
                nominalF = difference(cluster.get(i).get(j).getNominalData(), center.get(i).getNominalData());
                jcF = jcF + numericF + a * nominalF;
            }
        }
        jc.add(jcF);
    }

    /**
     * 求int数组的最大值及其下标
     *
     * @param ints 数组
     * @return temp :temp[0] = indexOfMax;tmep[1]=Max
     */
    private int[] maxofIntArray(int[] ints){
        int[] temp = new int[2];
        temp[0] = -1;
        temp[1] = -1;
        for (int i=0;i<ints.length;i++){
            if(ints[i]>temp[1]){
                temp[1] = ints[i];
                temp[0] = i;
            }
        }
        return temp;
    }

    private int getModesOfIntArray(int[] ints){
        int[] maxAndIndex = maxofIntArray(ints);
        int[] temps = new int[maxAndIndex[1]+1];
        for (int i=0;i<temps.length;i++){
            temps[i] = 0;
        }
        for (int i:ints){
            temps[i] += 1;
        }
        int[] modes = maxofIntArray(temps);
        return modes[0];
    }

    private int[] modesOfData(ArrayList<DataObjectBean> theCluster){
        ArrayList<int[]> data = new ArrayList<>();
        for (int j=0;j<theCluster.get(0).getNominalData().length;j++){
            int[] row = new int[theCluster.size()];
            for (int i=0;i<theCluster.size();i++){
                row[i] = theCluster.get(i).getNominalData()[j];
            }
            data.add(row);
        }
        int[] modes = new int[data.size()];
        for (int i=0;i<modes.length;i++){
            modes[i] = getModesOfIntArray(data.get(i));
        }
        return modes;
    }

    /**
     * 设置新的簇中心方法
     */
    private void setNewCenter() {
        for (int l = 0; l < k; l++) {
            int n = cluster.get(l).size();
            if (n != 0) {
                DataObjectBean newCenter = new DataObjectBean();
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
                newNominalCenter = modesOfData(cluster.get(l));

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
                if (Math.abs(jc.get(m) - jc.get(m - 1)) <= 0.0001f) {
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