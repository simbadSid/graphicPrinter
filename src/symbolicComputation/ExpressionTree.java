package symbolicComputation;

import java.util.LinkedList;







public class ExpressionTree
{
//--------------------------------------------
// Attributs:
//--------------------------------------------
	private Node			node;
	private ExpressionTree	LS						= null;
	private ExpressionTree	RS						= null;

//--------------------------------------------
// Constructeur:
//--------------------------------------------
	/**===========================================================
	 * Construit l'arbre represente par l'expression
	 * formelle totalement parenthesee
	 * @throws ExceptionUnrecognizedExpression
	 =============================================================*/
	public ExpressionTree(String formalExpression, Variable[] var) throws ExceptionUnrecognizedExpression
	{
		LinkedList<Node> fe = parseInTab(formalExpression, var);	// Construire le tab des elements
		if (fe.size() != 0)	buildTree(fe);
	}
	public ExpressionTree(Node n, ExpressionTree ls, ExpressionTree rs)
	{
		this.node			= new Node(n);
		if (ls != null)	this.LS	= new ExpressionTree(ls.node, ls.LS, ls.RS);
		if (rs != null)	this.RS	= new ExpressionTree(rs.node, rs.LS, rs.RS);
	}
	public ExpressionTree(double constant)
	{
		this.node	= new Node(Node.Constant, ""+ constant);
	}
	public ExpressionTree(ExpressionTree et)
	{
		this(et.node, et.LS, et.RS);
	}
	public ExpressionTree()
	{
		this.node	= null;
	}

//--------------------------------------------
// Methode Locale:
//--------------------------------------------
	public Node				getNode()		{return this.node;}
	public ExpressionTree	getLeftTree()	{return this.LS;}
	public ExpressionTree	getRightTree()	{return this.RS;}
	public String toString()
	{
		String val			= node.getVal();

		if (node.isConstant())				return val;
		if (node.isVariable())				return val;
		if (node.isBinaryOperator())		return ('(' + LS.toString() + val + RS.toString() + ')');
		if (node.isUnaryOperator())			return (val + '(' + LS.toString() + ')');
		throw new RuntimeException("Unhandled node type: " + node);
	}

//--------------------------------------------
// Methode De reduction d'arbre 
//--------------------------------------------
	/**========================================================
	 * Retourne un arbre equivalent a l'arbre courrent ou les 
	 * formes simples ont ete supprime
	 ==========================================================*/
	public ExpressionTree getReduceTree()
	{
		ExpressionTree res	= new ExpressionTree();
		if (!node.isBinaryOperator())
		{
			res.node				= new Node(node);
			if (LS != null)	res.LS	= LS.getReduceTree();
			if (RS != null)	res.RS	= RS.getReduceTree();
			return res;
		}

		Node nl				= LS.getNode();
		Node nr				= RS.getNode();
		if ((nl.isConstant()) && (nr.isConstant()))	return new ExpressionTree(node.eval(nl.getVal(), nr.getVal()));
		if (nl.equalConst(0))
		{
			if (node.getVal().equals("+"))	return RS.getReduceTree();
			if (node.getVal().equals("*"))	return new ExpressionTree(0);
			if (node.getVal().equals("/"))	return new ExpressionTree(0);
			if (node.getVal().equals("%"))	return new ExpressionTree(0);
			if (node.getVal().equals("^"))	return new ExpressionTree(0);
			if (node.getVal().equals("-"))	return new ExpressionTree(new Node(Node.UnaryOperator, "-"), RS.getReduceTree(), null);
			else throw new RuntimeException("Unhandeld Binary Operator: " + node.getVal());
		}
		if (nr.equalConst(0))
		{
			if (node.getVal().equals("+"))	return LS.getReduceTree();
			if (node.getVal().equals("-"))	return LS.getReduceTree();
			if (node.getVal().equals("*"))	return new ExpressionTree(0);
			if (node.getVal().equals("/"))	throw new ArithmeticException("division par zero");
			if (node.getVal().equals("%"))	return new ExpressionTree(0);
			if (node.getVal().equals("^"))	return new ExpressionTree(1);
			else throw new RuntimeException("Unhandeld Binary Operator: " + node.getVal());			
		}
		if (nl.equalConst(1))
		{
			if (node.getVal().equals("+"))	return new ExpressionTree(node, LS, RS.getReduceTree());
			if (node.getVal().equals("-"))	return new ExpressionTree(node, LS, RS.getReduceTree());
			if (node.getVal().equals("*"))	return RS.getReduceTree();
			if (node.getVal().equals("/"))	return new ExpressionTree(node, LS, RS.getReduceTree());
			if (node.getVal().equals("%"))	return new ExpressionTree(node, LS, RS.getReduceTree());
			if (node.getVal().equals("^"))	return new ExpressionTree(1);
			else throw new RuntimeException("Unhandeld Binary Operator: " + node.getVal());
		}
		if (nr.equalConst(1))
		{
			if (node.getVal().equals("+"))	return new ExpressionTree(node, LS.getReduceTree(), RS);
			if (node.getVal().equals("-"))	return new ExpressionTree(node, LS.getReduceTree(), RS);
			if (node.getVal().equals("*"))	return LS.getReduceTree();
			if (node.getVal().equals("/"))	return LS.getReduceTree();
			if (node.getVal().equals("%"))	return new ExpressionTree(node, LS.getReduceTree(), RS);
			if (node.getVal().equals("^"))	return LS.getReduceTree();
			else throw new RuntimeException("Unhandeld Binary Operator: " + node.getVal());
		}
		else	return new ExpressionTree(node, LS.getReduceTree(), RS.getReduceTree());
	}

//--------------------------------------------
// Methode Auxiliaire De preparation de la chaine:
//--------------------------------------------
	/*=======================================================================
	 * Rend la chaine parametre sous forme de tableau en supprimant 
	 * les espaces et chaque element
	 * @throws ExceptionUnrecognizedExpression 
	 ========================================================================*/
	private LinkedList<Node> parseInTab(String formalExpression, Variable[] var) throws ExceptionUnrecognizedExpression
	{
		LinkedList<Node> res = new LinkedList<Node>();
		int i = 0, test;

		while (i < formalExpression.length())
		{
			char c = formalExpression.charAt(i);
			if (c == ' ') 								{i++; 					continue;}
			if (c == '(')								{i++; res.add(new Node(Node.OpeningParenthesis, "" + c));	continue;}
			if (c == ')')								{i++; res.add(new Node(Node.ClosingParenthesis, "" + c));	continue;}
			test = isOperator(formalExpression, i, res);
			if (test != -1)								{i += test;				continue;}
			test = isNumber(formalExpression, i, res);
			if (test != -1) 							{i += test;				continue;}
			test = isVar(formalExpression, i, res, var);
			if (test != -1)								{i += test;				continue;}

			throw new ExceptionUnrecognizedExpression(formalExpression.substring(i));
		}
		return res;
	}
	/*======================================================================
	 * rend -1 si s[i] n'est pas un opperateur defini en attributs
	 * rend la taille de l'opperatur si non, et l'ajoute a tab
	 =======================================================================*/
	private int isOperator(String s, int i, LinkedList<Node> tab)
	{
		int leftLength = s.length() - i;
		if (leftLength <= 0) throw new RuntimeException("Empty String!");

		if (s.charAt(i) == '-')												// Cas problematique: Le moins:
		{
			if ((tab.size() ==0) || (tab.getLast().isOpeningParenthesis()))	//		Cas d'un moins unaire
					tab.add(new Node(Node.UnaryOperator, "-"));
			else	tab.add(new Node(Node.BinaryOperator, "-"));					//		Cas d'un moins binaire
				
			return 1;
		}

		for (int k=0; k<Node.binaryOperator.length; k++)					// Recherche d'opperateur binaire
		{
			int l = Node.binaryOperator[k].length();
			if (l > leftLength) continue;
			String ns = s.substring(i, i+l);
			if (ns.equals(Node.binaryOperator[k]))
			{
				tab.add(new Node(Node.BinaryOperator, ns));
				return l;
			}
		}
		for (int k=0; k<Node.unaryOperator.length; k++)						// Recherche d'opperateur unaire
		{
			int l = Node.unaryOperator[k].length();
			if (l > leftLength) continue;
			String ns = s.substring(i, i+l);
			if (ns.equals(Node.unaryOperator[k]))
			{
				tab.add(new Node(Node.UnaryOperator, ns));
				return l;
			}
		}
		return -1;
	}
	/*======================================================================
	 * rend -1 si s[i] n'est pas un nombre
	 * rend la taille du nombre si non, et l'ajoute a tab
	 * accepte la virgule ( un point )
	 * accepte l'exposant xey ou xEy (sans espaces)
	 =======================================================================*/
	private int isNumber(String s, int i, LinkedList<Node> tab)
	{
		String numb = "";
		boolean isComa	= false;
		boolean isExp 	= false;
		int k;
		char c;

		for (k=i; k<s.length(); k++)
		{
			c = s.charAt(k);
			if (c == '.')											// Cas d'une virgule
			{
				if ((isComa) || (isExp)) 	{k = i; break;}			//		Erreur dans la chaine
				isComa = true;
			}
			else if ((c == 'E') || (c == 'e'))						// Cas d'un exposant
			{
				if (isExp)					{k = i; break;}			//		Erreur dans la chaine
				isExp  = true;
				isComa = false;
			}
			else if ((c <'0') || (c >'9'))	{		break;}			// Fin de chaine
			numb += ""+c;
		}
		if (k == i) return -1;
		try					{Float.valueOf(numb);}					// Test du nombre obtenu
		catch (Exception e)	{return -1;}
		tab.add(new Node(Node.Constant, numb));
		return (k-i);
	}
	/*=======================================================================
	 * rend -1 si s[i] n'est pas une variable acceptable
	 * rend la taille de la variable si non, et l'ajoute a tab
	 * accepte la virgule ( un point )
	 * accepte l'exposant xey ou xEy (sans espaces)
	 ========================================================================*/
	private int isVar(String s, int i, LinkedList<Node> tab, Variable[] variables)
	{
		String var = "";
		int k;
		char c;

		for (k=i; k<s.length(); k++)
		{
			c = s.charAt(k);
			if ((c >= '0') && (c <= '9'))
			{
				if (k == i) break;							// Variable commencant par un chiffre
			}
			else if (((c < 'a') || (c > 'z')) &&			// Fin De chaine
					 ((c < 'A') || (c > 'Z')))	break;
			var += ""+c;
		}
		if (k == i) return -1;
		if (!isIn(var, variables)) return -1;
		tab.add(new Node(Node.Variable, var));
		return (k-i);
	}
//---------------------------------------------------
// Methode Auxiliaire De Construction de l'arbre:
//---------------------------------------------------
	/*=======================================================================
	 * Construit l'arbre en utilisant la tables des elements entre 
	 * les index beginning et end
	 * @param l
	 * @param beginning
	 * @param end
	 * @throws ExceptionUnrecognizedExpression 
	 ========================================================================*/
	private void buildTree(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		boolean test;

		switch(l.size())													// Cas acceptes:
		{
			case 0: return;
			case 1: if (isConfig1(l))		return;							// a;
					else 					break;
			case 2: if (isConfig2(l))		return;							// -a
					else					break;
			case 3: if (isConfig30(l))		return;							// (a)
					if (isConfig31(l))		return;							// a + b
											break;
			case 4:	if (isConfig40(l))		return;							// (-a)
					if (isConfig41(l))		return;							// sin(a)
											break;
			case 5: 
					if (isConfig5(l))		return;							// (a + b)
					else					break;
			case 6: if (isConfig60(l))		return;							// (sin(a))
					if (isConfig61(l))		return;							// sin(a+b)
					else					break;
		}
		if (l.size() > 6)
		{
			test = buildUnaryTree(l);				if (test) return;		// sin(a+b)
			test = buildRightTree(l);				if (test) return;		// (a       + (b*c))
			test = buildLeftTree(l);				if (test) return;		// ((a*b)   + c)
			test = buildLeftRightTree(l);			if (test) return;		// ((a*b)   + (c*d))
			test = buildLeftUnaryTree(l);			if (test) return;		// (sin(..) + b)
			test = buildRightUnaryTree(l);			if (test) return;		// (a       + sin(..))
			test = buildLeftRightUnaryTree(l);		if (test) return;		// (sin(..) + sin(...))
			test = buildLeft_RightUnary(l);			if (test) return;		// ((a+b)   + sin(..))
			test = buildRight_LeftUnary(l);			if (test) return;		// (sin(..) + (a+b))
			test = buildWithUselessParenthesis(l);	if (test) return;		// (sin(..))    ou ((a+b))
		}
		throw new ExceptionUnrecognizedExpression(getStringOfList(l));
	}
// ------------------------------------------------
// Fonctions de reconnaissance de
// formes simples
// En cas de reconnaissance, l'arbre est construit
// ------------------------------------------------
	private boolean isConfig1(LinkedList<Node> l)
	{
		Node n0 = l.get(0);

		if ((!n0.isConstant()) && 
			(!n0.isVariable()))		return false;

		this.node	= l.getFirst();
		return true;
}
	private boolean isConfig2(LinkedList<Node> l)
	{
		Node n0 = l.get(0),		n1 = l.get(l.size()-1);

		if ((!n0.isMinus()) ||
			(!n1.isConstant() && (!n1.isVariable())))	return false;

		this.node	= n0;
		this.LS		= new ExpressionTree();
		this.LS.node= n1;
		return true;
}
	private boolean isConfig30(LinkedList<Node> l)
	{
		Node n0 = l.get(0),	n = l.get(1), 	n1 = l.get(2);

		if ((!n0.isOpeningParenthesis()) ||
			(!n1.isClosingParenthesis()) ||
			((!n.isConstant()) && (!n.isVariable())))	return false;

		this.node = n;
		return true;
	}
	private boolean isConfig31(LinkedList<Node> l)
	{
		Node n0 = l.get(0),	n = l.get(1), 	n1 = l.get(2);

		if (((!n0.isConstant()) && (!n0.isVariable()))	||
			(!n.isBinaryOperator()) 					||
			((!n1.isConstant()) && (!n1.isVariable())))	return false;

		this.node	= n;
		this.LS		= new ExpressionTree();
		this.RS		= new ExpressionTree();
		this.LS.node= n0;
		this.RS.node= n1;
		return true;
	}
	private boolean isConfig40(LinkedList<Node> l)
	{
		Node n0	= l.get(0),	ln = l.get(1),
			 rn = l.get(2),	n1	= l.get(3);

		if ((!n0.isOpeningParenthesis())	||
			(!n1.isClosingParenthesis())	||
			(!ln.isMinus())					||
			((!rn.isConstant()) && (!rn.isVariable())))	return false;		

		this.node	= ln;
		this.LS		= new ExpressionTree();
		this.LS.node= rn;
		return true;
	}
	private boolean isConfig41(LinkedList<Node> l)
	{
		Node n0 = l.get(0),	ln = l.get(1),
			 rn = l.get(2),	n1 = l.get(3);

		if ((!n0.isUnaryOperator())			||
			(!ln.isOpeningParenthesis()) 	||
			(!n1.isClosingParenthesis())	||
			((!rn.isConstant()) && (!rn.isVariable())))	return false;
		this.node	= n0;
		this.LS		= new ExpressionTree();
		this.LS.node= rn;
		return true;
	}
	private boolean isConfig5(LinkedList<Node> l)
	{
		Node n0 = l.get(0),
			 ln = l.get(1),	mn  = l.get(2),	rn = l.get(3),
			 n1 = l.get(4);

		if ((!n0.isOpeningParenthesis())				||
			((!ln.isConstant()) && (!ln.isVariable()))	||
			(!mn.isBinaryOperator())					||
			((!rn.isConstant()) && (!rn.isVariable()))	||
			(!n1.isClosingParenthesis()))				return false;

		this.node	= mn;
		this.LS		= new ExpressionTree();
		this.RS		= new ExpressionTree();
		this.LS.node= ln;
		this.RS.node= rn;
		return true;
	}
	private boolean isConfig60(LinkedList<Node> l)
	{
		Node n0 = l.get(0),
			 ln1 = l.get(1),	ln2 = l.get(2),
			 rn1 = l.get(3),	rn2 = l.get(4),
			 n1 = l.get(5);

		if ((!n0.isOpeningParenthesis())					||
			(!ln1.isUnaryOperator())						||
			(!ln2.isOpeningParenthesis())					||
			((!rn1.isConstant()) && (!rn1.isVariable()))	||
			(!rn2.isClosingParenthesis())					||
			(!n1.isClosingParenthesis()))					return false;

		this.node	= ln1;
		this.LS		= new ExpressionTree();
		this.LS.node= rn1;
		return true;
	}
	private boolean isConfig61(LinkedList<Node> l)
	{
		Node n0 = l.get(0),
			 ln1 = l.get(1),	ln2 = l.get(2),
			 rn1 = l.get(3),	rn2 = l.get(4),
			 n1 = l.get(5);

		if ((!n0.isUnaryOperator())							||
			(!ln1.isOpeningParenthesis())					||
			((!ln2.isConstant()) && (!ln2.isVariable()))	||
			(!rn1.isBinaryOperator())						||
			((!rn2.isConstant()) && (!rn2.isVariable()))	||
			(!n1.isClosingParenthesis()))					return false;

		this.node	= n0;
		this.LS		= new ExpressionTree();
		this.LS.LS	= new ExpressionTree();
		this.LS.RS	= new ExpressionTree();		
		this.LS.node= rn1;
		this.LS.LS.node = ln2;
		this.LS.RS.node = rn2;
		return true;
	}
// ------------------------------------------------
// Fonctions de construction d'arbre sur forme composee
// Rend vrai si la forme cherchee a ete trouvee
// l.size() >= 6
// ------------------------------------------------
	private boolean buildUnaryTree(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		int s = l.size();
		LinkedList<Node> subList;

		if ((!l.get(0).isUnaryOperator()) 		||
			(!l.get(1).isOpeningParenthesis())	||
			(!l.get(s-1).isClosingParenthesis()))
			return false;

		this.node	= l.get(0);
		this.LS		= new ExpressionTree();
		subList		= subList(l, 1, s);
		this.LS		.buildTree(subList);
		return true;
	}
	private boolean buildRightTree(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		int s = l.size();
		LinkedList<Node> subListR;
		Node n0 = l.get(0),		n1 = l.get(1),		n2 = l.get(2),
			 n3 = l.get(3),		n4 = l.get(s-2),	n5 = l.get(s-1);

		if ((!n0.isOpeningParenthesis())				||
			((!n1.isConstant()) && (!n1.isVariable()))	||
			(!n2.isBinaryOperator())					||
			(!n3.isOpeningParenthesis())				||
			(!n4.isClosingParenthesis())				||
			(!n5.isClosingParenthesis()))
			return false;

		this.node	= n2;
		this.LS		= new ExpressionTree();
		this.RS		= new ExpressionTree();
		subListR	= subList(l, 3, s-1);
		this.LS.node= n1;
		this.RS		.buildTree(subListR);
		return true;
	}
	private boolean buildLeftTree(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		int s = l.size();
		LinkedList<Node> subListL;
		Node n0 = l.get(0),		n1 = l.get(1),
			 n2 = l.get(s-3),	n3 = l.get(s-2),	n4 = l.get(s-1);

		if ((!n0.isOpeningParenthesis())				||
			(!n1.isOpeningParenthesis())				||
			(!n2.isBinaryOperator())					||
			((!n3.isConstant()) && (!n3.isVariable()))	||
			(!n4.isClosingParenthesis()))
			return false;

		this.node	= n2;
		this.LS		= new ExpressionTree();
		this.RS		= new ExpressionTree();
		subListL	= subList(l, 1, s-3);
		this.LS		.buildTree(subListL);
		this.RS.node= n3;
		return true;
	}
	private boolean buildLeftRightTree(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		LinkedList<Node> subListL, subListR;
		int opIndex, s = l.size();
		Node n0 = l.get(0),		n1 = l.get(1),
			 n5 = l.get(s-2),	n6 = l.get(s-1);

		if ((!n0.isOpeningParenthesis()) ||
			(!n1.isOpeningParenthesis()) ||
			(!n5.isClosingParenthesis()) ||
			(!n6.isClosingParenthesis()))			return false;

		opIndex = getOpIndex(l, 2, 1);
		if ((opIndex == -1) || (opIndex > (s-3)))	return false;
		Node n2 = l.get(opIndex-1),	
			 n3 = l.get(opIndex), 
			 n4 = l.get(opIndex+1);
		
		if ((!n2.isClosingParenthesis())	||
			(!n3.isBinaryOperator())		||
			(!n4.isOpeningParenthesis()))			return false;
		this.node	= n3;
		this.LS		= new ExpressionTree();
		this.RS		= new ExpressionTree();
		subListL	= subList(l, 1, opIndex);
		subListR	= subList(l, opIndex+1, s-1);
		this.LS		.buildTree(subListL);
		this.RS		.buildTree(subListR);
		return true;
	}
	private boolean buildLeftUnaryTree(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		LinkedList<Node> subListL;
		int s = l.size();
		Node n0 = l.get(0),		n1 = l.get(1),		n2 = l.get(2),
			 n3 = l.get(s-3),
			 n4 = l.get(s-2),	n5 = l.get(s-1);

		if ((!n0.isOpeningParenthesis())				||
			(!n1.isUnaryOperator())						||
			(!n2.isOpeningParenthesis())				||
			(!n3.isBinaryOperator())					||
			((!n4.isConstant()) && (!n4.isVariable()))	||
			(!n5.isClosingParenthesis()))				return false;

		this.node	= n3;
		this.LS		= new ExpressionTree();
		this.LS.LS	= new ExpressionTree();
		this.RS		= new ExpressionTree();
		subListL	= subList(l, 2, s-3);
		this.RS.node= n4;
		this.LS.node= n1;
		this.LS.LS	.buildTree(subListL);
		return true;
	}
	private boolean buildRightUnaryTree(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		LinkedList<Node> subListR;
		int s = l.size();
		Node n0 = l.get(0),		n1 = l.get(1),
			 n2 = l.get(2),
			 n3 = l.get(3),	n4 = l.get(4),	n5 = l.get(s-2),	n6 = l.get(s-1);

		if ((!n0.isOpeningParenthesis())				||
			((!n1.isConstant()) && (!n1.isVariable()))	||
			(!n2.isBinaryOperator())					||
			(!n3.isUnaryOperator())						||
			(!n4.isOpeningParenthesis())				||
			(!n5.isClosingParenthesis())				||
			(!n6.isClosingParenthesis()))				return false;

		this.node	= n2;
		this.LS		= new ExpressionTree();
		this.RS		= new ExpressionTree();
		this.RS.LS	= new ExpressionTree();
		subListR	= subList(l, 4, s-1);
		this.LS.node= n1;
		this.RS.node= n3;
		this.RS.LS	.buildTree(subListR);
		return true;
	}
	private boolean buildLeftRightUnaryTree(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		LinkedList<Node> subListL, subListR;
		int s = l.size(), opIndex;
		Node n0 = l.get(0),		n1 = l.get(1),	n2 = l.get(2),
			 n7 = l.get(s-2),	n8 = l.get(s-1);

		if ((!n0.isOpeningParenthesis())	||
			(!n1.isUnaryOperator())			||
			(!n2.isOpeningParenthesis())	||
			(!n7.isClosingParenthesis())	||
			(!n8.isClosingParenthesis()))			return false;

		opIndex = getOpIndex(l, 3, 1);
		if ((opIndex == -1) || (opIndex > (s-5)))	return false;
		Node n3 = l.get(opIndex-1),	
			 n4 = l.get(opIndex), 
			 n5 = l.get(opIndex+1),
			 n6 = l.get(opIndex+2);

			if ((!n3.isClosingParenthesis())	||
				(!n4.isBinaryOperator())		||
				(!n5.isUnaryOperator())			||
				(!n6.isOpeningParenthesis()))		return false;

		this.node	= n4;
		this.LS		= new ExpressionTree();
		this.RS		= new ExpressionTree();
		this.LS.LS	= new ExpressionTree();
		this.RS.LS	= new ExpressionTree();
		subListL	= subList(l, 2			, opIndex);
		subListR	= subList(l, opIndex+2	, s-1);
		this.LS.node= n1;
		this.RS.node= n5;
		this.LS.LS	.buildTree(subListL);
		this.RS.LS	.buildTree(subListR);
		return true;
	}
	private boolean buildLeft_RightUnary(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		LinkedList<Node> subListL, subListR;
		int opIndex, s = l.size();
		Node n0 = l.get(0),		n1 = l.get(1),
			 n6 = l.get(s-2),	n7 = l.get(s-1);
		if ((!n0.isOpeningParenthesis())	||
			(!n1.isOpeningParenthesis())	||
			(!n6.isClosingParenthesis())	||
			(!n7.isClosingParenthesis()))				return false;
		opIndex = getOpIndex(l, 2, 1);
		if ((opIndex == -1) || (opIndex > (s-4)))		return false;
		Node n2 = l.get(opIndex-1),	n3 = l.get(opIndex),
			 n4 = l.get(opIndex+1), n5 = l.get(opIndex+2);
		if ((!n2.isClosingParenthesis())	||
			(!n3.isBinaryOperator())		||
			(!n4.isUnaryOperator())			||
			(!n5.isOpeningParenthesis()))				return false;

		this.node	= n3;
		this.LS		= new ExpressionTree();
		this.RS		= new ExpressionTree();
		this.RS.LS	= new ExpressionTree();
		subListL	= subList(l, 1, 		opIndex);
		subListR	= subList(l, opIndex+2,	s-1);
		this.RS.node= n4;
		this.LS		.buildTree(subListL);
		this.RS.LS	.buildTree(subListR);
		return true;
	}
	private boolean buildRight_LeftUnary(LinkedList<Node> l) throws ExceptionUnrecognizedExpression
	{
		LinkedList<Node> subListL, subListR;
		int opIndex, s = l.size();
		Node n0 = l.get(0),		n1 = l.get(1),	n2 = l.get(2),
			 n6 = l.get(s-2),	n7 = l.get(s-1);
		if ((!n0.isOpeningParenthesis())	||
			(!n1.isUnaryOperator())			||
			(!n2.isOpeningParenthesis())	||
			(!n6.isClosingParenthesis())	||
			(!n7.isClosingParenthesis()))				return false;
		opIndex = getOpIndex(l, 3, 1);
		if ((opIndex == -1) || (opIndex > (s-4)))		return false;
		Node n3 = l.get(opIndex-1),	n4 = l.get(opIndex),
			 n5 = l.get(opIndex+1);
		if ((!n3.isClosingParenthesis())	||
			(!n4.isBinaryOperator())		||
			(!n5.isOpeningParenthesis()))				return false;

		this.node	= n4;
		this.LS		= new ExpressionTree();
		this.LS.LS	= new ExpressionTree();
		this.RS		= new ExpressionTree();
		subListL	= subList(l, 2, 		opIndex);
		subListR	= subList(l, opIndex+1,	s-1);
		this.LS.node= n1;
		this.LS.LS	.buildTree(subListL);
		this.RS		.buildTree(subListR);
		return true;
	}
	private boolean buildWithUselessParenthesis(LinkedList<Node>l) throws ExceptionUnrecognizedExpression
	{
		int s = l.size(), beginning = -1;
		Node n0 = l.get(0),		n1 = l.get(1),		n2 = l.get(2),
			 n3 = l.get(s-2),	n4 = l.get(s-1);
		if ((!n0.isOpeningParenthesis()) 	||
			(!n3.isClosingParenthesis())	||
			(!n4.isClosingParenthesis())) 	return false;
		if (n1.isOpeningParenthesis())		beginning = 2;
		if ((n1.isUnaryOperator())	&&
			(n2.isOpeningParenthesis()))	beginning = 3;
		if (beginning == -1)				return false;

		beginning = getOpIndex(l, beginning, 1);
		if ((beginning == -1) || (beginning >(s-1))) throw new RuntimeException();
		if (beginning < (s-1))				return false;

		l.removeFirst();
		l.removeLast();
		buildTree(l);
		return true;
	}
// -------------------------------------------------
// Methodes Auxiliaires
// -------------------------------------------------
	// Initialiser nbrOpening = 1
	private int getOpIndex(LinkedList<Node> l, int beginning, int nbrOpening)
	{
		if ((beginning >= l.size()) || (nbrOpening < 0))	return -1;
		if (nbrOpening == 0)								return beginning;
		Node n = l.get(beginning);
		if (n.isOpeningParenthesis())	return getOpIndex(l, beginning+1, nbrOpening+1);
		if (n.isClosingParenthesis())	return getOpIndex(l, beginning+1, nbrOpening-1);
		else							return getOpIndex(l, beginning+1, nbrOpening);
	}
	private LinkedList<Node> subList(LinkedList<Node> l, int beginning, int end)
	{
		LinkedList<Node> res = new LinkedList<Node>();

		for (int i=beginning; i<end; i++)
		{
			res.add(l.get(i));
		}
		return res;
	}
	private boolean isIn(String s, Variable[] tab)
	{
		if (tab == null) return false;
		for (int i=0; i<tab.length; i++)
		{
			if (s.equals(tab[i].name)) return true;
		}
		return false;
	}
	private String getStringOfList(LinkedList<Node> l)
	{
		String res = "";
		for (int i=0; i<l.size(); i++)
		{
			res += l.get(i).getVal();
		}
		return res;
	}
}