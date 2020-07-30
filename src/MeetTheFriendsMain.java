import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.*;

public class MeetTheFriendsMain extends JFrame {
    JPanel pane, paneCenter;
    JPanel topPanel, bottomPanel;
    JPanel column1, column2, column3;
    JPanel selAnimalPanel, selLocationPanel;

    JComboBox<String> mediaBox, episodeBox;
    JList<String> animalList, locationList;
    DefaultListModel<String> animalListModel, locationListModel;
    JButton connectBtn;
    JLabel selAnimalLabel, selLocationLabel;
    JLabel statusLabel;

    String driverClassName = "org.postgresql.Driver";
    String databasename = "test";
    String servername = null;
    String urlprefix = "jdbc:postgresql://";    // jdbc:postgresql://servername/database
    String url = null;
    String user = "dbpuser";
    String password = "hogehoge";
    Statement statement;
    Connection connection;
    ResultSet resultSet;

    PreparedStatement prepStmt_S;
    String strPrepSQL_S = "SELECT ? FROM ? WHERE ?";

    public static void main(String[] args) {
        JFrame w = new MeetTheFriendsMain("Meet the Friends");
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.setSize(800, 400);
        w.setVisible(true);
    }

    public MeetTheFriendsMain(String title) {
        super(title);
        initializeGUI();
    }

    void renewAnimalList (String contentid, int episode) {
        String dbcolumn = "animalname";
        String dbtable = "appearance";
        String sql = "SELECT animalname FROM appearance WHERE contentid = '" + contentid + "' AND episode = '" + episode + "'";
        try {
            resultSet = statement.executeQuery(sql);

            animalListModel.clear();
            while (resultSet.next()) {
                animalListModel.addElement(resultSet.getString(dbcolumn));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    class ConnectAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            String value = JOptionPane.showInputDialog("サーバーアドレスを入力してください");
            if (value == null) {
                statusLabel.setText("もう一度接続してください");
            } else {
                servername = value;
                url = urlprefix + servername + "/" + databasename;

                try {
                    Class.forName(driverClassName);
                    connection = DriverManager.getConnection(url, user, password);
                    prepStmt_S = connection.prepareStatement(strPrepSQL_S);
                    statement = connection.createStatement();
                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                }
                statusLabel.setText("接続 OK");

                //renewListModel(animalListModel, "appearance", "animalname");
                //renewAnimalList("KMFR1", 1);
            }
        }
    }

    private void initializeGUI() {
        pane = (JPanel)getContentPane();
        pane.setLayout(new BorderLayout());
        paneCenter = new JPanel();
        paneCenter.setLayout(new BoxLayout(paneCenter, BoxLayout.X_AXIS));
        //paneCenter.setLayout(new FlowLayout(FlowLayout.LEFT));


        //一番上の部分
        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        Action connectAction = new ConnectAction();
        connectAction.putValue(Action.NAME, "データベースに接続");
        connectBtn = new JButton(connectAction);
        topPanel.add(connectBtn);
        pane.add(topPanel, BorderLayout.PAGE_START);

        //一番下の部分（ステータスバー）
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        statusLabel = new JLabel("接続してください");
        bottomPanel.add(statusLabel);
        pane.add(bottomPanel, BorderLayout.PAGE_END);

        //メインの内容
        //カラム1
        column1 = new JPanel();
        column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));

        mediaBox = new JComboBox<>();
        column1.add(mediaBox);
        episodeBox = new JComboBox<>();
        column1.add(episodeBox);
        animalListModel = new DefaultListModel<>();
        animalList = new JList<>(animalListModel);
        JScrollPane animalScrollPane = new JScrollPane(animalList);
        column1.add(animalScrollPane);
        paneCenter.add(column1);

        //カラム2
        column2 = new JPanel();
        column2.setLayout(new BoxLayout(column2, BoxLayout.Y_AXIS));

        locationListModel = new DefaultListModel<>();
        locationList = new JList<>(locationListModel);
        JScrollPane placeScrollPane = new JScrollPane(locationList);
        column2.add(placeScrollPane);
        paneCenter.add(column2);

        //カラム3
        column3 = new JPanel();
        column3.setLayout(new BoxLayout(column3, BoxLayout.Y_AXIS));

        selLocationPanel = new JPanel();
        selLocationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        selLocationLabel = new JLabel("選択された場所");
        selLocationPanel.add(selLocationLabel);
        column3.add(selLocationPanel);
        selAnimalPanel = new JPanel();
        selAnimalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        selAnimalLabel = new JLabel("選択された動物");
        selAnimalPanel.add(selAnimalLabel);
        column3.add(selAnimalPanel);
        paneCenter.add(column3);

        pane.add(paneCenter);
    }
}
