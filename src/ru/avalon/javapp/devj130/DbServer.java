package ru.avalon.javapp.devj130;

import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DbServer implements IDbService {
    private String url;
    private String user;
    private String psw;
    private Connection con;
    private Statement st;
    private PreparedStatement ps;
    private ResultSet rs;


    public DbServer(String url, String user, String psw) {
        this.url = url;
        this.user = user;
        this.psw = psw;
    }

    @Override
    public boolean addAuthor(Author author) throws DocumentException {

        try (Connection conn = DriverManager.getConnection(url, user, psw)) {
            int maxOrderId = 0;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("select max (ID) from AUTHORS")) {
                rs.next();
                maxOrderId = rs.getInt(1);
                // если id меньше максимального, то обновим таблицу, иначе добавим нового пользователя
                if (author.getAuthor_id() <= maxOrderId) {
                    System.out.println("update");

                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "update AUTHORS\n" +
                                    "set OTHER = ?\n" +
                                    "where ID = ?")) {
                        pstmt.setString(1, author.getNotes());
                        pstmt.setInt(2, author.getAuthor_id());
                        pstmt.executeUpdate();
                    }
                    return false;
                } else {
                    System.out.println("add");
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "insert into AUTHORS (ID, CUSTOMER_FIO, OTHER)\n" +
                                    "values (?, ?, ?)")) {
                        pstmt.setInt(1, author.getAuthor_id());
                        pstmt.setString(2, author.getAuthor());
                        pstmt.setString(3, author.getNotes());
                        pstmt.executeUpdate();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DocumentException("Exception in addAuthors");
        }
    }

    @Override
    public boolean addDocument(Document doc, Author author) throws DocumentException {

        try (Connection conn = DriverManager.getConnection(url, user, psw)) {
            int maxOrderId = 0;

            addAuthor(author);

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("select max (ID) from DOCUMENTS")) {
                rs.next();
                maxOrderId = rs.getInt(1);

                // если id меньше максимального, то обновим таблицу, иначе добавим новый документ
                if (doc.getDocument_id() <= maxOrderId) {
                    System.out.println("update");
                    return false;
                } else {
                    System.out.println("add");
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "insert into DOCUMENTS (ID, DOC_NAME, TEXT, AUTHOR_ID)\n" +
                                    "values (?, ?, ?, ?)")) {
                        pstmt.setInt(1, doc.getDocument_id());
                        pstmt.setString(2, doc.getTitle());
                        pstmt.setString(3, doc.getText());
                        pstmt.setInt(4, author.getAuthor_id());
                        pstmt.executeUpdate();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DocumentException("Exception in addAuthors");
        }
    }

    @Override
    public Document[] findDocumentByAuthor(Author author) throws DocumentException {

        if (author == null)
            throw new DocumentException("'author' can't be empty");

        try (Connection conn = DriverManager.getConnection(url, user, psw)) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "select DOCUMENTS.ID, DOC_NAME, TEXT, AUTHOR_ID\n" +
                            "from DOCUMENTS join AUTHORS on AUTHOR_ID = AUTHORS.ID\n" +
                            "where AUTHORS.ID = ?"
            )) {
                pstmt.setInt(1,author.getAuthor_id());
                try (ResultSet rs = pstmt.executeQuery()) {
                    return createDocArray(rs);
                }
            }
        } catch (SQLException e) {
            throw new DocumentException("Exception in findDocumentByContent");
        }
    }

    @Override
    public Document[] findDocumentByContent(String content) throws DocumentException {

        if (content == null || content.equals(""))
            throw new DocumentException("'content' can't be null");

        try (Connection conn = DriverManager.getConnection(url, user, psw)) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "select DOCUMENTS.ID, DOC_NAME, TEXT, AUTHOR_ID\n" +
                            "from DOCUMENTS\n" +
                            "where TEXT like ?"
            )) {
                pstmt.setString(1, "%" + content + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    return createDocArray(rs);
                }
            }
        } catch (SQLException e) {
            throw new DocumentException("Exception in findDocumentByContent");
        }
    }


    @Override
    public boolean deleteAuthor(Author author) throws DocumentException {
        int id = author.getAuthor_id();
        return deleteAuthor(id);
    }

        @Override
    public boolean deleteAuthor(int id) throws DocumentException {
            try (Connection conn = DriverManager.getConnection(url, user, psw)) {
                try (Statement stmt = conn.createStatement()) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "delete from AUTHORS\n" +
                                    "where ID = ?")) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }
                    return false;
                }
            } catch (SQLException e) {
                throw new DocumentException("Exception in addAuthors");
            }
    }

    @Override
    public void close() throws SQLException {
        if (con != null && con.isValid(0)) {
            con.close();
        }
    }

    private void info() {
        Enumeration<Driver> e = DriverManager.getDrivers();
        Driver drv = null;
        while (e.hasMoreElements()) {
            drv = e.nextElement();
            System.out.println(drv.getClass().getCanonicalName());
        }
    }

    private void init() throws SQLException {
         con = DriverManager.getConnection(url,user,psw);
         if (con == null)
             throw new IllegalArgumentException("Connection is null");
    }

    public static void main(String[] args) throws DocumentException {
        try (DbServer dbServer = new DbServer("jdbc:derby://localhost:1527/test", "test", "test")) {
            dbServer.info();
            dbServer.init();
            //dbServer.addAuthor(new Author(6, "Santa Claus3"));
            //dbServer.addAuthor(new Author(6, "", "correct comments"));
            //dbServer.deleteAuthor(new Author(6,"",""));
            //dbServer.deleteAuthor(5);
            //dbServer.findDocumentByAuthor(new Author(3, "Jim Beam", "wdwd"));
            dbServer.findDocumentByContent("Report");
            //dbServer.addDocument(new Document(6,"Test report", "Report first", 3), new Author(5, "Jim Beamenst"));
        } catch (SQLException e) {
            System.out.println("Bad connection with database");
        }
    }


    // Заполнение массива документами
    private static Document[] createDocArray(ResultSet rs) throws SQLException {
        List<Document> documents = new ArrayList<>();
        while (rs.next()) {
            documents.add(new Document(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
        }
        Document[] res = documents.toArray(new Document[0]);

        return res;
    }
}
