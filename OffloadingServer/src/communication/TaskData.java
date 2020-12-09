package communication;

public class TaskData extends SocketData {
   private int finalHour;
   private int finalMinute;

    public TaskData(int type, int finalHour, int finalMinute) {
        super(type);
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
