package communication;

import java.io.Serializable;
import java.util.*;

public class SocketData implements Serializable
{
    private int type;
    public Vector<Integer> vector;

    public SocketData(int type){
        this.type = type;
    }

    public SocketData(int type,Vector<Integer> vector)
    {
        this.type=type;
        this.vector = vector;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType()
    {
        return type;
    }
}