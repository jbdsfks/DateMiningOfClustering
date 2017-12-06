package bean;

public class DataObjectBean {
    private int[] nominalData;
    private float[] numericData;
    private int theClass;
    private int index = -1;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTheClass() {
        return theClass;
    }

    public void setTheClass(int theClass) {
        this.theClass = theClass;
    }
}
