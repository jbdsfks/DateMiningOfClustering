import bean.DataObjectBean;
import util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;


public class Test {

    public ArrayList<String> dataObjectToArrayList(DataObjectBean dataObjectBean) {
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

    public int[][] analysis(Kprototypes kprototypes) {
        int[][] result = new int[3][3];
        for (int l = 0; l < kprototypes.getK(); l++) {
            int[] centerNominal = kprototypes.getCenter().get(l).getNominalData();
            int centerClass = centerNominal[centerNominal.length - 1];
            ArrayList<DataObjectBean> theCluster = kprototypes.getCluster().get(l);
            for (DataObjectBean dataObjectBean : theCluster) {
                int[] nominal = dataObjectBean.getNominalData();
                int actClass = nominal[nominal.length - 1];
                result[l][actClass-1]++;
            }
        }
        return result;
    }

    public void printCluster(Kprototypes kprototypes, IOFile ioFile) {
        int i = 0;
        for (ArrayList<DataObjectBean> cluster : kprototypes.getCluster()) {
            ArrayList<ArrayList<String>> clu = new ArrayList<>();
            i++;
            String outfile = "out_" + i + ".csv";
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

    public static void main(String[] args) {
        String filename = "diabetic_data.csv";
        IOFile ioFile = new IOFile();
        DataSet ds = new DataSet();
        ds.setOriginalSet(ioFile.readCSV(filename));
        ds.setFeaturesName(ds.getOriginalSet().get(0));
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
        Precessing precessing = new Precessing();
        precessing.setNominalFeature(nominalFeature);
        precessing.setNumericFeature(numericFeature);
        precessing.setDataSet(ds);
        precessing.setIoFile(ioFile);
        precessing.dataProcessing();
        float a = precessing.getA();
        int[] randoms_ANN = {101280,56697,-1};
        int[][] results_ANN = {{-1,-1,-1},{-1,-1,-1},{-1,-1,-1}};
        int n = precessing.getTrainSet().size();
        int k = 3;


        for (int i=0;i<n;i++){
            int nominalSize = precessing.getTrainSet().get(i).getNominalData().length;
            if (precessing.getTrainSet().get(i).getNominalData()[nominalSize-1]!=3)
                continue;
            randoms_ANN[2] = i;
            try {
                File writename = new File("test.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
                if(!writename.exists()){
                    writename.createNewFile();
                }
                FileWriter fileWritter = new FileWriter(writename.getName(),true);
                BufferedWriter out = new BufferedWriter(fileWritter);
                Kprototypes kprototypes = new Kprototypes(k, a, nominalFeature, numericFeature, precessing.getTrainSet(),randoms_ANN);

                kprototypes.execute();
                Test test = new Test();
                int[] randoms = kprototypes.getRandoms();

                int[][] result = test.analysis(kprototypes);
                float old_re=1.0f*(results_ANN[0][0]+results_ANN[1][1]+results_ANN[2][2])/n;
                float new_re = 1.0f*(result[0][0]+result[1][1]+result[2][2])/n;
                if (new_re>old_re){

                    test.printCluster(kprototypes, ioFile);

                    System.out.println(randoms[0]+","+randoms[1]+","+randoms[2]);
                    out.write(randoms[0]+","+randoms[1]+","+randoms[2]+"\r\n");

                    results_ANN = result;

                    for (int j=0;j<3;j++){
                        System.out.println(result[j][0]+","+result[j][1]+","+result[j][2]);
                        out.write(result[j][0]+","+result[j][1]+","+result[j][2]+"\r\n");
                    }
                    System.out.println("ACC:"+1.0f*(result[0][0]+result[1][1]+result[2][2])/n);
                    System.out.println();
                    out.write("ACC:"+(1.0f*(result[0][0]+result[1][1]+result[2][2])/n)+"\r\n");
                    out.flush(); // 把缓存区内容压入文件
                }
                out.close(); // 最后记得关闭文件
            }catch (Exception e){
                e.printStackTrace();
            }
        }
//        boolean flag = true;
//        while (flag){
//            kprototypes.execute();
//            ArrayList<DataObjectBean> center = kprototypes.getCenter();
//            int j = 0;
//            for (int i=1;i<=k;i++){
//                int sizeOfNominal = center.get(i-1).getNominalData().length;
//                if (center.get(i-1).getNominalData()[sizeOfNominal-1]==i){
//                    j++;
//                }
//            }
//            if (j==k)
//                flag=false;
//        }


//        int n = precessing.getTrainSet().size();
//        int[] results_ANN = {-1,-1,-1,-1};
//        int[] randoms_ANN = {-1,-1};
//        for (int i=0;i<n;i++){
//            for (int j=i+1;j<n;j++){
//                int k = 2;
//                try {
//                    File writename = new File("test.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
//                    if(!writename.exists()){
//                        writename.createNewFile();
//                    }
//                    FileWriter fileWritter = new FileWriter(writename.getName(),true);
//                    BufferedWriter out = new BufferedWriter(fileWritter);
//
//                    float a = precessing.getA();
//                    Kprototypes kprototypes = new Kprototypes(k, a, nominalFeature, numericFeature, precessing.getTrainSet());
//
//                    kprototypes.execute();
//                    Test test = new Test();
//                    test.printCluster(kprototypes, ioFile);
//                    int[] randoms = kprototypes.getRandoms();
//                    int[] result = test.analysis(kprototypes);
//                    if ((1.0f*(result[0]+result[2])/n) > (1.0f*(results_ANN[0]+results_ANN[2])/n)){
//                        System.out.println(randoms[0]+","+randoms[1]);
//                        out.write(randoms[0]+","+randoms[1]+"\r\n");
//                        results_ANN = result;
//                        out.write(result[0]+","+result[1]+","+result[2]+","+result[3]+"\r\n");
//                        System.out.println("ACC:"+1.0f*(result[0]+result[2])/n);
//                        out.write((1.0f*(result[0]+result[2])/n)+"\r\n");
//                        out.flush(); // 把缓存区内容压入文件
//                    }
//                    out.close(); // 最后记得关闭文件
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//        }
    }
}
