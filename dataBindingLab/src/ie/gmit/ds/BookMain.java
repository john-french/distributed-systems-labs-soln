package ie.gmit.ds;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BookMain {

	private static final String BOOKSTORE_XML = "./bookstore-jaxb.xml";

	public static void main(String[] args) {

		ArrayList<Book> bookList = new ArrayList<Book>();

		// create books
		Book book1 = new Book();
		book1.setIsbn("9780099512158");
		book1.setName("The Leopard");
		book1.setAuthor("Giuseppe Tomasi De Lampedusa");
		book1.setPublisher("Vintage Classics");
		bookList.add(book1);

		Book book2 = new Book();
		book2.setIsbn("978-0-6796-4019-6");
		book2.setName("Shadow Country");
		book2.setAuthor("Peter Matthiessen");
		book2.setPublisher("Random House");
		bookList.add(book2);

		// create bookstore, assigning book
		BookStore bookstore = new BookStore();
		bookstore.setName("Charlie Byrne's");
		bookstore.setLocation("Galway");
		bookstore.setBookList(bookList);

	}
}