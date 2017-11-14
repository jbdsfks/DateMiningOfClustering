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
    public ArrayList<List<String>> removeFeatures(Set<Integer> indexs) {
        ArrayList<List<String>> newDataSet = new ArrayList<>();
        for (List<String> oldCell : dataSet.getOriginalSet()) {
            List<String> newCell = new ArrayList<>();
            int k = 0;
            while (k < oldCell.size()) {
                if (indexs.contains(k)) {
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
        DataSet ds = new DataSet();
        ds.setOriginalSet(tempDataSet);
        int j = ds.getTheIndexByFeatureName("diag_1");
        JSONObject groupOfDiagnosis = ds.readJsonFile("json/groupOfDiagnosis.json");
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

    public int intFeacture(String featureName, String featureValue) {
        Class<?> classType = dataSet.getClass();
        int i = -1;
        try {
            Field field = classType.getDeclaredField(featureName);
            JSONObject raceJsonObject = (JSONObject) field.get(dataSet);
            i = (int) raceJsonObject.get(featureValue);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }



    public void dataProcessing() {
        ArrayList<List<String>> tempDataSet;
        ArrayList<List<Integer>> tempTrainSet;

        String removeFeatureName = "encounter_id,patient_nbr,weight,payer_code,medical_specialty,metformin," +
                "repaglinide,nateglinide,diag_2,diag_3," +
                "chlorpropamide,glimepiride,acetohexamide,glipizide,glyburide,tolbutamide,pioglitazone,rosiglitazone," +
                "acarbose,miglitol,troglitazone,tolazamide,examide,citoglipton,insulin,glyburide-metformin," +
                "glipizide-metformin,glimepiride-pioglitazone,metformin-rosiglitazone,metformin-pioglitazone";
        String[] removeFeatureNameArray = removeFeatureName.split(",");
        Set<Integer> removeFeatureIndexSet = dataSet.getIndexSetByFeatureNames(removeFeatureNameArray);
        tempDataSet = removeFeatures(removeFeatureIndexSet);
        tempDataSet = groupDiagnosis(tempDataSet);
        tempTrainSet = new ArrayList<>();
        for (int i =0 ;i<tempDataSet.size()-1;i++){
            List<Integer> cell = new ArrayList<>();
            for (int j=0;j<tempDataSet.get(i).size();j++){
                cell.add(0);
            }
            tempTrainSet.add(cell);
        }
        for (int i = 0; i < tempDataSet.get(0).size(); i++) {
            for (int j = 1; j < tempDataSet.size(); j++) {
                try{
                    File file = new File("json/" + tempDataSet.get(0).get(i) + ".json");
                    if (file.exists()) {
                        tempTrainSet.get(j-1).set(i, intFeacture(tempDataSet.get(0).get(i),tempDataSet.get(j).get(i)));
                    } else{
                        tempTrainSet.get(j-1).set(i, Integer.parseInt(tempDataSet.get(j).get(i)));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println(tempDataSet.get(0).get(i)+": "+tempDataSet.get(j).get(i));
                }
            }
        }
        dataSet.setTrainSet(tempTrainSet);
    }

}