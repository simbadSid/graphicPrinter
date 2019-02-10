package symbolicComputation;

import java.util.LinkedList;




public class Domain
{
// -------------------------------
// Attributs
// -------------------------------
	public double	x0;
	public double	x1;
	public boolean	isX0In	= true;
	public boolean	isX1In	= true;

// -------------------------------
// Constructeur
//-------------------------------
	public Domain(double x0, double x1) throws RuntimeException
	{
		if (x0 >= x1) throw new RuntimeException("Wrong Domain: "+ x0+", "+x1);
		this.x0 = x0;
		this.x1 = x1;
		if (Double.isInfinite(x0))	this.isX0In	= false;
		if (Double.isInfinite(x1))	this.isX1In	= false;
	}
	public Domain(double x0, double x1, boolean isX0In, boolean isX1In)throws RuntimeException
	{
		this(x0, x1);
		this.isX0In	= isX0In;
		this.isX1In	= isX1In;
	}
	public Domain(Domain d)
	{
		this.x0		= d.x0;
		this.x1		= d.x1;
		this.isX0In	= d.isX0In;
		this.isX1In	= d.isX1In;
	}

// -------------------------------
// Methode Locales
//-------------------------------
	public boolean isInDomain(double x)
	{
		if ((x <  x0) || (x > x1))	return false;
		if ((x >  x0) && (x < x1))	return true;
		if ((x == x0) && (isX0In))	return true;
		if ((x == x1) && (isX1In))	return true;
		return false;
	}
	/*==================================================================
	 * Rend le dommaine minimal contenant d et le domaine courant
	 * Rend null si les 2 domaines ne peuvent pas fusionner(intersection nulle)
	 ===================================================================*/
	public Domain merge(Domain d)
	{
		if (!intersect(d))	 return null;							// Domaines distinct
		if ((d.x0 == x0) && (d.x1 == x1) && 
			(d.isX0In == isX0In) && (d.isX1In == isX1In))	return new Domain(d);
		Domain test;
		test = auxMerge(d, this);
		if (test != null) return test;
		test = auxMerge(this, d);
		return test;
	}
	/*=============================================================
	 * Indique si le domaine courent intersecte d
	 ==============================================================*/
	public boolean intersect(Domain d)
	{
		if (((d.x0 == x0) && (d.isX0In == isX0In)) ||
			((d.x1 == x1) && (d.isX1In == isX1In)))						return true;
		if ((d.x1 > x0) || ((d.x1 == x0) && (d.isX1In) && (isX0In)))	return true;
		if ((d.x0 < x1) || ((d.x0 == x1) && (d.isX0In) && (isX1In))) 	return true;
		return false;
	}
	public String toString()
	{
		char c;
		if (isX0In)	c = '[';
		else		c = ']';
		String res = c + " " + x0 + " --> " + x1;
		if (isX1In)	res += ']';
		else		res += '[';
		return res;
	}
// -------------------------------
// Parseur de chaines de char
//-------------------------------
	/*==============================================================
	 * Annalyse la chaine en entree pour construire le
	 * tableau de dommaines
	 * @param String: chaine representant des domaines de la forme
	 * [a, b] ou [a, b[ ou ]a, b] ou ]a, b[   separes par 'U'
	 ===============================================================*/
	public static Domain[] parseDomain(String domaine)
	{
		LinkedList<Domain> dl = new LinkedList<Domain>();
		int i = 0, l = domaine.length();
		double x0, x1;
		boolean isX0In, isX1In;

		try
		{
			while (i < l)
			{
				while(domaine.charAt(i) == ' ') i++;
				if (i >= l) break;
				switch (domaine.charAt(i))											// Lire '[' ou ']'
				{
					case '[': isX0In = true;	break;
					case ']': isX0In = false;	break;
					default	: throw new Exception();
				}
				i++;
				while (domaine.charAt(i) == ' ') i++;
				String s = "";
				int sig = 1;
				if (domaine.charAt(i) == '-'){sig = -1; i++;}
				while (((domaine.charAt(i) >='0') && (domaine.charAt(i)<= '9')) ||
						(domaine.charAt(i) == '.'))									// Lire le chiffre min
				{
					s += domaine.charAt(i);
					i++;
				}
				if (s.length() == 0) throw new Exception();
				x0 = sig * Double.parseDouble(s);
				while (domaine.charAt(i) == ' ') i++;				
				if (domaine.charAt(i) != ',')	throw new Exception();				// Lire ','
				i++;
				while (domaine.charAt(i) == ' ') i++;
				s = "";
				sig = 1;
				if (domaine.charAt(i) == '-'){sig = -1; i++;}
				while (((domaine.charAt(i) >='0') && (domaine.charAt(i)<= '9')) ||
						(domaine.charAt(i) == '.'))									// Lire le chiffre max
				{
					s += domaine.charAt(i);
					i++;
				}
				if (s.length() == 0) throw new Exception();
				x1 = Double.parseDouble(s);
				while (domaine.charAt(i) == ' ') i++;				
				switch (domaine.charAt(i))											// Lire '[' ou ']'
				{
					case '[': isX1In = false;	break;
					case ']': isX1In = true;	break;
					default	: throw new Exception();
				}
				dl.add(new Domain(x0, x1, isX0In, isX1In));
				i++;
				while ((i<l) && (domaine.charAt(i) == ' ')) i++;
				if (i == l) break;
				if ((domaine.charAt(i) == 'U') || (domaine.charAt(i) == 'u')) i++;
				else throw new Exception();			
			}
		}
		catch(Exception e){e.printStackTrace(); throw new RuntimeException("Unknown Domaine: " + domaine);}

		return toArray(dl);
	}

// -------------------------------
// Methodes Auxiliaires
//-------------------------------
	private static Domain[] toArray(LinkedList<Domain> l)
	{
		Domain[] res = new Domain[l.size()];
		for (int i=0; i<l.size(); i++) res[i] = l.get(i);
		return res;
	}
	private Domain auxMerge(Domain d0, Domain d1)
	{
		boolean dInfCour0 = d0.x0 < d1.x0;
		boolean dEquCour0 = d0.x0 == d1.x0;
		boolean dSupCour0 = d0.x0 > d1.x0;
		boolean dInfCour1 = d0.x1 < d1.x1;
		boolean dEquCour1 = d0.x1 == d1.x1;
		boolean dSupCour1 = d0.x1 > d1.x1;

		if (((dSupCour0) || ((dEquCour0) && (!d0.isX0In) && (d1.isX0In))) &&		//		-----		d0
			((dInfCour1) || ((dEquCour1) && (!d0.isX1In) && (d1.isX1In))))			//	-------------	d1
		{
			return new Domain(d1.x0, d1.x1, d1.isX0In, d1.isX1In);
		}
		if (((dInfCour0) || ((dEquCour0) && (d0.isX0In) 	&& (!d1.isX0In))) &&	//	--------		d0
			((dInfCour1) || ((dEquCour1) && (!d0.isX1In) && (d1.isX1In))))			// 		--------	d1
		{
			return new Domain(d0.x0, d1.x1, d0.isX0In, d1.isX1In);
		}
		if (((dSupCour0) || ((dEquCour0) && (!d0.isX0In) && (d1.isX0In))) &&		//		--------	d0
			((dSupCour1) || ((dEquCour1) && (d0.isX1In)  && (!d1.isX1In))))			//	--------		d1
		{
			return new Domain(d1.x0, d0.x1, d1.isX0In, d0.isX1In);
		}
		if (((d0.x0 > d1.x0) || ((d0.x0 == d1.x0) && (!d0.isX0In) && (d1.isX0In))) &&//		---------	d0
			((d0.x1 == d1.x1) && (d0.isX1In == d1.isX1In)))							 //	-------------	d1
		{
			return new Domain(d1.x0, d1.x1, d1.isX0In, d1.isX1In);
		}
		if (((d0.x0 == d1.x0) && (d0.isX0In == d1.isX0In)) &&						//	--------		d0
			((d0.x1 < d1.x1) || ((d0.x1 == d1.x1) && (!d0.isX1In) && (d1.isX0In))))	//	------------	d1
		{
			return new Domain(d1.x0, d1.x1, d1.isX0In, d1.isX1In);
		}
		return null;
	}
}