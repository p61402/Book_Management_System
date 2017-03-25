import java.io.*;
import java.lang.Object.*;
import java.util.Vector;

public class Client implements Serializable{
	private int ID;
	private String username;
	private Vector<String> books = new Vector<String>();
	
	//Basic Constructor
	public Client(int ID, String username)
	{
		this.ID = ID;
		this.username = username;
	}
	
	//Default Constructor
	public Client()
	{
		
	}
	
	//----Getters----
	public int get_ID()
	{
		return this.ID;
	}
	
	public String get_username()
	{
		return this.username;
	}
	
	public int get_booksize()
	{
		return this.books.size();
	}
	//----Getters----
	
	public void add_book(String newbook)
	{
		this.books.add(newbook);
	}
	
	public boolean del_book(String bookname)
	{
		boolean success = false;
		
		for(int i=0; i<books.size(); i++)
		{
			if(books.get(i).equals(bookname))
			{
				books.remove(i);
				success = true;
			}
		}
		
		return success;
	}
	

	public void print_books()
	{
		//System.out.println(books.size());
		
		for(int i=0; i<books.size(); i++)
		{
			System.out.print(books.get(i));
			
			if(i != books.size() - 1)
			{
				System.out.print(", ");
			}
			else
			{
				System.out.println("");
			}
		}
		
	}
	
	@Override
	public String toString()
	{
		String s = "ID: " + ID + ", username: " + username;
		
		return s;
	}

}
