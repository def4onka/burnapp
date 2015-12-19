	package burnapp;

	import javax.swing.*;
	import javax.swing.table.*;
	import java.awt.*;
	import java.awt.event.*;
	import java.sql.*;
	import java.util.*;
	import org.knowm.xchart.SwingWrapper;
	import org.knowm.xchart.Chart;
	import org.knowm.xchart.XChartPanel;

	public class MainWindow extends JFrame {


		private static final int VERTICAL_GAP = 10;
		private static final int HORIZONTAL_GAP = 30;
		private static final int TASKS_WIDTH = 600;
		private static final int TASKS_HEIGTH = 300;
		private static final int WORKDAYS_WIDTH = 300;
		private static final int WORKDAYS_HEIGTH = 300;

		protected Connection conn = null;
		protected static Statement st = null;
		//protected static ResultSetMetaData tasks_rsmd = null;
		//protected static ResultSetMetaData workdays_rsmd = null;
		//protected static ResultSet tasks_rs = null;
		//protected static ResultSet workdays_rs = null;

		JButton change_task = new JButton("Изменить");
		JButton delete_task = new JButton("Удалить");
		JButton addnew_task = new JButton("Добавить");
		JButton change_workday = new JButton("Изменить");
		JButton delete_workday = new JButton("Удалить");
		JButton addnew_workday = new JButton("Добавить");
		JButton showDiag_bttn = new JButton("Показать диграмму");
		JTextField begin_tf = new JTextField(10);
		JTextField end_tf = new JTextField(10);

		private AddValues av = new AddValues(this, "Новая задача");
		//private RemoveEntry re = new RemoveEntry(this, "Удалить задачу");


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

			JPanel panel = new JPanel(new BorderLayout());
			JPanel headPan = new JPanel(new BorderLayout());

			JLabel head_lbl = new JLabel("Ведение Burndown диаграммы");
			head_lbl.setFont(new Font("Tahoma", Font.PLAIN, 26));
			head_lbl.setVisible(true);
			headPan.add(head_lbl,BorderLayout.NORTH);
			JPanel periodPan = new JPanel();
			periodPan.add(new Label("Дата начала спринта"));
			periodPan.add(begin_tf);
			JButton begin_bt = new JButton("OK");
			periodPan.add(begin_bt);
			periodPan.add(new Label("Дата начала спринта"));
			periodPan.add(end_tf);
			JButton end_bt = new JButton("OK");
			periodPan.add(end_bt);

			begin_bt.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {
								begin_tf.setEditable(false);
							}

						});

			end_bt.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {
								end_tf.setEditable(false);
							}

						});

			headPan.add(periodPan);
			panel.add(headPan,BorderLayout.NORTH);
			JPanel tasksPan = new JPanel(new BorderLayout());
			tasksPan.setSize(700,500);

			JTable tasks_tb = new JTable(buildTableModel("tasks"));
			//tasks_tb.getDataVector();
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

			JTable workdays_tb = new JTable(buildTableModel("workdays"));
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

			JPanel tasks_bttns = new JPanel(new BorderLayout());

			JPanel chdel = new JPanel();
			chdel.add(change_task);
			chdel.add(delete_task);
			tasks_bttns.add(chdel,BorderLayout.CENTER);
			tasks_bttns.add(addnew_task,BorderLayout.EAST);

			JPanel workdays_bttns = new JPanel(new BorderLayout());


			JPanel chdelwd = new JPanel();
			chdelwd.add(change_workday);
			chdelwd.add(delete_workday);
			workdays_bttns.add(chdelwd,BorderLayout.CENTER);
			workdays_bttns.add(addnew_workday,BorderLayout.EAST);

			JPanel showDiagPan = new JPanel();
			showDiagPan.add(showDiag_bttn);
			tasksPan.add(tasks_bttns,BorderLayout.SOUTH);
			workdaysPan.add(workdays_bttns,BorderLayout.SOUTH);
			panel.add(tasksPan,BorderLayout.CENTER);
			panel.add(workdaysPan,BorderLayout.EAST);
			panel.add(showDiagPan,BorderLayout.SOUTH);

			addnew_task.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {

								if(!av.isVisible()) {
									av.setVisible(true);
								}
							}

						});


				showDiag_bttn.addActionListener(new ActionListener() {

								public void actionPerformed(ActionEvent e) {

									JavaPaintUI exampleChart = new JavaPaintUI(conn/*,begin_tf.getText(),end_tf.getText()*/);
									Chart chart = exampleChart.getChart();
									JFrame frame = new JFrame("Диаграмма");
									frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
									JPanel chartPanel = new XChartPanel(chart);
									frame.add(chartPanel);
									frame.pack();
									frame.setVisible(true);
															}

							});


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

		public static DefaultTableModel buildTableModel(String nametable) {
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
			private JTextField note_tf = new JTextField(10);
			private JTextField labor_vol_tf = new JTextField(10);
			private JTextField date_tf = new JTextField(10);
			private JCheckBox done_cb = new JCheckBox();
			private boolean isDone = false;
			private JButton sendToDbBttn = new JButton("Добавить");


			public AddValues(JFrame frame, String title) {

				super(frame, title, true);

				JPanel container = new JPanel(new GridLayout(3, 1));

				JPanel stuffPanel = new JPanel(new GridLayout(5, 2));
				stuffPanel.add(new Label("Задача "));
				stuffPanel.add(title_tf);
				stuffPanel.add(new Label("Описание задачи "));
				stuffPanel.add(note_tf);
				stuffPanel.add(new Label("Трудоемкость:"));
				stuffPanel.add(labor_vol_tf);
				stuffPanel.add(done_cb);
				stuffPanel.add(new Label("Выполнена"));
				Label date_lbl = new Label("Дата: ");
				stuffPanel.add(date_lbl);
				stuffPanel.add(date_tf);
				date_lbl.setVisible(false);
				date_tf.setVisible(false);

				done_cb.addItemListener(new ItemListener() {

						@Override
						public void itemStateChanged(ItemEvent e) {
							if(e.getStateChange() == ItemEvent.SELECTED) {
								isDone = true;
								date_lbl.setVisible(true);
								date_tf.setVisible(true);
								pack();
							}
							else{
								date_lbl.setVisible(false);
								date_tf.setVisible(false);

							}
						}
				});

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
							if(isDone) statement.executeUpdate("insert into tasks (title, note, labor_vol, status, readyday) values (" +
												"'" + title_tf.getText() + "', '"+ note_tf.getText() +"'," + Integer.parseInt(labor_vol_tf.getText()) +",'Выполнено', to_date('" + date_tf.getText() + "', 'dd/mm/yyyy'));");

							else
							statement.executeUpdate("insert into tasks (title, note, labor_vol,status) values (" +
								"'" + title_tf.getText() + "', '"+ note_tf.getText() +"'," + Integer.parseInt(labor_vol_tf.getText()) +", "+ "'Запланировано'" +");");//to_date('" + date_tf.getText() + "', 'mm/dd/yyyy'));");
							labor_vol_tf.setText("");
							isDone = false;
							done_cb.setSelected(false);
							date_tf.setText("");
							note_tf.setText("");
							title_tf.setText("");
							pack();
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
