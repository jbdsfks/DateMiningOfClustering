package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Clustering {

    public double distance_2(double x[], double y[]) {
        double dis = 0;
        for (int i = 0; i < x.length-1; i++) {
            dis += (x[i] - y[i]) * (x[i] - y[i]);
        }
        return dis;
    }

    public int getMinIndex(double x[]) {
        int index = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] < x[index]) {
                index = i; // 保存数组的下标与数组中的元素进行比较
            }
        }
        return index;
    }

    public double[] getClusterMeans(ArrayList<List<Double>> clusterData) {
        int m = clusterData.size();
        int n = clusterData.get(0).size();
        double[] means = new double[n];
        for (int j = 0; j < n; j++) {
            double sum = 0;
            for (int i = 0; i < m; i++) {
                sum += clusterData.get(i).get(j);
            }
            means[j] = sum / m;
        }
        return means;
    }

//    public double E(ArrayList<Cluster> clusteringResult,int k){
//
//        double sum = 0;
//
//        for (int i=0;i<k;i++){
//            double center[] = clusteringResult.get(i).getCenter();
//            ArrayList<List<Double>> clusterData = clusteringResult.get(i).getClusterData();
//            for (List<Double> cell:clusterData){
//                double data[] = new double[cell.size()];
//                for (int n=0;n<cell.size();n++){
//                    data[n] = cell.get(n);
//                }
//                sum+=distance_2(center,data);
//            }
//        }
//        return sum;
//
//    }
//
//    public ArrayList<Cluster>  KNN(int k, double[][] data) {
//        ArrayList<Cluster> clusteringResult = new ArrayList<>();
//
//        Random random = new Random();
//        int i;
//        for (i = 0; i < k; i++) {
//            Cluster cluster = new Cluster();
//            cluster.setClusterData(new ArrayList<>());
//            cluster.setCenter(data[random.nextInt(data.length)]);
//            clusteringResult.add(cluster);
//        }
//        double distToCenter[] = new double[k];
//        double tempErrorSum = 0;
//        double newErrorSum = 1;
//
//        while (tempErrorSum!=newErrorSum) {
//
//            tempErrorSum = newErrorSum;
//
//            for (i = 0; i < k; i++) {
//                clusteringResult.get(i).getClusterData().clear();
//            }
//            for (int m = 0; m < data.length; m++) {
//                for (i = 0; i < k; i++) {
//                    distToCenter[i] = distance_2(clusteringResult.get(i).getCenter(), data[m]);
//                }
//                int clusterNum = getMinIndex(distToCenter);
//                List<Double> cell = new ArrayList<>();
//                for (int n = 0; n < data[m].length; n++) {
//                    cell.add(data[m][n]);
//                }
//                clusteringResult.get(clusterNum).getClusterData().add(cell);
//            }
//
//            for (i = 0; i < k; i++) {
//                ArrayList<List<Double>> clusterData = clusteringResult.get(i).getClusterData();
//                clusteringResult.get(i).setCenter(getClusterMeans(clusterData));
//            }
//
//            newErrorSum = E(clusteringResult,k);
//
//            for (i=0;i<k;i++){
//                System.out.println("第"+i+"簇中心点："+Arrays.toString(clusteringResult.get(i).getCenter()));
//            }
//            System.out.println("原始误差平方和："+tempErrorSum+"\t误差平方和："+newErrorSum);
//        }
//
//
//        return clusteringResult;
//    }
}
