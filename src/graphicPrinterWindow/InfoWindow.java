package graphicPrinterWindow;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;








@SuppressWarnings("serial")
public class InfoWindow extends JPanel
{
//-------------------------------------------------
// Attributs
//-------------------------------------------------
	// Parametres de la fenetre
	private Color			backGroundColor		=	Color.LIGHT_GRAY;

	private JTextPane		functionPan;
	private JTextPane		functionTypePan;
	private JTextPane		domainPan;
	private JTextPane		dialogPan;
	private JTextPane		positionPan;
	private JLabel			functionLab;
	private JLabel			functionTypeLab;
	private JLabel			domainLab;
	private JLabel			dialogLab;
	private JLabel			positionLab;

//-------------------------------------------------
// Constructeur
//-------------------------------------------------
	public InfoWindow(int width, int height)
	{
		super();
		this.setSize(width, height);
		this.setBackground(backGroundColor);
		this.functionPan		= new JTextPane();
		this.functionTypePan	= new JTextPane();
		this.domainPan			= new JTextPane();
		this.dialogPan			= new JTextPane();
		this.positionPan		= new JTextPane();
		this.functionLab		= new JLabel("Function Expression:");
		this.functionTypeLab	= new JLabel("Function Type:");
		this.domainLab			= new JLabel("Definition Domain:");
		this.dialogLab			= new JLabel("Informtion:");
		this.positionLab		= new JLabel("Mouse position:");
		this.functionPan		.setEditable(false);
		this.functionTypePan	.setEditable(false);
		this.domainPan			.setEditable(false);
		this.dialogPan			.setEditable(false);
		this.positionPan		.setEditable(false);
		GridLayout disposition = new GridLayout(10, 1);
	    this.setLayout(disposition);
	    this.add(functionLab);
	    this.add(functionPan);
	    this.add(functionTypeLab);
	    this.add(functionTypePan);
	    this.add(domainLab);
	    this.add(domainPan);
	    this.add(dialogLab);
	    this.add(dialogPan);
	    this.add(positionLab);
	    this.add(positionPan);
	}

// ------------------------------------------------
// Modificateur
//-------------------------------------------------
	public void setFunction(String function, String variable)
	{
		if (function.length() == 0) this.functionPan.setText("");
		else						this.functionPan.setText("f(" + variable + ") = " + function);
	}
	public void setFunctionType(String functionType)
	{
		this.functionTypePan.setText(functionType);
	}
	public void setFunctionDomain(String domain)
	{
		this.domainPan.setText(domain);
	}
	public void setDialog(String text)
	{
		this.dialogPan.setText(text);
	}
	public void setPosition(String txt)
	{
		this.positionPan.setText(txt);
	}
}