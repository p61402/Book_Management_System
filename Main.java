import java.util.*;

import java.io.*;

public class Main {

	public static void main(String args[]) throws FileNotFoundException, IOException, ClassNotFoundException {
		LinkedList <Book> lib = new LinkedList<Book>();
		LinkedList <Client> user = new LinkedList<Client>();
		
		System.out.println("Welcome to Book Management System. Please enter your command.");
		
		Scanner scanner = new Scanner(System.in);
		
		while(true)
		{
			try {
				ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("book.dat"));
				Object o = inputStream.readObject();
				lib = (LinkedList<Book>)o;
				inputStream.close();
		    } catch (IOException ex) {
		    	
		    }
			
			try {
				ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("client.dat"));
				Object o = inputStream.readObject();
				user = (LinkedList<Client>)o;
				inputStream.close();
		    } catch (IOException ex) {
		    	
		    }

			System.out.println("A.Book Management B.Client Service C.Exit");
			String command = scanner.nextLine();
			
			if(command.equals("A") || command.equals("a"))
			{
				System.out.println("<<Book Management Mode>>");
				
				while (true)
				{
					System.out.println("1.Add Book 2.Delete Book 3.Query Book 4.Modify Book 5.Print All Books 6.Back");
					
					int num;
					
					try {
						num = scanner.nextInt();
						scanner.nextLine();
					} catch (Exception e) {
						scanner.next();
						System.out.println("<Invalid Input! Please enter again.>");
						continue;
					}
					
					try {
						ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("book.dat"));
						Object o = inputStream.readObject();
						lib = (LinkedList<Book>)o;
						inputStream.close();
				    } catch (IOException ex) {
				    	
				    }
					
					if (num == 1) {
						//Add
						System.out.println("<Add New Book>");
						
						String bookname;
						String author;
						String publisher;
						float price;

						System.out.println("Enter book name: ");
						bookname = scanner.nextLine();
						System.out.println("Enter author name: ");
						author = scanner.nextLine();
						System.out.println("Enter publisher name: ");
						publisher = scanner.nextLine();
						System.out.println("Enter the price: ");
						price = scanner.nextFloat();
						
						lib.add(new Book(bookname, author, publisher, price));
						
						System.out.println("Add new book successfully!.");
						
					} else if (num == 2) {
						//Delete
						System.out.println("<Delete Book>");
						
						String book;
						boolean found = false;
						System.out.print("Enter the bookname you want to delete: ");
						book = scanner.nextLine();
						for(int i=0; i<lib.size(); i++)
						{
							if(lib.get(i).get_bookname().equals(book))
							{
								lib.remove(i);
								System.out.println("Delete book '" + book + "' successfully!");
								found = true;
								break;
							}
						}
						
						if(!found)
						{
							System.out.println("Book name: '" + book + "' not found!");
						}
						
					} else if (num == 3) {
						//Query
						System.out.println("<Find Book>");
						
						String book;
						boolean found = false;
						System.out.print("Enter the bookname you want to find: ");
						book = scanner.nextLine();
						for(int i=0; i<lib.size(); i++)
						{
							if(lib.get(i).get_bookname().equals(book))
							{
								System.out.println("Here you go! Followings are the details.");
								System.out.println(lib.get(i));
								found = true;
								break;
							}
						}
						
						if(!found)
						{
							System.out.println("Book name: '" + book + "' not found!");
						}
						
					} else if (num == 4) {
						//Modify
						System.out.println("<Modify Book>");
						
						String book;
						boolean found = false;
						System.out.print("Enter the bookname you want to modify: ");
						book = scanner.nextLine();
						for(int i=0; i<lib.size(); i++)
						{
							if(lib.get(i).get_bookname().equals(book))
							{
								while(true)
								{
									System.out.println("1.Book Name 2.Author Name 3.Publisher 4.Price 5.Exit");
									System.out.print("Enter the attribute you want to modify: ");
									
									int c = scanner.nextInt();
									scanner.nextLine();
									
									if(c == 1)
									{
										String bookname;
										System.out.println("Enter book name: ");
										bookname = scanner.nextLine();
										lib.get(i).set_bookname(bookname);
									}
									else if(c == 2)
									{
										String author;
										System.out.println("Enter author name: ");
										author = scanner.nextLine();
										lib.get(i).set_author(author);
									}
									else if(c == 3)
									{
										String publisher;
										System.out.println("Enter publisher name: ");
										publisher = scanner.nextLine();
										lib.get(i).set_publisher(publisher);
									}
									else if(c == 4)
									{
										float price;
										System.out.println("Enter the price: ");
										price = scanner.nextFloat();
										lib.get(i).set_price(price);
									}
									else if(c == 5)
									{
										break;
									}
								}
								
								found = true;
								break;
							}
						}
						
						if(!found)
						{
							System.out.println("Book name: '" + book + "' not found!");
						}

					} else if (num == 5) {
						//Print
						System.out.println("<Print All Books>");
						System.out.println("Book_Name, Author_Name, Publisher, User, Price");
						for (int i=0; i<lib.size(); i++)
						{
							System.out.println(lib.get(i));
						}
						System.out.println();
					} else if (num == 6) {
						//Back
						System.out.println("<Back to Main Menu>");
						break;
					} else {
						System.out.println("<No command '" + num + "' Please enter again.>");
					}
					
			        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("book.dat"));
			        outputStream.writeObject(lib);
			        outputStream.close();
				}
				
				ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("client.dat"));
			    outputStream.writeObject(user);
			    outputStream.close();
			}
			else if(command.equals("B") || command.equals("b"))
			{
				System.out.println("<<Client Service Mode>>");

				while(true)
				{					
					System.out.println("1.Add User 2.Delete User 3.Query 4.Print All Clients 5.Borrow 6.Return 7.Back");
					
					int num;
					
					try {
						num = scanner.nextInt();
						scanner.nextLine();
					} catch (Exception e) {
						scanner.next();
						System.out.println("<Invalid Input! Please enter again.>");
						continue;
					}

					try {
						ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("client.dat"));
						Object o = inputStream.readObject();
						user = (LinkedList<Client>)o;
						inputStream.close();
				    } catch (IOException ex) {
				    	
				    }
					
					if(num == 1)
					{
						//Add
						System.out.println("<Add New User>");
						
						int ID;
						String username;
						System.out.print("Enter the user ID: ");
						ID = scanner.nextInt();
						scanner.nextLine();
						System.out.print("Enter the username: ");
						username = scanner.nextLine();
						
						user.add(new Client(ID, username));
						
						System.out.println("Add new user successfully!.");
					}
					else if(num == 2)
					{
						//Delete
						System.out.println("<Delete User>");
						
						int ID;
						boolean found = false;
						System.out.print("Enter the user ID you want to delete: ");
						ID = scanner.nextInt();
						scanner.nextLine();
						for(int i=0; i<user.size(); i++)
						{
							if(user.get(i).get_ID() == ID)
							{
								user.remove(i);
								System.out.println("Delete user '" + ID + "' successfully!");
								found = true;
								break;
							}
						}
						
						if(!found)
						{
							System.out.println("User ID: '" + ID + "' not found!");
						}
					}
					else if(num == 3)
					{
						//Query
						System.out.println("<Find User>");
						
						int ID;
						boolean found = false;
						System.out.print("Enter your user ID: ");
						ID = scanner.nextInt();
						scanner.nextLine();
						for(int i=0; i<user.size(); i++)
						{
							if(user.get(i).get_ID() == ID)
							{
								System.out.println("Hello '" + user.get(i).get_username() + "'!");
								int numOfbooks = user.get(i).get_booksize();
								
								if(numOfbooks != 0)
								{
									System.out.println("You have borrowed " + numOfbooks + " following books:");
								}
								else
								{
									System.out.println("You havn't borrow any book yet.");
								}
								
								user.get(i).print_books();
								found = true;
								break;
							}
						}
						
						if(!found)
						{
							System.out.println("User ID: '" + ID + "' not found!");
						}
					}
					else if(num == 4)
					{
						//Print
						System.out.println("<Print All Users>");
						
						for (int i=0; i<user.size(); i++)
						{
							System.out.println(user.get(i));
						}
					}
					else if(num == 5)
					{
						//Borrow
						System.out.println("<Borrow Book>");
						
						int ID;
						System.out.print("Enter your user ID: ");
						ID = scanner.nextInt();
						scanner.nextLine();
						
						for(int i=0; i<user.size(); i++)
						{
							if(user.get(i).get_ID() == ID)
							{
								System.out.println("Hello " + user.get(i).get_username() + "!");
								boolean success = false;
								String bookname;
								System.out.print("Please enter the bookname you want to borrow: ");
								bookname = scanner.nextLine();
								//System.out.println(bookname);
								//System.out.println(lib.size());
								for(int j=0; j<lib.size(); j++)
								{		
									//System.out.println(lib.get(j).get_bookname());
									
									if(lib.get(j).get_bookname().equals(bookname) && lib.get(j).get_user() == null)
									{
										user.get(i).add_book(bookname);
										lib.get(j).set_user(user.get(i).get_username());
										success = true;
										System.out.println("Borrow successfully!");
										break;
									}
								}
								
								if(!success)
								{
									System.out.println("Book '" + bookname + "' not found or has been borrowed.");
								}
							}
						}
					}
					else if(num == 6)
					{
						//Return
						System.out.println("<Return Book>");
						
						int ID;
						System.out.print("Enter user ID: ");
						ID = scanner.nextInt();
						scanner.nextLine();
						
						for(int i=0; i<user.size(); i++)
						{
							if(user.get(i).get_ID() == ID)
							{
								System.out.println("Hello " + user.get(i).get_username() + "!");
								System.out.println("Followings are the books you have borrowed.");
								user.get(i).print_books();
								
								String bookname;
								System.out.print("Enter the bookname you want to return: ");
								bookname = scanner.nextLine();
								
								for(int j=0; j<lib.size(); j++)
								{
									if(lib.get(j).get_bookname().equals(bookname))
									{
										if(user.get(i).del_book(bookname))
										{
											lib.get(j).set_user(null);
											System.out.println("Return Successfully!");
										}
										else
										{
											System.out.println("Return Failed!");
										}
									}
								}
							}
						}				
					}
					else if(num == 7)
					{
						//Back
						System.out.println("<Back to Main Menu>");
						
						break;
					}
					else
					{
						System.out.println("<No command '" + num + "' Please enter again.>");
					}
					
					ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("client.dat"));
				    outputStream.writeObject(user);
				    outputStream.close();
				}
			    
		        ObjectOutputStream outputStream1 = new ObjectOutputStream(new FileOutputStream("book.dat"));
		        outputStream1.writeObject(lib);
		        outputStream1.close();
			}
			else if(command.equals("C") || command.equals("c"))
			{				
				System.out.println("Exit successfully!");
				break;
			}
			else
			{
				System.out.println("No command '" + command + "' Please enter again.");
			}
		}
		
		scanner.close();
	}
}