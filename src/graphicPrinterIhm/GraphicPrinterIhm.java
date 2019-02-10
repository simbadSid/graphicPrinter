package graphicPrinterIhm;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import graphicPrinter.Application;
import graphicPrinterInterface.*;
import graphicPrinterWindow.*;












public class GraphicPrinterIhm
{
//--------------------------------------------- 
// Attributs:
//----------------------------------------------
		// Parametres du jeux
		private final String		frameName 			= "Graphic Printer";
		private final int 			defaultFrameWidth	= 1280;
		private final int			defaultFrameHeight	= 800;
		private final double		partitionW			= 8./10.;		// Division horizontal de la fenetre
		private final double		partitionH			= 7./10;		// Division verticale de la fenetre

//---------------------------------------------
// Constructeur:
//---------------------------------------------
		public GraphicPrinterIhm(Application app, int xMin, int xMax, int yMin, int yMax)
		{
			// Initialiser les panneaux graphiques
			PanelManager panelManager 		= new PanelManager(frameName, xMin, xMax, yMin, yMax, defaultFrameWidth, defaultFrameHeight, partitionW, partitionH);
			JFrame frame					= panelManager.getFrame();
			MainWindow mainWindow			= panelManager.getMainWindow();
			InfoWindow infoWindow			= panelManager.getInfoWindow();
			ControlWindow controlWindow		= panelManager.getControlWindow();
			GraphicGround gg				= panelManager.getGraphicGround();

			// Initialiser l'application
			app.setGraphicAttributs(gg, mainWindow, infoWindow, controlWindow);

			// Initialiser la souris
			MouseInterface ecouteurSouris	= new MouseInterface(app);
			mainWindow.addMouseListener	(ecouteurSouris);
			mainWindow.addMouseMotionListener (ecouteurSouris);

			// Initialisation des boutons
			initButtons(app, frame, controlWindow, gg, ecouteurSouris);

			// Initialisation de la fenetre graphique
			initFrame(frame, app, panelManager);
		}

//-------------------------------------------
// Methodes Auxiliaires:
//-------------------------------------------
		private void initButtons(Application app, JFrame frame, ControlWindow controlWindow, GraphicGround gg, MouseInterface ecouteurSouris)
		{
			JMenuBar menuBar;
			JMenu menu, subMenu;
			JMenuItem item;

			// Initialisation des ecouteur d'actions
			NewInterface									ni		= new NewInterface(app);
			SaveInterface									si		= new SaveInterface(app);
			ExitInterface									ei		= new ExitInterface(si);
			AddPointFunctionInterface						apfi	= new AddPointFunctionInterface(app);
			AddPiecesOfPolynomialInterface					apopi	= new AddPiecesOfPolynomialInterface(app);
			AddVandermondeInterpolationFunctionInterface	avifi	= new AddVandermondeInterpolationFunctionInterface(app);
			AddLagrangeInterpolationFunctionInterface		alifi	= new AddLagrangeInterpolationFunctionInterface(app);
			AddContinueFunction1VarInterface				acf1vi	= new AddContinueFunction1VarInterface(app);
			DerivateFunctionInterface						dfi		= new DerivateFunctionInterface(app);
			RemoveFunctionInterface							rfi		= new RemoveFunctionInterface(app);
			DimensionsInterface								di		= new DimensionsInterface(app, gg);
			FieldColorInterface								fci		= new FieldColorInterface(app);
			FunctionsParameterInterface						fpi		= new FunctionsParameterInterface(app);
			ShowAxesInterface								sai		= new ShowAxesInterface(app);
			ShowGridInterface								sgridi	= new ShowGridInterface(app);
			ShowGraduationInterface							sgradi	= new ShowGraduationInterface(app);

			// Ajout des boutons du pan de control
			controlWindow.setAxesButtonActionListener(sai);
			controlWindow.setGridButtonActionListener(sgridi);
			controlWindow.setGraduationButtonActionListener(sgradi);
			controlWindow.setTangentButtonActionListener(ecouteurSouris);

			// Initialisation de la bar des menus
			menuBar = new JMenuBar();
			menu	= new JMenu("Main");						// Menu "Main"
			item	= new JMenuItem("New");						//		Item "New"
			item.addActionListener(ni);
			menu.add(item);
			item	= new JMenuItem("Save");					//		Item "Save"
			item.addActionListener(si);
			menu.add(item);
			item	= new JMenuItem("Exit");					//		Item "Exit"		
			item.addActionListener(ei);
			menu.add(item);
			menuBar.add(menu);
			menu	= new JMenu("Functions");					// Menu "Functions"

			item	= new JMenuItem("Add Point Function");		//		Item "Add Point Function
			item	.addActionListener(apfi);
			menu	.add(item);
																//		Item "Add Point Function
			item	= new JMenuItem("Add Pieces Of Polynomial Function");
			item	.addActionListener(apopi);
			menu	.add(item);

			subMenu	= new JMenu("Add Interpolation Function");	//		Menu "Add Interpolation Function
			item 	= new JMenuItem("Vandermonde Interpolation");//			Item "Vandermonde Interpolation"
			item	.addActionListener(avifi);
			subMenu	.add(item);
			item	= new JMenuItem("Lagrange Interpolation");	//			Item "Lagrange Interpolation"
			item	.addActionListener(alifi);
			subMenu	.add(item);
			menu	.add(subMenu);

			item	= new JMenuItem("Add Continue Function");	//		Item "Add Continue Function
			item	.addActionListener(acf1vi);
			menu	.add(item);

			item	= new JMenuItem("Derivate Function");		//		Item "Add Continue Function
			item	.addActionListener(dfi);
			menu	.add(item);

			item	= new JMenuItem("Remove Function");			//		Item "Remove Function
			item	.addActionListener(rfi);
			menu	.add(item);
			menuBar.add(menu);
			menu	= new JMenu("Parameters");					// Menu "Parameters
			item	= new JMenuItem("Dimensions");				//		Item "Dimensions"
			item	.addActionListener(di);
			menu	.add(item);
			item	= new JMenuItem("Field Colors");			//		Item "FieldColor
			item	.addActionListener(fci);
			menu	.add(item);
			item	= new JMenuItem("Functions Parameters");	//		Item "Functions Parameters"
			item	.addActionListener(fpi);
			menu	.add(item);
			menuBar.add(menu);

			frame.setJMenuBar(menuBar);
		}
		private void initFrame(JFrame frame, Application app, PanelManager panelManager)
		{
			ResizerInterface resizer = new ResizerInterface(app, panelManager);
			frame.addComponentListener(resizer);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
}