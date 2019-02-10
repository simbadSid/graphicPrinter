package graphicPrinter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import symbolicComputation.ExceptionEmptyDomain;
import symbolicComputation.ExceptionVariableRepresentation;
import function.*;
import graphicPrinterIhm.GraphicGround;
import graphicPrinterWindow.ControlWindow;
import graphicPrinterWindow.InfoWindow;
import graphicPrinterWindow.MainWindow;







public class Application
{
//--------------------------------------------
// Attributs:
//--------------------------------------------
		// Attributs de la fenetre graphique
		private GraphicGround			gg;
		private MainWindow				mainWindow;
		private InfoWindow				infoWindow;
		private ControlWindow			controlWindow;

		// Atributs du pan principal
		private double					defaultXMin;
		private double					defaultXMax;
		private double					defaultYMin;
		private double					defaultYMax;
		private double					gridX;
		private double					gridY;
		private double					graduationX;
		private double					graduationY;
		private double					xGapPercent					= 5./100.;		// Pourcentage de l'espace horizontal entre les max du panneau et celui des fonctions
		private double					yGapPercent					= 5./100.;		// Pourcentage de l'espace vertical entre les max du panneau et celui des fonctions
		private double					tangentPercent				= 40./100.;		// Pourcentage du panneau pour le dessin de tangente
		private boolean					drawAxes					= true;
		private boolean					drawGrid					= false;
		private boolean					drawGraduation				= false;
		private boolean					linkInterpolationPoint		= true;
		private boolean					displayInterpolationPoint	= true;

		// Stockage des fonctions
		private LinkedList<History>		functions;
		private int						selectedFunctionIndex		= -1;
		private Function				selectedFunction;
		private Function				selectedDerivateFunction;
		private Function				tangent;
		private Color					tangentColor;

//--------------------------------------------
// Constructeur:
//--------------------------------------------
		public Application(int xMin, int xMax, int yMin, int yMax)
		{
			this.defaultXMin = xMin;
			this.defaultXMax = xMax;
			this.defaultYMin = yMin;
			this.defaultYMax = yMax;
		}

//--------------------------------------------
// Methodes d'initialisation des attributs:
//--------------------------------------------
		public void setGraphicAttributs(GraphicGround gg, MainWindow mainWindow, InfoWindow infoWindow, ControlWindow	controlWindow)
		{
			this.gg						= gg;
			this.mainWindow				= mainWindow;
			this.infoWindow				= infoWindow;
			this.controlWindow			= controlWindow;
			this.functions				= new LinkedList<History>();
		}

//--------------------------------------------
//Methodes principale:
//--------------------------------------------
		/***********************************************************************
		 * Efface le fenetre graphique en gardant les memes attributs
		 ***********************************************************************/
		public void initApplication()
		{
			// Init Attributs
			this.drawAxes		= true;
			this.drawGraduation	= false;
			this.drawGrid		= false;
	
			// Init Graphic Ground
			gg.resetAxes(defaultXMin, defaultXMax, defaultYMin, defaultYMax);
	
			// Init MainWindow
			initMainWindow();
	
			// Init Info window
			infoWindow.setFunction("null", null);
			infoWindow.setFunctionType("");
			infoWindow.setFunctionDomain("");
			infoWindow.setDialog("");
	
			// Init Control window
			controlWindow.setAxesButtonText(this.drawAxes);
			controlWindow.setGridButtonText(this.drawGrid);
			controlWindow.setGraduationButtonText(this.drawGraduation);

			// Init List of functions
			functions.clear();
			deselectFunction();
		}
		/***********************************************************************
		 * Ajout une fonction et l'affiche avec les characteriqtiques donnees
		 ***********************************************************************/
		public void addFunction(Function f, Color c)
		{
			boolean test = adjustMainWindowAttributs(f);
			if (test)
			{
				initMainWindow();
				drawAllFunctions();
			}
			mainWindow.drawFunction(f, linkInterpolationPoint, displayInterpolationPoint, false, c);
			infoWindow.setFunction(f.getFunctionRepresentation(), f.getVariable());
			infoWindow.setFunctionType(f.getTypeRepresentation());
			infoWindow.setFunctionDomain(f.getDomainRepresentation());
			infoWindow.setDialog("Function Added");
			functions.add(new History(f, c));
		}
		/***********************************************************************
		 * Supprime la fonction dont la representation textuelle est en entree
		 ***********************************************************************/
		public void removeFunction(String functionRepresentation)
		{
			for (int i = 0; i<functions.size(); i++)
			{
				Function f = functions.get(i).f;
				this.gg.resetAxes(-1, 1, -1, 1);
				if (f.equal(functionRepresentation))
				{
					functions.remove(i);
					LinkedList<History> tmp = this.functions;
					this.functions = new LinkedList<History>();
					for (int j=0; j<tmp.size(); j++)
					{
						History h = tmp.get(j);
						adjustMainWindowAttributs(h.f);
						functions.add(h);
					}
					initMainWindow();
					drawAllFunctions();
					deselectFunction();
					infoWindow.setFunction("", "");
					infoWindow.setFunctionType("");
					infoWindow.setFunctionDomain("");
					infoWindow.setDialog("Function f(x) = " + functionRepresentation + " Removed");
					if (functions.size() == 0) this.resetAxes(defaultXMin, defaultXMax, defaultYMin, defaultYMax);
					return;
				}
			}
			throw new RuntimeException("The function f(x) = " + functionRepresentation + " is not known!");
		}
		/******************************************************************
		 * Ajoute la fonction derivee dont la representation 
		 * textuelle est en entree
		 *****************************************************************/
		public void derivateFunction(String functionRepresentation)
		{
			String message = "";
			for(int i=0; i<functions.size(); i++)
			{
				Function f	= functions.get(i).f;
				Color c		= functions.get(i).c;
				if (f.equal(functionRepresentation))
				{
					Function fprim;
					try 							{fprim = f.derivate();}
					catch (ExceptionEmptyDomain e)
					{
						message = "The function f(x) = " + functionRepresentation + " is not Derivable on this domain!";
						break;
					}
					addFunction(fprim, c);
					return;
				}
			}
			if (message.length() == 0) message = "The function f(x) = " + functionRepresentation + " is not known!";
			infoWindow.setDialog(message);
			throw new RuntimeException(message);
		}
		/***********************************************************************
		 * Selectionne ou desselectionne une fonction
		 * @param xReal, yReal: Coordonnee du point selectionee(en coordonnes reel)
		 ***********************************************************************/
		public void selectFunction(double xReal, double yReal)
		{
			double x		= gg.convertRealToAxesX(xReal);
			double y		= gg.convertRealToAxesY(yReal);
			double dx		= gg.convertDistRealToAxesX(mainWindow.getMatchGapX());
			double dy		= gg.convertDistRealToAxesY(mainWindow.getMatchGapY());
			int selection	= -1;

			// Recherche de la fonction choisie
			Function f = null;
			for (int i=0; i<functions.size(); i++)
			{
				f = functions.get(i).f;
				if (f.matchPoint(x, y, dx, dy)){selection = i; break;}
			}

			// Refaire l'affichage
			if ((selection != -1) && (selection == selectedFunctionIndex)) return;
			if (selection != -1)
			{
				this.selectedFunctionIndex			= selection;
				this.selectedFunction				= f;
				try {this.selectedDerivateFunction	= f.derivate();}
				catch (ExceptionEmptyDomain e) {throw new RuntimeException();}
				infoWindow.setFunction(f.getFunctionRepresentation(), f.getVariable());
				infoWindow.setFunctionType(f.getTypeRepresentation());
				infoWindow.setFunctionDomain(f.getDomainRepresentation());
				infoWindow.setDialog("Selected Function");
			}
			else
			{
				if (this.selectedFunction == null) return;
				deselectFunction();
				infoWindow.setFunction("null", "");
				infoWindow.setFunctionType("");
				infoWindow.setFunctionDomain("");
				infoWindow.setDialog("");
			}
			initMainWindow();
			drawAllFunctions();
		}
		/***********************************************************************
		 * @return the name of the selected function, or null if no function 
		 * has been selected
		 ***********************************************************************/
		public String getSelectedFunction()
		{
			if (selectedFunction == null) return null;
			return selectedFunction.getFunctionRepresentation();
		}
		/***********************************************************************
		 * Redimensionne le terrain graphique et refait l'affichage
		 ***********************************************************************/
		public void resetSize(int width, int height)
		{
			// Cas ou il n'y a aucun changement
			if ((width == mainWindow.getWidth()) && (height == mainWindow.getHeight())) return;

			gg.resize(width, height);
			mainWindow.resetSize(width, height);
			initMainWindow();
			drawAllFunctions();
		}
		/***********************************************************************
		 * Redimensionne le terrain graphique et refait l'affichage
		 ***********************************************************************/
		public void resetAxes(double xMin, double xMax, double yMin, double yMax)
		{
			if ((xMin >= xMax) || (yMin >= yMax))
				throw new RuntimeException("Parametre invalide: xMin = " + xMin + "  xMax = " + xMax + "  yMin = " + yMin + "  yMax = " + yMax);

			// Cas ou il n'y a aucun changement
			if ((xMin == gg.getXMin()) && (xMax == gg.getXMax()) && (yMin == gg.getYMin()) && (yMax == gg.getYMax())) return;

			gg.resetAxes(xMin, xMax, yMin, yMax);
			initMainWindow();
			drawAllFunctions();
		}
		/***********************************************************************
		 * Sauvegarde l'image courante de mainWindow dans le fichier fic
		 * @param fic
		 * @throws FileNotFoundException
		 * @throws IOException
		 ***********************************************************************/
		public void saveImage(File fic) throws FileNotFoundException, IOException
		{
			BufferedImage image = mainWindow.getImage();
			FileImageOutputStream outputFile = new FileImageOutputStream(fic);
			ImageIO.write(image, "png", outputFile);
			outputFile.close();
		}
		/*************************************************************************
		 * Affiche ou efface les axes
		 *************************************************************************/
		public void showAxes()
		{
			drawAxes = !drawAxes;
			controlWindow.setAxesButtonText(drawAxes);
			initMainWindow();
			drawAllFunctions();
		}
		/**********************************************************************
		 * Affiche ou efface les gaduations sur les axes
		 * @param show
		 * @param x: indique l'espacement horizontal des graduations
		 * @param y: indique l'espacement vertical des graduations
		 * @return: true si l'affiche a ete realise.
		 * false si non
		 **********************************************************************/
		public boolean setGraduation(boolean show, double x, double y)
		{
			if (show == drawGraduation) return false;
			if (show == false)											// Cas d'un effacement
			{
				drawGraduation = false;
				initMainWindow();
				drawAllFunctions();
				controlWindow.setGraduationButtonText(show);
				infoWindow.setDialog("Graduation hiden");
				return true;
			}
			else														// Cas d'un dessin
			{
				if (mainWindow.isDrawableGraduation(x, y))
				{
					graduationX		= x;
					graduationY		= y;
					drawGraduation	= true;
					initMainWindow();
					drawAllFunctions();
					controlWindow.setGraduationButtonText(show);
					infoWindow.setDialog("Graduation drawen");				
					return true;
				}
				else
				{
					infoWindow.setDialog("Too Much Graduation!\nGraduation draw interupted.");
					return false;
				}
			}
		}
		/**********************************************************************
		 * Affiche ou efface la grille sur le terrain
		 * @param show
		 * @param x: indique l'espacement horizontal de la grille
		 * @param y: indique l'espacement vertical de la grille
		 * @return: true si l'affiche a ete realise.
		 * false si non
		 **********************************************************************/
		public boolean setGrid(boolean show, double x, double y)
		{
			if (show == drawGrid) return false;
			if (show == false)											// Cas d'un effacement
			{
				drawGrid = false;
				initMainWindow();
				drawAllFunctions();
				controlWindow.setGridButtonText(show);
				infoWindow.setDialog("Grid hiden");
				return true;
			}
			else														// Cas d'un dessin
			{
				if (mainWindow.isDrawableGrid(x, y))
				{
					gridX		= x;
					gridY		= y;
					drawGrid	= true;
					initMainWindow();
					drawAllFunctions();
					controlWindow.setGridButtonText(show);
					infoWindow.setDialog("Grid drawen");
					return true;
				}
				else
				{
					infoWindow.setDialog("Too Much Grid!\nGrid draw interupted.");
					return false;
				}
			}
		}
		/**********************************************************************
		 * Permet d'activer ou de desactiver la fonction d'affichage de tangente
		 * @param enable
		 **********************************************************************/
		public void enableTangentPrinter(boolean enable)
		{
			if (!enable)
			{
				controlWindow.setTangentButtonText(enable);
				infoWindow.setDialog("");
				if (tangent != null)
				{
					tangent				= null;
					initMainWindow();
					drawAllFunctions();
				}
			}
			else
			{
				controlWindow.setTangentButtonText(enable);
				infoWindow.setDialog("Tangent printer enabled");
			}
		}
		/**********************************************************************
		 * Affiche la tangent de la fonction passant par le point (xReal, yReal)
		 * @param isInScreen: indique si la sourie est sortie du panneau graphique
		 **********************************************************************/
		public void printTangent(boolean isInScreen, double xReal, double yReal)
		{
			if (!isInScreen)
			{
				infoWindow.setDialog("");
				if (tangent != null)
				{
					tangent 		= null;
					tangentColor	= null;
					initMainWindow();
					drawAllFunctions();
				}
			}
			else
			{
				// Cas ou il n'y a pas de fonction choisie
				if (selectedFunctionIndex == -1)
				{
					String txt = "Tangent printer enabled";
					infoWindow.setDialog(txt);
					if (tangent != null)
					{
						tangent 			= null;
						initMainWindow();
						drawAllFunctions();
					}
				}
				// Cas ou une fonction a ete trouvee
				else
				{
					double fprim, yPos;
					double xPos	= (double) gg.convertRealToAxesX(xReal);
					try {yPos	= selectedFunction.eval(xPos);}
					catch (Exception e)
					{
						String txtDial = "Tangent printer enabled\nPoint out of the definition domain";
						infoWindow.setDialog(txtDial);
						return;
					}
					double[] tX	= new double[3];
					double[] tY	= new double[3];
					try {fprim	= selectedDerivateFunction.eval(xPos);}
					catch(Exception e) 
					{
						tangent = null;
						initMainWindow();
						drawAllFunctions();
						String txtDial = "Tangent printer enabled\nf`(x) = undefine";
						infoWindow.setDialog(txtDial);
						return;
					}
					String txtDial = "Tangent printer enabled\n";
					double l	= (gg.getXMax() - gg.getXMin()) * this.tangentPercent;
					double alpha= Math.atan(fprim);
					double lx	= Math.cos(alpha) * l;
					double ly	= Math.sin(alpha) * l;
					tX[0] = xPos - lx/2;		tY[0] = yPos - ly/2;
					tX[1] = xPos;				tY[1] = yPos;
					tX[2] = xPos + lx/2;		tY[2] = yPos + ly/2;
					String funcName = selectedFunction.getFunctionRepresentation();
					if ((Double.isNaN(tX[0])) 		|| (Double.isNaN(tX[1]))	 || (Double.isNaN(tX[2])) ||
						(Double.isNaN(tY[0])) 		|| (Double.isNaN(tY[1]))	 || (Double.isNaN(tY[2])) ||
						(Double.isInfinite(tY[0]))	|| (Double.isInfinite(tY[1])) || (Double.isInfinite(tY[2])))
					{
						boolean test = (tangent != null);
						tangent	= null;
						if (test) {initMainWindow(); drawAllFunctions();infoWindow.setDialog(txtDial + "No tangent Defined");}
					}
					else
					{
						tangent				= new PointFunction(tX, tY, "Tangente of the function " + funcName);
						tangentColor		= functions.get(selectedFunctionIndex).c;
						initMainWindow();
						drawAllFunctions();
						txtDial += "f`(x) = " + (float)fprim;
						infoWindow.setDialog(txtDial);				
					}
				}
			}
		}
		/**********************************************************************
		 * Affiche l'emplacement de la souris
		 * @param inScreen: indique si la sourie est sortie du panneau graphique
		 **********************************************************************/
		public void mooveMouse(boolean isInScreen, double xReal, double yReal)
		{
			if (!isInScreen)
			{
				infoWindow.setPosition("");
			}
			else
			{
				float xPos = (float) gg.convertRealToAxesX(xReal);
				float yPos = (float) gg.convertRealToAxesY(yReal);
				String txt = "x = " + xPos + "\ny = " + yPos;
				infoWindow.setPosition(txt);				
			}
		}

//--------------------------------------------
// Accesseurs:
//--------------------------------------------
		public String	getFunctionName(int funcId)		{return functions.get(funcId).f.getFunctionRepresentation();}
		public int 		getPointType()					{return mainWindow.getPointType();}
		public double	getPointSizePercent()			{return mainWindow.getPointSizePercent();}
		public boolean	getDisplayInterpolationPoint()	{return displayInterpolationPoint;}
		public boolean	getLinkInterpolationPoint()		{return linkInterpolationPoint;}
		public Color 	getBGColor()					{return mainWindow.getBGColor();}
		public Color 	getAxesColor()					{return mainWindow.getAxesColor();}
		public Color 	getGridColor()					{return mainWindow.getGridColor();}
		public Color 	getGraduationColor()			{return mainWindow.getGraduationColor();}
		public String	getFunctionVar(int funcId)		{return functions.get(funcId).f.getVariable();}				
		public boolean	isFunctionVariableSetable(int funcId)
		{
			Function f = functions.get(funcId).f;
			return f.isVariableSetable();
		}
		public String[]	getAllFunction()
		{
			int nbrFunc = functions.size();
			if (nbrFunc == 0) return null;
			String[] res = new String[nbrFunc];
			for (int i = 0; i<nbrFunc; i++)
			{
				res[i] = functions.get(i).f.getFunctionRepresentation();
			}
			return res;
		}
		public String[] getDerivableFunction()
		{
			int nbr = 0;
			for (int i=0; i<functions.size(); i++) if (functions.get(i).f.isDerivable()) nbr++;
			if (nbr == 0) return null;
			String[] res = new String[nbr];
			int j = 0;
			for (int i=0; i<functions.size(); i++)
			{
				if (functions.get(i).f.isDerivable())
				{
					res[j] = functions.get(i).f.getFunctionRepresentation();
					j++;
				}
			}
			return res;
		}
		public Color[]	getFunctionsColor()
		{
			Color[] res = new Color[functions.size()];

			for (int i = 0; i<functions.size(); i++)
			{
				Color c = functions.get(i).c;
				if (c == null)	res[i] = null;
				else			res[i] = new Color(c.getRed(), c.getGreen(), c.getBlue());
			}
			return res;
		}

//--------------------------------------------
// Modificateur:
//--------------------------------------------
		public void setPointType(int pt)
		{
			boolean test = mainWindow.setPointType(pt);
			if (test == true)
			{
				initMainWindow();
				drawAllFunctions();
			}
		}
		public void setPointSizePercent(double percent)
		{
			boolean test = mainWindow.setPointSize(percent);
			if (test == true)
			{
				initMainWindow();
				drawAllFunctions();
			}
		}
		public void setDisplayInterpolationPoint(boolean b)
		{
			if (b == displayInterpolationPoint) return;
			this.displayInterpolationPoint = b;
			initMainWindow();
			drawAllFunctions();
		}
		public void setLinkInterpolationPoint(boolean b)
		{
			if (b == linkInterpolationPoint) return;
			this.linkInterpolationPoint = b;
			initMainWindow();
			drawAllFunctions();
		}
		public void setBGColor(Color c)
		{
			boolean test = mainWindow.setBGColor(c);
			if (test == true)
			{
				initMainWindow();
				drawAllFunctions();
			}
		}
		public void setAxesColor(Color c)
		{
			boolean test = mainWindow.setAxesColor(c);
			if (test == true)
			{
				initMainWindow();
				drawAllFunctions();
			}
		}
		public void setGridColor(Color c)
		{
			boolean test = mainWindow.setGridColor(c);
			if (test == true)
			{
				initMainWindow();
				drawAllFunctions();
			}
		}
		public void setGraduationColor(Color c)
		{
			boolean test = mainWindow.setGraduationColor(c);
			if (test == true)
			{
				initMainWindow();
				drawAllFunctions();
			}
		}
		public void setFunctionVar(String var, int functionId) throws ExceptionVariableRepresentation, ExceptionNoVariableAccepted
		{
			Function f = functions.get(functionId).f;
			f.setVariable(var);
			this.infoWindow.setFunction(f.getFunctionRepresentation(), var);
		}
		public void setFunctionColor(Color c, int functionId)
		{
			History h = functions.get(functionId);
			if ((h.c != null) && (h.c.equals(c)))	return;
			if ((h.c == null) && (c == null))		return;
			h.c = c;
			boolean select = (functionId == selectedFunctionIndex);
			mainWindow.drawFunction(h.f, linkInterpolationPoint, displayInterpolationPoint, select, c);
		}
		public void setAllFieldColors(Color bg, Color axes, Color grid, Color grad)
		{
			boolean t1 = mainWindow.setBGColor(bg);
			boolean t2 = mainWindow.setAxesColor(axes);
			boolean t3 = mainWindow.setGridColor(grid);
			boolean t4 = mainWindow.setGraduationColor(grad);
			if (t1 || t2 || t3 || t4)
			{
				initMainWindow();
				drawAllFunctions();
			}
		}
		public void setAllFunctionsColor(Color[] colorTab)
		{
			if (colorTab.length != functions.size()) throw new RuntimeException("Missing colors in argument colorTab");
			for (int i = 0; i<colorTab.length; i++)
			{
				Color c = colorTab[i];
				if (c == null) continue;
				setFunctionColor(c, i);
			}
		}

//--------------------------------------------
// Methodes Auxiliaires
//--------------------------------------------
		/**********************************************************************
		 * Ajuste la fenetre graphique en fonction de 
		 * f et de l'encien affichage
		 * @param f
		 * @return false si aucun ajustement n'a ete realise
		 **********************************************************************/
		private boolean adjustMainWindowAttributs(Function f)
		{
			double xGap = (gg.getXMax() - gg.getXMin()) * this.xGapPercent;
			double yGap = (gg.getYMax() - gg.getYMin()) * this.yGapPercent;

			if (functions.size() == 0)
			{
				gg.resetAxes(f.getXMin()-xGap, f.getXMax()+xGap, f.getYMin()-yGap, f.getYMax()+yGap);
				return true;
			}
			else
			{
				boolean res = false;
				double xMin0	= f.getXMin() - xGap;
				double xMin1	= gg.getXMin();
				double xMax0	= f.getXMax() + xGap;
				double xMax1	= gg.getXMax();
				double yMin0	= f.getYMin() - yGap;
				double yMin1	= gg.getYMin();
				double yMax0	= f.getYMax() + yGap;
				double yMax1	= gg.getYMax();
				if (xMin0 < xMin1) res = true;
				if (xMax0 > xMax1) res = true;
				if (yMin0 < yMin1) res = true;
				if (yMax0 > yMax1) res = true;
				resetAxes(Math.min(xMin0, xMin1), Math.max(xMax0, xMax1),
							Math.min(yMin0, yMin1), Math.max(yMax0, yMax1));
				return res;
			}
		}
		private void initMainWindow()
		{
			mainWindow.initDrawingWindow();
			if (drawGrid)		mainWindow.drawGrid(gridX, gridY);
			if (drawGraduation)	mainWindow.drawGraduation(graduationX, graduationY);
			if (drawAxes)		mainWindow.drawAxes();
		}
		private void drawAllFunctions()
		{
			for (int i = 0; i<functions.size(); i++)
			{
				History h = functions.get(i);
				if(i == selectedFunctionIndex)	mainWindow.drawFunction(h.f, linkInterpolationPoint, displayInterpolationPoint, true, h.c);
				else							mainWindow.drawFunction(h.f, linkInterpolationPoint, displayInterpolationPoint, false, h.c);
			}
			if (tangent != null)				mainWindow.drawTangent(tangent, tangentColor);
		}
		private void deselectFunction()
		{
			selectedFunctionIndex		= -1;
			selectedFunction			= null;
			selectedDerivateFunction	= null;
			tangent						= null;
			tangentColor				= null;
		}

//--------------------------------------------
// Classe de sauvegarde de l'historique des fonctions:
//--------------------------------------------
		private class History
		{
			// Attributs
			public Function f;
			public Color	c;
			// Constructeur
			public History(Function f, Color c)
			{
				this.f = f;
				this.c = c;
			}
		}
}