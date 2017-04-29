import java.net.*;
import java.util.Scanner;
import java.io.*;

public class MultiThreadServer extends Thread {
    private Socket link = null;

    public MultiThreadServer(Socket socket) 
    {
    	super("KKMultiServerThread");
    	this.link = socket;
    }

    public void run() 
    {		
		try
		{
			Scanner input = new Scanner(link.getInputStream());
			PrintWriter output = new PrintWriter(link.getOutputStream(), true);
			
			int numMessages = 0;
			String message = input.nextLine();
			
			while (!message.equals("***CLOSE***"))
			{
				System.out.println("Message received.");
				numMessages++;
				output.println("After sorting " + numMessages + ": " + MultiThreadSorting.Sorting(message));
				message = input.nextLine();
			}
			output.println(numMessages + " messages received.");
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
		finally
		{
			try
			{
				System.out.println("\n* Cloing connection... *");
				
				link.close();
			}
			catch(IOException ioEx)
			{
				System.out.println("Unable to disconnect!");
				
				System.exit(1);
			}
		}
    }
}
