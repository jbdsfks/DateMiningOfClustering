package bean;

public class DataObjectBean {
    private int[] nominalData;
    private float[] numericData;


    public float[] getNumericData() {
        return numericData;
    }

    public void setNominalData(int[] nominalData) {
        this.nominalData = nominalData;
    }

    public int[] getNominalData() {
        return nominalData;
    }

    public void setNumericData(float[] numericData) {
        this.numericData = numericData;
    }
}
