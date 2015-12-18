	package burnapp;

	import javax.swing.*;
	import java.awt.event.*;
	import java.sql.*;

	public class Main {

		private static final int HEIGT = 1000;
		private static final int WIDTH = 1000;

		public static void main(String args[]) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					ConnectionDial connD = new ConnectionDial();
					connD.setSize(500,200);
					connD.setVisible(true);
					connD.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					connD.setLocationRelativeTo(null);
					//
					// MainWindow mw = new MainWindow("Burndown app");
					//
					// mw.setSize(HEIGT, WIDTH);
					// mw.setVisible(true);
					// mw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					// mw.setLocationRelativeTo(null);


				}
			});
		}
	}
