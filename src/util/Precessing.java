package util;

import bean.DataObjectBean;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.*;

public class Precessing {

    private DataSet dataSet;
    private IOFile ioFile;
    private String[] nominalFeature;
    private String[] numericFeature;
    private ArrayList<List<String>> numericData;
    private ArrayList<List<String>> nominalData;
    private ArrayList<DataObjectBean> trainSet;

    private float a;


    private void setA(float a) {
        this.a = a;
    }

    public float getA() {
        return a;
    }

    public IOFile getIoFile() {
        return ioFile;
    }

    public void setIoFile(IOFile ioFile) {
        this.ioFile = ioFile;
    }

    private ArrayList<List<String>> getNominalData() {
        return nominalData;
    }

    private ArrayList<List<String>> getNumericData() {
        return numericData;
    }

    public String[] getNumericFeature() {
        return numericFeature;
    }

    public void setNumericFeature(String[] numericFeature) {
        this.numericFeature = numericFeature;
    }

    public String[] getNominalFeature() {
        return nominalFeature;
    }

    public void setNominalFeature(String[] nominalFeature) {
        this.nominalFeature = nominalFeature;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public ArrayList<DataObjectBean> getTrainSet() {
        return trainSet;
    }

    //通过索引集合删除dataSet中的相应feature
    private ArrayList<List<String>> getDataByFeatures(Set<Integer> indexs) {
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
    private int intFeacture(String featureName, String featureValue) {
        Class<?> classType = ioFile.getClass();
        int i = -1;
        try {
            Field field = classType.getDeclaredField(featureName);
            JSONObject raceJsonObject = (JSONObject) field.get(ioFile);
            i = (int) raceJsonObject.get(featureValue);
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }
    //将数据集中的diag_1按照groupOfDiagnosis.json规则进行分类
    private ArrayList<List<String>> groupDiagnosis(ArrayList<List<String>> tempDataSet) {
        int j = dataSet.getTheIndexByFeatureName("diag_1", tempDataSet);
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

    private void changeToTrainSet(){
        int n = getNumericData().size();

        for (int i = 1; i < n; i++) {
            DataObjectBean dataObjectBean = new DataObjectBean();
            int nominalSize = nominalFeature.length;
            int numericSize = numericFeature.length;
            int[] nominalData = new int[nominalSize];
            float[] numericData = new float[numericSize];
            if (nominalSize>0){
                for (int j = 0; j < getNominalData().get(i).size(); j++) {
                    nominalData[j] = Integer.parseInt(getNominalData().get(i).get(j));
                }
            }
            if (numericSize>0){
                for (int j = 0; j < getNumericData().get(i).size(); j++) {
                    numericData[j] = Float.parseFloat(getNumericData().get(i).get(j));
                }
            }
            dataObjectBean.setNominalData(nominalData);
            dataObjectBean.setNumericData(numericData);
            dataObjectBean.setIndex(i);
            if (nominalSize>0){
                dataObjectBean.setTheClass(nominalData[nominalSize-1]);
            }else {
                dataObjectBean.setTheClass((int)numericData[numericSize-1]);
            }

            trainSet.add(dataObjectBean);
        }
    }

    private float getNumericMin(int j) {
        float min = 0;
        for (int i = 0; i < trainSet.size(); i++) {
            if (min > trainSet.get(i).getNumericData()[j]) {
                min = trainSet.get(i).getNumericData()[j];
            }
        }
        return min;
    }

    private float getNumericAve(int j) {
        float total = 0;
        for (int i = 0; i < trainSet.size(); i++) {
            total+=trainSet.get(i).getNumericData()[j];
        }
        return total/trainSet.size();
    }

    private float getNumericMax(int j) {
        float max = 0;
        for (int i = 0; i < trainSet.size(); i++) {
            if (max < trainSet.get(i).getNumericData()[j]) {
                max = trainSet.get(i).getNumericData()[j];
            }
        }
        return max;
    }

    private void normalizedData() {

        int numericSize = numericFeature.length;
        float[] max = new float[numericSize];
        float[] min = new float[numericSize];
        for (int j = 0; j <numericFeature.length; j++) {
            max[j] = getNumericMax(j);
            min[j] = getNumericMin(j);
        }
        for (int i=0;i<trainSet.size();i++){
            for (int j=0;j<trainSet.get(i).getNumericData().length;j++){
                float oldValue = trainSet.get(i).getNumericData()[j];
                float newValue = (oldValue-min[j])*(1.0f-0.0f)/(max[j]-min[j])+0.0f;
                trainSet.get(i).getNumericData()[j] = newValue;
            }
        }
    }

    private double getStandardDeviation(){
        double stand = 0;
        double[] standard = new double[numericFeature.length];
        double[] avgs = new double[numericFeature.length];
        double total;

        for (int j = 0; j <numericFeature.length; j++) {
            total=0;
            avgs[j] = getNumericAve(j);
            for (int i=0;i<trainSet.size();i++){
                total = total+(trainSet.get(i).getNumericData()[j]-avgs[j])*(trainSet.get(i).getNumericData()[j]-avgs[j]);
            }
            standard[j]=Math.sqrt(total/trainSet.size());
        }

        for (double flo:standard){
            stand+=flo;
        }
        return stand/standard.length;

    }

    private void nominalToNumeric(){
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.addAll(Arrays.asList(numericFeature));
        stringArrayList.addAll(Arrays.asList(nominalFeature));
        numericFeature = stringArrayList.toArray(new String[stringArrayList.size()]);
        nominalFeature = new String[0];

        for (int i=0;i<numericData.size();i++){
            numericData.get(i).addAll(nominalData.get(i));
        }
        nominalData.clear();
        nominalData = new ArrayList<>();
    }

    public void kpProcessing() {

        Set<Integer> FeatureIndexSet = dataSet.getIndexSetByFeatureNames(numericFeature);
        numericData = getDataByFeatures(FeatureIndexSet);

        FeatureIndexSet = dataSet.getIndexSetByFeatureNames(nominalFeature);
        nominalData = getDataByFeatures(FeatureIndexSet);
        nominalData = groupDiagnosis(nominalData);

        String[] nominal = {
          "age",
          "change",
          "diabetesMed",
          "A1Cresult",
          "diag_1",
          "gender",
          "max_glu_serum",
          "race",
          "readmitted"
        };

        for (String str:nominal){
            for (int i=1;i<nominalData.size();i++){
                int index = dataSet.getTheIndexByFeatureName(str,nominalData);
                String stringValue = nominalData.get(i).get(index);
                int intValue= intFeacture(str,stringValue);
                nominalData.get(i).set(index,String.valueOf(intValue));
            }
        }
        trainSet = new ArrayList<>();
        changeToTrainSet();
        normalizedData();
        setA((float) getStandardDeviation());
    }
    public void kmProcessing() {

        Set<Integer> FeatureIndexSet = dataSet.getIndexSetByFeatureNames(numericFeature);
        numericData = getDataByFeatures(FeatureIndexSet);

        FeatureIndexSet = dataSet.getIndexSetByFeatureNames(nominalFeature);
        nominalData = getDataByFeatures(FeatureIndexSet);
        nominalData = groupDiagnosis(nominalData);

        String[] nominal = {
                "age",
                "change",
                "diabetesMed",
                "A1Cresult",
                "diag_1",
                "gender",
                "max_glu_serum",
                "race",
                "readmitted"
        };

        for (String str:nominal){
            for (int i=1;i<nominalData.size();i++){
                int index = dataSet.getTheIndexByFeatureName(str,nominalData);
                String stringValue = nominalData.get(i).get(index);
                int intValue= intFeacture(str,stringValue);
                nominalData.get(i).set(index,String.valueOf(intValue));
            }
        }
        nominalToNumeric();
        trainSet = new ArrayList<>();
        changeToTrainSet();
        normalizedData();
        setA((float) getStandardDeviation());
    }

}