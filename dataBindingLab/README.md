# GMIT Distributed Systems
## Lab 1: Data Binding with Text-Based Formats
### Overview
In this lab we'll explore data binding using text-based formats, XML, JSON and YAML. We'll use the main Java libraries for XML and JSON processing, [JAXB](https://github.com/eclipse-ee4j/jaxb-ri) and [Jackson](https://github.com/FasterXML/jackson).
We'll use these libraries to serialise data to and from formatted text files, and we'll look some of the many annotations they provide which can give you fine-grained control over how the conversion process is handled.

### Lab Procedure
#### Project Import
- Clone this repository, and import the project `dataBindingLab` into your IDE.
The project contains three java classes
    - `Book.java`: a simple POJO representing a book, with 4 `String` fields.
    - `BookStore.java`: a simple POJO representing a book store, with 2 `String` fields and a book list contained in an `ArrayList`
    - `BookMain.java`: a test class with a `main` method; used to run the data marshalling/unmarshalling operations.

#### Marshal Objects to XML
##### Get the JAXB Dependencies
- In order to use JAXB to manage our data, we'll need to add the necessary libraries to our project. This is because we're using a recent version of Java. JAXB was part of the Java SE library up until Java 8, but was deprecated in Java SE 9 and 10, and removed entirely in Java SE 11.

- To include the official JAXB API dependency, add the following inside the `<dependencies>` section of your pom.xml:
```
    <dependency>
        <groupId>javax.xml.bind</groupId>
        <artifactId>jaxb-api</artifactId>
        <version>2.3.1</version>
    </dependency>
```
- Like many Java EE APIs, JAXB is just an API – a specification of a number of classes and interfaces. To use JAXB you not only need to have a dependency on the API, you also need to make sure that an implementation of the API is available in the runtime environment of your program. To use the official reference implementation, add the following dependency:
```
    <dependency>
        <groupId>org.glassfish.jaxb</groupId>
        <artifactId>jaxb-runtime</artifactId>
        <version>2.3.2</version>
        <scope>runtime</scope>
    </dependency>
```
##### Annotate Classes
- Now that we have JAXB available, we can use it to annotate our classes so that they can be parsed to/from XML.
- Add the following imports to the `Book` class:
    ```
    import javax.xml.bind.annotation.XmlElement;
    import javax.xml.bind.annotation.XmlRootElement;
    import javax.xml.bind.annotation.XmlType;
    ```
- JAXB gives us control over how the data is marshalled using annotations. Full details on the available JAXB annoations are available in the [user's guide](https://javaee.github.io/jaxb-v2/doc/user-guide/release-documentation.html#users-guide). Try out some annotations by adding them at the relevant places in the `Book` class:    
    ```
    @XmlRootElement(name = "book")
    // Optionally, can also define the order in which the fields are written
    @XmlType(propOrder = { "author", "name", "publisher", "isbn" })
    public class Book {
    ```

    ```
    //Optionally the field name, e.g. "name", can easily be changed in the XML output:
    @XmlElement(name = "title")
    public String getName() {
    ```
- Add the following imports and annotations in the appropriate places in the `BookStore` class:
    ```
    //This statement means that class "Bookstore.java" is the root-element of our example
    @XmlRootElement(namespace = "ie.gmit.ds")
    public class BookStore {
    ```
    ```
    // XmLElementWrapper generates a wrapper element around XML representation
    @XmlElementWrapper(name = "bookList")
    // XmlElement sets the name of the entities
    @XmlElement(name = "book")
    private ArrayList<Book> bookList;
    ```
- What do these annotations do? Find out by experimenting with them and referring to the JAXB [user's guide](https://javaee.github.io/jaxb-v2/doc/user-guide/release-documentation.html#users-guide).

##### Write to XML file
- Now we're ready to get marshalling! In the `BookMain` class, create the JAXB context and instantiate the marshaller.
    ```
    JAXBContext context = JAXBContext.newInstance(BookStore.class);
    Marshaller jaxbMarshaller = context.createMarshaller();
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    ```
- `BookMain` creates a couple of `Book` instances and adds them to an instance of `BookStore`. Let's use the JAXB marshaller to convert our book store to XML.
    ```
    // Write to System.out
    jaxbMarshaller.marshal(bookstore, System.out);
    // Write to File
    jaxbMarshaller.marshal(bookstore, new File("./bookstore-jaxb.xml"));
    ```
- Open the file bookstore-jaxb.xml and verify that it contains an xml representation of the `BookStore` data objects and the `Book` objects that it references.

#### Marshal Objects to JSON
##### Get the Jackson Dependencies
[Jackson](https://github.com/FasterXML/jackson) is one of the most popular libraries for JSON parsing in Java. To use it, add the following dependency to the `pom.xml`:
```
	<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>
	</dependency>    
```
##### Annotate Classes
Similar to JAXB, Jackson also gives us control over how our objects are represented in JSON. Jackson annotations are described in the library's [documentation](https://github.com/FasterXML/jackson-docs/wiki/JacksonAnnotations). Add the following imports and Jackson annotations to the `Book` class:
```
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
```

```
@XmlRootElement(name = "book")
@JsonRootName(value="book")
// If you want you can define the order in which the fields are written
// Optional
@XmlType(propOrder = { "author", "name", "publisher", "isbn" })
@JsonPropertyOrder({ "author", "name", "publisher", "isbn" })
public class Book {
```

##### Write to JSON file
- In order to map an object to/from JSON, we need an instance of the Jackson class `ObjectMapper`:
```
// Object to JSON
ObjectMapper jsonMarshaller = new ObjectMapper();
jsonMarshaller.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
String jsonBookStore = jsonMarshaller.writeValueAsString(bookstore);
System.out.println(jsonBookStore);
FileWriter fileWriter = new FileWriter("./bookstore.json");
fileWriter.write(jsonBookStore);
fileWriter.close();
```	

- Open the file `bookstore.json`. It's all printed on one line, which isn't great for human-readability. It would be nice if it could be printed....pretty. Let's use the pretty printer!
```
    String jsonBookStore = jsonMarshaller.writerWithDefaultPrettyPrinter().writeValueAsString(bookstore);
```
- Now `bookstore.json` should look more readable.

#### Unmarshal from XML
- Now lets do the reverse: unmarshalling XML back to Java objects. We can use the XML output from earlier, and we'lll need to create a JAXB `Unmarshaller`:
```
Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
BookStore bookstore2 = (BookStore) jaxbUnmarshaller.unmarshal(new FileReader(BOOKSTORE_XML));
```		 
- `bookstore2` should be a fully reconstructed copy of our original `bookstore`. Iterate through the booklist in `bookstore2` and print out the book titles to verify this.

#### Unmarshal from JSON
- You're on your own for this final part. Create 2 data files, one JSON and one YAML each containing a single book in JSON/YAML format (e.g. book1.json, book2.yaml). Using Jackson, parse these two data files and unmarshal them to two new `Book` objects, then add these new objects to the `BookStore`s booklist. Write this updated `BookStore` out to an XML file, and note that the two new books have been added. Find resources on the internet to help you to do this.

### Summary
In this lab we showed how in-program data strcutures (objects) can be freely converted to the main text-based formats (XML, JSON and YAML) and vice-versa, and found out that we can take fine-grained control of this process where necessary using library-specific annotations. Instead of reading/writing from/to a file, we could just as easily be receiving/sending the data across a network connection, the marshalling/unmarshalling process is the same. In future labs we'll use these libraries to handle data in web service APIs.



_Lab adapted from content developed by John Healy and vogella.com_
