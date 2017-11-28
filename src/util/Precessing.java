package util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class Precessing {

    private DataSet dataSet;


    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    //通过索引集合删除dataSet中的相应feature
    public ArrayList<List<String>> getDataByFeatures(Set<Integer> indexs) {
        ArrayList<List<String>> newDataSet = new ArrayList<>();
        for (List<String> oldCell : dataSet.getOriginalSet()) {
            List<String> newCell = new ArrayList<>();
            int k = 0;
            while (k < oldCell.size()) {
                if (!indexs.contains(k)) {
                    k++;
                    continue;
                }
                newCell.add(oldCell.get(k));
                k++;
            }
            newDataSet.add(newCell);
        }
        return newDataSet;
    }

    //将数据集中的diag_1按照groupOfDiagnosis.json规则进行分类
    public ArrayList<List<String>> groupDiagnosis(ArrayList<List<String>> tempDataSet) {
        int j = dataSet.getTheIndexByFeatureName("diag_1");
        IOFile ioFile = new IOFile();
        JSONObject groupOfDiagnosis = ioFile.readJsonFile("json/groupOfDiagnosis.json");
        for (int i = 1; i < tempDataSet.size(); i++) {
            String diag_1 = tempDataSet.get(i).get(j);
            if (diag_1.startsWith("E") || diag_1.startsWith("V")) {
                tempDataSet.get(i).set(j, "Other_6");
            } else if (diag_1.startsWith("?")) {
                tempDataSet.get(i).set(j, "Missing");
            } else {
                double num_diag_1 = Double.parseDouble(diag_1);
                Iterator iterator = groupOfDiagnosis.keys();
                String key;
                JSONObject classDetail;
                while (iterator.hasNext()) {
                    key = (String) iterator.next();
                    classDetail = (JSONObject) groupOfDiagnosis.get(key);
                    double max = (double) classDetail.get("Max");
                    double min = (double) classDetail.get("Min");

                    JSONArray jsonArray = (JSONArray) classDetail.get("Special");
                    Set<Double> special = new HashSet<>();
                    for (int k = 0; k < jsonArray.length(); k++) {
                        special.add(jsonArray.getDouble(k));
                    }
                    if (special.contains(num_diag_1) || (num_diag_1 <= max && num_diag_1 >= min)) {
                        tempDataSet.get(i).set(j, key);
                    }
                }
            }
        }
        return tempDataSet;
    }
//
//    public int intFeacture(String featureName, String featureValue) {
//        Class<?> classType = dataSet.getClass();
//        int i = -1;
//        try {
//            Field field = classType.getDeclaredField(featureName);
//            JSONObject raceJsonObject = (JSONObject) field.get(dataSet);
//            i = (int) raceJsonObject.get(featureValue);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return i;
//    }
//
//    public double getMinDoubleArray(int j){
//        double temp[][] = dataSet.getNumericalMatrix();
//        double min = 0;
//        for (int i= 0;i<temp.length;i++){
//            if (min>temp[i][j]){
//                min = temp[i][j];
//            }
//        }
//        return min;
//    }
//
//    public double getMaxDoubleArray(int j){
//        double temp[][] = dataSet.getNumericalMatrix();
//        double max = 0;
//        for (int i= 0;i<temp.length;i++){
//            if (max<temp[i][j]){
//                max = temp[i][j];
//            }
//        }
//        return max;
//    }
//
//    public void normalizedData(){
//
//        double temp[][] = dataSet.getNumericalMatrix();
//
//        for (int j=0;j<temp[0].length-1;j++){
//            double max = getMaxDoubleArray(j);
//            double min = getMinDoubleArray(j);
//            for (int i=0;i<temp.length;i++){
//                temp[i][j] = (temp[i][j]-min)*(1.0-0.0)/(max-min)+0.0;
//            }
//        }
//        dataSet.setNumericalMatrix(temp);
//    }


    public void dataProcessing() {
        ArrayList<List<String>> tempDataSet;
        String[] FeatureNameArray = {
                "admission_type_id",
                "number_emergency",
                "num_procedures",
                "number_outpatient",
                "time_in_hospital",
                "diag_1",
                "change",
                "discharge_disposition_id",
                "readmitted"
        };
        Set<Integer> FeatureIndexSet = dataSet.getIndexSetByFeatureNames(FeatureNameArray);
        tempDataSet = getDataByFeatures(FeatureIndexSet);
//        dataSet.setTrainSet(getDataByFeatures(FeatureIndexSet));
        tempDataSet = groupDiagnosis(tempDataSet);
        double tempTrainSet[][] = new double[tempDataSet.size()-1][tempDataSet.get(0).size()];
        for (int i = 0; i < tempDataSet.get(0).size(); i++) {
            for (int j = 1; j < tempDataSet.size(); j++) {
                try{

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        dataSet.setTrainSet(tempDataSet);
//        dataSet.setNumericalMatrix(tempTrainSet);
    }

}