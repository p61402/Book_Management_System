import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MediaServer extends Thread {
    private Socket link = null;

    public MediaServer(Socket socket)
    {
        link = socket;
    }

    public void run()
    {
        System.out.println("--------------------------");
        System.out.println(link + " Opening port");

        List<String> MusicList = updateMusicList();
        List<String> VideoList = updateVideoList();

        try
        {
            BufferedReader inStream = new BufferedReader(new InputStreamReader(link.getInputStream(), "UTF-8"));
            ObjectOutputStream outputStream = new ObjectOutputStream(link.getOutputStream());
            String message;

            message = inStream.readLine();

            if (message.equals("TEST")) {
                System.out.println("User Testing");
                PrintWriter out = new PrintWriter(link.getOutputStream(), true);
                out.println("Connection Successful");
            }

            if (message.equals("MUSIC")) {
                System.out.println("Return Music List");
                sendList(outputStream, MusicList);
            }

            if (message.equals("VIDEO")) {
                System.out.println("Return Video List");
                sendList(outputStream, VideoList);
            }

            if (message.length() > 5) {
                System.out.println("Transfer " + message);
                String tokens[] = message.split(" ", 2);
                sendFile(tokens[0], tokens[1], outputStream);
            }
        }
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }

    private static void sendList(ObjectOutputStream objectOutputStream, List<String> list)
    {
        try
        {
            objectOutputStream.writeObject(list);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void sendFile(String fileType, String fileName, ObjectOutputStream outStream) throws IOException
    {
        FileInputStream fileIn = new FileInputStream(fileType + "/" + fileName);
        long fileLen = (new File(fileType + "/" + fileName)).length();
        int intFileLen = (int)fileLen;
        byte[] byteArray = new byte[intFileLen];
        fileIn.read(byteArray);
        fileIn.close();
        outStream.writeObject(byteArray);
        outStream.flush();
    }

    private static List<String> updateMusicList()
    {
        List<String> list = new ArrayList<>();
        final File folder = new File("MUSIC");

        for (final File fileEntry : folder.listFiles())
            if (fileEntry.isFile())
                list.add(fileEntry.getName());

        return list;
    }

    private static List<String> updateVideoList()
    {
        List<String> list = new ArrayList<>();
        final File folder = new File("VIDEO");

        for (final File fileEntry : folder.listFiles())
            if (fileEntry.isFile())
                list.add(fileEntry.getName());

        return list;
    }

    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(1234);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1234.");
            System.exit(-1);
        }

        while (listening)
            new MediaServer(serverSocket.accept()).start();

        serverSocket.close();
    }
}
