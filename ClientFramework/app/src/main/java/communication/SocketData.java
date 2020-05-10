package communication;

import java.io.Serializable;

public class SocketData implements Serializable
{

    private int type;

    public SocketData(){
    }

    public SocketData(int type)
    {
        this.type=type;
    }

    public void setType(int type) {

        this.type = type;
    }
    public int getType()
    {
        return type;
    }

}