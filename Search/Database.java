package sample;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import net.ucanaccess.jdbc.JackcessOpenerInterface;

public class Database {

    private List<String> titles = new LinkedList<>();
    private List<String> links = new LinkedList<>();
    private String path = "account.accdb";

    public boolean exist_keyword(String keyword) {
        Connection connDB = null;
        boolean found = false;

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String dataSource = "jdbc:ucanaccess://" + path;
            connDB = DriverManager.getConnection(dataSource);
            Statement st = connDB.createStatement();

            st.execute("SELECT keyword FROM Keyword");
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                if (rs.getString("keyword").equals(keyword)) {
                    found = true;
                }
            }

            st.close();
            connDB.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        if (found)
            return true;
        else
            return false;
    }

    public void access_keyword(String keyword) {
        Connection connDB = null;

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String dataSource = "jdbc:ucanaccess://" + path;
            connDB = DriverManager.getConnection(dataSource);
            Statement st = connDB.createStatement();

            st.execute("SELECT title, link FROM Keyword WHERE keyword='" + keyword + "'");
            ResultSet rs = st.getResultSet();
            rs.next();
            String title_name = rs.getString("title"), link_name = rs.getString("link");

            st.execute("SELECT title FROM " + title_name);
            ResultSet t = st.getResultSet();
            st.execute("SELECT link FROM " + link_name);
            ResultSet l = st.getResultSet();

            while (t.next()) {
                titles.add(t.getString("title"));
            }

            while (l.next()) {
                links.add(l.getString("link"));
            }

            st.close();
            connDB.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void store_keyword(String keyword, List<String> titles, List<String> links) {
        Connection connDB = null;

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String dataSource = "jdbc:ucanaccess://" + path;
            connDB = DriverManager.getConnection(dataSource);
            Statement st = connDB.createStatement();

            int count = 0;
            st.execute("SELECT keyword FROM Keyword");
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                count++;
            }

            String title_name = "t" + count;
            String link_name = "l" + count;

            st.executeUpdate("INSERT INTO Keyword (keyword, title, link) VALUES ('" + keyword + "', '" + title_name + "', '" + link_name + "')");
            st.executeUpdate("CREATE TABLE " + title_name + " (title varchar(255));");
            st.executeUpdate("CREATE TABLE " + link_name + " (link varchar(255))");

            for (String title: titles) {
                st.executeUpdate("INSERT INTO " + title_name + " (title) VALUES ('" + title + "')");
            }

            for (String link: links) {
                st.executeUpdate("INSERT INTO " + link_name + " (link) VALUES ('" + link + "')");
            }

            st.close();
            connDB.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> get_titles() {
        return titles;
    }

    public List<String> get_links() {
        return links;
    }
}
