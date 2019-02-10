package graphicPrinterInterface;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import auxiliaire.Tableur;
import function.LagrangeInterpolationFunction;
import graphicPrinter.Application;










public class AddLagrangeInterpolationFunctionInterface implements ActionListener
{
// --------------------------------
// Attributs:
//--------------------------------
	// Parametres
	private final String			title		= "Lagrange Interpolation Function Creation";
	private final int				width		= 300;
	private final int				height		= 600;
	private final Point				location	= new Point(400, 200);
	private final Point				tabLocation	= new Point(400, 150);
	private final int				tabWidth	= 300;
	private final int				tabHeight	= 600;

	private Application				app;
	private JFrame					frame;
	private JFrame					tabFrame;
	private JPanel					pan;
	private JTextField				functionArea;
	private JTextField				nbrPointArea;
	private Tableur					tabl;
	private Color					color;

// --------------------------------
//Constructeur:
//--------------------------------
		public AddLagrangeInterpolationFunctionInterface( Application app)
		{
			this.app = app;
		}

//--------------------------------
//Methodes Locales:
//--------------------------------
	public void actionPerformed(ActionEvent e)
	{
		color = null;
		if (frame != null)	frame.setVisible(true);
		else				initDialBox(0);
	}

//--------------------------------
//Dial Box 
//--------------------------------
		private void initDialBox(int type)
		{
			// Init frame
			GridLayout disposition	= new GridLayout(14, 1);
			frame	= new JFrame(title);
			frame.setSize(width, height);
			frame.setLocation(location);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.addComponentListener(new Resizer());
			pan = new JPanel();
			pan.setSize(width, height);
			pan.setLayout(disposition);

			// Init Text area
			this.functionArea		= new JTextField();
			this.functionArea		.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			this.nbrPointArea		= new JTextField("5");
			this.nbrPointArea		.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			pan						.add(new JLabel());
			pan						.add(new JLabel("Function Name: "));
			pan						.add(functionArea);
			pan						.add(new JLabel());
			pan						.add(new JLabel("Number Of Points"));
			pan						.add(nbrPointArea);
			pan						.add(new JLabel());

			// Edit Point pan
			String[] title = {"Abscissa", "ordered"};
			this.tabl				= new Tableur(tabWidth, tabHeight, 2, 5, title, null);
			JLabel pointLab			= new JLabel("Edit Points");
			JButton pointBut		= new JButton("...");
			pointBut				.addActionListener(new pointBL());
			JPanel p = new JPanel();
			GridLayout g = new GridLayout(1, 2);
			p.setLayout(g);
			p.add(pointLab);
			p.add(pointBut);
			pan.add(p);
			pan.add(new JLabel());

			// Init buttons
			pan						.add(new JLabel());
			JButton loadBut			= new JButton("Load From File");
			loadBut					.addActionListener(new loadFromFileBL());
			pan						.add(loadBut);
			JButton colBut			= new JButton("Set Color");
			colBut					.addActionListener(new colorBL());
			pan						.add(colBut);
			JButton okBut			= new JButton("OK");
			okBut					.addActionListener(new OkBL());
			pan						.add(okBut);
			JButton canBut			= new JButton("Cancel");
			canBut					.addActionListener(new CancelBL());
			pan						.add(canBut);

			// Ajout du panneau
			frame			.add(pan);
		}

// --------------------------------
// Cancel
// --------------------------------
		private void cancel()
		{
			frame.setVisible(false);
			frame = null;
			if (tabFrame != null)
			{
				tabFrame.setVisible(false);
				tabFrame = null;
			}
		}

// --------------------------------
// Load from file:
// --------------------------------
		private boolean loadInterpolationFunction(Scanner sc)
		{
			double[] X, Y;
			String func;
			int nbrPoint;

			try
			{
				func 		= sc.nextLine();
				nbrPoint	= sc.nextInt();
				X			= new double[nbrPoint];
				Y			= new double[nbrPoint];
				for (int i = 0; i<nbrPoint; i++)
				{
					X[i] = sc.nextDouble();
					Y[i] = sc.nextDouble();
				}
				new LagrangeInterpolationFunction(X, Y, "");
			}
			catch(Exception e){e.printStackTrace();return false;}
			this.nbrPointArea.setText("" + nbrPoint);
			String[] title = {"Abscissa", "ordered"};
			tabl.reset(2, nbrPoint, title, null);
			this.functionArea.setText(func);
			this.nbrPointArea.setText(""+nbrPoint);
			for (int i=0; i<X.length; i++)
			{
				tabl.setElement(0, i, ""+X[i]);
				tabl.setElement(1, i, ""+Y[i]);
			}
			if (tabFrame != null)tabFrame.repaint();
			return true;
		}

//--------------------------------
//Ecouteur Button "load From File":
//--------------------------------
		private class loadFromFileBL  implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser fc = new JFileChooser();
				int res			= fc.showOpenDialog(null);
				if (res == JFileChooser.CANCEL_OPTION) return;
				File fic = fc.getSelectedFile();
				Scanner sc;
				try								{sc = new Scanner(fic);}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
					String message = "The input file has not been found";
					JOptionPane.showInternalMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				boolean test;
				test = loadInterpolationFunction(sc);
				if (test == false)
				{
					String message = "An error has been detected in the input file";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
				}
				sc.close();
			}
		}

//--------------------------------
//Ecouteur Button "Edit Points":
//--------------------------------
		private class pointBL implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				int nbrPoint;
				try
				{
					nbrPoint = Integer.parseInt(nbrPointArea.getText());
					if (nbrPoint <= 0) throw new RuntimeException();
				}
				catch(Exception e){JOptionPane.showMessageDialog(null, "Wrong point number", "", JOptionPane.ERROR_MESSAGE); return;}
				tabFrame = new JFrame("Edit Points Coordinates");
				tabFrame.setSize(tabWidth, tabHeight);
				tabFrame.setLocation(tabLocation);
				tabFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				tabFrame.setVisible(true);
				int n = tabl.getNbrLign();
				if (n > nbrPoint)		tabl.removeLign(n - nbrPoint);
				else if (n < nbrPoint)	tabl.addLign(nbrPoint - n, null);
				tabFrame.add(tabl);
			}
		}

//--------------------------------
//Ecouteur Button set Color:
//--------------------------------
		private class colorBL implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				Color c = JColorChooser.showDialog(null, "Function Color", Color.red);
				color = c;
			}
		}

//--------------------------------
//Ecouteur Button OK:
//--------------------------------
		private class OkBL implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				int nbrPoint	= Integer.parseInt(nbrPointArea.getText());
				double[] X		= new double[nbrPoint];
				double[] Y		= new double[nbrPoint];
				try
				{
					String func		= functionArea.getText();
					for (int i = 0; i<nbrPoint; i++)
					{
						X[i] = Double.parseDouble(tabl.getElement(0, i));
						Y[i] = Double.parseDouble(tabl.getElement(1, i));
					}
					LagrangeInterpolationFunction f = new LagrangeInterpolationFunction(X, Y, func);
					app.addFunction(f, color);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					String message = "An error has been detected in the input datta";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
					return;
				}				
				frame.setVisible(false);
				frame = null;
				if (tabFrame != null)
				{
					tabFrame.setVisible(false);
					tabFrame = null;
				}
			}
		}

//--------------------------------
//Ecouteur Button Cancel:
//--------------------------------
		private class CancelBL implements ActionListener
		{
			public void actionPerformed(ActionEvent e){cancel();}
		}

//--------------------------------
//Ecouteur de fenetre:
//--------------------------------
		private class Resizer implements ComponentListener
		{
			public void componentHidden(ComponentEvent arg0)
			{
				if (frame == null) return;
				cancel();
			}
			public void componentMoved(ComponentEvent arg0)		{}
			public void componentResized(ComponentEvent arg0)	{}
			public void componentShown(ComponentEvent arg0)		{}			
		}
}