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

import symbolicComputation.Domain;
import function.PiecesOfPolynomials;
import function.Polynomial;
import graphicPrinter.Application;
import auxiliaire.Tableur;







public class AddPiecesOfPolynomialInterface implements ActionListener
{
// --------------------------------
// Attributs:
// --------------------------------
	// Parametres
	private final String			title					= "Pieces of polynomials Function Creation";
	private final int				width					= 300;
	private final int				height					= 600;
	private final Point				location				= new Point(400, 200);
	private final Point				tabLocation				= new Point(400, 150);
	private final int				tabWidth				= 400;
	private final int				tabHeight				= 600;
	private final int				defaultNbrPolynomial	= 2;
	private final int				defaultPolynomialDeg	= 2;

	private Application				app;
	private JFrame					frame;
	private JFrame					tabFrame;
	private JPanel					pan;
	private JTextField				functionArea;
	private JTextField				nbrPolynomialArea;
	private JTextField				polynomialDegArea;
	private Tableur					tabl;
	private Color					color;

//--------------------------------
//Constructeur:
//--------------------------------
		public AddPiecesOfPolynomialInterface( Application app)
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
			GridLayout disposition	= new GridLayout(18, 1);
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
			this.nbrPolynomialArea	= new JTextField("" + defaultNbrPolynomial);
			this.nbrPolynomialArea	.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			this.polynomialDegArea	= new JTextField("" + defaultPolynomialDeg);
			this.polynomialDegArea	.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			pan						.add(new JLabel());
			pan						.add(new JLabel("Function Name: "));
			pan						.add(functionArea);
			pan						.add(new JLabel());
			pan						.add(new JLabel("Number Of Polynomials"));
			pan						.add(nbrPolynomialArea);
			pan						.add(new JLabel());
			pan						.add(new JLabel("Max Polynomial Degres"));
			pan						.add(this.polynomialDegArea);
			pan						.add(new JLabel());

			// Edit Point pan
			String[] titleY = new String[this.defaultNbrPolynomial];
			String[] titleX = new String[this.defaultPolynomialDeg + 2];
			titleX[0]= "x min";
			titleX[1]= "x max";
			for (int i=0; i<this.defaultPolynomialDeg; i++)	titleX[i+2]= "X^" + i;
			for (int i=0; i<this.defaultNbrPolynomial; i++)		titleY[i]= "P" + i;
			this.tabl				= new Tableur(tabWidth, tabHeight, defaultPolynomialDeg+2, defaultNbrPolynomial, titleX, titleY);
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

//--------------------------------
//Cancel
//--------------------------------
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

//--------------------------------
//Load from file:
//--------------------------------
		private boolean loadPiecesOfPolynomialsFunction(Scanner sc)
		{
			String func;
			int nbrPoly;
			Integer polyDeg = null;
			Polynomial[] polys;
			Domain[] doms;

			try
			{
				func 		= sc.nextLine();
				nbrPoly		= sc.nextInt();
				polys		= new Polynomial[nbrPoly];
				doms		= new Domain[nbrPoly];
				for (int i = 0; i<nbrPoly; i++)
				{
					double min			= sc.nextDouble();
					double max			= sc.nextDouble();
					int  nbrCoeff		= sc.nextInt();
					boolean isMinIn		= true;
					// Init domain
					if ((i != 0) && (doms[i-1].isX1In) && (min == doms[i-1].x1))
					{
						isMinIn = false;
						doms[i-1].isX1In = false;
					}
					if ((polyDeg == null) || (polyDeg < nbrCoeff)) polyDeg = nbrCoeff;
					double[] poly = new double[nbrCoeff];
					Domain d = new Domain(min, max, isMinIn, true);
					for (int j=0; j<nbrCoeff; j++)
					{
						poly[j] = sc.nextDouble();
					}
					polys[i] = new Polynomial(poly);
					doms[i]	 = d;
				}
				new PiecesOfPolynomials(polys, doms, "");
			}
			catch(Exception e){e.printStackTrace();return false;}
			functionArea.setText(func);
			nbrPolynomialArea.setText("" + nbrPoly);
			polynomialDegArea.setText("" + polyDeg);
			String[] titleY = new String[nbrPoly];
			String[] titleX = new String[polyDeg + 2];
			titleX[0]= "x min";
			titleX[1]= "x max";
			for (int i=0; i<polyDeg; i++)	titleX[i+2]	= "X^" + i;
			for (int i=0; i<nbrPoly; i++)	titleY[i]	= "P" + i;
			tabl.reset(polyDeg+2, nbrPoly, titleX, titleY);
			for (int i=0; i<nbrPoly; i++)
			{
				Polynomial p 	= polys[i];
				Domain d		= doms[i];
				tabl.setElement(0, i, ""+d.x0);
				tabl.setElement(1, i, ""+d.x1);
				for (int j=0; j<=p.getDegres(); j++)
				{
					tabl.setElement(j+2, i, ""+p.getCoeff(j));
				}
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
				try	{sc = new Scanner(fic);}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
					String message = "The input file has not been found";
					JOptionPane.showInternalMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				boolean test;
				test = loadPiecesOfPolynomialsFunction(sc);
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
				int nbrPoly=-1, polyDeg=-1;
				try
				{
					nbrPoly = Integer.parseInt(nbrPolynomialArea.getText());
					polyDeg = Integer.parseInt(polynomialDegArea.getText());
					if (nbrPoly <= 0) {nbrPoly = -1; throw new RuntimeException();}
					if (polyDeg <= 0) {polyDeg = -1; throw new RuntimeException();}
				}
				catch(Exception e)
				{
					String message;
					if 		(nbrPoly == -1) message = "Wrong number of polynomials";
					else if (polyDeg == -1) message = "Wrong polynomials degre ";
					else					message = "Wrong Input";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				tabFrame = new JFrame("Edit Points Coordinates");
				tabFrame.setSize(tabWidth, tabHeight);
				tabFrame.setLocation(tabLocation);
				tabFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				tabFrame.setVisible(true);
				int nl = tabl.getNbrLign();
				int nc = tabl.getNbrColumn() - 2;
				if (nl > nbrPoly)		tabl.removeLign(nl - nbrPoly);
				else if (nl < nbrPoly)
				{
					String[] strl = new String[nbrPoly-nl];
					for (int i=0; i<nbrPoly-nl; i++) strl[i] = "P" + (i+nl);
					tabl.addLign(nbrPoly-nl, strl);
				}
				if (nc > polyDeg)		tabl.removeColumns(nc - polyDeg);
				else if (nc < polyDeg)
				{
					String[] strc = new String[polyDeg-nc+2];
					for (int i=0; i<polyDeg-nc; i++) strc[i] = "X^" + (i+nc);
					tabl.addColumns(polyDeg-nc, strc);
				}
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
				int nbrPoly	= Integer.parseInt(nbrPolynomialArea.getText());
				int degPoly	= Integer.parseInt(polynomialDegArea.getText());
				Polynomial[] poly	= new Polynomial[nbrPoly];
				Domain[] domain		= new Domain[nbrPoly];
				try
				{
					String func		= functionArea.getText();
					for (int i = 0; i<nbrPoly; i++)
					{
						double[] l		= new double[degPoly+1];
						double min		= Double.parseDouble(tabl.getElement(0, i));
						double max		= Double.parseDouble(tabl.getElement(1, i));
						boolean isMinIn	= true;
						// Init domain
						if ((i != 0) && (domain[i-1].isX1In) && (min == domain[i-1].x1))
						{
							isMinIn = false;
							domain[i-1].isX1In = false;
						}
						// Init polynomial
						for (int j=0; j<degPoly; j++)
						{
							Double coeff = null;
							String c = tabl.getElement(j+2, i);
							if ((c == null) || (c.length() == 0))	coeff = 0.;
							else									coeff = Double.parseDouble(c);
							l[j] = coeff;
						}
						poly[i]		= new Polynomial(l);
						domain[i]	= new Domain(min, max, isMinIn, true);
					}
					PiecesOfPolynomials f = new PiecesOfPolynomials(poly, domain, func);
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