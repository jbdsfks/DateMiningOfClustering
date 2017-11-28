import util.Kmeans;
import util.Clustering;
import util.DataSet;
import util.Precessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class test {


    public static void main(String[] args) {
        //初始化一个Kmean对象，将k置为10
        Kmeans k=new Kmeans(10);
        ArrayList<float[]> dataSet=new ArrayList<float[]>();

        dataSet.add(new float[]{1,2});
        dataSet.add(new float[]{3,3});
        dataSet.add(new float[]{3,4});
        dataSet.add(new float[]{5,6});
        dataSet.add(new float[]{8,9});
        dataSet.add(new float[]{4,5});
        dataSet.add(new float[]{6,4});
        dataSet.add(new float[]{3,9});
        dataSet.add(new float[]{5,9});
        dataSet.add(new float[]{4,2});
        dataSet.add(new float[]{1,9});
        dataSet.add(new float[]{7,8});
        //设置原始数据集
        k.setDataSet(dataSet);
        //执行算法
        k.execute();
        //得到聚类结果
        ArrayList<ArrayList<float[]>> cluster=k.getCluster();
        //查看结果
        for(int i=0;i<cluster.size();i++)
        {
            k.printDataArray(cluster.get(i), "cluster["+i+"]");
        }
//        DataSet ds = new DataSet();
////        Precessing dataProcessing = new Precessing();
////
////        ds.readCSV("diabetic_data.csv");
////
////        dataProcessing.setDataSet(ds);
////        dataProcessing.dataProcessing();
////
////        ds.writerStringCSV(dataProcessing.getDataSet().getTrainSet(), "out1.csv");
////        ArrayList<List<String>> outData1 = new ArrayList<>();
////        outData1.add(dataProcessing.getDataSet().getTrainSet().get(0));
////        for (int m=1;m<dataProcessing.getDataSet().getNumericalMatrix().length;m++){
////            List<String> cell = new ArrayList<>();
////            for (int n=0;n<dataProcessing.getDataSet().getNumericalMatrix()[0].length;n++){
////                cell.add(Double.toString(dataProcessing.getDataSet().getNumericalMatrix()[m][n]));
////            }
////            outData1.add(cell);
////        }
////        ds.writerStringCSV(outData1,"out2.csv");
//
//
//
//
//
//        double data[][]={
//                {1,1,1},
//                {-12,-10,-30},
//                {1,2,4},
//                {2,2,5},
//                {-4,-3,-9},
//                {5,3,1},
//                {4,4,1},
//                {5,4,1},
//                {-10,10,10}
//        };
//
//
//        int k=2;
//        Clustering clustering = new Clustering();
////        ArrayList<Cluster> clusterResult =  clustering.KNN(k,dataProcessing.getDataSet().getNumericalMatrix());
//
//
//        ArrayList<Cluster> clusterResult = clustering.KNN(k,data);
////        for (int i=0;i<k;i++){
////            ArrayList<List<String>> outData = new ArrayList<>();
////            outData.add(dataProcessing.getDataSet().getTrainSet().get(0));
////            for (int m=1;m<clusterResult.get(i).getClusterData().size()+1;m++){
////                List<String> cell = new ArrayList<>();
////                for (int n=0;n<clusterResult.get(i).getClusterData().get(m-1).size();n++){
////                    cell.add(clusterResult.get(i).getClusterData().get(m-1).get(n).toString());
////                }
////                outData.add(cell);
////            }
////            ds.writerStringCSV(outData,"cluster_"+i+".csv");
////        }
    }
}
