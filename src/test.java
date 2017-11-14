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

        ds.readCSV("diabetic_data.csv");
        ArrayList<List<Integer>> initTrainSet = new ArrayList<>();
        for (int i =0 ;i<ds.getOriginalSet().size()-1;i++){
            List<Integer> cell = new ArrayList<>();
            for (int j=0;j<ds.getOriginalSet().get(i).size();j++){
                cell.add(0);
            }
            initTrainSet.add(cell);
        }
        ds.setTrainSet(initTrainSet);

        dataProcessing.setDataSet(ds);
        dataProcessing.dataProcessing();

        ds.writerCSV(dataProcessing.getDataSet().getTrainSet(), "out.csv");


    }
}
