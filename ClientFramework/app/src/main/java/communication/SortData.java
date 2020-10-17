package communication;

import java.util.Vector;

public class SortData extends SocketData {
    public Vector<Integer> vector;

    public SortData(int type, Vector<Integer> vector) {
        super(type);
        this.vector = vector;
    }
}
