package util;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IOFile {
    public ArrayList<List<String>> readCSV(String filename) {
        ArrayList<List<String>> dataSet = new ArrayList<>();
        try {
            CsvReader reader = new CsvReader(filename, ',');
            List<String> cell;
            while (reader.readRecord()) {
                cell = Arrays.asList(reader.getValues());
                dataSet.add(cell);
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dataSet;
    }

    //写入dataSet到.csv文件
    public void writerStringCSV(ArrayList<List<String>> dataSet, String outFile) {
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
        return new JSONObject(lastStr);
    }
}
