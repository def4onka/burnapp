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
	import java.text.*;
	import java.util.regex.*;
	import javax.swing.event.*;

	public class MainWindow extends JFrame {
	  private static final long serialVersionUID = 3L;

		final static boolean shouldFill = true;
		final static boolean shouldWeightX = true;

		private static final int VERTICAL_GAP = 10;
		private static final int HORIZONTAL_GAP = 30;
		private static final int TASKS_WIDTH = 600;
		private static final int TASKS_HEIGTH = 500;
		private static final int WORKDAYS_WIDTH = 300;
		private static final int WORKDAYS_HEIGTH = 500;

		protected Connection conn = null;
		protected static Statement st = null;

		JButton change_task = new JButton("Изменить");
		JButton delete_task = new JButton("Удалить");
		JButton addnew_task = new JButton("Добавить");
		JButton change_workday = new JButton("Изменить");
		JButton delete_workday = new JButton("Удалить");
		JButton addnew_workday = new JButton("Добавить");
		JButton showDiag_bttn = new JButton("Показать диграмму");
		static JTextField begin_tf = new JTextField(10);
		static JTextField end_tf = new JTextField(10);
		public JTable tasks_tb;
		protected JTable workdays_tb;

		private AddValues av = new AddValues(this, "Новая задача");
		private AddValuesWD avwd = new AddValuesWD(this, "Новый рабочий день");
		private AddDate aad = new AddDate(this, "Введите дату");
		private static String titleRow = "";
		private static int rowNum = 0;


		public MainWindow(String title, Connection conn) {

			super(title);

			this.conn = conn;

			try{
				st = conn.createStatement();
			}catch(SQLException stExc){
				System.out.println("statement not created");
				stExc.printStackTrace();
			}
			//setPeriodFields("sprintdates");

			int datefind = 0;
			try {
				ResultSet rsx = st.executeQuery("select id from sprintdates;");
				while(rsx.next()){
						datefind = rsx.getInt("id");
					}
			} catch (SQLException aa) {
				aa.printStackTrace();
				System.out.println("NOT QUIET WHAT WAS EXPECTED");
			}
			if(datefind==1){
			setPeriodFields("sprintdates");
			}
			setLayout(new FlowLayout(FlowLayout.LEFT, HORIZONTAL_GAP, VERTICAL_GAP));
			BorderLayout bl = new BorderLayout();
			bl.setHgap(30);
			JPanel panel = new JPanel(bl);
			JPanel headPan = new JPanel(new BorderLayout());

			JLabel head_lbl = new JLabel("Ведение Burndown диаграммы");
			head_lbl.setFont(new Font("Tahoma", Font.PLAIN, 26));
			head_lbl.setVisible(true);
			headPan.add(head_lbl,BorderLayout.NORTH);
			JPanel periodPan = new JPanel();
			periodPan.add(new Label("Дата начала спринта"));
			periodPan.add(begin_tf);
			periodPan.add(new Label("Дата окончания спринта"));
			periodPan.add(end_tf);
			JButton ok_bt = new JButton("Внести");
			periodPan.add(ok_bt);
			headPan.add(periodPan);
			panel.add(headPan,BorderLayout.NORTH);
			JPanel tasksPan = new JPanel(new BorderLayout());
			tasksPan.setSize(700,500);
			MyTableModel mtb = new MyTableModel();
			mtb.setData("tasks");
			tasks_tb = new JTable(mtb);
			tasks_tb.setSize(new Dimension(TASKS_WIDTH, TASKS_HEIGTH));
			tasks_tb.setPreferredSize(new Dimension(TASKS_WIDTH, TASKS_HEIGTH));
			JScrollPane tasks_scrtb = new JScrollPane(tasks_tb);
			tasks_tb.setFillsViewportHeight(true);
			tasks_scrtb.setSize(new Dimension(TASKS_WIDTH, TASKS_HEIGTH));
			tasks_scrtb.setPreferredSize(new Dimension(TASKS_WIDTH, TASKS_HEIGTH));
			JLabel task_lbl = new JLabel("Задачи");
			task_lbl.setFont(new Font("Colibri", Font.PLAIN, 18));
			tasksPan.add(task_lbl,BorderLayout.NORTH);
			tasksPan.add(tasks_scrtb,BorderLayout.CENTER);

			JPanel workdaysPan = new JPanel(new BorderLayout());
			workdaysPan.setSize(300,200);
			//workdaysModel = buildTableModel("workdays");
			MyTableModel mtbwd = new MyTableModel();
			//mtbwd.setColumnNames("День,Трудоемкость");
			mtbwd.setData("workdays");
			workdays_tb = new JTable(mtbwd);
			workdays_tb.setSize(new Dimension(WORKDAYS_WIDTH, WORKDAYS_HEIGTH));
			workdays_tb.setPreferredSize(new Dimension(WORKDAYS_WIDTH, WORKDAYS_HEIGTH));
			JScrollPane workdays_scrtb = new JScrollPane(workdays_tb);
			workdays_tb.setFillsViewportHeight(true);
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
			ok_bt.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					if (isDateStr(begin_tf.getText()) && isDateStr(end_tf.getText())){

						try{
						java.util.Date d1 =  new SimpleDateFormat("yyyy-MM-dd").parse(begin_tf.getText());
						java.util.Date d2 =  new SimpleDateFormat("yyyy-MM-dd").parse(end_tf.getText());
						if(d2.after(d1) && d1.before(d2)){
							int datefind = 0;
							try {
								ResultSet rsx = st.executeQuery("select id from sprintdates;");
								while(rsx.next()){
										datefind = rsx.getInt("id");
									}
							} catch (SQLException cc) {
								cc.printStackTrace();
								System.out.println("NOT QUIET WHAT WAS EXPECTED");
							}
							if(datefind==1){
								try{
									st.executeUpdate("UPDATE sprintdates SET begindate ='"+ begin_tf.getText() +"', enddate ='"+ end_tf.getText() +"';");
									JOptionPane.showMessageDialog(null, "Добавлены", "Уведомленька", JOptionPane.ERROR_MESSAGE);
								}catch (SQLException aa) {
									aa.printStackTrace();
									System.out.println("не обновилось");
								}
							}
							if(datefind==0){
								try{
										st.executeUpdate("INSERT INTO sprintdates (begindate, enddate) VALUES ('"+ begin_tf.getText()+"','"+ end_tf.getText() +"');");
										JOptionPane.showMessageDialog(null, "Добавлены потом ", "Уведомленька", JOptionPane.ERROR_MESSAGE);
									}catch (SQLException bb) {
										bb.printStackTrace();
										System.out.println("не добавилось");
									}
							}

					}else{
						JOptionPane.showMessageDialog(null, "Дата начала должна быть меньше даты окончания", "Ошибочка((", JOptionPane.ERROR_MESSAGE);
					}
				}catch(Exception parseExc){
					parseExc.printStackTrace();
				}
				}else{
					JOptionPane.showMessageDialog(null, "Неверный формат даты", "Ошибочка((", JOptionPane.ERROR_MESSAGE);
				}

			}
			});
			tasks_tb.getSelectionModel().addListSelectionListener(new RowListener());
			tasks_tb.getModel().addTableModelListener(new TableModelListener() {

				public void tableChanged(TableModelEvent e) {

				if (e.getType()!=TableModelEvent.INSERT && e.getType()!=TableModelEvent.DELETE) {
					int row = e.getFirstRow();
					int column = e.getColumn();
				TableModel model = (TableModel)e.getSource();
				String columnName = model.getColumnName(column);
				Object data = model.getValueAt(row, column);

				// Do something with the data...
				System.out.println("data="+data+" row = "+ row+ " col= "+ column
				+"colname= "+columnName+"title= "+ tasks_tb.getValueAt(row,0));

				try{
				if(column == 2)
				st.executeUpdate("update tasks set "+ columnName +"= " + data
				+ "where title = '" + tasks_tb.getValueAt(row,0)+"' ;");
				else{
				if(column==3){
				if(data.equals("Выполнено")){

				if(!aad.isVisible()) {
				setRow((String) tasks_tb.getValueAt(row,0));
				setRowNum(row);
				aad.setVisible(true);
				}
				}else{

				if(data.equals("Запланировано")){
				try{
				Statement statement = conn.createStatement();
				statement.executeUpdate("update tasks set "+ columnName +"= '" + data
				+ "' , readyday = null where title = '" + tasks_tb.getValueAt(row,0)+"' ;");

				tasks_tb.getModel().setValueAt((Object) "",row,4);
				pack();
				statement.close();
				}catch(SQLException aaa){
				JOptionPane.showMessageDialog(null, "Что-то пошло не так", "Уведомленька", JOptionPane.ERROR_MESSAGE);
				aaa.printStackTrace();
				}
				}
				else{
				JOptionPane.showMessageDialog(null, "Проверьте вводимые данные", "Уведомленька", JOptionPane.ERROR_MESSAGE);
				//тут окно с ошибкой типа не вып не зап
				}

				}

				}
				/*
				st.executeUpdate("update tasks set "+ columnName +"= '" + data
				+ "' where title = '" + tasks_tb.getValueAt(row,0)+"' ;");
				*/
				}
				}catch(SQLException updExc){
				System.out.println("updExc !!!!!!!!!!!!!");
				updExc.printStackTrace();
				}
				}else{
				int row = e.getFirstRow();
				System.out.println("rowww= "+row);

				}
				}
    	});

			workdays_tb.getSelectionModel().addListSelectionListener(new RowListener());
			workdays_tb.getModel().addTableModelListener(new TableModelListener() {

      	public void tableChanged(TableModelEvent e) {
										int row = e.getFirstRow();
					if (e.getType()!=TableModelEvent.INSERT && e.getType()!=TableModelEvent.DELETE) {

					int column = e.getColumn();
					TableModel model = (TableModel)e.getSource();
					String columnName = model.getColumnName(column);
					Object data = model.getValueAt(row, column);

				// Do something with the data...
					System.out.println("data="+data+" row = "+ row+ " col= "+ column
					+"colname= "+columnName+"title= "+ tasks_tb.getValueAt(row,0));

					try{
						if(column==1)
							st.executeUpdate("update workdays set "+ columnName +"= '" + data
							+ "' where workday = to_date('" + workdays_tb.getValueAt(row,0)+"','yyyy-MM-dd') ;");
							else
							st.executeUpdate("update workdays set "+ columnName +"= " + data
							+ " where workday = '" + (String)workdays_tb.getValueAt(row,0)+"' ;");
					}catch(SQLException updExc){
						System.out.println("updExc !!!!!!!!!!!!!");
						updExc.printStackTrace();
					}
				}else{

						System.out.println("rowww= "+row);
				}
			}
    	});


			addnew_task.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {

								if(!av.isVisible()) {
									av.setVisible(true);
								}
							}

						});

			addnew_workday.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {

								if(!avwd.isVisible()) {
									avwd.setVisible(true);
								}
							}

						});


			delete_task.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {
								delRowInMain((MyTableModel)tasks_tb.getModel(),"tasks", tasks_tb);

							}

						});


			delete_workday.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {
								delRowInMain((MyTableModel)workdays_tb.getModel(),"workdays",workdays_tb);

							}

						});

			showDiag_bttn.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {

								JavaPaintUI exampleChart = new JavaPaintUI(conn/*,begin_tf.getText(),end_tf.getText()*/);
								Chart chart = exampleChart.getChart();
								JFrame frame = new JFrame("Диаграмма");
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

		public void deleteQuery(String title,String nametable,String colname){
			try{
				System.out.println("title= "+title);
			st.executeUpdate("delete from "+nametable+" where "+ colname +" ='"+ title +"';");
		}catch(SQLException e){
			System.out.println("Deleting failed");
			e.printStackTrace();
		}
		}

		public static void setPeriodFields(String nametable){
			try{
					ResultSet rs = st.executeQuery("select * from " + nametable + " where id = 1;");
					while(rs.next()){
						begin_tf.setText(rs.getString("begindate"));
						end_tf.setText(rs.getString("enddate"));
					}

			}catch(SQLException rsmdExc){
				System.out.println("setfields not success");
				rsmdExc.printStackTrace();
			}
		}

		public static boolean isDateStr(String testString){
					 Pattern p = Pattern.compile("[0-9]{4}-(0[1-9]|1[012])-(0[1-9]|1[0-9]|2[0-9]|3[01])");
					 Matcher m = p.matcher(testString);
					 return m.matches();
	 }

	 private void addRowInMain(Object[] obj, MyTableModel customModel) {
					 customModel.safeAddRow(obj);
	 }

	 private void delRowInMain(MyTableModel customModel,String nametable, JTable table) {
					 customModel.safeDelRow(table.getSelectionModel().getLeadSelectionIndex(),nametable,table);
	 }

	 class AddDate extends JDialog{
		private static final long serialVersionUID = 4L;

	 private JButton sendToDbBttn = new JButton("Добавить");
	 private JTextField date_tf = new JTextField(10);

	 public AddDate(JFrame frame, String title){
	 super(frame,title,true);
	 JPanel stuffPan = new JPanel(new GridBagLayout());
	 GridBagConstraints c = new GridBagConstraints();
	 if (shouldFill) {
	 c.fill = GridBagConstraints.HORIZONTAL;
	 }
	 if (shouldWeightX) {
	 c.weightx = 1.0;
	 }
	 c.fill = GridBagConstraints.HORIZONTAL;
	 c.insets = new Insets(5, 10, 5, 10);
	 c.ipady = 12;
	 c.gridwidth = 2;
	 c.gridx = 0;
	 c.gridy = 0;
	 Label changDay = new Label("Введите дату выполнения");
	 changDay.setFont(new Font("Colibri", Font.BOLD, 14));
	 stuffPan.add(changDay, c);
	 c.gridwidth = 1;
	 c.insets = new Insets(0, 10, 0, 0);
	 c.gridx = 0;
	 c.gridy = 1;
	 stuffPan.add(new Label("Дата: "), c);
	 c.insets = new Insets(0, 0, 0, 10);
	 c.gridx = 1;
	 stuffPan.add(date_tf, c);
	 c.insets = new Insets(5, 10, 5, 10);
	 c.gridwidth = 2;
	 c.gridx = 0;
	 c.gridy = 2;
	 stuffPan.add(sendToDbBttn, c);
	 add(stuffPan);

	 pack();
	 setLocationRelativeTo(frame);

	 sendToDbBttn.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e) {
	 Vector<Object> task_row = new Vector<Object>();
	 if(isDateStr(date_tf.getText())){
	 try{
	 Statement statement = conn.createStatement();
	 statement.executeUpdate("update tasks set status = 'Выполнено',readyday = '" + date_tf.getText() + "' where title = '" + getRow() + "';");
	 task_row.add(date_tf.getText());
	 tasks_tb.getModel().setValueAt((Object) date_tf.getText(),getRowNum(),4);
	 pack();
	 statement.close();
	 } catch (Exception r) {
	 r.printStackTrace();
	 System.out.println("Oops, here goes some shit again");
	 }
	 setVisible(false);
	 }else{
	 JOptionPane.showMessageDialog(null, "Неверный формат даты", "Уведомленька", JOptionPane.ERROR_MESSAGE);
	 }
	 }
	 });

	 }
	 }

		class AddValues extends JDialog {
			private static final long serialVersionUID = 2L;
			private JTextField title_tf = new JTextField(10);
			private JTextField note_tf = new JTextField(10);
			private JTextField labor_vol_tf = new JTextField(10);
			private JTextField date_tf = new JTextField(10);
			private JCheckBox done_cb = new JCheckBox();
			private boolean isDone = false;
			private JButton sendToDbBttn = new JButton("Добавить");


			public AddValues(JFrame frame, String title) {

				super(frame, title, true);

				JPanel stuffPan = new JPanel(new GridBagLayout());

				GridBagConstraints c = new GridBagConstraints();
				if (shouldFill) {
				c.fill = GridBagConstraints.HORIZONTAL;
				}
				if (shouldWeightX) {
				c.weightx = 1.0;
				}
				c.fill = GridBagConstraints.HORIZONTAL;
				c.insets = new Insets(5, 10, 5, 10);

				c.ipady = 12;
				c.gridwidth = 2;
				c.gridx = 0;
				c.gridy = 0;
				Label addTasks = new Label("Введите данные добавляемой задачи");
				addTasks.setFont(new Font("Colibri", Font.BOLD, 14));
				stuffPan.add(addTasks, c);
				c.gridwidth = 1;
				c.insets = new Insets(0, 10, 0, 0);
				c.gridx = 0;
				c.gridy = 1;
				stuffPan.add(new Label("Задача: "), c);
				c.insets = new Insets(0, 0, 0, 10);
				c.gridx = 1;
				stuffPan.add(title_tf, c);
				c.insets = new Insets(0, 10, 0, 0);
				c.gridx = 0;
				c.gridy = 2;
				stuffPan.add(new Label("Описание задачи: "), c);
				c.insets = new Insets(0, 0, 0, 10);
				c.gridx = 1;
				stuffPan.add(note_tf, c);
				c.insets = new Insets(0, 10, 0, 0);
				c.gridx = 0;
				c.gridy = 3;
				stuffPan.add(new Label("Трудоемкость: "), c);
				c.insets = new Insets(0, 0, 0, 10);
				c.gridx = 1;
				stuffPan.add(labor_vol_tf, c);
				c.insets = new Insets(0, 10, 0, 0);
				c.gridx = 0;
				c.gridy = 4;
				stuffPan.add(new Label("Выполнена: "), c);
				c.insets = new Insets(0, 0, 0, 10);
				c.gridx = 1;
				stuffPan.add(done_cb, c);
				c.insets = new Insets(0, 10, 0, 0);
				Label date_lbl = new Label("Дата: ");
				c.gridx = 0;
				c.gridy = 5;
				stuffPan.add(date_lbl, c);
				c.insets = new Insets(0, 0, 0, 10);
				c.gridx = 1;
				stuffPan.add(date_tf, c);
				date_lbl.setVisible(false);
				date_tf.setVisible(false);

				c.insets = new Insets(5, 10, 5, 10);
				c.gridwidth = 2;
				c.gridx = 0;
				c.gridy = 6;
				stuffPan.add(sendToDbBttn, c);
				add(stuffPan);

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
				isDone = false;
				date_lbl.setVisible(false);
				date_tf.setVisible(false);
				pack();
				}
				}
				});


				pack();
				setLocationRelativeTo(frame);

				sendToDbBttn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Vector<Object> task_row = new Vector<Object>();
						try{
							Statement statement = conn.createStatement();
							if(isDone){
								statement.executeUpdate("insert into tasks (title, note, labor_vol, status, readyday) values (" +
												"'" + title_tf.getText() + "', '"+ note_tf.getText() +"'," + Integer.parseInt(labor_vol_tf.getText()) +",'Выполнено', to_date('" + date_tf.getText() + "', 'yyyy-mm-dd'));");
							task_row.add(title_tf.getText());
							task_row.add(note_tf.getText());
							task_row.add(Integer.parseInt(labor_vol_tf.getText()));
							task_row.add("Выполнено");
							task_row.add(date_tf.getText());
							}else{
							statement.executeUpdate("insert into tasks (title, note, labor_vol,status) values (" +
								"'" + title_tf.getText() + "', '"+ note_tf.getText() +"'," + Integer.parseInt(labor_vol_tf.getText()) +", "+ "'Запланировано'" +");");//to_date('" + date_tf.getText() + "', 'mm/dd/yyyy'));");
								task_row.add(title_tf.getText());
								task_row.add(note_tf.getText());
								task_row.add(Integer.parseInt(labor_vol_tf.getText()));
								task_row.add("Запланировано");
								task_row.add("");
									System.out.println("vo[0] "+task_row.get(0)+"vo[1] "+task_row.get(1)+"vo[2] "+task_row.get(2));
								}
							Object[] tmp = task_row.toArray();
							addRowInMain(tmp,(MyTableModel)tasks_tb.getModel());
							task_row.clear();
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
		public static String getRow(){
		return titleRow;
		}

		public static void setRow(String title){
		titleRow = title;
		}

		public static void setRowNum(int a){
		rowNum = a;
		}
		public static int getRowNum(){
		return rowNum;
		}


		class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
    private Vector<String> columnNames = new Vector<String>();
		private Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		public void setData(String nametable){
			try{
				ResultSet rs = st.executeQuery("select * from " + nametable);
				ResultSetMetaData	 rsmd = rs.getMetaData();

				try{
					int columnCount = rsmd.getColumnCount();
			    for (int column = 2; column <= columnCount; column++) {
			        columnNames.add(rsmd.getColumnName(column));
			    }
					// data of the table
					while (rs.next()) {
							Vector<Object> vector = new Vector<Object>();
							//int columnCount = rsmd.getColumnCount();
							for (int columnIndex = 2; columnIndex <= columnCount; columnIndex++) {
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
		}

		public void safeAddRow(Object[] o) {
				Vector<Object> vo = new Vector<Object>();
					for(int i=0; i<o.length;i++)
							vo.add(o[i]);
					data.add(vo);
					System.out.println("data.size()= " + data.size());
					System.out.println("vo[0] "+vo.get(0)+"vo[1] "+vo.get(1));
					if (SwingUtilities.isEventDispatchThread()) {
						addRow(vo);
    			}
					else {
       			SwingUtilities.invokeLater(new Runnable() {
          		public void run() {
								addRow(vo);
							}
       			});
    			}
		}

		public void addRow(Vector<Object> vo) {

			this.fireTableRowsInserted(data.size()-1, data.size()-1);
		}

		public void safeDelRow(int row,String nametable,JTable jt) {
			if (SwingUtilities.isEventDispatchThread()) {
				delRow(row,nametable,jt);
			} else {
			 SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						delRow(row,nametable,jt);
					}
			 });
			}
		}

		public void delRow(int row,String nametable,JTable jt) {
			if(nametable.equals("tasks"))
				deleteQuery((String)jt.getValueAt(row,0),nametable,"title");
			else
				deleteQuery(new SimpleDateFormat("yyyy-MM-dd").format(jt.getValueAt(row,0)),nametable,"workday");
			data.remove(row);
			this.fireTableRowsDeleted(row,row);
		}

    public int getColumnCount() {
      return columnNames.size();
    }

    public int getRowCount() {
      return data.size();
    }

    public String getColumnName(int col) {
      return columnNames.get(col);
    }

    public Object getValueAt(int row, int col) {
      return data.get(row).get(col);
    }

    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      if (col == 4) {
				if(data.get(row).get(3).equals("Выполнено"))
        return true;
				else
				return false;
      } else {
        return true;
      }
    }

		public void setValueAt(Object value, int row, int col) {
			data.get(row).set(col,value);
			fireTableCellUpdated(row, col);
		}

	 }


		class AddValuesWD extends JDialog {
			private static final long serialVersionUID = 5L;

			private JTextField labor_vol_tf = new JTextField(10);
			private JTextField date_tf = new JTextField(10);
			private JButton sendToDbBttn = new JButton("Добавить");


			public AddValuesWD(JFrame frame, String title) {

				super(frame, title, true);

				JPanel container = new JPanel(new GridLayout(3, 1));

				JPanel stuffPanel = new JPanel(new GridLayout(2, 2));
				stuffPanel.add(new Label("Дата: "));
				stuffPanel.add(date_tf);
				stuffPanel.add(new Label("Трудоемкость:"));
				stuffPanel.add(labor_vol_tf);


				container.add(new Label("Введите данные добавляемого рабочего дня"));
				container.add(stuffPanel);
				container.add(sendToDbBttn);
				add(container);

				pack();
				setLocationRelativeTo(frame);

				sendToDbBttn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(isDateStr(date_tf.getText())){
						try{
							Statement statement = conn.createStatement();
							statement.executeUpdate("insert into workdays (workday, labor_vol) values ( '" + date_tf.getText() + "', " + Integer.parseInt(labor_vol_tf.getText()) +");");
							Vector<Object> tmp = new Vector<Object>();
							tmp.add(date_tf.getText());
							tmp.add(Integer.parseInt(labor_vol_tf.getText()));
							System.out.println("vo[0] "+tmp.get(0)+"vo[1] "+tmp.get(1));
							Object[] dayObj=tmp.toArray();
							addRowInMain(dayObj,(MyTableModel)workdays_tb.getModel());
							tmp.clear();
							labor_vol_tf.setText("");
							date_tf.setText("");
							pack();
							statement.close();
						} catch (Exception r) {
							r.printStackTrace();
							System.out.println("Oops, here goes some shit again");
						}

						setVisible(false);

				}else{
					JOptionPane.showMessageDialog(null, "Неверный формат даты", "Ошибочка((", JOptionPane.ERROR_MESSAGE);
				}
			}
				});


			}
		}

		private class RowListener implements ListSelectionListener {
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting()) {
			return;
			}

			int rowSelected = tasks_tb.getSelectionModel().getLeadSelectionIndex();
			if(rowSelected!=-1) {
			System.out.println("Row selected: "+tasks_tb.getSelectionModel().getLeadSelectionIndex());
			}

			}
		}


	}
