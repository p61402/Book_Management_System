package sample;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MediaClient {
    private static InetAddress host;
    private static final int PORT = 1234;
    private static List<String> list = new ArrayList<>();


    public String Connect(String ip, String message)
    {
        try {
            host = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try
        {
            Socket connection = new Socket(host, PORT);
            ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());
            PrintWriter outStream = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"), true);

            if (message.equals("TEST"))
            {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                outStream.println(message);
                return reader.readLine();
            }

            if (message.equals("MUSIC") || message.equals("VIDEO"))
            {
                outStream.println(message);
                setList(inputStream);
                return "SUCCESS";
            }
            else if (message.length() > 5)
            {
                outStream.println(message);
                getFile(inputStream, message);
                return "SUCCESS";
            }
        }
        catch (IOException | ClassNotFoundException ioEx)
        {
            ioEx.printStackTrace();
        }

        return "Nothing";
    }

    private static void setList(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
    {
        Object object = inputStream.readObject();
        list = (ArrayList<String>)object;
    }

    private static void getFile(ObjectInputStream inputStream, String fileType) throws IOException, ClassNotFoundException
    {
        byte[] byteArray = (byte[])inputStream.readObject();
        FileOutputStream mediaStream;
        String tokens[] = fileType.split(" ", 2);

        mediaStream = new FileOutputStream(tokens[0] + "/" + tokens[1]);

        mediaStream.write(byteArray);
    }

    public List<String> getList()
    {
        return list;
    }
}
