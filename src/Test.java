import bean.DataObjectBean;
import util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Test {

    private ArrayList<String> dataObjectToArrayList(DataObjectBean dataObjectBean) {
        ArrayList<String> data = new ArrayList<>();
        String[] numericSet = new String[dataObjectBean.getNumericData().length];
        for (int j = 0; j < dataObjectBean.getNumericData().length; j++) {
            numericSet[j] = String.valueOf(dataObjectBean.getNumericData()[j]);
        }
        data.addAll(Arrays.asList(numericSet));
        String[] nominalSet = new String[dataObjectBean.getNominalData().length];
        for (int j = 0; j < dataObjectBean.getNominalData().length; j++) {
            nominalSet[j] = String.valueOf(dataObjectBean.getNominalData()[j]);
        }
        data.addAll(Arrays.asList(nominalSet));
        return data;
    }

    private int[][] analysisKPrototypes(Kprototypes kprototypes) {
        int[][] result = new int[kprototypes.getK()][kprototypes.getK()];
        for (int l = 0; l < kprototypes.getK(); l++) {
            int[] centerNominal = kprototypes.getCenter().get(l).getNominalData();
            int centerClass = centerNominal[centerNominal.length - 1];
            ArrayList<DataObjectBean> theCluster = kprototypes.getCluster().get(l);
            for (DataObjectBean dataObjectBean : theCluster) {
                int[] nominal = dataObjectBean.getNominalData();
                int actClass = nominal[nominal.length - 1];
                result[l][actClass - 1]++;
            }
        }
        return result;
    }

    private int[][] analysisKMeans(KMeans kMeans) {
        int[][] result = new int[kMeans.getK()][kMeans.getK()];
        for (int l = 0; l < kMeans.getK(); l++) {
            for (int i = 0; i < kMeans.getCluster().get(l).size(); i++) {
                int actClass = kMeans.getCluster().get(l).get(i).getTheClass();
                result[l][actClass - 1]++;
            }
        }
        return result;
    }

    private void printKPrototypesCluster(Kprototypes kprototypes, IOFile ioFile) {
        int i = 0;
        for (ArrayList<DataObjectBean> cluster : kprototypes.getCluster()) {
            ArrayList<ArrayList<String>> clu = new ArrayList<>();
            i++;
            String outfile = "kp_out_" + i + ".csv";
            ArrayList<String> featureName = new ArrayList<>();
            featureName.addAll(Arrays.asList(kprototypes.getNumericFeature()));
            featureName.addAll(Arrays.asList(kprototypes.getNominalFeature()));
            clu.add(featureName);
            for (DataObjectBean dataObjectBean : cluster) {
                ArrayList<String> data = dataObjectToArrayList(dataObjectBean);
                clu.add(data);
            }
            ioFile.writerStringCSV(clu, outfile);
        }
    }

    private void printKMeansCluster(KMeans kMeans, IOFile ioFile) {
        int i = 0;
        for (ArrayList<DataObjectBean> cluster : kMeans.getCluster()) {
            ArrayList<ArrayList<String>> clu = new ArrayList<>();
            i++;
            String outfile = "km_out_" + i + ".csv";
            for (DataObjectBean dataObjectBean : cluster) {
                ArrayList<String> data = dataObjectToArrayList(dataObjectBean);
                data.add(String.valueOf(dataObjectBean.getTheClass()));
                clu.add(data);
            }
            ioFile.writerStringCSV(clu, outfile);
        }
    }

    private void testKPrototypes(Precessing precessing, int[] randoms_ANN) {
        float a = precessing.getA();
        int n = precessing.getTrainSet().size();
        int k = 2;
//        int m = n;
        float old_re = Float.MIN_VALUE;
        float new_re = 0;
//        while (m > 0) {
            try {
                File writename = new File("kp_test.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
                if (!writename.exists()) {
                    writename.createNewFile();
                }
                FileWriter fileWritter = new FileWriter(writename.getName(), true);
                BufferedWriter out = new BufferedWriter(fileWritter);


                Kprototypes kprototypes = new Kprototypes(k,a,precessing.getNominalFeature(), precessing.getNumericFeature(), precessing.getTrainSet(),randoms_ANN);
                kprototypes.execute();

                int[] randoms = kprototypes.getRandoms();
//
                int[][] result = analysisKPrototypes(kprototypes);

                new_re = 0;
                for (int i = 0; i < result.length; i++) {
                    new_re += result[i][i];
                }
                new_re = 1.0f * new_re / n;
//                if (new_re >= old_re) {
//
                    printKPrototypesCluster(kprototypes, precessing.getIoFile());
//
                System.out.println("初始点：");
                for (int i = 0; i < randoms.length; i++) {
                    out.write(randoms[i] + ",");
                    System.out.print(randoms[i] + ",");
                }
                out.write("\r\n");
                System.out.println();
//
                System.out.println("混淆矩阵：");
//
                    for (int i = 0; i < result.length; i++) {
                        for (int j = 0; j < result.length; j++) {
                            System.out.print(result[i][j] + ",");
                            out.write(result[i][j] + ",");
                        }
                        System.out.println();
                        out.write("\r\n");
                    }

                    System.out.println("ACC:" + new_re);
                    System.out.println();
                    out.write("ACC:" + new_re + "\r\n\r\n");
                    out.flush(); // 把缓存区内容压入文件
                    old_re = new_re;
//                }
//                m--;
                out.close(); // 最后记得关闭文件
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
    }

    private void testKMeans(Precessing precessing, int[] randoms_ANN) {
        int n = precessing.getTrainSet().size();
        int k = 2;
//        int m = n;
        float old_re = Float.MIN_VALUE;
        float new_re = 0;
//        while (m > 0) {
            try {
                File writename = new File("km_test.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
                if (!writename.exists()) {
                    writename.createNewFile();
                }
                FileWriter fileWritter = new FileWriter(writename.getName(), true);
                BufferedWriter out = new BufferedWriter(fileWritter);


                KMeans kMeans = new KMeans(k, precessing.getTrainSet(), randoms_ANN);
                kMeans.execute();
                int[] randoms = kMeans.getRandoms();
//
                int[][] result = analysisKMeans(kMeans);

                new_re = 0;
                for (int i = 0; i < result.length; i++) {
                    new_re += result[i][i];
                }
                new_re = 1.0f * new_re / n;
//                if (new_re >= old_re) {
//
                    printKMeansCluster(kMeans, precessing.getIoFile());
//
                System.out.println("初始点：");
                    for (int i = 0; i < randoms.length; i++) {
                        out.write(randoms[i] + ",");
                        System.out.print(randoms[i] + ",");
                    }
                    out.write("\r\n");
                    System.out.println();
//
                System.out.println("混淆矩阵：");
                    for (int i = 0; i < result.length; i++) {
                        for (int j = 0; j < result.length; j++) {
                            System.out.print(result[i][j] + ",");
                            out.write(result[i][j] + ",");
                        }
                        System.out.println();
                        out.write("\r\n");
                    }

                    System.out.println("ACC:" + new_re);
                    System.out.println();
                    out.write("ACC:" + new_re + "\r\n\r\n");
                    out.flush(); // 把缓存区内容压入文件
                    old_re = new_re;
//                }
//                m--;
                out.close(); // 最后记得关闭文件
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
    }

    private int[] initRandoms(int k, int Max) {
        Random random = new Random();
        int[] randoms = new int[k];
        boolean flag;
        int temp = random.nextInt(Max);
        randoms[0] = temp;
        for (int i = 1; i < k; i++) {
            flag = true;
            while (flag) {
                temp = random.nextInt(Max);
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
        return randoms;
    }


    public static void main(String[] args) {
        String filename = "diabetic_data.csv";
        IOFile ioFile = new IOFile();
        DataSet ds = new DataSet();
        ds.setOriginalSet(ioFile.readCSV(filename));
        ds.setFeaturesName(ds.getOriginalSet().get(0));

        Precessing precessing = new Precessing();
        precessing.setDataSet(ds);
        precessing.setIoFile(ioFile);
        String[] numericFeature = {
                "time_in_hospital",
                "num_lab_procedures",
                "num_procedures",
                "num_medications",
                "number_outpatient",
                "number_emergency",
                "number_inpatient",
                "number_diagnoses"
        };
        String[] nominalFeature = {
                "race",
                "gender",
                "age",
                "admission_type_id",
                "discharge_disposition_id",
                "admission_source_id",
                "diag_1",
                "max_glu_serum",
                "A1Cresult",
                "change",
                "diabetesMed",
                "readmitted"
        };


        int k=2;
        Test test = new Test();
        int[][] randoms_ANNs ={
                {101280,56697},
                {90647,80472},
                {48860, 14124},
                 {73135,76651},
                {22179,41927},
                 {97329,91370},
        };


        for (int[] randoms_ANN:randoms_ANNs){
            precessing.setNominalFeature(nominalFeature);
            precessing.setNumericFeature(numericFeature);
            precessing.kmProcessing();
//        randoms_ANN = test.initRandoms(k, precessing.getTrainSet().size());
            test.testKMeans(precessing, randoms_ANN);

            precessing.setNominalFeature(nominalFeature);
            precessing.setNumericFeature(numericFeature);
            precessing.kpProcessing();
            test.testKPrototypes(precessing, randoms_ANN);
        }



    }
}
