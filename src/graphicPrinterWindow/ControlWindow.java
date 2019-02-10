package graphicPrinterWindow;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;










@SuppressWarnings("serial")
public class ControlWindow extends JPanel
{
// ------------------------------------------------
// Attributs
//-------------------------------------------------
		// Parametres de la fenetre
		private final Color	backGroundColor		= Color.LIGHT_GRAY;

		// Attributs graphiques
		private JButton	axesButton;
		private JButton	gridButton;
		private JButton	graduationButton;
		private JButton	tangentButton;

//-------------------------------------------------
// Constructeur
//-------------------------------------------------
		public ControlWindow(int width, int height)
		{
			super();
			this.setSize(width, height);
			this.setBackground(backGroundColor);
			this.axesButton			= new JButton("Hide Axes");
			this.gridButton			= new JButton("Show Grid");
			this.graduationButton	= new JButton("Show Graduation");
			this.tangentButton		= new JButton("Print Tangent");
			GridLayout disposition = new GridLayout(5, 1);
		    this.setLayout(disposition);
		    this.add(axesButton);
		    this.add(gridButton);
		    this.add(graduationButton);
		    this.add(tangentButton);
		}

// ------------------------------------------------
// Modificateur de text:
// -------------------------------------------------
		public void setAxesButtonText(boolean show)
		{
			String txt;
			if (show )	txt = "Hide Axes";
			else		txt = "Show Axes";
			this.axesButton.setText(txt);
		}
		public void setGridButtonText(boolean show)
		{
			String txt;
			if (show )	txt = "Hide Grid";
			else		txt = "Show Grid";
			this.gridButton.setText(txt);
		}
		public void setGraduationButtonText(boolean show)
		{
			String txt;
			if (show )	txt = "Hide Graduarion";
			else		txt = "Show Graduation";
			this.graduationButton.setText(txt);
		}
		public void setTangentButtonText(boolean show)
		{
			String txt;
			if (show )	txt = "Hide Tangent";
			else		txt = "Show Tangent";
			this.tangentButton.setText(txt);
		}

// ------------------------------------------------
// Modificateur de "ActionListener":
// -------------------------------------------------
		public void setAxesButtonActionListener(ActionListener al)
		{
			this.axesButton.addActionListener(al);
		}
		public void setGridButtonActionListener(ActionListener al)
		{
			this.gridButton.addActionListener(al);
		}
		public void setGraduationButtonActionListener(ActionListener al)
		{
			this.graduationButton.addActionListener(al);
		}
		public void setTangentButtonActionListener(ActionListener al)
		{
			this.tangentButton.addActionListener(al);
		}
}