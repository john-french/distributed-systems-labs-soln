package ie.gmit.ds;

import java.util.ArrayList;

public class BookStore {

    private ArrayList<Book> bookList;
    private String name;
    private String location;

    public void setBookList(ArrayList<Book> bookList) {
        this.bookList = bookList;
    }

    public ArrayList<Book> getBooksList() {
        return bookList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public void addBookToList(Book book) {
    	bookList.add(book);
    }
}