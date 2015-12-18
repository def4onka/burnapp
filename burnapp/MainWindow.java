	package burnapp;

	import javax.swing.*;
	import java.awt.*;
	import java.awt.event.*;
	import java.sql.*;

	public class MainWindow extends JFrame {


		private static final int VERTICAL_GAP = 10;
		private static final int HORIZONTAL_GAP = 30;
		private JButton addBttn = new JButton("Добавить задачу");
		private JButton remBttn = new JButton("Удалить задачу");
		private JButton cnnctBttn = new JButton("Подключиться к БД");
		protected Connection conn = null;
		private AddValues av = new AddValues(this, "New Entry");
		private RemoveEntry re = new RemoveEntry(this, "Remove Entry");
		private ConnectionData cd = new ConnectionData(this, "Connect");
		private DBConnection dbc = new DBConnection();

		public MainWindow(String title, Connection conn) {

			super(title);
			this.conn = conn;

			setLayout(new FlowLayout(FlowLayout.LEFT, HORIZONTAL_GAP, VERTICAL_GAP));

			addBttn.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					if(!av.isVisible()) {
						av.setVisible(true);
					}
				}

			});

			remBttn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!re.isVisible()) {
						re.setVisible(true);
					}
				}
			});

			cnnctBttn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!cd.isVisible()) {
						cd.setVisible(true);
					}
				}
			});

			JPanel bttnPanel = new JPanel(new GridLayout(3, 1, HORIZONTAL_GAP, VERTICAL_GAP));

			bttnPanel.add(cnnctBttn);
			bttnPanel.add(addBttn);
			bttnPanel.add(remBttn);

			JPanel dataOverview = new JPanel(new GridLayout(3, 1));

			add(bttnPanel);

			WindowListener listener = new WindowAdapter() {
				public void windowClosing(WindowEvent w) {
					try {
						conn.close();
						System.out.println("Connection closed.");
					} catch (Exception closeconnExc) {
						System.out.println();
						closeconnExc.printStackTrace();
					}
					System.exit(0);
				}
			};
			addWindowListener(listener);



		}

		class ConnectionData extends JDialog {

			private String login;
			private String pass;
			private String address;
			private String port;
			private String dbname;

			private JTextField login_tf = new JTextField(15);
			private JPasswordField pass_tf = new JPasswordField(15);
			private JTextField address_tf = new JTextField(15);
			private JTextField port_tf = new JTextField(15);
			private JTextField dbname_tf = new JTextField(15);
			private JButton accept = new JButton("Подключиться к БД");


			public ConnectionData(JFrame frame, String title) {
				super(frame, title, true);


				JPanel stuffPan = new JPanel(new GridLayout(7, 2));

				stuffPan.add(new Label("Login: "));
				stuffPan.add(login_tf);
				stuffPan.add(new Label("Password: "));
				stuffPan.add(pass_tf);
				stuffPan.add(new Label("Address: "));
				stuffPan.add(address_tf);
				address_tf.setText("localhost");
				stuffPan.add(new Label("Port: "));
				port_tf.setText("5432");
				stuffPan.add(port_tf);
				stuffPan.add(new Label("Database: "));
				stuffPan.add(dbname_tf);
				stuffPan.add(accept);

			//setVisible(true);

				add(stuffPan);

				pack();

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
						dbc.connect();
						dispose();
					}
				});



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


		class RemoveEntry extends JDialog {

			private JTextField title_tf = new JTextField(10);
			private JButton remFromDBBttn = new JButton("Remove Entry");

			public RemoveEntry (JFrame frame, String title) {

				super(frame, title, true);

				JPanel container = new JPanel(new GridLayout(3, 1));

				container.add(new Label("Remove Entry"));
				container.add(title_tf);
				container.add(remFromDBBttn);
				add(container);
				pack();
				setLocationRelativeTo(frame);

				remFromDBBttn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {

							Statement statement = conn.createStatement();
							statement.executeUpdate("delete from tasks where id = " + title_tf.getText() + ";");
							title_tf.setText("");
							statement.close();
						} catch (Exception sqlExc) {
							System.out.println("Would you like a cup of fucking exceptions, fag?");
							sqlExc.printStackTrace();
						}
						setVisible(false);
					}

				});
			}

		}


		class AddValues extends JDialog {

			private JTextField title_tf = new JTextField(10);
			private JTextField labor_vol_tf = new JTextField(10);
			private JTextField date_tf = new JTextField(10);
			private JCheckBox done_cb = new JCheckBox();

			private JButton sendToDbBttn = new JButton("Add");


			public AddValues(JFrame frame, String title) {

				super(frame, title, true);

				JPanel container = new JPanel(new GridLayout(3, 1));

				JPanel stuffPanel = new JPanel(new GridLayout(4, 2));
				stuffPanel.add(new Label("Задача "));
				stuffPanel.add(title_tf);
				stuffPanel.add(new Label("Трудоемкость:"));
				stuffPanel.add(labor_vol_tf);
				stuffPanel.add(new Label("Дата: "));
				stuffPanel.add(date_tf);
				stuffPanel.add(done_cb);
				stuffPanel.add(new Label("Выполнена"));


				container.add(new Label("Введите данные добавляемой задачи"));
				container.add(stuffPanel);
				container.add(sendToDbBttn);
				add(container);

				pack();
				setLocationRelativeTo(frame);

				sendToDbBttn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try{
							Statement statement = conn.createStatement();
				//ResultSet rs =
			//	statement.executeQuery("insert into tasks values (" +
					// "\"" + labor_vol_tf.getText() + "\", " + date_tf.getText() + ");");

							statement.executeUpdate("insert into tasks (title, labor_vol,status) values (" +
								"'" + title_tf.getText() + "', "+ Integer.parseInt(labor_vol_tf.getText()) +", "+ "'Запланировано'" +");");//to_date('" + date_tf.getText() + "', 'mm/dd/yyyy'));");
							labor_vol_tf.setText("");
							date_tf.setText("");
							title_tf.setText("");
							statement.close();
						} catch (Exception r) {
							r.printStackTrace();
							System.out.println("Oops, here goes some shit again");
						}

						setVisible(false);

					}
				});


			}
		}

		class DBConnection {


			public DBConnection() { };

			public void connect() {

				try {
					Class.forName("org.postgresql.Driver");
					System.out.println("Database driver initialized.");
				} catch (Exception e) {
					System.out.println("Can't initialize the JDBC driver.");
					e.printStackTrace();
				}

				try {
					conn = DriverManager.getConnection("jdbc:postgresql://" +
						cd.getAddress() + ":" + cd.getPort() +
						"/" + cd.getDBName(), cd.getLogin(), cd.getPass());
					System.out.println("Connected to database.");
					try{
					Statement st = conn.createStatement();
					st.executeUpdate("create table if not exists Tasks("+
						"id serial primary key,"+
						"title text not null unique,"+
						"labor_vol integer not null,"+
						"status text not null check(status in ('Выполнено','Запланировано')),"+
						"readyday date check(status = 'Выполнено' or null));");
					System.out.println("Table tasks created or exists");
					st.executeUpdate("create table if not exists Days("+
						"id serial primary key,"+
						"workday date not null unique,"+
						"labor_vol int not null);");
						System.out.println("Table days created or exists");
					st.close();
				}catch (Exception r) {
					r.printStackTrace();
					System.out.println("Table tasks or days not added");
				}


				} catch (SQLException e) {
					System.out.println("Some SQL Exception encountered.");
					JOptionPane.showMessageDialog(null, "Wrong connection details!", "Some fucking error", JOptionPane.ERROR_MESSAGE);

					e.printStackTrace();
				}

			}

			public void closeConnection() {
				try {
					conn.close();
					System.out.println("Connection closed.");
				} catch (Exception e) {
					System.out.println();
					e.printStackTrace();
				}

			}

			public Connection returnConnection() {
				return conn;
			}

		}

	}
