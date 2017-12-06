package util;

import bean.DataObjectBean;

import java.util.ArrayList;

public class AGNES {

    private int k;
    private float a;
    private int trainSetLength;// 数据集元素个数，即数据集的长度
    private String[] nominalFeature;
    private String[] numericFeature;
    private ArrayList<DataObjectBean> trainSet;
    private ArrayList<ArrayList<DataObjectBean>> clusters;
    private ArrayList<double[]> differences;


    public AGNES(ArrayList<DataObjectBean> trainSet,int k,float a){
        this.trainSet = trainSet;
        this.trainSetLength = trainSet.size();
        this.k = k;
        this.a = a;
    }

    private void initCluster(){
        clusters = new ArrayList<>();
        for (int i = 0; i < trainSetLength; i++) {
            DataObjectBean dataObjectBean = trainSet.get(i);
            ArrayList<DataObjectBean> arrayList = new ArrayList<>();
            arrayList.add(dataObjectBean);
            clusters.add(arrayList);
        }
    }
    private void initDifference(){
        differences = new ArrayList<>();
    }
    /**
     * 计算两个点之间的欧式距离
     *
     * @param point1 点1
     * @param point2  点2
     * @return 距离
     */
    private float distance(float[] point1, float[] point2) {
        float distance = 0.0f;
        for (int i = 0; i < point1.length; i++) {
            distance = distance + (point1[i] - point2[i]) * (point1[i] - point2[i]);
        }
        distance = (float) Math.sqrt(distance);
        return distance;
    }
    /**
     * 计算两个点之间的相异度
     * 相等部分为0，不等部分为1
     *
     * @param point1 点1
     * @param point2  点2
     * @return 相异度
     */
    private float difference(int[] point1, int[] point2) {
        float difference = 0.0f;
        for (int i = 0; i < point1.length; i++) {
            if (point1[i] != point2[i])
                difference = difference + 1;
        }
        return difference;
    }
    private double proximity(DataObjectBean objectBean1,DataObjectBean objectBean2){
        int[] nominal1 = objectBean1.getNominalData();
        int[] nominal2 = objectBean2.getNominalData();
        float[] numeric1 = objectBean1.getNumericData();
        float[] numeric2 = objectBean2.getNumericData();
        return  distance(numeric1,numeric2) + a*difference(nominal1,nominal2);
    }

    private void computeDifference(){
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i=0;i<trainSetLength;i++){
            for (int j=0;j<i;j++){
                double temp = proximity(trainSet.get(i),trainSet.get(j));
                if (i==j)
                    continue;
                else{
                    if(temp>max)
                        max = temp;
                    if(temp<min)
                        min = temp;
                }

            }
        }
        System.out.println("Min："+min);
        System.out.println("Max："+max);
    }

    private double avgDifference(ArrayList<DataObjectBean> cluster1,ArrayList<DataObjectBean> cluster2){

        double difference = 0;
//        double[][] distance = new double[cluster1.size()][cluster2.size()];
        for (int i=0;i<cluster1.size();i++){
            for (int j=0;j<cluster2.size();j++){
                int index1 = cluster1.get(i).getIndex()-1;
                int index2 = cluster2.get(j).getIndex()-1;
                if (index1<index2){
                    int temp = index2;
                    index2 = index1;
                    index1 = temp;
                }
                difference = difference + differences.get(index1)[index2];
            }
        }
        return difference/(cluster1.size()*cluster2.size());
    }

    private ArrayList<DataObjectBean> unionCluster(ArrayList<DataObjectBean> cluster1,ArrayList<DataObjectBean> cluster2){
        ArrayList<DataObjectBean> union = new ArrayList<>();
        union.addAll(cluster1);
        union.addAll(cluster2);
        return union;
    }


    private void agens(){
        initDifference();
        initCluster();
//        computeDifference();
        int q=trainSetLength;

        while (q>k){
            long startTime = System.currentTimeMillis();

            System.out.println(q);
            ArrayList<ArrayList<DataObjectBean>> tempCluster = clusters;
            int indexOfCluster1 = -1;
            int indexOfCluster2 = -2;
            double differenceOfTwoCluster = Double.MAX_VALUE;
            for (int i=0;i<q;i++){
                for (int j=i+1;j<q;j++){
                    double temp = avgDifference(tempCluster.get(i),tempCluster.get(j));
                    if (differenceOfTwoCluster<temp){
                        differenceOfTwoCluster = temp;
                        indexOfCluster1 = i;
                        indexOfCluster2 = j;
                    }
                }
            }

            ArrayList<DataObjectBean> union = unionCluster(tempCluster.get(indexOfCluster1),tempCluster.get(indexOfCluster2));
            tempCluster.remove(indexOfCluster1);
            tempCluster.remove(indexOfCluster2-1);
            tempCluster.add(union);
            q=tempCluster.size();
            clusters = tempCluster;
            long endTime = System.currentTimeMillis();
            System.out.println("q="+q+ ":"+ (endTime - startTime)/1000.0f
                    + "s");
        }
    }

    public void execute() {
        long startTime = System.currentTimeMillis();

        System.out.println("K-prototypes begins");
        agens();
        long endTime = System.currentTimeMillis();
        System.out.println("K-prototypes running time=" + (endTime - startTime)/1000.0f
                + "s");
        System.out.println("K-prototypes ends");
    }
}
