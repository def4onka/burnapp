package burnapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class ConnectionDial extends JFrame{
  private static final long serialVersionUID = 1L;

  private String login;
  private String pass;
  private String address;
  private String port;
  private String dbname;
  final static boolean shouldFill = true;
  final static boolean shouldWeightX = true;
  private JTextField login_tf = new JTextField(15);
  private JPasswordField pass_tf = new JPasswordField(15);
  private JTextField address_tf = new JTextField(15);
  private JTextField port_tf = new JTextField(15);
  private JTextField dbname_tf = new JTextField(15);
  private JButton accept = new JButton("Подключиться к БД");

  private Connection conn = null;
 @SuppressWarnings({ "deprecation"})
  public ConnectionDial(){

    super("Подключение");
    login_tf.setText("def11");
    pass_tf.setText("wordpass");
    address_tf.setText("localhost");
    port_tf.setText("5432");
    dbname_tf.setText("burn");

    JPanel stuffPan = new JPanel(new GridBagLayout());
GridBagConstraints c = new GridBagConstraints();
if (shouldFill) {
c.fill = GridBagConstraints.HORIZONTAL;
}
if (shouldWeightX) {
c.weightx = 1.0;
}
c.insets = new Insets(5, 10, 0, 10);
c.fill = GridBagConstraints.HORIZONTAL;
c.ipady = 12;
c.gridx = 0;
c.gridy = 0;
stuffPan.add(new Label("Login: "), c);
c.insets = new Insets(0, 0, 0, 10);
c.gridx = 1;
stuffPan.add(login_tf, c);

c.insets = new Insets(0, 10, 0, 0);
c.gridx = 0;
c.gridy = 1;
stuffPan.add(new Label("Password: "), c);
c.insets = new Insets(0, 0, 0, 10);
c.gridx = 1;
stuffPan.add(pass_tf, c);

c.insets = new Insets(0, 10, 0, 0);
c.gridx = 0;
c.gridy = 2;
stuffPan.add(new Label("Address: "), c);
c.insets = new Insets(0, 0, 0, 10);
c.gridx = 1;
stuffPan.add(address_tf, c);

c.insets = new Insets(0, 10, 0, 0);
c.gridx = 0;
c.gridy = 3;
stuffPan.add(new Label("Port: "), c);
c.insets = new Insets(0, 0, 0, 10);
c.gridx = 1;
stuffPan.add(port_tf, c);

c.insets = new Insets(0, 10, 0, 0);
c.gridx = 0;
c.gridy = 4;
stuffPan.add(new Label("Database: "), c);
c.insets = new Insets(0, 0, 0, 10);
c.gridx = 1;
stuffPan.add(dbname_tf, c);

c.insets = new Insets(5, 10, 5, 10);
c.gridwidth = 2;
c.gridx = 0;
c.gridy = 5;
stuffPan.add(accept, c);
add(stuffPan);

    accept.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setLogin(login_tf.getText());
        login_tf.setText("");
        setPass(pass_tf.getText());
        pass_tf.setText("");
        setAddress(address_tf.getText());
        address_tf.setText("localhost");
        setPort(port_tf.getText());
        port_tf.setText("5432");
        setDBName(dbname_tf.getText());
        dbname_tf.setText("");

        try {
          Class.forName("org.postgresql.Driver");
          System.out.println("Database driver initialized.");
        } catch (Exception connExc) {
          System.out.println("Can't initialize the JDBC driver.");
          connExc.printStackTrace();
        }

        try {
          conn = DriverManager.getConnection("jdbc:postgresql://" +
            getAddress() + ":" + getPort() +
            "/" + getDBName(), getLogin(), getPass());
          System.out.println("Connected to database.");
          try{
          Statement st = conn.createStatement();
          st.executeUpdate("create table if not exists tasks("+
          "id serial primary key,"+
          "title text not null unique,"+
          "note text,"+
          "labor_vol integer not null,"+
          "status text not null check(status in ('Выполнено','Запланировано')),"+
          "readyday date check(status = 'Выполнено' and readyday is not null or status = 'Запланировано' and readyday is null));");
          System.out.println("Table tasks created or exists");
          st.executeUpdate("create table if not exists workdays("+
            "id serial primary key,"+
            "workday date not null unique,"+
            "labor_vol int not null);");
          System.out.println("Table workdays created or exists");
          st.executeUpdate("create table if not exists sprintdates("+
            "id serial primary key,"+
            "begindate date not null,"+
            "enddate date not null);");
          System.out.println("Table sprintdates created or exists");

          st.close();

          MainWindow mw = new MainWindow("Burndown app", conn);

          mw.setSize(1000, 1000);
          mw.setVisible(true);
          mw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          mw.setLocationRelativeTo(null);

          dispose();
        }catch (Exception r) {
          r.printStackTrace();
          System.out.println("Table tasks or days not added");
        }


      } catch (SQLException conncreateExc) {
          System.out.println("Some SQL Exception encountered.");
          JOptionPane.showMessageDialog(null, "Wrong connection details!", "Some fucking error", JOptionPane.ERROR_MESSAGE);

          conncreateExc.printStackTrace();
        }

      }
    });


    WindowListener listener = new WindowAdapter() {
      public void windowClosing(WindowEvent w) {
      //  dbc.closeConnection();
        try {
					conn.close();
					System.out.println("Connection closed.");
				} catch (Exception e) {
					System.out.println();
					e.printStackTrace();
				}
      }
    };
    addWindowListener(listener);

  }



  public void setLogin(String str) {
    this.login = str;
  }

  public void setPass(String str) {
    this.pass = str;
  }

  public void setAddress(String str) {
    this.address = str;
  }

  public void setPort(String str) {
    this.port = str;
  }

  public void setDBName(String str) {
    this.dbname = str;
  }

  public String getLogin() {
    return login;
  }

  public String getPass() {
    return pass;
  }

  public String getAddress() {
    return address;
  }

  public String getPort() {
    return port;
  }

  public String getDBName() {
    return dbname;
  }

}
