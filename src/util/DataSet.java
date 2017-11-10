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
    public ArrayList<List<String>> readCSV(String filename) {
        ArrayList<List<String>> csvList = null;
        try {
            csvList = new ArrayList<>();
            CsvReader reader = new CsvReader(filename, ',');
            List<String> cell;
            while (reader.readRecord()) {
                cell = Arrays.asList(reader.getValues());
                csvList.add(cell);
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return csvList;
    }

    //写入dataSet到.csv文件
    public void writerCSV(ArrayList<List<String>> dataSet, String outFile) {
        try {
            CsvWriter writer = new CsvWriter(outFile, ',', Charset.forName("utf-8"));
            for (List<String> cell : dataSet) {
                writer.writeRecord(cell.toArray(new String[cell.size()]));
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //通过feature名字，获取其索引
    public int getTheIndexByFeatureName(ArrayList<List<String>> dataSet, String featureName) {
        List<String> featureNameSet = dataSet.get(0);
        for (int i = 0; i < featureNameSet.size(); i++) {
            if (featureName.equals(featureNameSet.get(i)))
                return i;
        }
        return -1;
    }

    //通过featureNameSet获取索引集合
    public Set<Integer> getIndexSetByFeatureNames(ArrayList<List<String>> dataSet, String[] featureNameArray) {
        List<String> featureNameSet = dataSet.get(0);
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

    //读取json文件
    public JSONObject readJsonFile(String fileName) {
        String lastStr = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));// 读取原始json文件
            String s = null;
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
}
