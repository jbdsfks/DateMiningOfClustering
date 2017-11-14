package util;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class DataSet {

    //读取dateSet.csv
    private ArrayList<List<String>> originalSet = null;
    private ArrayList<List<Integer>> trainSet = null;


    public JSONObject A1Cresult = readJsonFile("json/A1Cresult.json");
    public JSONObject age = readJsonFile("json/age.json");
    public JSONObject change = readJsonFile("json/change.json");
    public JSONObject diabetesMed = readJsonFile("json/diabetesMed.json");
    public JSONObject diag_1 = readJsonFile("json/diag_1.json");
    public JSONObject gender = readJsonFile("json/gender.json");
    public JSONObject max_glu_serum = readJsonFile("json/max_glu_serum.json");
    public JSONObject race = readJsonFile("json/race.json");
    public JSONObject readmitted = readJsonFile("json/readmitted.json");

    //读取json文件
    public JSONObject readJsonFile(String fileName) {
        String lastStr = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));// 读取原始json文件
            String s;
            while ((s = br.readLine()) != null) {
                lastStr += s;
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject(lastStr);
        return json;
    }

    public ArrayList<List<String>> getOriginalSet() {
        return originalSet;
    }

    public void setOriginalSet(ArrayList<List<String>> originalSet) {
        this.originalSet = originalSet;
    }

    public ArrayList<List<Integer>> getTrainSet() {
        return trainSet;
    }

    public void setTrainSet(ArrayList<List<Integer>> trainSet) {
        this.trainSet = trainSet;
    }


    public void readCSV(String filename) {
        try {
            originalSet = new ArrayList<>();
            CsvReader reader = new CsvReader(filename, ',');
            List<String> cell;
            while (reader.readRecord()) {
                cell = Arrays.asList(reader.getValues());
                originalSet.add(cell);
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //写入dataSet到.csv文件
    public void writerCSV(ArrayList<List<Integer>> dataSet, String outFile) {
        try {
            CsvWriter writer = new CsvWriter(outFile, ',', Charset.forName("utf-8"));
            for (List<Integer> cell : dataSet) {
                List<String> newList = new ArrayList<>(cell.size());
                for (Integer myInt : cell) {
                    newList.add(String.valueOf(myInt));
                }
                writer.writeRecord(newList.toArray(new String[newList.size()]));
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //通过feature名字，获取其索引
    public int getTheIndexByFeatureName(String featureName) {
        List<String> featureNameSet = originalSet.get(0);
        for (int i = 0; i < featureNameSet.size(); i++) {
            if (featureName.equals(featureNameSet.get(i)))
                return i;
        }
        return -1;
    }

    //通过featureNameSet获取索引集合
    public Set<Integer> getIndexSetByFeatureNames(String[] featureNameArray) {
        List<String> featureNameSet = originalSet.get(0);
        Set<Integer> indexSet = new HashSet<>();
        for (String featureName : featureNameArray) {
            if (featureNameSet.contains(featureName))
                indexSet.add(featureNameSet.indexOf(featureName));
        }
        return indexSet;
    }

    public void printDataSet(ArrayList<List<String>> dataSet) {
        for (int i = 0; i < 10; i++) {
            List<String> cell = dataSet.get(i);
            for (String str : cell) {
                System.out.print(str + ",");
            }
            System.out.println();
        }
    }
}
