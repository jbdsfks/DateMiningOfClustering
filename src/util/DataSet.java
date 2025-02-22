package util;

import java.util.*;

public class DataSet {

    //读取dateSet.csv
    private ArrayList<List<String>> originalSet;
    private List<String> featuresName;

    public DataSet() {
    }

    public ArrayList<List<String>> getOriginalSet() {
        return originalSet;
    }

    public void setOriginalSet(ArrayList<List<String>> originalSet) {
        this.originalSet = originalSet;
    }

    public List<String> getFeaturesName() {
        return featuresName;
    }

    public void setFeaturesName(List<String> featuresName) {
        this.featuresName = featuresName;
    }

    //通过FeatureNameArray获取数据集特征下标值
    public Set<Integer> getIndexSetByFeatureNames(String[] FeatureNameArray) {
        List<String> featureNameSet = this.getOriginalSet().get(0);
        Set<Integer> indexSet = new HashSet<>();
        for (String featureName : FeatureNameArray) {
            if (featureNameSet.contains(featureName))
                indexSet.add(featureNameSet.indexOf(featureName));
        }
        return indexSet;
    }

    //通过feature名字，获取其索引
    public int getTheIndexByFeatureName(String featureName, ArrayList<List<String>> dataSet) {
        List<String> featureNameSet = dataSet.get(0);
        for (int i = 0; i < featureNameSet.size(); i++) {
            if (featureName.equals(featureNameSet.get(i)))
                return i;
        }
        return -1;
    }
}
