package ie.gmit.ds;// See README.txt for information and build instructions.

import ie.gmit.ds.AddressBookProtos.AddressBook;
import ie.gmit.ds.AddressBookProtos.Person;

import java.io.*;

class AddPerson {
    final static String ADDRESS_BOOK_FILE = "addressBook.data";

    // This function fills in a Person message based on user input.
    static Person PromptForAddress(BufferedReader stdin, PrintStream stdout) throws IOException {
        Person.Builder person = Person.newBuilder();

        stdout.print("Enter person ID: ");
        person.setId(Integer.valueOf(stdin.readLine()));

        stdout.print("Enter name: ");
        person.setName(stdin.readLine());

        stdout.print("Enter email address (blank for none): ");
        String email = stdin.readLine();
        if (email.length() > 0) {
            person.setEmail(email);
        }

        while (true) {
            stdout.print("Enter a phone number (or leave blank to finish): ");
            String number = stdin.readLine();
            if (number.length() == 0) {
                break;
            }

            Person.PhoneNumber.Builder phoneNumber =
                    Person.PhoneNumber.newBuilder().setNumber(number);

            stdout.print("Is this a mobile, home, or work phone? ");
            String type = stdin.readLine();
            if (type.equals("mobile")) {
                phoneNumber.setType(Person.PhoneType.MOBILE);
            } else if (type.equals("home")) {
                phoneNumber.setType(Person.PhoneType.HOME);
            } else if (type.equals("work")) {
                phoneNumber.setType(Person.PhoneType.WORK);
            } else {
                stdout.println("Unknown phone type.  Using default.");
            }

            person.addPhones(phoneNumber);
        }

        return person.build();
    }

    // Main function:  Reads the entire address book from a file,
    //   adds one person based on user input, then writes it back out to the same
    //   file.
    public static void main(String[] args) throws Exception {

        AddressBook.Builder addressBook = AddressBook.newBuilder();

        // Read the existing address book.
        try {
            FileInputStream input = new FileInputStream(ADDRESS_BOOK_FILE);
            try {
                addressBook.mergeFrom(input);
            } finally {
                try {
                    input.close();
                } catch (Throwable ignore) {
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(ADDRESS_BOOK_FILE + ": File not found.  Creating a new " +
                    "file.");
        }

        // Add an address.
        addressBook.addPeople(PromptForAddress(new BufferedReader(new InputStreamReader(System.in)), System.out));

        // Write the new address book back to disk.
        FileOutputStream output = new FileOutputStream(ADDRESS_BOOK_FILE);
        try {
            addressBook.build().writeTo(output);
        } finally {
            output.close();
        }
    }
}
