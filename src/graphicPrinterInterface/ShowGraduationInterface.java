package graphicPrinterInterface;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import graphicPrinter.Application;








public class ShowGraduationInterface  implements ActionListener
{
// ------------------------------------ Attributs ----------------------------
	private Application 	app;
	private boolean			show		= false;
	private JFrame			frame;
	private JTextField		xTextArea;
	private JTextField		yTextArea;

// ------------------------------------ Constructeur--------------------------
	public ShowGraduationInterface(Application app)
	{
		this.app	= app;
	}

// ------------------------------------ Methodes locales ---------------------
	public void actionPerformed(ActionEvent e) 
	{
		if (show == true)
		{
			show = false;
			if (frame != null){frame.setVisible(false); frame = null;}
			app.setGraduation(show, -1, -1);
		}
		else
		{
			if (frame != null) {frame.setVisible(true); return;}
			initDialBox();
		}
	}

//------------------------------------
//Methodes Auxiliaires
//-----------------------------------
		private void initDialBox()
		{
			this.frame = new JFrame("Set Graduation Dimension");
			this.frame.setSize(250, 400);
			this.frame.setLocation(450, 200);
			GridLayout disposition = new GridLayout(6, 1);
			frame.setLayout(disposition);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

			// Initialisation des label
			JLabel xLabel			= new JLabel("Dimension of the X axe graduation:");
			JLabel yLabel			= new JLabel("Dimension of the Y axe graduation:");

			// Initialisation des zones de text
			xTextArea				= new JTextField();
			yTextArea				= new JTextField();

			// Initialisation des boutons
			JButton	okButton		= new JButton("OK");
			JButton	cancelButton	= new JButton("Cancel");
			okButton				.addActionListener(new okButtonListener());				
			cancelButton			.addActionListener(new cancelButtonListener());				

			frame.add(xLabel);
			frame.add(xTextArea);
			frame.add(yLabel);
			frame.add(yTextArea);
			frame.add(okButton);
			frame.add(cancelButton);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setVisible(true);
		}

//------------------------------------
//Listener des boutons
//-----------------------------------
		private class okButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent arg0)
			{
				double x, y;
				String tx	= xTextArea.getText();
				String ty	= yTextArea.getText();
				try
				{
					x = Double.parseDouble(tx);
					y = Double.parseDouble(ty);
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(null, "The string does not contain a parsable double");
					return;					
				}
				if ((x <= 0) || (y <= 0))
				{
					JOptionPane.showMessageDialog(null, "The x and y dimensions must to be Strictly positive");
					return;
				}
				boolean test = app.setGraduation(true, x, y);				
				if (test == true) show = true;
				frame.setVisible(false);
				frame = null;
			}
		}
		private class cancelButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				frame.setVisible(false);
				frame = null;
			}
		}
}