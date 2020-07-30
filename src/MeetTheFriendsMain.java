import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.*;

public class MeetTheFriendsMain extends JFrame {
    JPanel pane, paneCenter;
    JPanel topPanel, bottomPanel;
    JPanel column1, column2, column3;
    JPanel selAnimalPanel, selLocationPanel;

    JComboBox<String> mediaBox, episodeBox;
    DefaultComboBoxModel<String> mediaBoxModel, episodeBoxModel;
    JList<String> animalList, locationList;
    DefaultListModel<String> animalListModel, locationListModel;
    JButton connectBtn;
    JLabel selAnimalLabel, selLocationLabel;
    JLabel locationNameLabel, locationAddressLabel, locationRemarkLabel;
    JLabel animalNameLabel, animalEnglishNameLabel, animalDesctiptionLabel;
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

    void renewAnimalList(String medianame, String episode) {
        String dbcolumn = "animalname";
        String dbtable = "appearance";
        String sql = "SELECT animalname FROM appearance WHERE medianame = '" + medianame + "' AND episode = '" + episode + "'";
        try {
            resultSet = statement.executeQuery(sql);
            ArrayList<String> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(resultSet.getString(dbcolumn));
            }
            animalListModel.clear();
            for (String line: results) {
                animalListModel.addElement(line);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void renewLocationList(String animalname) {
        String sql = "SELECT DISTINCT locationname FROM animallocation WHERE animalname = '" + animalname + "'";
        try {
            resultSet = statement.executeQuery(sql);
            ArrayList<String> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(resultSet.getString("locationname"));
            }
            locationListModel.clear();
            for (String line: results) {
                //System.out.println(line);
                locationListModel.addElement(line);
            }
            if (results.size() == 0) {
                locationListModel.addElement("(データなし)");
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

                    String sql = "SELECT DISTINCT medianame FROM appearance";
                    resultSet = statement.executeQuery(sql);
                    //episodeBoxModel.removeAllElements();
                    ArrayList<String> results = new ArrayList<>();
                    while (resultSet.next()) {
                        results.add(resultSet.getString("medianame"));
                    }
                    mediaBoxModel.removeAllElements();
                    for (String line: results) {
                        System.out.println(line);
                        mediaBoxModel.addElement(line);
                    }
                    statusLabel.setText("接続 OK");
                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                    statusLabel.setText("接続失敗。やりなおしてください。");
                }

                //renewListModel(animalListModel, "appearance", "animalname");
                //renewAnimalList("KMFR1", 1);
            }
        }
    }

    class SelectMediaActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String value = mediaBox.getItemAt(mediaBox.getSelectedIndex());
            String sql = "SELECT DISTINCT episode FROM appearance WHERE medianame = '" + value + "'";
            try {
                resultSet = statement.executeQuery(sql);
                ArrayList<String> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(resultSet.getString("episode"));
                }
                episodeBoxModel.removeAllElements();
                for (String line: results) {
                    episodeBoxModel.addElement(line);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    class SelectEpisodeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String medianame = mediaBox.getItemAt(mediaBox.getSelectedIndex());
            String episode = episodeBox.getItemAt(episodeBox.getSelectedIndex());

            renewAnimalList(medianame, episode);
        }
    }

    class SelectAnimalActionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            String value = animalList.getSelectedValue();
            //System.out.println("selected: "  + value);
            renewLocationList(value);
            revealSelectedAnimal(value);
        }
    }

    class SelectLocationActionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            String value = locationList.getSelectedValue();
            //System.out.println("selected: " + value);
            revealSelectedLocation(value);
        }
    }

    void revealSelectedLocation(String locationname) {
        String sql = "SELECT DISTINCT * FROM animallocation WHERE locationname = '" + locationname + "'";

        String postalcode = null;
        String address = null;
        String remark = null;

        try {
            resultSet = statement.executeQuery(sql);
            resultSet.next();
            do {
                postalcode = resultSet.getString("postalcode");
                remark = resultSet.getString("remark");
            } while (resultSet.next() && postalcode == null);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        address = getAddress(postalcode);
        //System.out.println("postal" + postalcode);
        //System.out.println(address);
        //System.out.println("remark " + remark);

        locationNameLabel.setText(locationname);
        if (address.equals("")) {
            locationAddressLabel.setText("該当の住所なし");
        } else {
            locationAddressLabel.setText(address);
        }
        locationRemarkLabel.setText("");
        if (remark != null) {
            locationRemarkLabel.setText("<html>備考:<br>" + remark + "<html>");
        }
    }

    void revealSelectedAnimal(String animalname) {
        String sql = "SELECT DISTINCT * FROM animallocation WHERE animalname = '" + animalname + "'";

        String nameEN = null;
        //String description = null;
        try {
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                nameEN = resultSet.getString("animalnameen");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        animalNameLabel.setText(animalname);
        animalEnglishNameLabel.setText(nameEN);

    }

    String getAddress(String postalcode) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT * FROM postalcode WHERE CODE = '" + postalcode + "'";

        try {
            resultSet = statement.executeQuery(sql);
            //resultSet.next();
            while (resultSet.next()) {
                sb.append(resultSet.getString("prefecture"));
                sb.append(resultSet.getString("city"));
                sb.append(resultSet.getString("area"));
                break;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return sb.toString();
    }

    private void initializeGUI() {
        pane = (JPanel)getContentPane();
        pane.setLayout(new BorderLayout());
        paneCenter = new JPanel();
        //paneCenter.setLayout(new BoxLayout(paneCenter, BoxLayout.X_AXIS));
        paneCenter.setLayout(new GridLayout(1, 3));


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

        JLabel mediaLabel = new JLabel("メディアを選択");
        column1.add(mediaLabel);
        mediaBoxModel = new DefaultComboBoxModel<>();
        mediaBoxModel.addElement("（未接続）");
        mediaBox = new JComboBox<>(mediaBoxModel);
        mediaBox.addActionListener(new SelectMediaActionListener());
        column1.add(mediaBox);

        JLabel episodeLabel = new JLabel("エピソード等を選択");
        column1.add(episodeLabel);
        episodeBoxModel = new DefaultComboBoxModel<>();
        episodeBoxModel.addElement("（未接続）");
        episodeBox = new JComboBox<>(episodeBoxModel);
        episodeBox.addActionListener(new SelectEpisodeActionListener()) ;
        column1.add(episodeBox);

        animalListModel = new DefaultListModel<>();
        animalList = new JList<>(animalListModel);
        animalList.addListSelectionListener(new SelectAnimalActionListener());
        JScrollPane animalScrollPane = new JScrollPane(animalList);
        column1.add(animalScrollPane);
        paneCenter.add(column1);

        //カラム2
        column2 = new JPanel();
        column2.setLayout(new BoxLayout(column2, BoxLayout.Y_AXIS));

        locationListModel = new DefaultListModel<>();
        locationList = new JList<>(locationListModel);
        locationList.addListSelectionListener(new SelectLocationActionListener());
        JScrollPane placeScrollPane = new JScrollPane(locationList);
        column2.add(placeScrollPane);
        paneCenter.add(column2);

        //カラム3
        column3 = new JPanel();
        //column3.setLayout(new BoxLayout(column3, BoxLayout.Y_AXIS));
        column3.setLayout(new GridLayout(2, 1));

        selLocationPanel = new JPanel();
        selLocationPanel.setLayout(new BoxLayout(selLocationPanel, BoxLayout.Y_AXIS));
        selLocationLabel = new JLabel("<html>選択された場所<br> <html>");
        selLocationPanel.add(selLocationLabel);
        locationNameLabel = new JLabel("(未選択)");
        locationNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        selLocationPanel.add(locationNameLabel);
        locationAddressLabel = new JLabel("所在地");
        selLocationPanel.add(locationAddressLabel);
        locationRemarkLabel = new JLabel();
        selLocationPanel.add(locationRemarkLabel);
        column3.add(selLocationPanel);

        selAnimalPanel = new JPanel();
        selAnimalPanel.setLayout(new BoxLayout(selAnimalPanel, BoxLayout.Y_AXIS));
        selAnimalLabel = new JLabel("<html>選択された動物<br> <html>");
        selAnimalPanel.add(selAnimalLabel);
        animalNameLabel = new JLabel("(未選択)");
        animalNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        selAnimalPanel.add(animalNameLabel);
        animalEnglishNameLabel = new JLabel("英名");
        selAnimalPanel.add(animalEnglishNameLabel);
        column3.add(selAnimalPanel);
        paneCenter.add(column3);

        pane.add(paneCenter);
    }
}
