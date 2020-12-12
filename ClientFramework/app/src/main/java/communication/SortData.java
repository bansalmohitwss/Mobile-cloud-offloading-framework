package communication;

import java.util.Vector;

public class SortData extends TaskData {
    public Vector<Integer> vector;

    public SortData(int type, int cpuFreq, int finalHour, int finalMinute, Vector<Integer> vector) {
        super(type, cpuFreq, finalHour, finalMinute);
        this.vector = vector;
    }
}
