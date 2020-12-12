package communication;

public class DeviceInfoData extends SocketData {
    private String model;
    private int avalHour;
    private int avalMinute;
    private double bidPrice;

    public DeviceInfoData(int type, int cpuFreq, String model, int avalHour, int avalMinute, double bidPrice) {
        super(type, cpuFreq);
        this.model = model;
        this.avalHour = avalHour;
        this.avalMinute = avalMinute;
        this.bidPrice = bidPrice;
    }

    public String getModel() {
        return model;
    }

    public int getAvalHour() {
        return avalHour;
    }

    public int getAvalMinute() {
        return avalMinute;
    }

    public double getBidPrice() {
        return bidPrice;
    }
}
