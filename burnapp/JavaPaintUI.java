package burnapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;
import java.util.*;
import org.knowm.xchart.Chart;
import org.knowm.xchart.ChartBuilder;
import org.knowm.xchart.Series;
import org.knowm.xchart.SeriesLineStyle;
import org.knowm.xchart.StyleManager.ChartTheme;
import org.knowm.xchart.SwingWrapper;

public class JavaPaintUI{
private static final long serialVersionUID = 1L;
  protected Connection conn = null;
  protected static Statement st = null;

  public JavaPaintUI(Connection conn) {
    this.conn = conn;
      try{
        st = conn.createStatement();
      }catch(SQLException stExc){
        System.out.println("statement not created");
        stExc.printStackTrace();
      }
  }

  public static Vector<Vector<String>> getTasksDay() {
    Vector<Vector<String>> days = new Vector<Vector<String>>();
    try{
      ResultSet rs = st.executeQuery("select readyday,labor_vol from tasks where readyday is not null");
      Vector<String> vector1 = new Vector<String>();
      Vector<String> vector2 = new Vector<String>();
      while (rs.next()){
          vector1.add(rs.getString("readyday"));
          vector2.add(rs.getString("labor_vol"));
      }
      days.add(vector1);
      days.add(vector2);
      vector1.clear();
      vector2.clear();
      }catch(SQLException e){
        e.printStackTrace();
      }
    return days;
  }
  public static Vector<Vector<String>> getWorkDay() {
    Vector<Vector<String>> days = new Vector<Vector<String>>();
    try{
      ResultSet rs = st.executeQuery("select workday,labor_vol from workdays");
      Vector<String> vector1 = new Vector<String>();
      Vector<String> vector2 = new Vector<String>();
      while (rs.next()){
          vector1.add(rs.getString("workday"));
          vector2.add(rs.getString("labor_vol"));
      }
      days.add(vector1);
      days.add(vector2);
      vector1.clear();
      vector2.clear();
      }catch(SQLException e){
        e.printStackTrace();
      }
    return days;
  }
  public static String getBeginDay() {
    String day = "";
    try{
      ResultSet rs = st.executeQuery("select begindate from sprintdates");
      while (rs.next()) {
        day = rs.getString("begindate");
      }
      }catch(SQLException e){
        e.printStackTrace();
      }
    return day;
  }
  public static String getEndDay() {
    String day = "";
    try{
      ResultSet rs = st.executeQuery("select enddate from sprintdates");
      while (rs.next()) {
        day = rs.getString("enddate");
      }
      }catch(SQLException e){
        e.printStackTrace();
      }
    return day;
  }
  public static int sumLaborVolume() {
    int sum = 0;
    try{
      ResultSet rs = st.executeQuery("select labor_vol from tasks");
      while (rs.next()) {
        sum += rs.getInt("labor_vol");
      }
      }catch(SQLException e){
        e.printStackTrace();
      }
    return sum;
  }
  public Chart getChart() {
  // Create Chart
  Chart chart = new ChartBuilder().width(800).height(600).theme(ChartTheme.Matlab).title("Burndown-диаграмма").xAxisTitle("Рабочие дни").yAxisTitle("Трудоемкость").build();
  chart.getStyleManager().setPlotGridLinesVisible(true);
  chart.getStyleManager().setXAxisTickMarkSpacingHint(100);
  // generate data
  List<Date> x1Data = new ArrayList<Date>();
  List<Date> x2Data = new ArrayList<Date>();
  List<Date> x3Data = new ArrayList<Date>();
  List<Integer> y1Data = new ArrayList<Integer>();
  List<Integer> y2Data = new ArrayList<Integer>();
  List<Integer> y3Data = new ArrayList<Integer>();
  DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  Date date;
  System.out.println(getBeginDay());
  System.out.println(getEndDay());
  int sum = sumLaborVolume();
  try {
  date = sdf.parse(getBeginDay());
  x1Data.add(date);
  x2Data.add(date);
  x3Data.add(date);
  y1Data.add(sum);
  y2Data.add(sum);
  y3Data.add(sum);

  date = sdf.parse(getEndDay());
  x1Data.add(date);
  y1Data.add(0);

  Vector<Vector<String>> days = getWorkDay();
  System.out.println(days.get(0).size());
  if(days.get(0).size()>0){
    int sumT = sum;
    for(int i = 0; i<days.get(0).size(); i++){
      date = sdf.parse(days.get(0).get(i));
      x2Data.add(date);
      sumT = sumT - Integer.parseInt(days.get(1).get(i));
      y2Data.add(sumT);
    }
  }

  Vector<Vector<String>> tasksDays = getTasksDay();
  System.out.println(tasksDays.get(0).size());
  if(tasksDays.get(0).size()>0){
    int sumT = sum;
    for(int i = 0; i<tasksDays.get(0).size(); i++){
      date = sdf.parse(tasksDays.get(0).get(i));
      x3Data.add(date);
      sumT = sumT - Integer.parseInt(tasksDays.get(1).get(i));
      y3Data.add(sumT);
    }
  }
  tasksDays.clear();
  days.clear();
  } catch (ParseException e) {
  e.printStackTrace();
  }
  Series series1 = chart.addSeries("ideal", x1Data, y1Data);
  series1.setLineStyle(SeriesLineStyle.DOT_DOT);
  chart.addSeries("real", x2Data, y2Data);
  chart.addSeries("userstory", x3Data, y3Data);


  return chart;
  }
}
