import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.Border;

public class MeetTheFriendsMain extends JFrame {
    JPanel pane, paneCenter;
    JPanel topPanel, bottomPanel;
    JPanel column1, column2, column3;
    JPanel selAnimalPanel, selPlacePanel;

    JComboBox<String> mediaBox, episodeBox;
    JList<String> animalList, placeList;
    DefaultListModel<String> animalListModel, placeListModel;
    JButton connectBtn;
    JLabel selAnimalLabel, selPlaceLabel;
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

                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                }
                statusLabel.setText("接続 OK");
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

        placeListModel = new DefaultListModel<>();
        placeList = new JList<>(animalListModel);
        JScrollPane placeScrollPane = new JScrollPane(placeList);
        column2.add(placeScrollPane);
        paneCenter.add(column2);

        //カラム3
        column3 = new JPanel();
        column3.setLayout(new BoxLayout(column3, BoxLayout.Y_AXIS));

        selPlacePanel = new JPanel();
        selPlacePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        selPlaceLabel = new JLabel("選択された場所");
        selPlacePanel.add(selPlaceLabel);
        column3.add(selPlacePanel);
        selAnimalPanel = new JPanel();
        selAnimalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        selAnimalLabel = new JLabel("選択された動物");
        selAnimalPanel.add(selAnimalLabel);
        column3.add(selAnimalPanel);
        paneCenter.add(column3);

        pane.add(paneCenter);
    }
}
