package communication;

import java.io.Serializable;

public class SocketData implements Serializable
{
    private int type;
    private int cpuFreq;
    public SocketData(int type, int cpuFreq){
        this.type=type;
        this.cpuFreq = cpuFreq;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getCpuFreq() {
        return cpuFreq;
    }

    public void setCpuFreq(int cpuFreq) {
        this.cpuFreq = cpuFreq;
    }
}