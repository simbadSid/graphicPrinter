package graphicPrinterInterface;

import graphicPrinter.Application;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;






public class FieldColorInterface implements ActionListener
{
// --------------------------------
// Attributs:
// --------------------------------
		// Parametres
		private final String	title		= "Set Field Colors";
		private final int		width		= 300;
		private final int		height		= 400;
		private final Point		location	= new Point(400, 200);

		// Identifiants des boutons
		private final int		bg			= 0;
		private final int		axes		= 1;
		private final int		grid		= 2;
		private final int		graduation	= 3;

		private Application		app;
		private JFrame			frame;
		private History			history;

// --------------------------------
// Constructeur:
//--------------------------------
		public FieldColorInterface(Application app)
		{
			this.app = app;
		}

// --------------------------------
// Methodes Locales:
//--------------------------------
	public void actionPerformed(ActionEvent e)
	{
		this.history = new History(app.getBGColor(), app.getAxesColor(), app.getGridColor(), app.getGraduationColor());
		if (frame != null)	frame.setVisible(true);
		else				initDialBox();
	}

// --------------------------------
// Methodes Auxiliaires:
//--------------------------------
		private void initDialBox()
		{
			// Init Frame
			frame	= new JFrame(title);
			frame.setSize(width, height);
			frame.setLocation(location);
			GridLayout disposition = new GridLayout(5, 2);
			frame.setLayout(disposition);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setVisible(true);
	
			// Init Labels
			JLabel bgLab	= new JLabel("Background Color: ");
			JLabel axLab	= new JLabel("Axes Color: ");
			JLabel gridLab	= new JLabel("Grid Color: ");
			JLabel gradLab	= new JLabel("GraduationColor: ");
	
			// Init Boutons
			JButton bgBut	= new JButton("Choose ..");		bgBut	.addActionListener(new ColBL(bg));
			JButton axesBut	= new JButton("Choose ..");		axesBut	.addActionListener(new ColBL(axes));
			JButton gridBut	= new JButton("Choose ..");		gridBut	.addActionListener(new ColBL(grid));
			JButton gradBut	= new JButton("Choose ..");		gradBut	.addActionListener(new ColBL(graduation));
			JButton okBut	= new JButton("OK");			okBut	.addActionListener(new OkBL());
			JButton canBut	= new JButton("Cancel");		canBut	.addActionListener(new CancelBL());

			// Ajout des elements
			frame.add(bgLab);
			frame.add(bgBut);
			frame.add(axLab);
			frame.add(axesBut);
			frame.add(gridLab);
			frame.add(gridBut);
			frame.add(gradLab);
			frame.add(gradBut);
			frame.add(okBut);
			frame.add(canBut);
		}

// --------------------------------
// Button Action Listener (Chooser):
// --------------------------------
		private class ColBL  implements ActionListener
		{
			// Attribut
			private int butId;

			// Constructeur
			public ColBL (int butId) {this.butId = butId;}
			public void actionPerformed(ActionEvent arg0)
			{
				Color c = JColorChooser.showDialog(null, "Choose a color: ", Color.RED);
				switch(butId)
				{
					case bg:			app.setBGColor(c);			break;
					case axes:			app.setAxesColor(c);		break;
					case grid:			app.setGridColor(c);		break;
					case graduation:	app.setGraduationColor(c);	break;
					default: throw new RuntimeException("Unknown button Id");
				}
			}
		}
// --------------------------------
// Button Action Listener (OK):
// --------------------------------
		private class OkBL implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				frame.setVisible(false);
				frame = null;
			}
		}

// --------------------------------
// Button Action Listener (Cancel):
// --------------------------------
		private class CancelBL implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				app.setAllFieldColors(history.bg, history.axes, history.grid, history.graduation);
				frame.setVisible	(false);
				frame = null;
			}
		}

// ----------------------------------
//Class de sauvegarde de l'historique
//----------------------------------
		private class History
		{
			// Attributs
			public Color bg;
			public Color axes;
			public Color grid;
			public Color graduation;

			// Constructeur
			public History(Color bg, Color axes, Color grid, Color graduation)
			{
				this.bg			= bg;
				this.axes		= axes;
				this.grid		= grid;
				this.graduation	= graduation;
			}
		}

}