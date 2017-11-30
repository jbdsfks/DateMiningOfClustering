import bean.DataObjectBean;
import util.*;

import java.util.ArrayList;
import java.util.Arrays;


public class test {


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

        int k = 2;
        float a = 0.5f;

        Kprototypes kprototypes = new Kprototypes(k, a, nominalFeature, numericFeature, precessing.getTrainSet());
        kprototypes.execute();

        int i=0;
        for (ArrayList<DataObjectBean> cluster:kprototypes.getCluster()) {
            ArrayList<ArrayList<String>> clu = new ArrayList<>();
            i++;
            String outfile = "out_"+i+".csv";
            ArrayList<String> featureName = new ArrayList<>();
            featureName.addAll(Arrays.asList(kprototypes.getNumericFeature()));
            featureName.addAll(Arrays.asList(kprototypes.getNominalFeature()));
            clu.add(featureName);
            for (DataObjectBean dataObjectBean:cluster){
                ArrayList<String> data = new ArrayList<>();
                String[] numericSet = new String[kprototypes.getNumericFeature().length];
                for (int j=0;j<kprototypes.getNumericFeature().length;j++){
                    numericSet[j] = String.valueOf(dataObjectBean.getNumericData()[j]);
                }
                data.addAll(Arrays.asList(numericSet));
                String[] nominalSet = new String[kprototypes.getNominalFeature().length];
                for (int j=0;j<kprototypes.getNominalFeature().length;j++){
                    nominalSet[j] = String.valueOf(dataObjectBean.getNominalData()[j]);
                }
                data.addAll(Arrays.asList(nominalSet));
                clu.add(data);
            }
            ioFile.writerStringCSV(clu,outfile);
        }
    }
}
