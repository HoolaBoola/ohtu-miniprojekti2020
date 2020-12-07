package ohtu.userinterface;

import ohtu.database.DbCommands;
import ohtu.domain.Blog;
import ohtu.domain.Book;
import ohtu.domain.Movie;
import ohtu.domain.Youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for the application UI
 */
public class UserInterface {

    private final DbCommands db;
    private final BufferedReader br;

    /**
     * Constructor
     *
     * @param dbName Database name for production
     */
    public UserInterface(String dbName) throws SQLException, ClassNotFoundException {
        br = new BufferedReader(new InputStreamReader(System.in));
        db = new DbCommands(dbName);
    }

    /**
     * Constructor for testing
     *
     * @param br Mock command-line reader
     * @param db Test database manager
     */
    protected UserInterface(BufferedReader br, DbCommands db) {
        this.br = br;
        this.db = db;
    }

    /**
     * Launches the application's command-line UI
     */
    public void commandLine() throws IOException, SQLException {
        String help = "\nNumber or name of the command can be used."
                + "\n0 - exit    | Exits the application"
                + "\n1 - help    | Prints help"
                + "\n2 - book    | Stores book"
                + "\n3 - youtube | Stores YouTube link"
                + "\n4 - blog    | Stores blog"
                + "\n5 - movie   | Stores movie"
                + "\n6 - list    | Lists suggestions from specified category"
                + "\n7 - search  | Searches for specified suggestion"
                + "\n8 - delete  | Deletes specified suggestion";

        String categories = "Categories: book, youtube, blog, movie";

        System.out.println(help);

        while (true) {
            String msg = "";

            System.out.println("\n\n\n\nEnter nothing to get help.");
            System.out.print("Command: ");
            String command = br.readLine();

            switch (command.toLowerCase()) {
                case "0":
                case "exit":
                    System.out.println("Exiting..");
                    br.close();
                    return;
                case "":
                case "1":
                case "help":
                    msg = help;
                    break;
                case "2":
                case "book":
                    msg = store(getBook());
                    msg = msg.isEmpty() ? "Book added successfully!" : msg;
                    break;
                case "3":
                case "youtube":
                    msg = store(getYoutube());
                    msg = msg.isEmpty() ? "Youtube link added successfully!" : msg;
                    break;
                case "4":
                case "blog":
                    msg = store(getBlog());
                    msg = msg.isEmpty() ? "Blog added successfully!" : msg;
                    break;
                case "5":
                case "movie":
                    msg = store(getMovie());
                    msg = msg.isEmpty() ? "Movie added successfully!" : msg;
                    break;
                case "6":
                case "list":
                    System.out.println(categories);
                    System.out.print("Enter category to list: ");
                    msg = search(br.readLine(), "");
                    break;
                case "7":
                case "search":
                    System.out.println(categories);
                    System.out.print("Enter category: ");
                    String category = br.readLine();
                    System.out.print("Enter search term: ");
                    String searchTerm = br.readLine();
                    msg = search(category, searchTerm);
                    break;
                case "8":
                case "delete":
                    // String searchTerm = br.readLine();
                    // msg = delete(item);
                    break;
                default:
                    System.out.println(command.toLowerCase());
                    System.out.println("No such command exists. Enter 'help' to get help.");
            }
            System.out.println(msg);
        }
    }

    /**
     * Gets the data from user for the book suggestion in command-line
     *
     * @return Book object or null
     */
    protected Book getBook() throws IOException {
        System.out.println("Enter title*: ");
        String title = br.readLine();
        if (title.isBlank()) {
            System.out.println("Title cannot be blank.");
            return null;
        }

        System.out.println("Enter author*: ");
        String author = br.readLine();
        if (author.isBlank()) {
            System.out.println("Author cannot be blank.");
            return null;
        }

        System.out.println("Enter year: ");
        int year = -1;
        try {
            year = Integer.parseInt(br.readLine());
        } catch (NumberFormatException ignored) {
            System.out.println("Non-integer argument, no year added");
        }

        System.out.println("Enter pages: ");
        int pages = -1;
        try {
            pages = Integer.parseInt(br.readLine());
        } catch (NumberFormatException ignored) {
            System.out.println("Non-integer argument, no page count added");
        }

        System.out.println("Enter ISBN: ");
        String isbn = br.readLine();
        if (isbn.isBlank()) {
            isbn = "";
        }

        return new Book(title, author, year, pages, isbn);
    }

    /**
     * Gets the data from user for the YouTube suggestion in command-line
     *
     * @return Youtube object or null
     */
    protected Youtube getYoutube() throws IOException {
        System.out.println("Enter URL*: ");
        String url = br.readLine();
        if (url.isBlank()) {
            System.out.println("URL cannot be blank.");
            return null;
        }

        System.out.println("Enter title*: ");
        String title = br.readLine();
        if (title.isBlank()) {
            title = "";
        }

        System.out.println("Enter description*: ");
        String description = br.readLine();
        if (description.isBlank()) {
            description = "";
        }

        return new Youtube(url, title, description);
    }

    /**
     * Gets the data from user for the Blog suggestion in command-line
     *
     * @return Blog object or null
     */
    protected Blog getBlog() throws IOException {
        System.out.println("Enter URL*: ");
        String url = br.readLine();
        if (url.isBlank()) {
            System.out.println("URL cannot be blank.");
            return null;
        }

        System.out.println("Enter title*: ");
        String title = br.readLine();
        if (title.isBlank()) {
            title = "";
        }

        System.out.println("Enter writer: ");
        String writer = br.readLine();
        if (writer.isBlank()) {
            writer = "";
        }

        System.out.println("Enter date (year-month-day): ");
        String dateInput = br.readLine();
        LocalDate date;

        if (dateInput.isBlank()) {
            date = LocalDate.now();
        } else {
            try {
                List<Integer> dateArray = Arrays.stream(dateInput.split("-"))
                    .map(Integer::parseInt).collect(Collectors.toList());
                date = LocalDate.of(dateArray.get(0), dateArray.get(1), dateArray.get(2));
            } catch (NumberFormatException e) {
                System.out.println("Date incorrectly formatted! Added current date instead. \n");
                date = LocalDate.now();

            }
        }

        return new Blog(url, title, writer, date);
    }

    /**
     * Gets the data from user for the Movie suggestion in command-line
     *
     * @return Movie object or null
     */
    protected Movie getMovie() throws IOException {
        System.out.println("Enter title*: ");
        String title = br.readLine();
        if (title.isBlank()) {
            System.out.println("Title cannot be blank.");
            return null;
        }

        System.out.println("Enter director: ");
        String director = br.readLine();
        if (director.isBlank()) {
            director = "";
        }

        System.out.println("Enter release year: ");
        int year = -1;
        try {
            year = Integer.parseInt(br.readLine());
        } catch (NumberFormatException ignored) {
        }

        System.out.println("Enter duration (min): ");
        int duration = -1;
        try {
            duration = Integer.parseInt(br.readLine());
        } catch (NumberFormatException ignored) {
        }

        return new Movie(title, director, year, duration);
    }

    /**
     * Searches for the searchTerm in database. If the searchTerm is empty, the
     * method lists all items in the category.
     *
     * @param category Object can be Book, Youtube, Blog, Movie
     * @param searchTerm String used for searching
     * @return formatted String of found items
     */
    protected String search(String category, String searchTerm) throws SQLException {
        StringBuilder output = new StringBuilder();

        List<?> results;
        int[] lengths = {20, 20, 20};

        if (category.equalsIgnoreCase("book")) {
            results = searchTerm.isEmpty() ? db.listBook() : db.searchBook(searchTerm);

            if (results.isEmpty()) {
                return "Nothing found.";
            }

            for (Object o : results) {
                lengths[0] = Math.max(((Book) o).getTitle().length(), lengths[0]);
                lengths[1] = Math.max(((Book) o).getAuthor().length(), lengths[1]);
            }

            for (Object o: results) {
                ((Book) o).setLengths(lengths[0], lengths[1]);
            }

            // Header
            output.append(String.format("%-" + lengths[0] + "s", "Title")).append(" ")
                    .append(String.format("%-" + lengths[1] + "s", "Author")).append(" ")
                    .append(String.format("%-6s", "Year")).append(" ")
                    .append(String.format("%-7s", "Pages")).append(" ")
                    .append("ISBN").append("\n");

        } else if (category.equalsIgnoreCase("youtube")) {
            results = searchTerm.isEmpty() ? db.listYoutube() : db.searchYoutube(searchTerm);

            if (results.isEmpty()) {
                return "Nothing found.";
            }

            for (Object o : results) {
                lengths[0] = Math.max(((Youtube) o).getUrl().length(), lengths[0]);
                lengths[1] = Math.max(((Youtube) o).getTitle().length(), lengths[1]);
            }

            for (Object o: results) {
                ((Youtube) o).setLengths(lengths[0], lengths[1]);
            }

            // Header
            output.append(String.format("%-" + lengths[0] + "s", "URL")).append(" ")
                    .append(String.format("%-" + lengths[1] + "s", "Title")).append(" ")
                    .append(String.format("%-10s", "Created")).append(" ")
                    .append("Description").append("\n");
        } else if (category.equalsIgnoreCase("movie")) {
            results = searchTerm.isEmpty() ? db.listMovie() : db.searchMovie(searchTerm);

            for (Object o : results) {
                lengths[0] = Math.max(((Movie) o).getTitle().length(), lengths[0]);
                lengths[1] = Math.max(((Movie) o).getDirector().length(), lengths[1]);
            }

            for (Object o: results) {
                ((Movie) o).setLengths(lengths[0], lengths[1]);
            }

            // Header
            output.append(String.format("%-" + lengths[0] + "s", "Title")).append(" ")
                    .append(String.format("%-" + lengths[1] + "s", "Director")).append(" ")
                    .append(String.format("%-6s", "Year")).append(" ")
                    .append("Length (min)").append("\n");
        } else if (category.equalsIgnoreCase("blog")) {
            results = searchTerm.isEmpty() ? db.listBlog() : db.searchBlog(searchTerm);

            for (Object o : results) {
                lengths[0] = Math.max(((Blog) o).getUrl().length(), lengths[0]);
                lengths[1] = Math.max(((Blog) o).getTitle().length(), lengths[1]);
                lengths[2] = Math.max(((Blog) o).getWriter().length(), lengths[2]);
            }

            for (Object o: results) {
                ((Blog) o).setLengths(lengths[0], lengths[1], lengths[2]);
            }

            // Header
            output.append(String.format("%-" + lengths[0] + "s", "URL")).append(" ")
                    .append(String.format("%-" + lengths[1] + "s", "Title")).append(" ")
                    .append(String.format("%-" + lengths[2] + "s", "Writer")).append(" ")
                    .append("Date").append("\n");
        } else {
            return "No such category.";
        }

        for (Object o : results) {
            output.append(o.toString());
        }

        return output.toString();
    }

    /**
     * Stores the object (Book, Youtube) to the database
     *
     * @param o Object can be Book or Youtube
     * @return String empty string if storing object was successful, otherwise
     * an error
     */
    protected String store(Object o) throws SQLException {
        if (db.contains(o)) {
            return "The suggestion already exists.";
        }

        if (o != null) {
            db.add(o);
            return "";
        }
        return "An unknown error has occurred.";
    }
}
