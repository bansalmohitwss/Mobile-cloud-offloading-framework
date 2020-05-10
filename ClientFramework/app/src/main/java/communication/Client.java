package communication;

import android.util.Log;

import com.example.clientframework.MainActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

    /*
    1. connect method
    2. disconnect
    3. sendData
    4. receiveData
     */

public class Client {

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private boolean isConnected;

    public Client(){
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void connect()
    {
        try {
            Log.i("Client.java  ","going to create sockets");
            socket = new Socket(MainActivity.SERVER_IP,MainActivity.PORT_NO);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Client.java  ","Error in creating sockets");
            isConnected = false;
            return;
        }
        Log.i("Client.java ","Successfully socket created");
        isConnected = true;
    }

    public void disconnect()
    {
        try {
            socket.close();
            objectOutputStream.close();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketData receiveData()
    {
        SocketData socketData = null;
        try {
            socketData = (SocketData)objectInputStream.readObject();
            Log.i("In Client.java",""+socketData);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(socketData == null)
            Log.i("Client.java ","Does not receive message");

        return socketData;
    }

    public void sendData(SocketData socketData)
    {
        try {
            objectOutputStream.writeObject(socketData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
