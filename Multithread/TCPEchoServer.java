import java.io.*;
import java.net.*;
import java.util.*;

public class TCPEchoServer {
	private static ServerSocket serverSocket;
	private static final int PORT = 1234;
	
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
	    new MultiThreadServer(serverSocket.accept()).start();

        serverSocket.close();
    }
}

