package graphicPrinterInterface;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;

import symbolicComputation.ExceptionVariableRepresentation;
import function.ExceptionNoVariableAccepted;
import graphicPrinter.Application;
import graphicPrinter.GraphicPrinter;






public class FunctionsParameterInterface implements ActionListener
{
// --------------------------------
// Attributs:
//--------------------------------
		// Parametres
		private final String		title		= "Functions Parameter";
		private final int			width		= 300;
		private final int			height		= 600;
		private final Point			location	= new Point(400, 200);

		private Application			app;
		private JFrame				frame;
		private JComboBox<String>	funcCombo;
		private JComboBox<String>	varCombo;
		private JComboBox<String>	pointCombo;
		private Color[]				newColor;
		private History				history;

//--------------------------------
// Constructeur:
//--------------------------------
		public FunctionsParameterInterface(Application app)
		{
			this.app = app;
		}

//--------------------------------
// Methodes Locales:
//--------------------------------
	public void actionPerformed(ActionEvent e)
	{
		this.history	= new History();
		this.newColor	= new Color[history.color.length];
		if (frame != null)	frame.setVisible(true);
		else				initDialBox();			
	}

//--------------------------------
// Methodes Auxiliaires:
//--------------------------------
		private void initDialBox()
		{
			int nbrFunc				= newColor.length;
			GridLayout disposition	= new GridLayout(22, 1);
			String[] funcName		= new String[nbrFunc+1];
			String[] pointTab		= new String[3];

			// Init Frame
			frame	= new JFrame(title);
			frame.setSize(width, height);
			frame.setLocation(location);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setVisible(true);
			frame.setSize(frame.getWidth(), frame.getHeight());
			frame.setLayout(disposition);
			frame.addComponentListener(new Resizer());

			// First Gap
			frame.add(new JLabel());

			// Init function color Combo box
			JLabel funLab = new JLabel("Set the functions color: ");
			frame.add(funLab);
			funcName[0] = "<Choose a function>";
			for (int i = 0; i<nbrFunc; i++) funcName[i+1] = app.getFunctionName(i);
			funcCombo = new JComboBox<String>(funcName);
			funcCombo.addItemListener(new ColComboListener());
			frame.add(funcCombo);
			frame.add(new JLabel());
			frame.add(new JLabel());

			// Init Variable Combo box
			JLabel varLab = new JLabel("Set the functions variable: ");
			frame.add(varLab);
			varCombo = new JComboBox<String>(funcName);
			varCombo.addItemListener(new VarComboListener());
			frame.add(varCombo);
			frame.add(new JLabel());
			frame.add(new JLabel());

			// Init Point Combo box
			JLabel pointLabel = new JLabel("Set the point representation type: ");
			frame.add(pointLabel);
			pointTab[0] = "<Choose a point type>";
			pointTab[1] = "Cross";
			pointTab[2] = "Point";
			pointCombo = new JComboBox<String>(pointTab);
			pointCombo.addItemListener(new PointComboListener());
			frame.add(pointCombo);
			frame.add(new JLabel());
			frame.add(new JLabel());

			// Init Point size
			JLabel pointSizeLabel = new JLabel("Set the point size: ");
			frame.add(pointSizeLabel);
			JScrollBar sizeBar = new JScrollBar(JScrollBar.HORIZONTAL);
			int value = (int) (app.getPointSizePercent() * sizeBar.getMaximum() / 100);
			sizeBar.setValue(value);
			sizeBar.addAdjustmentListener(new SizeBarListener());
			frame.add(sizeBar);
			frame.add(new JLabel());
			frame.add(new JLabel());

			// Create JRadioButton
			JRadioButton lip	= new JRadioButton("Link Interpolation Point: ", history.linkInterpol);
			JRadioButton dip	= new JRadioButton("Print Interpolation Point", history.displayInterpol);
			lip					.addActionListener(new LIPListener());
			dip					.addActionListener(new DIPListener());
			frame.add(lip);
			frame.add(dip);
			frame.add(new JLabel());

			// Ajout de OK et Cancel
			JButton okBut		= new JButton("OK");
			okBut				.addActionListener(new OkBL());
			frame				.add(okBut);
			JButton canBut		= new JButton("Cancel");
			canBut				.addActionListener(new CancelBL());
			frame				.add(canBut);
		}
		private void cancel()
		{
			app.setAllFunctionsColor(history.color);
			app.setPointType(history.pointType);
			app.setPointSizePercent(history.pointSize);
			app.setDisplayInterpolationPoint(history.displayInterpol);
			app.setLinkInterpolationPoint(history.linkInterpol);
			try
			{
				for (int i=0; i<history.var.size(); i++)
				{
					app.setFunctionVar(history.var.get(i).name, history.var.get(i).id);
				}
			}
			catch(Exception e){e.printStackTrace(); throw new RuntimeException();}
			frame.setVisible(false);
			frame = null;
		}

// ----------------------------------
// Ecouteur des Combo Box Couleur
//----------------------------------
		private class ColComboListener implements ItemListener
		{
			public void itemStateChanged(ItemEvent e)
			{
				int choice   = funcCombo.getSelectedIndex()-1;
				if (choice == -1) return;
				String title = "Color of the function: " + app.getFunctionName(choice);
				Color c = JColorChooser.showDialog(null, title, Color.GREEN);
				if (c != null) 
				{
					app.setFunctionColor(c, choice);
					newColor[choice] = c;
				}
				funcCombo.setSelectedIndex(0);
			}
		}

// ----------------------------------
// Ecouteur des Combo Box Variable
//----------------------------------
		private class VarComboListener implements ItemListener
		{
			public void itemStateChanged(ItemEvent e)
			{
				String message = "";
				int choice   = varCombo.getSelectedIndex()-1;
				if (choice == -1) return;
				if (!app.isFunctionVariableSetable(choice))
				{
					message = "The function " + app.getFunctionName(choice) + " has no setable variable";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);						
					return;
				}
				boolean test = false;
				while (!test)
				{
					test = true;
					message = "Set the variable of the function " + app.getFunctionName(choice);
					String var = JOptionPane.showInputDialog(message);
					if (var == null) break;
					try
					{
						app.setFunctionVar(var, choice);
					}
					catch(ExceptionVariableRepresentation exc)
					{
						message = "wrong variable name";
						JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
						test = false;
					}
					catch (ExceptionNoVariableAccepted e1)
					{
						e1.printStackTrace();
					}
				}
				funcCombo.setSelectedIndex(0);
			}
			
		}

// ----------------------------------
// Ecouteur des Combo Box Point:
//----------------------------------
		private class PointComboListener implements ItemListener
		{
			public void itemStateChanged(ItemEvent e)
			{
				int choice   = pointCombo.getSelectedIndex();
				switch(choice)
				{
					case 0: return;
					case 1: app.setPointType(GraphicPrinter.crossPoint);	break;
					case 2: app.setPointType(GraphicPrinter.roundPoint);	break;
					default: throw new RuntimeException();
				}
			}
		}

//--------------------------------
// Ecouteur Size bar Action:
//--------------------------------
		private class SizeBarListener implements AdjustmentListener
		{
			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				int percentage = (int) (e.getValue() * 10. / 9.);
				app.setPointSizePercent(percentage);
			}
		}

//--------------------------------
// Ecouteur radioButton 
// LinkInterpolationPoint:
//--------------------------------
		private class LIPListener implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				boolean b = app.getLinkInterpolationPoint();
				app.setLinkInterpolationPoint(!b);
			}
		}

//--------------------------------
// Ecouteur radioButton 
// PrintInterpolationPoint:
//--------------------------------
		private class DIPListener implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				boolean b = app.getDisplayInterpolationPoint();
				app.setDisplayInterpolationPoint(!b);
			}
		}

//--------------------------------
// Ecouteur Button OK:
//--------------------------------
		private class OkBL implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				boolean b1 = app.getLinkInterpolationPoint();
				boolean b2 = app.getDisplayInterpolationPoint();
				if (!b1 && !b2)
				{
					String message = "Interpolation fonction can't be draw.\nChange one of the radio button!";
					JOptionPane.showMessageDialog(null, message, title, JOptionPane.OK_OPTION);
				}
				else
				{
					app.setAllFunctionsColor(newColor);
					frame.setVisible(false);
					frame = null;
				}
			}
		}

//--------------------------------
// Ecouteur Button Cancel:
//--------------------------------
		private class CancelBL implements ActionListener
		{
			public void actionPerformed(ActionEvent e){cancel();}
		}

//--------------------------------
// Ecouteur de fenetre:
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

//--------------------------------
// Classe de sauvegarde de 
// l'etat initial:
//--------------------------------
		private class History
		{
			// Attributs
			public Color[]				color;
			public LinkedList<NameId>	var;
			public int					pointType;
			public double				pointSize;
			public boolean				linkInterpol;
			public boolean				displayInterpol;

			// Constructeur
			public History()
			{
				this.color				= app.getFunctionsColor();
				this.pointType			= app.getPointType();
				this.pointSize			= app.getPointSizePercent();
				this.linkInterpol		= app.getLinkInterpolationPoint();
				this.displayInterpol	= app.getDisplayInterpolationPoint();
				this.var				= new LinkedList<NameId>();
				for (int i=0; i<color.length; i++)
					if (app.isFunctionVariableSetable(i)) var.add(new NameId(app.getFunctionVar(i), i));
			}
		}
		private class NameId
		{
			// Attributs
			public String 	name;
			public int		id;

			// Constructeur
			public NameId(String name, int id)
			{
				this.name 	= name;
				this.id		= id;
			}
		}
}