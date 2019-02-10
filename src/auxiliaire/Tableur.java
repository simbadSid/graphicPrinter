package auxiliaire;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;









@SuppressWarnings("serial")
public class Tableur extends JPanel
{
//--------------------------------------------
// Attributs:
//--------------------------------------------
	// Parameters
	private final int							titleBorder		= 3;
	private final int							cellBorder		= 1;
	private final Color							cellColor		= Color.BLACK;

	private LinkedList<LinkedList<JTextField>>	elements;
	private LinkedList<JTextField>				titlesX;
	private LinkedList<JTextField>				titlesY;

//--------------------------------------------
// Constructeur:
//--------------------------------------------
	public Tableur(int width, int height, int x, int y, String[] titleX, String[] titleY) throws RuntimeException
	{
		this.setSize(width, height);
		this.reset(x, y, titleX, titleY);
	}

//--------------------------------------------
// Constructeur:
//--------------------------------------------
	/************************************************
	 * Remove all the dattas and reset the nummber of
	 * ligns and columns
	 ************************************************/
	public void reset(int columns, int ligns, String[] titleX, String[] titleY)
	{
		if ((columns<= 0) || (ligns<=0))	throw new RuntimeException("Wrong dimension: lign = " + ligns + "   columns = " + columns);
		if (columns != titleX.length)		throw new RuntimeException("Missing X Titles: columns = " + columns + "  titlesX = " + titleX.length);
		if ((titleY != null) &&(ligns != titleY.length))	
											throw new RuntimeException("Missing Y Titles: ligns = " + ligns + "  titlesY = " + titleY.length);

		this.removeAll();
		int c = 0;
		if (titleY != null) c = 1;
		GridLayout disposition	= new GridLayout(ligns + 1, columns + c);
		this.setLayout(disposition);
		this.elements	= new LinkedList<LinkedList<JTextField>>();
		this.titlesX	= new LinkedList<JTextField>();
		if (titleY != null)
		{
			this.titlesY = new LinkedList<JTextField>();
			JTextField t = new JTextField("Titles");
			t.setEditable(false);
			t.setBorder(BorderFactory.createLineBorder(cellColor, titleBorder));
			this.add(t);
		}
		else this.titlesY = null;

		// Init titlesX
		for (int i = 0; i<columns; i++)
		{
			JTextField t	= new JTextField(titleX[i]);
			t.setEditable(false);
			t.setBorder(BorderFactory.createLineBorder(cellColor, titleBorder));
			this.add(t);
			titlesX.add(t);
		}
		// Init titlesY
		if (titlesY != null)
		for (int i = 0; i<ligns; i++)
		{
			JTextField t	= new JTextField(titleY[i]);
			t.setEditable(false);
			t.setBorder(BorderFactory.createLineBorder(cellColor, titleBorder));
			titlesY.add(t);
		}
		// Init Cels
		for (int i=0; i<ligns; i++)
		{
			if (titlesY != null) this.add(titlesY.get(i));
			LinkedList<JTextField> l = new LinkedList<JTextField>();
			for (int j=0; j<columns; j++)
			{
				JTextField jta = new JTextField();
				jta.setBorder(BorderFactory.createLineBorder(cellColor, cellBorder));
				l.add(jta);
				this.add(jta);
			}
			elements.add(l);
		}
	}
	public String toString()
	{
		String res = "";
		int maxLen	= 0;

		// Trouver la longueur max
		for (int y=0; y<elements.size(); y++)
		{
			for (int x=0; x<elements.get(y).size(); x++)
			{
				String txt = elements.get(y).get(x).getText();
				if ((txt != null)  && (txt.length() > maxLen)) maxLen = txt.length();
			}
		}
		// Ecrire
		for (int y=0; y<elements.size(); y++)
		{
			for (int x=0; x<elements.get(y).size(); x++)
			{
				String txt = this.getElement(x, y);
				if (txt == null) for (int i=0; i<maxLen; i++) res += " ";
				else
				{
					res += txt;
					for (int i=0; i<maxLen-txt.length(); i++) res += " ";
				}
				res += " | ";
			}
			res += "\n";
		}
		return res;
	}

//--------------------------------------------
// Accesseur:
//--------------------------------------------
	public String getElement(int x, int y)	{return elements.get(y).get(x).getText();}
	public int getNbrLign()					{return elements.size();}
	public int getNbrColumn()				{return elements.get(0).size();}

//--------------------------------------------
// Modificateur:
//--------------------------------------------
	public void setElement(int x, int y, String txt)
	{
		elements.get(y).get(x).setText(txt);
	}
	/*****************************************************
	 * Ajoute des colonnes au tableur
	 *****************************************************/
	public void addColumns(int nbrColumns, String[] titles)
	{
		if (nbrColumns < 0) 			throw new RuntimeException("negative number: nbrColumns = " + nbrColumns );
		if (titles.length < nbrColumns)	throw new RuntimeException("Missing Titles!");

		int nbrLigns 			= getNbrLign();
		int oldNbrColumns		= getNbrColumn();
		this.removeAll();
		int c = 0;
		if (titlesY != null) c = 1;
		GridLayout disposition	= new GridLayout(nbrLigns+1, oldNbrColumns+nbrColumns+c);
		this.setLayout(disposition);

		// Set Titles
		if (titlesY != null)
		{
			JTextField t = new JTextField("Titles");
			t.setEditable(false);
			t.setBorder(BorderFactory.createLineBorder(cellColor, titleBorder));
			add(t);
		}
		for (int i = 0; i<oldNbrColumns; i++)	{this.add(this.titlesX.get(i));}
		for (int i = 0; i<nbrColumns; i++)
		{
			JTextField jta = new JTextField(titles[i]);
			jta.setEditable(false);
			jta.setEditable(false);
			jta.setBorder(BorderFactory.createLineBorder(this.cellColor, titleBorder));
			add(jta);
			titlesX.add(jta);
		}

		// Set Table
		for (int y=0; y<nbrLigns; y++)
		{
			if (titlesY != null) add(titlesY.get(y));
			// Put the old elements
			for (int x=0; x<oldNbrColumns; x++){this.add(elements.get(y).get(x));}
			// Put the new elements
			for (int x = 0; x<nbrColumns; x++)
			{
				JTextField jta = new JTextField();
				jta.setBorder(BorderFactory.createLineBorder(cellColor, cellBorder));
				this.add(jta);
				this.elements.get(y).add(jta);
			}
		}
	}
	/*****************************************************
	 * Ajoute des Lignes au tableur
	 *****************************************************/
	public void addLign(int nl, String[] ti)
	{
		if (nl < 0) 							throw new RuntimeException("negative number: nbrColumns = " + nl );
		if ((titlesY != null) && ((ti == null) || (ti.length < nl)))
												throw new RuntimeException("Missing Titles!");

		int nbrColumns 		= getNbrColumn();
		int oldNbrLigns		= getNbrLign();
		this.removeAll();
		int c = 0;
		if (titlesY != null) c = 1;
		GridLayout disposition	= new GridLayout(oldNbrLigns+nl+1, nbrColumns+c);
		this.setLayout(disposition);

		// Set Titles X
		if (titlesY != null)
		{
			JTextField t = new JTextField("Titles");
			t.setEditable(false);
			t.setBorder(BorderFactory.createLineBorder(cellColor, titleBorder));
			add(t);
		}
		for (int i = 0; i<nbrColumns; i++)	{add(titlesX.get(i));}

		// Set Table
		// Put the old elements
		for (int y=0; y<oldNbrLigns; y++)
		{
			if (titlesY != null) add(titlesY.get(y));
			for (int x=0; x<nbrColumns; x++){add(elements.get(y).get(x));}
		}
		// Put the new elements
		for (int y=oldNbrLigns; y<oldNbrLigns+nl; y++)
		{
			elements.add(new LinkedList<JTextField>());
			if (titlesY != null) 
			{
				JTextField jta = new JTextField(ti[y-oldNbrLigns]);
				jta.setEditable(false);
				jta.setEditable(false);
				jta.setBorder(BorderFactory.createLineBorder(cellColor, titleBorder));
				add(jta);
				titlesY.add(jta);
			}
			for (int x=0; x<nbrColumns; x++)
			{
				JTextField jta = new JTextField();
				jta.setBorder(BorderFactory.createLineBorder(cellColor, cellBorder));
				this.add(jta);
				this.elements.get(y).add(jta);
				add(jta);
			}
		}
	}
	/************************************************
	 * Remove the last columns
	 ************************************************/
	public void removeColumns(int nbrColumns)
	{
		if (nbrColumns < 0) throw new RuntimeException("negative number: nbrColumns = " + nbrColumns );
		if (nbrColumns >= getNbrColumn()) throw new RuntimeException("Too much columns : nbrColumns" + nbrColumns );

		int nbrLign			= getNbrLign();
		int oldNbrColumn	= getNbrColumn();
		int c = 0;
		if (titlesY != null) c = 1;
		GridLayout disposition = new GridLayout(nbrLign+1, oldNbrColumn-nbrColumns+c);
		this.removeAll();
		this.setLayout(disposition);

		// Set title
		if (titlesY != null)
		{
			JTextField t = new JTextField("Titles");
			t.setEditable(false);
			t.setBorder(BorderFactory.createLineBorder(cellColor, titleBorder));
			add(t);
		}
		for (int i=0; i<oldNbrColumn-nbrColumns; i++)			{this.add(titlesX.get(i));}
		for (int i=oldNbrColumn-nbrColumns; i<oldNbrColumn; i++){this.titlesX.removeLast();}
		// Set Table
		for (int y=0; y<nbrLign; y++)
		{
			if (titlesY != null)									this.add(titlesY.get(y));
			for (int x=0; x<oldNbrColumn-nbrColumns; x++) 			this.add(elements.get(y).get(x));
			for (int x=oldNbrColumn-nbrColumns; x<oldNbrColumn; x++)this.elements.get(y).removeLast();
		}
	}
	/************************************************
	 * Remove the last ligns
	 ************************************************/
	public void removeLign(int nbrLigns)
	{
		if (nbrLigns < 0) throw new RuntimeException("negative number: nbrColumns = " + nbrLigns);
		if (nbrLigns >= getNbrLign()) throw new RuntimeException("Too much columns : nbrColumns" + nbrLigns );

		int oldNbrLign	= getNbrLign();
		int nbrColumn	= getNbrColumn();
		int c = 0;
		if (titlesY != null) c = 1;
		GridLayout disposition = new GridLayout((oldNbrLign-nbrLigns)+1, nbrColumn+c);
		this.removeAll();
		this.setLayout(disposition);

		// Set title
		if (titlesY != null)
		{
			JTextField t = new JTextField("Titles");
			t.setEditable(false);
			t.setBorder(BorderFactory.createLineBorder(cellColor, titleBorder));
			add(t);
		}
		for (int i=0; i<nbrColumn; i++){this.add(titlesX.get(i));}
		// Set Table
		for (int y=0; y<oldNbrLign-nbrLigns; y++)
		{
			if (titlesY != null) add(titlesY.get(y));
			for (int x=0; x<nbrColumn; x++) this.add(elements.get(y).get(x));
		}
		for (int y=oldNbrLign-nbrLigns; y<oldNbrLign; y++)
		{
			if (titlesY != null) titlesY.removeLast();
			elements.removeLast();
		}
	}
}