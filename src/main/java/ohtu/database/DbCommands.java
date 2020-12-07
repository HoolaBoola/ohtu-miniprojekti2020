package ohtu.database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import ohtu.domain.Blog;
import ohtu.domain.Book;
import ohtu.domain.Movie;
import ohtu.domain.Youtube;

/**
 *
 * @author mahi
 */
public class DbCommands {

    private final Connection db;
    private final Statement s;

    public DbCommands(String databaseAddress) throws SQLException, ClassNotFoundException {
        Database database = new Database(databaseAddress);
        db = database.getConnection();
        s = db.createStatement();
        createTables();
    }

    private void createTables() throws SQLException {

        Statement s = db.createStatement();

        try {
            s.execute("CREATE TABLE Books (id INTEGER PRIMARY KEY, name TEXT NOT NULL, Writer TEXT NOT NULL, year INTEGER, pages INTEGER, isbn TEXT)");

            s.execute("CREATE TABLE Youtube_links (id INTEGER PRIMARY KEY, url TEXT NOT NULL, title TEXT, description TEXT, created DATETIME)");

            s.execute("CREATE TABLE Blogs (id INTEGER PRIMARY KEY, url TEXT NOT NULL, title TEXT NOT NULL, writer TEXT, date DATETIME)");

            s.execute("CREATE TABLE Movies (id INTEGER PRIMARY KEY, title TEXT NOT NULL, director TEXT, year INTEGER, length INTEGER)");

        } catch (org.sqlite.SQLiteException e) {
            // Tables have already been created
        }
    }

    public void add(Object o) throws SQLException {
        if (o instanceof Book) {
            addBook((Book) o);
        } else if (o instanceof Youtube) {
            addYoutube((Youtube) o);
        } else if (o instanceof Blog) {
            addBlog((Blog) o);
        } else {
            addMovie((Movie) o);
        }

    }

    private void addBook(Book b) throws SQLException {
        PreparedStatement p = db.prepareStatement("INSERT INTO Books(name, writer, year, pages, isbn) VALUES (?,?,?,?,?)");

        p.setString(1, b.getTitle());
        p.setString(2, b.getAuthor());
        p.setInt(3, b.getYear());
        p.setInt(4, b.getPages());
        p.setString(5, b.getIsbn());

        p.executeUpdate();

    }

    private void addYoutube(Youtube y) throws SQLException {
        PreparedStatement p = db.prepareStatement("INSERT INTO Youtube_links(url, title, description, created) VALUES (?,?,?,?)");

        p.setString(1, y.getUrl());
        p.setString(2, y.getTitle());
        p.setString(3, y.getDescription());
        p.setDate(4, Date.valueOf(y.getDate()));

        p.executeUpdate();

    }

    private void addBlog(Blog b) throws SQLException {
        PreparedStatement p = db.prepareStatement("INSERT INTO Blogs(url, title, writer, date) VALUES (?,?,?,?)");

        p.setString(1, b.getUrl());
        p.setString(2, b.getTitle());
        p.setString(3, b.getWriter());
        p.setDate(4, Date.valueOf(b.getDate()));

        p.executeUpdate();

    }

    private void addMovie(Movie m) throws SQLException {
        PreparedStatement p = db.prepareStatement("INSERT INTO Movies(title, director, year, length) VALUES (?,?,?,?)");

        p.setString(1, m.getTitle());
        p.setString(2, m.getDirector());
        p.setInt(3, m.getYear());
        p.setInt(4, m.getLength());

        p.executeUpdate();

    }

    public ArrayList<Book> listBook() {
        ArrayList<Book> books = new ArrayList<>();

        try {
            ResultSet r = s.executeQuery("SELECT * FROM Books");

            while (r.next()) {
                books.add(new Book(
                        r.getString("name"),
                        r.getString("writer"),
                        r.getInt("year"),
                        r.getInt("pages"),
                        r.getString("isbn")));
            }

        } catch (Exception ignored) {
        }

        return books;
    }

    public ArrayList<Youtube> listYoutube() {
        ArrayList<Youtube> youtubeLinks = new ArrayList<>();

        try {
            ResultSet r = s.executeQuery("SELECT * FROM Youtube_links");

            while (r.next()) {
                Youtube youtubeLink = new Youtube(
                        r.getString("url"),
                        r.getString("title"),
                        r.getString("description"));

                youtubeLink.setDate(r.getDate("created").toLocalDate());
                youtubeLinks.add(youtubeLink);
            }
        } catch (Exception ignored) {
        }

        return youtubeLinks;
    }

    public ArrayList<Blog> listBlog() {
        ArrayList<Blog> blogs = new ArrayList<>();

        try {
            ResultSet r = s.executeQuery("SELECT * FROM Blogs");

            while (r.next()) {
                Blog blog = new Blog(
                    r.getString("url"),
                    r.getString("title"),
                    r.getString("writer"),
                    r.getDate("date").toLocalDate()
                );

                blogs.add(blog);
            }
        } catch (Exception ignored) {
        }
        
        return blogs;
    }

    public ArrayList<Movie> listMovie() {
        ArrayList<Movie> movies = new ArrayList<>();

        return movies;
    }

    public ArrayList<Book> searchBook(String searchTerm) throws SQLException {
        ArrayList<Book> foundBooks = new ArrayList<>();

        PreparedStatement p = db.prepareStatement("SELECT * FROM Books WHERE name LIKE ? OR writer LIKE ?");
        p.setString(1, '%' + searchTerm + '%');
        p.setString(2, '%' + searchTerm + '%');
        ResultSet r = p.executeQuery();

        while (r.next()) {
            foundBooks.add(new Book(
                    r.getString("name"),
                    r.getString("writer"),
                    r.getInt("year"),
                    r.getInt("pages"),
                    r.getString("isbn")
            ));
        }

        return foundBooks;
    }

    public ArrayList<Youtube> searchYoutube(String searchTerm) throws SQLException {
        ArrayList<Youtube> foundYoutubeLinks = new ArrayList<>();

        PreparedStatement p = db.prepareStatement("SELECT * FROM Youtube_links WHERE title LIKE ?");
        p.setString(1, '%' + searchTerm + '%');

        ResultSet r = p.executeQuery();

        while (r.next()) {
            Youtube youtubeLink = new Youtube(
                    r.getString("url"),
                    r.getString("title"),
                    r.getString("description")
            );
            youtubeLink.setDate(r.getDate("created").toLocalDate());
            foundYoutubeLinks.add(youtubeLink);
        }

        return foundYoutubeLinks;
    }

    public ArrayList<Blog> searchBlog(String searchTerm) throws SQLException {
        ArrayList<Blog> foundBlogs = new ArrayList<>();

        PreparedStatement p = db.prepareStatement("SELECT * FROM Blogs WHERE title LIKE ? OR writer LIKE ?");
        p.setString(1, '%' + searchTerm + '%');
        p.setString(2, '%' + searchTerm + '%');
        ResultSet r = p.executeQuery();

        while (r.next()) {
            foundBlogs.add(new Blog(
                    r.getString("url"),
                    r.getString("title"),
                    r.getString("writer"),
                    r.getDate("date").toLocalDate()
            ));
        }

        return foundBlogs;
    }

    public ArrayList<Movie> searchMovie(String searchTerm) throws SQLException {
        ArrayList<Movie> foundMovies = new ArrayList<>();

        PreparedStatement p = db.prepareStatement("SELECT * FROM Movies WHERE title LIKE ? OR director LIKE ?");
        p.setString(1, '%' + searchTerm + '%');
        p.setString(2, '%' + searchTerm + '%');
        ResultSet r = p.executeQuery();

        while (r.next()) {
            foundMovies.add(new Movie(
                    r.getString("title"),
                    r.getString("director"),
                    r.getInt("year"),
                    r.getInt("length")
            ));
        }

        return foundMovies;
    }

    public void removeTable(String name) throws SQLException {
        PreparedStatement p = db.prepareStatement("DROP TABLE " + name);
        p.executeUpdate();
    }

    public boolean contains(Object o) throws SQLException {
        if (o instanceof Book) {
            return containsBook((Book) o);
        }

        if (o instanceof Youtube) {
            return containsYoutube((Youtube) o);
        }

        return false;
    }

    protected boolean containsBook(Book b) throws SQLException {
        ArrayList<Book> books = searchBook(b.getTitle());
        for (Book book : books) {
            if (book.equals(b)) {
                return true;
            }
        }
        return false;
    }

    protected boolean containsYoutube(Youtube yt) throws SQLException {
        ArrayList<Youtube> youtubeLinks = searchYoutube(yt.getTitle());
        for (Youtube link : youtubeLinks) {
            if (link.equals(yt)) {
                return true;
            }
        }
        return false;
    }

    public void closeDbConnection() throws SQLException {
        db.close();
    }

}
