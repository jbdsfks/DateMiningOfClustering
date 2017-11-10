package util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Precessing {

    //通过索引集合删除dataSet中的相应feature
    public ArrayList<List<String>> removeFeatures(ArrayList<List<String>> oldDataSet, Set<Integer> indexs) {
        ArrayList<List<String>> newDataSet = new ArrayList<>();
        for (List<String> oldCell : oldDataSet) {
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
    public ArrayList<List<String>> groupDiagnosis(ArrayList<List<String>> dataSet) {
        DataSet ds = new DataSet();
        int j = ds.getTheIndexByFeatureName(dataSet, "diag_1");
        for (int i = 1; i < dataSet.size(); i++) {
            String diag_1 = dataSet.get(i).get(j);
            if (diag_1.startsWith("E") || diag_1.startsWith("V")) {
                dataSet.get(i).set(j, "other_6");
            } else if (diag_1.startsWith("?")) {
                continue;
            } else {
                double num_diag_1 = Double.parseDouble(diag_1);
                JSONObject groupOfDiagnosis = ds.readJsonFile("groupOfDiagnosis.json");
                Iterator iterator = groupOfDiagnosis.keys();
                String key = "";
                JSONObject classDetail = new JSONObject();
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
                        dataSet.get(i).set(j, key);
                    }
                }
            }
        }

        return dataSet;
    }
}
