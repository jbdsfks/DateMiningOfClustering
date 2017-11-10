import util.DataSet;
import util.Precessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class test {
    public static void main(String[] args) {
        DataSet ds = new DataSet();
        Precessing dataProcessing = new Precessing();

        ArrayList<List<String>> dataSet = ds.readCSV("diabetic_data.csv");

//        ds.printDataSet(dataSet);
        String removeFeatureName = "encounter_id,patient_nbr,weight,metformin,repaglinide,nateglinide," +
                "chlorpropamide,glimepiride,acetohexamide,glipizide,glyburide,tolbutamide,pioglitazone,rosiglitazone," +
                "acarbose,miglitol,troglitazone,tolazamide,examide,citoglipton,insulin,glyburide-metformin," +
                "glipizide-metformin,glimepiride-pioglitazone,metformin-rosiglitazone,metformin-pioglitazone";
        String[] removeFeatureNameArray = removeFeatureName.split(",");
        Set<Integer> removeFeatureIndexSet = ds.getIndexSetByFeatureNames(dataSet, removeFeatureNameArray);


        dataSet = dataProcessing.removeFeatures(dataSet, removeFeatureIndexSet);

        dataSet = dataProcessing.groupDiagnosis(dataSet);

        ds.writerCSV(dataSet, "out.csv");


    }
}
