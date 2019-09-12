# Distributed Systems
## Data Binding: Text-Based Formats
- Book example from https://www.vogella.com/tutorials/JAXB/article.html

- Initial project with plain classes
- Add jaxb api import
- Add XML annotations
- run example
    - fails: no implementation
- Add jaxb implementation

- Repeat for json
- Repeat for yaml
- use xjc to create classes based on schema?

## Lab
### Overview
Java Architecture for XML Binding (JAXB) is a Java standard that defines how Java objects are converted from and to XML. It uses a standard set of mappings.

JAXB defines an API for reading and writing Java objects to and from XML documents.
As JAXB is defined via a specification, it is possible to use different implementations for this standard. JAXB defines a service provider which allows the selection of the JAXB implementation.

It applies a lot of defaults thus making reading and writing of XML via Java relatively easy.

### JAXB 2: Java Architecture for XML Binding
JAXB uses annotations to indicate the central elements.


| Annotation | Description |
| --------- | ---------- |
| ``@XmlRootElement(namespace = "namespace")``  |  Define the root element for an XML tree |
| `XmlType(propOrder = { "field2", "field1",.. })`  |  Allows us to define the order in which the fields are written in the XML file |
| `@XmlElement(name = "newName")`  |  Define the XML element which will be used. Only need to be used if the newName is different than the Java class name |

### Lab Procedure
#### Project Import
- Import the project `dataBindingLab` into your IDE.
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
- JAXB gives us control over how the data is marshalled using annotations. Try this out by adding the following annotations at the relevant places in the `Book` class:    
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
Similar to JAXB, Jackson also gives us control over how our objects are represented in JSON. Add the following imports and Jackson annotations to the `Book` class:
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
- Add the following imports and Jackson annotations to the `Bookstore` class:
```

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
#### Marshal Objects to YAML

#### Unmarshal from XML

#### Unmarshal from JSON and YAML