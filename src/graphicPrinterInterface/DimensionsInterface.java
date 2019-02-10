package graphicPrinterInterface;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import graphicPrinter.Application;
import graphicPrinterIhm.GraphicGround;








public class DimensionsInterface implements ActionListener
{
// ------------------------
// Attributs
// ------------------------
	// Parametres
		private final String			title		= "Set Dimensions Interface";
		private final int				width		= 350;
		private final int				height		= 500;
		private final Point				location	= new Point(400, 200);

		private Application				app;
		private GraphicGround			gg;
		private JFrame					frame;
		private JTextField				xMin;
		private JTextField				xMax;
		private JTextField				yMin;
		private JTextField				yMax;

// ------------------------
// Constructeur:
// ------------------------
		public DimensionsInterface(Application app, GraphicGround gg)
		{
			this.app	= app;
			this.gg		= gg;
		}

// ------------------------
// Methodes Locales:
// ------------------------
		public void actionPerformed(ActionEvent e)
		{
			if (frame != null)	frame.setVisible(true);
			else				initDialBox();
		}

// ------------------------
//Methodes Locales:
//------------------------
		private void initDialBox()
		{
			// Init Frame
			GridLayout disposition	= new GridLayout(5, 2);
			frame	= new JFrame(title);
			frame.setSize(width, height);
			frame.setLocation(location);
			frame.setLayout(disposition);
			frame.addComponentListener(new Resizer());
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setResizable(false);
			frame.setVisible(true);

			// Init Text Area
			this.xMin	= new JTextField("" + gg.getXMin());
			this.xMax	= new JTextField("" + gg.getXMax());
			this.yMin	= new JTextField("" + gg.getYMin());
			this.yMax	= new JTextField("" + gg.getYMax());
			JLabel lab1	= new JLabel("x min: ");
			JLabel lab2	= new JLabel("x max: ");
			JLabel lab3	= new JLabel("y min: ");
			JLabel lab4	= new JLabel("y max: ");
			frame.add(lab1);
			frame.add(xMin);
			frame.add(lab2);
			frame.add(xMax);
			frame.add(lab3);
			frame.add(yMin);
			frame.add(lab4);
			frame.add(yMax);

			// Init Buttons
			JButton okBut		= new JButton("OK");
			JButton cancelBut	= new JButton("Cancel");
			okBut.addActionListener(new OkButListener());
			cancelBut.addActionListener(new CancelButListener());
			frame.add(okBut);
			frame.add(cancelBut);
		}
		private void cancel()
		{
			if (frame == null) return;
			frame.setVisible(false);
			frame = null;
		}
// -----------------------------------
// Ecouteur button Ok
// -----------------------------------
		private class OkButListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				
				double XMin = Double.parseDouble(xMin.getText());
				double XMax = Double.parseDouble(xMax.getText());
				double YMin = Double.parseDouble(yMin.getText());
				double YMax = Double.parseDouble(yMax.getText());
				if ((XMin >= XMax) || (YMin >= YMax))
				{
					String message = "Wrong inputs: ";
					if (XMin >= XMax)	message += "xMin is bigger than xMax";
					else				message += "yMin is bigger than yax";
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				app.resetAxes(XMin, XMax, YMin, YMax);	
				frame.setVisible(false);
				frame = null;
			}			
		}

// -----------------------------------
// Ecouteur button Cancel
//-----------------------------------
		private class CancelButListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				cancel();
			}			
		}

//--------------------------------
//Ecouteur de fenetre:
//--------------------------------
		private class Resizer implements ComponentListener
		{
			public void componentHidden(ComponentEvent arg0)
			{
				cancel();
			}
			public void componentMoved(ComponentEvent arg0)		{}
			public void componentResized(ComponentEvent arg0)	{}
			public void componentShown(ComponentEvent arg0)		{}			
		}
}