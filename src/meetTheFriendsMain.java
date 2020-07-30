import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class meetTheFriendsMain extends JFrame {
    JPanel pane, paneCenter;

    String driverClassName = "org.postgresql.Driver";
    String url = "jdbc:postgresql://localhost/test";
    String user = "dbpuser";
    String password = "hogehoge";
    Statement statement;
    Connection connection;
    ResultSet resultSet;

    public static void main(String[] args) {
        JFrame w = new meetTheFriendsMain("Meet the Friends");
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.setSize(800, 400);
        w.setVisible(true);
    }

    public meetTheFriendsMain(String title) {
        super(title);
        initializeGUI();
    }

    private void initializeGUI() {
        pane = (JPanel)getContentPane();
        pane.setLayout(new BorderLayout());

    }
}
