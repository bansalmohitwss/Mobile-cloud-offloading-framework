/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

/**
 *
 * @author bansa
 */
public class DeviceInfoData extends SocketData {
    private String model;
    private int avalHour;
    private int avalMinute;
    private double bidPrice;

    public DeviceInfoData(int type, String model, int avalHour, int avalMinute, double bidPrice) {
        super(type);
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


