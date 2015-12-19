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

    public static int sumLaborVolume(String nametable) {
      // names of columns
      Vector<String> columnNames = new Vector<String>();
      Vector<Vector<Object>> data = new Vector<Vector<Object>>();
      try{
        ResultSet rs = st.executeQuery("select * from " + nametable);
        ResultSetMetaData  rsmd = rs.getMetaData();
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

      return 0;

    }

  public Chart getChart() {
    // Create Chart
    Chart chart = new ChartBuilder().width(800).height(600).theme(ChartTheme.Matlab).title("Burndown-диаграмма").xAxisTitle("Рабочие дни").yAxisTitle("Трудоемкость").build();
    chart.getStyleManager().setPlotGridLinesVisible(false);
    chart.getStyleManager().setXAxisTickMarkSpacingHint(100);
    // generate data
    List<Date> x1Data = new ArrayList<Date>();
    List<Date> x2Data = new ArrayList<Date>();
    List<Double> y1Data = new ArrayList<Double>();
    List<Double> y2Data = new ArrayList<Double>();
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-DD");
    Date date;
    try {
      date = sdf.parse("2012-08-11");
      x1Data.add(date);
      y1Data.add(120d);
      y2Data.add(15d);
      date = sdf.parse("2012-11-23");
      x1Data.add(date);
      y1Data.add(165d);
      y2Data.add(15d);
      date = sdf.parse("2013-01-12");
      x1Data.add(date);
      y1Data.add(210d);
      y2Data.add(20d);
      date = sdf.parse("2013-02-13");
      x1Data.add(date);
      y1Data.add(400d);
      y2Data.add(30d);
      date = sdf.parse("2013-03-15");
      x1Data.add(date);
      y1Data.add(800d);
      y2Data.add(100d);
      date = sdf.parse("2013-04-23");
      x1Data.add(date);
      y1Data.add(2000d);
      y2Data.add(120d);
      date = sdf.parse("2013-05-13");
      x1Data.add(date);
      y1Data.add(3000d);
      y2Data.add(150d);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Series series1 = chart.addSeries("ideal", x1Data, y1Data);
    series1.setLineStyle(SeriesLineStyle.DOT_DOT);
    chart.addSeries("real", x1Data, y2Data);
    return chart;
  }
}
