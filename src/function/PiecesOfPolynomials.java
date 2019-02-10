package function;

import symbolicComputation.Domain;
import symbolicComputation.ExceptionVariableRepresentation;
import symbolicComputation.Variable;
import auxiliaire.AuxGeneral;
import function.Polynomial.MinMax;







public class PiecesOfPolynomials implements Function
{
//--------------------------------------------
//Attributs:
//--------------------------------------------
		private int					functionType		= Function.PiecesOfPolynomialFunction;
		private String				function;			// Representation textuelle de l'expression formelle
		private String				stringDomain;		// Representation textuelle du domaine de definition
		private String				stringFunctionType	= "Continue function by pieces (of polynomial)";
		private double[]			X;
		private double[]			Y;
		private double				yMin;
		private double				yMax;
		private Polynomial[]		polynomes;
		private Domain[]			domains;
		private String				variable			= "x";

//--------------------------------------------
//Constructeur:
//--------------------------------------------
		/************************************************************
		 * Cree une fonction constitue des polynomes polynomes 
		 * sur les domaines domains
		 * @throws Runtime Exception si les domaines de definition 
		 * se chevauchent
		 **************************************************************/
		public PiecesOfPolynomials(Polynomial[] polynomial, Domain[] domains, String functionName) throws RuntimeException
		{
			this.polynomes	= copyPolynomesTab(polynomial);
			this.domains	= copyDomainsTab(domains);
			checkData();
			sortByDomain();
			initDomain();
			initXY();
			initYSides();
			if ((functionName == null) || (functionName.length()== 0))
				this.function = "Unknown formel expression";
			else
				this.function = "" + functionName;
		}

//--------------------------------------------
//Accesseur:
//--------------------------------------------
		public String	getVariable()				{return new String(variable);}
		public double[]	getX()						{return AuxGeneral.copyDoubleTab(X);}
		public double[]	getY()						{return AuxGeneral.copyDoubleTab(Y);}
		public double	getXMin()					{return domains[0].x0;}
		public double	getXMax()					{return domains[domains.length-1].x1;}
		public double	getYMin()					{return yMin;}
		public double	getYMax()					{return yMax;}
		public int		getNbrDomain()				{return domains.length;}
		public double	getDomainSize(int id)		{Domain d = domains[id]; return (d.x1 - d.x0);}
		public int 		getFunctionType()			{return functionType;}
		public String	getFunctionRepresentation()	{return ""+function;}
		public String	getDomainRepresentation()	{return stringDomain;}
		public String	getTypeRepresentation()		{return stringFunctionType;}
		public boolean	isDerivable()				{return true;}
 		public boolean	isVariableSetable()			{return true;}
 		public void		setVariable(String s) throws ExceptionVariableRepresentation
 		{
 			if (!Variable.isVariableName(s)) throw new ExceptionVariableRepresentation();
 			this.variable	= new String(s);
 		}
		public String toString()
		{
			String res = "";
			for (int i=0; i<this.polynomes.length; i++)
			{
				res += domains[i].toString() + "     " + this.polynomes[i].toString(variable) + "\n";
			}
			return res;
		}

//--------------------------------------------
//Methodes principale:
//--------------------------------------------
		public boolean	equal (String representation)
		{
			return (representation.equals(this.function));
		}
		/************************************************************
		 * Rend la valeur de y correspondant a x par interpolation 
		 * des points entourant x par un polynome de
		 * degres this.interpolationDegres
		 * @throws RuntimeException: Si le point d'abscisse x est en 
		 * dehors du domaine de definition
		 **************************************************************/
		public double eval(double x)
		{
			if ((x < getXMin()) || (x > getXMax())) throw new RuntimeException("X value is out of the definition domain: "+ x);

			// Determiner l'interval de x
			for (int i=0; i<domains.length; i++)
			{
				Domain d = domains[i];
				if ((d.isX1In)  && (d.x1 >= x))	return eval(x, i);
				if ((!d.isX1In) && (d.x1 > x))	return eval(x, i);
			}
			// Cas ou le point est en dehors du dom
			throw new RuntimeException("The value x = " + x + " is out of the definition domain!");
		}
		/****************************************************************
		 * Remplit les points X et Y de points equidistants
		 * appartenant au domaine domainId
		 ***************************************************************/
		public void eval(int domainId, Double[] X, Double[] Y)
		{
			if ((domainId < 0) && (domainId >= domains.length)) throw new RuntimeException("unknown definition domain Id");

			Domain d		= domains[domainId];
			Polynomial p	= polynomes[domainId];
			double dx		= (d.x1 - d.x0) / (X.length-1);
			for (int i = 0; i<X.length; i++)
			{
				X[i]		= d.x0 + (i * dx);
				Y[i]		= p.eval(X[i]);
			}
		}
		/**************************************************************
		 * Returns The derivated function
		 **************************************************************/
		public PiecesOfPolynomials derivate()
		{
			int					nbrGap		= domains.length;
			PiecesOfPolynomials	res			= new PiecesOfPolynomials(polynomes, domains, function);

			// Init Interpolation polynome
			for(int i=0; i<nbrGap; i++)
			{
				Polynomial poly	= res.polynomes[i].getDerivate();
				res.polynomes[i] = poly;
			}

			// Init yMin and yMax
			res.initXY();
			res.initYSides();

			// Init Name
			res.function = "d( " + res.function + " ) / dx";
			return res;
		}
		/**************************************************************
		 * Determine si le point (x, y) aproche a gap appartient 
		 * a la fonction
		 **************************************************************/
		public boolean matchPoint(double x, double y, double gapX, double gapY)
		{
			if (gapY <= 0) throw new RuntimeException("negative gap: " + gapY);
			double hx = gapX / 2.;
			double hy = gapY / 2.;

			if (x < getXMin()-hx)				return false;
			if (x > getXMax()+hx)				return false;
			// Determiner l'interval de x
			int n			= domains.length-1;
			Polynomial p	= null;
			if		 (x <  domains[0].x1)							p = polynomes[0];
			else if ((x == domains[0].x1) && (domains[0].isX1In))	p = polynomes[0];
			else if	 (x >  domains[n].x0)							p = polynomes[n];
			else if ((x == domains[n].x0) && (domains[n].isX0In))	p = polynomes[0];
			else
			{
				for (int i=1; i<n; i++)
				{
					Domain d = domains[i];
					if (((d.isX0In) && (d.x0 <= x)) || ((!d.isX0In) && (d.x0 < x)))
					{
						p = this.polynomes[i];
						break;
					}
				}
			}

			// Evaluation
			if (p == null) throw new RuntimeException("The value x = " + x + " is out of the definition domain!");
			double ny = p.eval(x);
			return ((ny >= y-hy) && (ny <= y+hy));
		}
		/**************************************************************
		 * Affiche les equations de tous les polynomes
		 **************************************************************/
		public void printPolynomials()
		{
			for (int i=0; i<polynomes.length; i++)
			{
				System.out.print(polynomes[i]);
				System.out.print(domains[i] + "   \n");
			}
		}

//--------------------------------------------
//Methodes auxiliaires:
//--------------------------------------------
		/***************************************************************** 
		 * Verifie la presence de valeur infinie ou Nan dans X et Y
		 * Verifie le degres des polynomes d'interpolation
		 *****************************************************************/
		private void checkData()
		{
			if  (polynomes.length != domains.length)	throw new RuntimeException("Uncompatibl arguments X, Y");
			if  (polynomes.length == 0)					throw new RuntimeException("Umpty arguments X and Y");

			for (int i = 0; i<domains.length-1; i++)
			{
				double xMin0 = domains[i].x0;
				double xMax0 = domains[i].x1;
				double xMin1 = domains[i+1].x0;
				double xMax1 = domains[i+1].x1;
				if (Double.isNaN(xMin0))			throw new RuntimeException("NaN min value at index: " + i);
				if (Double.isNaN(xMax0))			throw new RuntimeException("NaN max value at index: " + i);
				if (Double.isNaN(xMin1))			throw new RuntimeException("NaN min value at index: " + (i+1));
				if (Double.isNaN(xMax1))			throw new RuntimeException("NaN max value at index: " + (i+1));
			}
		}
		/******************************************************************
		 * Initialise les tableau X et Y
		 ******************************************************************/
		private void initXY()
		{
			int size	= 2*domains.length;
			this.X		= new double[size];
			this.Y		= new double[size];
			int j		= 0;

			for (int i=0; i<size; i++)
			{
				Domain d		= domains[j];
				Polynomial p	= polynomes[j];
				j++;
				X[i] = d.x0;
				Y[i] = p.eval(d.x0);
				i++;
				X[i] = d.x1;
				Y[i] = p.eval(d.x1);
			}
		}
		/******************************************************************
		 * Initialise les valeurs de yMin, yMax
		 ******************************************************************/
		private void initYSides()
		{
			// Determination de xMin et xMax
			Polynomial p	= polynomes[0];
			Domain d		= domains[0];
			MinMax mm		= p.MinMax(d.x0, d.x1);
			double min 		= mm.yMin;
			double max 		= mm.yMax;
			int n			= polynomes.length;

			for (int i=1; i<n; i++)
			{
				p	= polynomes[i];
				d	= domains[i];
				mm	= p.MinMax(d.x0, d.x1);
				if (mm.yMin < yMin) min = mm.yMin;
				if (mm.yMax > yMax) max = mm.yMax;
			}
			this.yMin = min;
			this.yMax = max;
		}
		/*******************************************************************
		 * Tri les tab polynomes et domain par domain croissants
		 *******************************************************************/
		private void sortByDomain()
		{
			int lenth = polynomes.length;
			boolean inv;

			// Trier les tab par xMin croissant
			do
			{
				inv = false;
				for(int i=0;i<lenth-1;i++)
				{
					if (domains[i].x0 == domains[i+1].x0) throw new RuntimeException("Double value of x0: " + domains[i].x0);
					if (domains[i].x0 >  domains[i+1].x0)
					{
						exchangeTab(i,i+1);
						inv = true;
					}
				}
			}while(inv);

			// verifier que les domaines ne se chevauchent pas
			Domain d0 = domains[0];
			for (int i=1; i<domains.length; i++)
			{
				Domain d1 = domains[i];
				if ((d0.x1 == d1.x0) && ((d0.isX1In) && (d1.isX0In)))
						throw new RuntimeException("Domains " + d0 + " and " + d1 + " overlap: ");
				if (d0.x1 > d1.x0)
						throw new RuntimeException("Domains " + d0 + " and " + d1 + " overlap: ");
				d0 = d1;
			}
		}
		/******************************************************************
		 * Initialise la representation textuelle du domaine
		 ******************************************************************/
		private void initDomain()
		{
			stringDomain = domains[0].toString();
			for (int i=1; i<polynomes.length; i++)
			{
				stringDomain	+= "  U  " + domains[i].toString();
			}
		}
		/******************************************************************
		 * evalue la fonction en x en utilisant le polynome polyId
		 * Mis en oeuvre de l'algorithme de Horner pour calculer y = p(x)
		 ******************************************************************/
		private double eval(double x, int polyId)
		{
			if ((polyId < 0) || (polyId >= polynomes.length)) throw new RuntimeException("Unknown polynomial ID: " + polyId);

			Polynomial poly	= polynomes[polyId];
			return poly.eval(x);
		}
		/******************************************************************
		 *  Echange les valeurs de polynomes et domains d'index i, j
		 ******************************************************************/
		private void exchangeTab(int i, int j)
		{
			Polynomial poly0	= polynomes[i];
			Domain dom0 		= domains[i];

			polynomes[i]	= polynomes[j];
			domains[i]		= domains[j];
			polynomes[j]	= poly0;
			domains[j]		= dom0;
		}
		private Polynomial[] copyPolynomesTab (Polynomial[] p)
		{
			Polynomial[] res = new Polynomial[p.length];
			for (int i=0; i<p.length; i++) res[i] = p[i];
			return res;
		}
		private Domain[] copyDomainsTab (Domain[] d)
		{
			Domain[] res = new Domain[d.length];
			for (int i=0; i<d.length; i++) res[i] = d[i];
			return res;
		}
}