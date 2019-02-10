package function;

import java.util.LinkedList;







public class Polynomial
{
// -------------------------------
// Attributs:
// -------------------------------
		private LinkedList<Double>	coeff;
		private final double		NMAX	= 50;

// -------------------------------
// Constructeur:
//-------------------------------
		/***********************************************************
		 * Cree un polynome avec les coefficients coeff tel que
		 * coeff[0] est le monome de degres 0
		 ***********************************************************/
		public Polynomial(double[] coeff) throws RuntimeException
		{
			if (coeff.length == 0) throw new RuntimeException("Umpty polynome");

			this.coeff = new LinkedList<Double>();
			for (int i=0; i<coeff.length; i++)
			{
				this.coeff.add(new Double(coeff[i]));
			}
			this.reduce();
		}
		/***********************************************************
		 * Cree un polynome de degres deg dont tous les coefficients 
		 * sont nul
		 ***********************************************************/
		public Polynomial(int deg) throws RuntimeException
		{
			if (deg < 0) throw new RuntimeException("Negative degres: " + deg);

			this.coeff = new LinkedList<Double>();
			for (int i=0; i<=deg; i++)
			{
				this.coeff.add(0.);
			}
		}
		/***********************************************************
		 * Cree une copie du polynome p
		 ***********************************************************/
		public Polynomial(Polynomial p) throws NullPointerException
		{
			this.coeff = new LinkedList<Double>();
			for (int i=0; i<p.coeff.size(); i++)
			{
				this.coeff.add(new Double(p.coeff.get(i)));
			}
		}

// -------------------------------
// Accesseur:
//-------------------------------
		/*****************************************************
		 * Determine le degres du polynome
		 *****************************************************/
		public int getDegres()
		{
			return coeff.size()-1;
		}
		/*****************************************************
		 * Determine le coeff de degres deg
		 *****************************************************/
		public double getCoeff(int deg)
		{
			return (new Double(coeff.get(deg)));
		}
		/*****************************************************
		 * Indique si tous les coeff du polynome sont a 0
		 *****************************************************/
		public boolean isNull()
		{
			for (int i=0; i<coeff.size(); i++)
			{
				if(coeff.get(i) != 0) return false;
			}
			return true;
		}
		/*****************************************************
		 * Indique si p est equals au polynome courent
		 *****************************************************/
		public boolean equals(Polynomial p)
		{
			Polynomial pMax, pMin;
			int max, min;

			if (p.getDegres() > getDegres())
			{
				max		= p.getDegres();min		= getDegres();
				pMax	= p;			pMin	= this;
			}
			else
			{
				max		= getDegres();	min		= p.getDegres();
				pMax	= this;			pMin	= p;
			}
			for (int i=max; i>min; i--) if (pMax.getCoeff(i) != 0)					return false;
			for (int i=min; i>=0; i++)	if (pMax.getCoeff(i) != pMin.getCoeff(i))	return false;
			return true;
		}
		/***********************************************************
		 * Determine le minimum et le maximum du polynome sur 
		 * l'intervale [x0, x1]
		 ***********************************************************/
		public MinMax MinMax(double x0, double x1)
		{
			if (x0 >= x1) throw new RuntimeException("xMin >= xMax: xMin = " + x0 + "  xMax = " + x1);

			int deg = getDegres();
			switch(deg)
			{
				case 0:		return get0DegresMinMax		(x0, x1);
				case 1:		return get1stDegresMinMax	(x0, x1);
				case 2:		return get2ndDegresMinMax	(x0, x1);
				default:	return getNDegresMinMax		(x0, x1);
			}
		}
		/***********************************************************
		 * Determine les racines reels du polynome entre x0 et x1.
		 * Le resultat est stocke dans xRoot
		 ***********************************************************/
		public void root(double x0, double x1, LinkedList<Double> xRoot)
		{
			if (x0 >= x1) throw new RuntimeException("xMin >= xMax: xMin = " + x0 + "  xMax = " + x1);

			int n = coeff.size();
			double[] coeff = new double[n];
			for (int i=0; i<n; i++) coeff[i] = this.getCoeff(i);
			Polynomial np = new Polynomial(coeff);
			xRoot.clear();
			privateRoot(np, x0, x1, xRoot);
		}
		/***********************************************************
		 * Indique si le polynome admet une racine entre xMin et xMax
		 ***********************************************************/
		public boolean isRoot(double xMin, double xMax)
		{
			if (xMin >= xMax) throw new RuntimeException("xMin >= xMax: xMin = " + xMin + "  xMax = " + xMax);

			MinMax s		= new MinMax();
			double px		= eval(xMin);
			double delta	= (xMax - xMin) / NMAX;
			double x;

			s.yMin	= px;
			s.yMax	= px;
			s.xMin	= xMin;
			s.xMax	= xMin;
			for (int i=1; i<=NMAX; i++)
			{
				x	= xMin + i*delta;
				px	= eval(x);
				if (px < s.yMin)
				{
					s.yMin	= px;
					s.xMin	= x;
				}
				else if (px > s.yMax)
				{
					s.yMax	= px;
					s.xMax	= x;
				}
			}
			return ((s.yMin <= 0) && (s.yMax >= 0));
		}
		/*****************************************************
		 * Rend une representation textuelle du poly
		 *****************************************************/
		public String toString()
		{
			String res = "";
			for (int i=coeff.size()-1; i>=0; i--)
			{
				res += (coeff.get(i) + " X^" +i+ "     ");
			}
			return res;
		}
		public String toString(String var)
		{
			String res = "";
			for (int i=coeff.size()-1; i>=0; i--)
			{
				res += (coeff.get(i) + var + "^" +i+ "     ");
			}
			return res;
		}

// -------------------------------
// Modificateur:
//-------------------------------
		public void setCoeff(int deg, double coeff)
		{
			this.coeff.remove(deg);
			this.coeff.add(deg, new Double(coeff));
		}
		/***********************************************************
		 * Rend le polynoe unitaire
		 ***********************************************************/
		public void unitise()
		{
			int deg = getDegres();
			if (deg == 0) return;

			double coeff = getCoeff(deg);
			for (int i=0; i<=deg; i++)
			{
				double c = getCoeff(i);
				this.setCoeff(i, c/coeff);
			}
		}
		/***********************************************************
		 * Supprime les coefficient de plus haut degres
		 ***********************************************************/
		public void reduce(int nbrReduce)
		{
			int deg = getDegres();
			if (nbrReduce <= 0)		throw new RuntimeException("Negative argument: " + nbrReduce);
			if (nbrReduce > deg)	throw new RuntimeException("Negative argument: " + nbrReduce);

			for (int i=0; i<nbrReduce; i++)
			{
				coeff.remove(coeff.size()-1);
			}
			if (coeff.size() == 0) coeff.add(0.);
		}
		/***********************************************************
		 * Supprime les coefficient de plus haut degres de valeur null
		 ***********************************************************/
		public void reduce()
		{
			while((coeff.size() > 0) && (coeff.get(coeff.size()-1) == 0))
			{
				coeff.removeLast();
			}
			if (coeff.size() == 0) coeff.add(0.);
		}

// -------------------------------
// Methodes Locales:
//--------------------------------
		/***********************************************************
		 * Evalue polynome en x en suivant l'algorithme de Horner
		 ***********************************************************/
		public double eval(double x)
		{
			int n		= this.getDegres();
			double bn	= this.getCoeff(n); 
			double cn	= x * bn;

			if (n == 0) return bn;
			for (int i=n-2; i>=0; i--)
			{
				bn = getCoeff(i+1)+ cn;
				cn = bn * x;
			}
			return (cn + getCoeff(0));
		}
		/***********************************************************
		 * Rend le polynome somme de p1 et p2
		 ***********************************************************/
		public static Polynomial add(Polynomial p1, Polynomial p2)
		{
			int deg1		= p1.getDegres();
			int deg2		= p2.getDegres();
			int max			= Math.max(deg1, deg2);
			int min			= Math.min(deg1, deg2);
			Polynomial res	= new Polynomial(max);
			Polynomial pp1, pp2;

			if (deg1 > deg2)	{pp1 = p1; pp2 = p2;}
			else				{pp1 = p2; pp2 = p1;}
			for (int i=0; i<=min; i++)
			{
				res.setCoeff(i, pp1.getCoeff(i) + pp2.getCoeff(i));
			}
			for (int i=min+1; i<=max; i++)
			{
				res.setCoeff(i, pp1.getCoeff(i));
			}
			res.reduce();
			return res;
		}
		/***********************************************************
		 * Rend le polynome p = p1 - p2
		 ***********************************************************/
		public static Polynomial sub(Polynomial p1, Polynomial p2)
		{
			int deg1		= p1.getDegres();
			int deg2		= p2.getDegres();
			int max			= Math.max(deg1, deg2);
			int min			= Math.min(deg1, deg2);
			Polynomial res	= new Polynomial(max);

			for (int i=0; i<=min; i++)
			{
				res.setCoeff(i, p1.getCoeff(i) - p2.getCoeff(i));
			}
			for (int i=min+1; i<=max; i++)
			{
				double coeff;
				if (p1.getDegres() > p2.getDegres())	coeff = p1.getCoeff(i);
				else									coeff = -p2.getCoeff(i);
				res.setCoeff(i, coeff);
			}
			res.reduce();
			return res;
		}
		/***********************************************************
		 * Rend le polynome produit de p par le monome coeff 
		 * puissance degres
		 ***********************************************************/
		public static Polynomial multiplyMonome(Polynomial p, double coeff, int degres)
		{
			if (degres < 0) throw new RuntimeException("Unhandled negatif degres: " + degres);
			if (coeff == 0)	return new Polynomial(0);

			double[] l = new double[p.coeff.size()+degres];
			for (int i=0; i<degres; i++)			l[i] = 0.;
			for (int i=degres; i<l.length; i++)		l[i] = p.getCoeff(i-degres)*coeff;
			Polynomial res = new Polynomial(l);
			return res;
		}
		/***********************************************************
		 * Rend le polynome produit de p1 et p2
		 ***********************************************************/
		public static Polynomial multiply(Polynomial A, Polynomial B)
		{
			int deg1		= A.getDegres();
			int deg2		= B.getDegres();
			Polynomial res	= new Polynomial(deg1 + deg2);

			for (int i=0; i<=deg1; i++)
			{
				double coeff = A.getCoeff(i);
				Polynomial p = multiplyMonome(B, coeff, i);
				res			 = add(res, p);
			}

			return res;
		}
		/***********************************************************
		 * Determine le polynome derive
		 ***********************************************************/
		public Polynomial getDerivate()
		{
			if (coeff.size() == 1) return (new Polynomial(0));
			else
			{
				double[] res = new double[coeff.size()-1];
				int n = 1;
				for (int i=1; i<coeff.size(); i++)
				{
					res[i-1] = n*coeff.get(i);
					n++;
				}
				return new Polynomial(res);
			}
		}

// -----------------------------
// Methodes Auxiliaires:
// Calcul d'extremum
// -----------------------------
		private MinMax get0DegresMinMax(double x0, double x1)
		{
			MinMax res	= new MinMax();
			res.xMin	= x0;
			res.xMax	= x1;
			res.yMin	= eval(x0);
			res.yMax	= eval(x1);
			return res;
		}
		private MinMax get1stDegresMinMax(double x0, double x1)
		{
			MinMax res	= new MinMax();
			double coef = getCoeff(1);

			if (coef > 0)	{res.xMin = x0; res.xMax = x1; res.yMin = eval(x0); res.yMax = eval(x1);}
			else			{res.xMin = x1; res.xMax = x0; res.yMin = eval(x1); res.yMax = eval(x0);}
			return res;
		}
		private MinMax get2ndDegresMinMax(double x0, double x1)
		{
			MinMax res	= new MinMax();
			double a	= getCoeff(2);
			double b	= getCoeff(1);
			double m	= -b/(2*a);

			if (m <= x0)
			{
				if (a > 0)	{res.xMin = x0; res.xMax = x1; res.yMin = eval(x0); res.yMax = eval(x1);}
				else		{res.xMin = x1; res.xMax = x0; res.yMin = eval(x1); res.yMax = eval(x0);}
			}
			else if ((m >= x0) && (m <= x1))
			{
				if (a > 0)
				{
					res.xMin 	= m;
					res.yMin	= eval(m);
					double max0	= eval(x0);
					double max1	= eval(x1);
					if(max0 > max1)	{res.xMax = x0; res.yMax = max0;}
					else			{res.xMax = x1; res.yMax = max1;}
				}
				else
				{
					res.xMax 	= m;
					res.yMax	= eval(m);
					double min0	= eval(x0);
					double min1	= eval(x1);
					if(min0 < min1)	{res.xMin = x0; res.yMin = min0;}
					else			{res.xMin = x1; res.yMin = min1;}
				}
			}
			else
			{
				if (a > 0)	{res.xMin = x1; res.xMax = x0; res.yMin = eval(x1); res.yMax = eval(x0);}
				else		{res.xMin = x0; res.xMax = x1; res.yMin = eval(x0); res.yMax = eval(x1);}
			}
			return res;
		}
		private MinMax getNDegresMinMax(double x0, double x1)
		{
			MinMax res	= new MinMax();

			//Calcul de la derivee
			Polynomial deriv = getDerivate();

			// Racines de la derivee
			LinkedList<Double> xRoot = new LinkedList<Double>();
			deriv.root(x0, x1, xRoot);

			// Comparer le min a eval(racine)
			double y0	= eval(x0);
			double y1	= eval(x1);
			if (y0 < y1)	{res.xMin = x0; res.xMax = x1; res.yMin = y0; res.yMax = y1;}
			else			{res.xMin = x1; res.xMax = x0; res.yMin = y1; res.yMax = y0;}
			for (int i=0; i<xRoot.size(); i++)
			{
				double x = xRoot.get(i);
				double y = eval(x);
				if (y < res.yMin) {res.yMin = y; res.xMin = x;}
				if (y > res.yMax) {res.yMax = y; res.xMax = x;}
			}
			return res;
		}
// -----------------------------
// Methodes Auxiliaires:
// Calcul de reacines
// -----------------------------
		private void privateRoot(Polynomial p, double xMin, double xMax, LinkedList<Double> xRoot)
		{
			int deg = p.getDegres();

			switch(deg)
			{
				case 0:
					if (p.getCoeff(0) == 0) throw new RuntimeException("The poly equals 0 on [" + xMin + " , " + xMax + "]  " + p.toString());
					else return;
				case 1:		get1DegRoot(p, xMin, xMax, xRoot);	return;
				case 2:		get2DegRoot(p, xMin, xMax, xRoot);	return;
				case 3:		get3DegRoot(p, xMin, xMax, xRoot);	return;
				case 4:		get4DegRoot(p, xMin, xMax, xRoot);	return;
				default:
///////////////////////			
throw new RuntimeException();
/*							MinMax m = new MinMax();
							boolean test = p.isRoot(xMin, xMax, m);
							if(!test) return;
							double res = p.findOneRoot(xMin, xMax, m);
							xRoot.add(res);
							Polynomial np = Factorize(res);
							privateRoot(np, xRoot, xMin, xMax);
*/			}
		}
		private void get1DegRoot(Polynomial p, double xMin, double xMax, LinkedList<Double> xRoot)
		{
			double a = p.getCoeff(1);
			double b = p.getCoeff(0);
			double r = -b / a;
			if ((r >= xMin) && (r <= xMax)) xRoot.add(a);
		}
		private void get2DegRoot(Polynomial p, double xMin, double xMax, LinkedList<Double> xRoot)
		{
			double a		= p.getCoeff(2);
			double b		= p.getCoeff(1);
			double c		= p.getCoeff(0);
			double delta	= b*b - 4*a*c;
			if (delta > 0)
			{
				double res0 = (-b + Math.sqrt(delta))/(2*a);
				double res1 = (-b - Math.sqrt(delta))/(2*a);
				if ((res0 >= xMin) && (res0 <= xMax)) xRoot.add(res0);
				if ((res1 >= xMin) && (res1 <= xMax)) xRoot.add(res1);
			}
			else if (delta == 0)
			{
				double res0 = -b/(2*a);
				if ((res0 >= xMin) && (res0 <= xMax)) xRoot.add(res0);
			}
		}
		private void get3DegRoot(Polynomial poly, double xMin, double xMax, LinkedList<Double> xRoot)
		{
			poly.unitise();
			double a = poly.getCoeff(2);
			double b = poly.getCoeff(1);
			double c = poly.getCoeff(0);
			double p = b - a*a/3;
			double q = (2*a*a*a/27) - (a*b/3) + c;

			if (p == 0)
			{
				double z = Math.pow(-q, 1/3);
				xRoot.add(z-a/3);
			}
			else
			{
///////////////////////			
throw new RuntimeException();
			}
		}
		private void get4DegRoot(Polynomial p, double xMin, double xMax, LinkedList<Double> xRoot)
		{
///////////////////////			
throw new RuntimeException();
			
		}
// -----------------------------
//  Classe Auxiliaires:
// -----------------------------
		public class MinMax
		{
			public double xMin;
			public double xMax;
			public double yMin;
			public double yMax;
		}
		public static class PolyInfo
		{
			// Attributs
			boolean		isNull;
			boolean 	isMonome;
			Double		monomeCoeff;
			Integer		monomeDegres;

			// Constructeur
			public PolyInfo(Polynomial p)
			{
				isNull		= true;
				isMonome	= false;
				monomeDegres= -1;
				monomeCoeff	= -1.;
				int nbrMon	= 0;
				for (int i=0; i<=p.getDegres(); i++)
				{
					double c = p.getCoeff(i);
					if (c != 0)
					{
						isNull			= false;
						monomeCoeff		= c;
						monomeDegres	= i;
						nbrMon ++;
					}
				}
				if (nbrMon == 1)isMonome = true;
				else
				{
					monomeCoeff		= null;
					monomeDegres	= null;
				}
			}
		}
}