import java.io.*;

public class Book implements Serializable{
	private String bookname;
	private String author;
	private String publisher;
	private String user;
	private float price;
	
	public Book(String bookname, String author, String publisher, float price) {
		this.bookname = bookname;
		this.author = author;
		this.publisher = publisher;
		this.user = null;
		this.price = price;
	}
	
	public Book() {
		
	}
	
	//----Getters----
	public String get_bookname()
	{
		return this.bookname;
	}
	
	public String get_author()
	{
		return this.author;
	}
	
	public String get_publisher()
	{
		return this.publisher;
	}
	
	public String get_user()
	{
		return this.user;
	}
	
	public float get_price()
	{
		return this.price;
	}
	//----Getters----
	
	//----Setters----
	public void set_bookname(String publisher)
	{
		this.bookname = publisher;
	}
	
	public void set_author(String author)
	{
		this.author = author;
	}
	
	public void set_publisher(String publisher)
	{
		this.publisher = publisher;
	}
	
	public void set_user(String user)
	{
		this.user = user;
	}
	
	public void set_price(float price)
	{
		this.price = price;
	}
	//----Setters----
	
	@Override
	public String toString()
	{
		String s = bookname + ", " + author + ", " + publisher + ", " + user + ", " + price;
		return s;
	}
}
