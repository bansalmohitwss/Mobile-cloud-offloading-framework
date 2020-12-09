package communication;

import java.util.Vector;

public class SortData extends TaskData {
    public Vector<Integer> vector;

    public SortData(int type, int finalHour, int finalMinute, Vector<Integer> vector) {
        super(type, finalHour, finalMinute);
        this.vector = vector;
    }
}
