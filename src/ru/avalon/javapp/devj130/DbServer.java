package ru.avalon.javapp.devj130;

import java.sql.*;
import java.util.Enumeration;

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
            new DocumentException("Exception in addAuthors");
            return false;
        }
    }

    @Override
    public boolean addDocument(Document doc, Author author) throws DocumentException {
        return false;
    }

    @Override
    public Document[] findDocumentByAuthor(Author author) throws DocumentException {
        return new Document[0];
    }

    @Override
    public Document[] findDocumentByContent(String content) throws DocumentException {
        return new Document[0];
    }

    @Override
    public boolean deleteAuthor(Author author) throws DocumentException {
        try (Connection conn = DriverManager.getConnection(url, user, psw)) {
            try (Statement stmt = conn.createStatement()) {
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "delete from AUTHORS\n" +
                                "where ID = ?")) {
                    pstmt.setInt(1, author.getAuthor_id());
                    pstmt.executeUpdate();
                }
                return false;
            }
        } catch (SQLException throwables) {
            new DocumentException("Exception in addAuthors");
            return false;
        }
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
            } catch (SQLException throwables) {
                new DocumentException("Exception in addAuthors");
                return false;
            }
    }

    @Override
    public void close() throws Exception {

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
        st = con.createStatement();
        if (con == null) {
            throw new SQLException("Connection is null");
        }
    }

    public static void main(String[] args) throws SQLException, DocumentException {
        DbServer dbServer = new DbServer("jdbc:derby://localhost:1527/test", "test", "test");
        dbServer.info();
        dbServer.init();
        //dbServer.addAuthor(new Author(6, "Santa Claus3"));
        //dbServer.addAuthor(new Author(6, "", "correct comments"));
        //dbServer.deleteAuthor(new Author(6,"",""));
        //dbServer.deleteAuthor(5);
    }


}
