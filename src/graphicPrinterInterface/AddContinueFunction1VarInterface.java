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

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import symbolicComputation.ExceptionDomainRepresentation;
import symbolicComputation.ExceptionEmptyDomain;
import symbolicComputation.ExceptionFunctionRepresentation;
import symbolicComputation.ExceptionVariableRepresentation;
import function.ContinueFunction1Var;
import graphicPrinter.Application;






public class AddContinueFunction1VarInterface implements ActionListener
{
// --------------------------------
//Attributs:
//--------------------------------
	// Parametres
	private final String			title		= "Continue Function Creation";
	private final int				width		= 350;
	private final int				height		= 500;
	private final Point				location	= new Point(400, 200);

	private Application				app;
	private JFrame					frame;
	private JPanel					pan;
	private JTextField				functionArea;
	private JTextField				variableArea;
	private JTextField				domainArea;
	private Color					color;

//--------------------------------
//Constructeur:
//--------------------------------
		public AddContinueFunction1VarInterface( Application app)
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
			GridLayout disposition	= new GridLayout(15, 1);
			frame	= new JFrame(title);
			frame.setSize(width, height);
			frame.setLocation(location);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setResizable(false);
			frame.setVisible(true);
			pan = new JPanel();
			pan.setSize(width, height);
			pan.setLayout(disposition);
			pan.addComponentListener(new Resizer());

			pan.add(new JLabel());
			this.functionArea	= new JTextField();
			this.variableArea	= new JTextField();
			this.domainArea		= new JTextField();
			pan.add(new JLabel("Function Expression"));
			pan.add(functionArea);
			pan.add(new JLabel());
			pan.add(new JLabel("Variable Expression"));
			pan.add(variableArea);
			pan.add(new JLabel());
			pan.add(new JLabel("Domain Expression"));
			pan.add(domainArea);
			pan.add(new JLabel());
			pan.add(new JLabel());

			// Ajout des boutons
			JButton loadBut	= new JButton("Load From File");
			loadBut			.addActionListener(new loadFromFileBL());
			pan				.add(loadBut);
			JButton colBut	= new JButton("Set Color");
			colBut			.addActionListener(new colorBL());
			pan				.add(colBut);
			JButton okBut	= new JButton("OK");
			okBut			.addActionListener(new OkBL());
			pan				.add(okBut);
			JButton canBut	= new JButton("Cancel");
			canBut			.addActionListener(new CancelBL());
			pan				.add(canBut);

			// Ajout du panneau
			frame			.add(pan);
		}

//--------------------------------
//Cancel
//--------------------------------
		private void cancel()
		{
			frame.setVisible(false);
			frame = null;
		}

//--------------------------------
//Load from file
//--------------------------------
		private boolean loadContinueFunction(Scanner sc)
		{
			try
			{
				String func	= sc.nextLine();
				String var	= sc.nextLine();
				String dom	= sc.nextLine();
				new ContinueFunction1Var(func, var, dom);
				this.functionArea.setText(func);
				this.variableArea.setText(var);
				this.domainArea.setText(dom);
				return true;
			}
			catch(Exception e){return false;}
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
					String message = "The input file has not been found";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				boolean test;
				test = loadContinueFunction(sc);
				if (test == false)
				{
					String message = "An error has been detected in the input file";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
				}
				sc.close();
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
				ContinueFunction1Var f = null;
				String func			= functionArea.getText();
				String var			= variableArea.getText();
				String dom			= domainArea.getText();
				if ((func.length() == 0) || (var.length() == 0) || (dom.length() == 0))
				{
					String message = "";
					if 		(func.length() == 0)	message = "Missing function representation!";
					else if (var.length() == 0)		message = "Missing variable representation!";
					else							message = "Missing definition domain representation!";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				boolean test = true;
				String message = "";
				try {f = new ContinueFunction1Var(func, var, dom);}
				catch (ExceptionFunctionRepresentation e)	{test = false; message = "Wrong function representation! ";}
				catch (ExceptionEmptyDomain e)				{test = false; message = "Wrong Domain representation (Empty Domain)";}
				catch (ExceptionDomainRepresentation e)		{test = false; message = "Wrong domain representation!";}
				catch (ExceptionVariableRepresentation e) 	{test = false; message = "Wrong variable representation";}
				if (!test)
				{
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				app.addFunction(f, color);
				frame.setVisible(false);
				frame = null;
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