package communication;

public class TaskData extends SocketData {
   private int finalHour;
   private int finalMinute;

    public TaskData(int type, int cpuFreq, int finalHour, int finalMinute) {
        super(type, cpuFreq);
        this.finalHour = finalHour;
        this.finalMinute = finalMinute;
    }

    public int getFinalHour() {
        return finalHour;
    }

    public int getFinalMinute() {
        return finalMinute;
    }
}
