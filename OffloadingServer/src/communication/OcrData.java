package communication;

public class OcrData extends TaskData {
    public byte[] image;
    public String resultText;

    public OcrData(int type, int cpuFreq, int finalHour, int finalMinute, byte[] image, String resultText) {
        super(type, cpuFreq, finalHour, finalMinute);
        this.image = image;
        this.resultText = resultText;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }
}
