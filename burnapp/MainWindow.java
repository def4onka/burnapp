	package burnapp;

	import javax.swing.*;
	import javax.swing.table.*;
	import java.awt.*;
	import java.awt.event.*;
	import java.sql.*;
	import java.util.*;

	public class MainWindow extends JFrame {


		private static final int VERTICAL_GAP = 10;
		private static final int HORIZONTAL_GAP = 30;
		private static final int TASKS_WIDTH = 600;
		private static final int TASKS_HEIGTH = 200;
		private static final int WORKDAYS_WIDTH = 300;
		private static final int WORKDAYS_HEIGTH = 200;
		//private JButton addBttn = new JButton("Добавить задачу");
		//private JButton remBttn = new JButton("Удалить задачу");
		//private JButton cnnctBttn = new JButton("Подключиться к БД");
		protected Connection conn = null;
		protected static Statement st = null;
		//protected static ResultSetMetaData tasks_rsmd = null;
		//protected static ResultSetMetaData workdays_rsmd = null;
		//protected static ResultSet tasks_rs = null;
		//protected static ResultSet workdays_rs = null;
		private AddValues av = new AddValues(this, "New Entry");
		private RemoveEntry re = new RemoveEntry(this, "Remove Entry");


		public MainWindow(String title, Connection conn) {

			super(title);

			this.conn = conn;

			try{
				st = conn.createStatement();
			}catch(SQLException stExc){
				System.out.println("statement not created");
				stExc.printStackTrace();
			}


			setLayout(new FlowLayout(FlowLayout.LEFT, HORIZONTAL_GAP, VERTICAL_GAP));

			// addBttn.addActionListener(new ActionListener() {
			//
			// 	public void actionPerformed(ActionEvent e) {
			//
			// 		if(!av.isVisible()) {
			// 			av.setVisible(true);
			// 		}
			// 	}
			//
			// });
			//
			// remBttn.addActionListener(new ActionListener() {
			// 	public void actionPerformed(ActionEvent e) {
			// 		if(!re.isVisible()) {
			// 			re.setVisible(true);
			// 		}
			// 	}
			// });
			//
			// cnnctBttn.addActionListener(new ActionListener() {
			// 	public void actionPerformed(ActionEvent e) {
			// 		if(!cd.isVisible()) {
			// 			cd.setVisible(true);
			// 		}
			// 	}
			// });

			// JPanel bttnPanel = new JPanel(new GridLayout(3, 1, HORIZONTAL_GAP, VERTICAL_GAP));
			//
			// bttnPanel.add(cnnctBttn);
			// bttnPanel.add(addBttn);
			// bttnPanel.add(remBttn);
			//
			// JPanel dataOverview = new JPanel(new GridLayout(3, 1));
			//
			// add(bttnPanel);

			JPanel panel = new JPanel(new BorderLayout());

			JLabel head_lbl = new JLabel("Ведение Burndown диаграммы");
			head_lbl.setFont(new Font("Tahoma", Font.PLAIN, 26));
			head_lbl.setVisible(true);
			panel.add(head_lbl,BorderLayout.NORTH);

			JPanel tasksPan = new JPanel(new BorderLayout());
			tasksPan.setSize(700,500);

			JTable tasks_tb = new JTable(buildTasksTableModel("tasks"));
			tasks_tb.setSize(new Dimension(TASKS_WIDTH, TASKS_HEIGTH));
			tasks_tb.setPreferredSize(new Dimension(TASKS_WIDTH, TASKS_HEIGTH));
			JScrollPane tasks_scrtb = new JScrollPane(tasks_tb);
			tasks_tb.setFillsViewportHeight(true);
			//tasks_tb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tasks_scrtb.setSize(new Dimension(TASKS_WIDTH, TASKS_HEIGTH));
			tasks_scrtb.setPreferredSize(new Dimension(TASKS_WIDTH, TASKS_HEIGTH));
			JLabel task_lbl = new JLabel("Задачи");
			task_lbl.setFont(new Font("Colibri", Font.PLAIN, 18));
			tasksPan.add(task_lbl,BorderLayout.NORTH);
			tasksPan.add(tasks_scrtb,BorderLayout.CENTER);

			JPanel workdaysPan = new JPanel(new BorderLayout());
			workdaysPan.setSize(300,200);

			JTable workdays_tb = new JTable(buildWorkdaysTableModel("workdays"));
			workdays_tb.setSize(new Dimension(WORKDAYS_WIDTH, WORKDAYS_HEIGTH));
			workdays_tb.setPreferredSize(new Dimension(WORKDAYS_WIDTH, WORKDAYS_HEIGTH));
			JScrollPane workdays_scrtb = new JScrollPane(workdays_tb);
			workdays_tb.setFillsViewportHeight(true);
			//tasks_tb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			workdays_scrtb.setSize(new Dimension(WORKDAYS_WIDTH, WORKDAYS_HEIGTH));
			workdays_scrtb.setPreferredSize(new Dimension(WORKDAYS_WIDTH, WORKDAYS_HEIGTH));
			JLabel workday_lbl = new JLabel("Рабочие дни");
			workday_lbl.setFont(new Font("Colibri", Font.PLAIN, 18));
			workdaysPan.add(workday_lbl,BorderLayout.NORTH);
			workdaysPan.add(workdays_scrtb,BorderLayout.CENTER);



			panel.add(tasksPan,BorderLayout.CENTER);
			panel.add(workdaysPan,BorderLayout.EAST);
			add(panel);

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

		public static DefaultTableModel buildTasksTableModel(String nametable) {
	    // names of columns
			Vector<String> columnNames = new Vector<String>();
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			try{
				ResultSet rs = st.executeQuery("select * from " + nametable);
				ResultSetMetaData	 rsmd = rs.getMetaData();
				try{
			    int columnCount = rsmd.getColumnCount();
			    for (int column = 1; column <= columnCount; column++) {
			        columnNames.add(rsmd.getColumnName(column));
			    }

			    // data of the table

			    while (rs.next()) {
			        Vector<Object> vector = new Vector<Object>();
			        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			            vector.add(rs.getObject(columnIndex));
			        }
			        data.add(vector);
			    }
				}catch(SQLException e){
					System.out.println("tasks table not filled cause sqlexc");
					e.printStackTrace();
				}
			}catch(SQLException rsmdExc){
				System.out.println("resulsetmeta not created");
				rsmdExc.printStackTrace();
			}

	    return new DefaultTableModel(data, columnNames);

		}

		public static DefaultTableModel buildWorkdaysTableModel(String nametable) {
			// names of columns
			Vector<String> columnNames = new Vector<String>();
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			try{
				ResultSet rs = st.executeQuery("select * from " + nametable);
				ResultSetMetaData	 rsmd = rs.getMetaData();
				try{
					int columnCount = rsmd.getColumnCount();
					for (int column = 1; column <= columnCount; column++) {
							columnNames.add(rsmd.getColumnName(column));
					}

					// data of the table

					while (rs.next()) {
							Vector<Object> vector = new Vector<Object>();
							for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
									vector.add(rs.getObject(columnIndex));
							}
							data.add(vector);
					}
				}catch(SQLException e){
					System.out.println("tasks table not filled cause sqlexc");
					e.printStackTrace();
				}
			}catch(SQLException rsmdExc){
				System.out.println("resulsetmeta not created");
				rsmdExc.printStackTrace();
			}

			return new DefaultTableModel(data, columnNames);

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



	}
