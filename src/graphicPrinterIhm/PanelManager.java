package graphicPrinterIhm;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import graphicPrinterWindow.ControlWindow;
import graphicPrinterWindow.InfoWindow;
import graphicPrinterWindow.MainWindow;











public class PanelManager
{
// ------------------------------------
// Attributs
// ------------------------------------
		// Variables graphique
		private JFrame					frame;
		private MainWindow				mainWindow;
		private InfoWindow				infoWindow;
		private ControlWindow			controlWindow;
		private GraphicGround 			gg;
		private int						mainPanelWidth;
		private int						mainPanelHeight;

		// Parametres du jeux
		private String					frameName;
		private int 					frameWidth;
		private int						frameHeight;
		private int						frameSide			= 55;
		private double					partitionW;			// Division horizontal de la fenetre
		private double					partitionH;			// Division verticale de la fenetre droite
		private JSplitPane 				frameOrganizer1;
		private JSplitPane 				frameOrganizer2;

// ------------------------------------
// Constructeur
// ------------------------------------	
	public PanelManager(String frameName, int xMin, int xMax, int yMin, int yMax, int frameWidth, int frameHeight, double partitionW, double partitionH) throws RuntimeException
	{
		if (!allDataAreOk(xMin, xMax, yMin, yMax, frameWidth, frameHeight, partitionW, partitionH)) throw new  RuntimeException();
		if ((frameWidth <= 0) || (frameHeight <= 0)) throw new RuntimeException("Parametre minimal de la fenetre");

		// Initialisation des donnees
		this.frameName		= frameName;
		this.frameWidth		= frameWidth;
		this.frameHeight	= frameHeight;
		this.partitionW		= partitionW;
		this.partitionH		= partitionH;

		int w = (int)(this.partitionW * frameWidth);			// Largeur du panneau principal
	    int h = (int)(this.partitionH * frameHeight);			// Hauteur du panneau lateral principale

        // Dimensionement de la fenetre graphique
		this.frame 					= new JFrame(this.frameName);
		this.frame					.setSize(this.frameWidth, this.frameHeight);

		// Initialisation du terrain graphique
		this.gg						= new GraphicGround(xMin, xMax, yMin, yMax,w, frameHeight);

		// Initialisation du panneaux principal
		this.mainPanelWidth			= w;
		this.mainPanelHeight		= frameHeight - frameSide;
		this.mainWindow				= new MainWindow(mainPanelWidth, mainPanelHeight, gg);

        // Initialisation du panneaux lateral haut
        this.infoWindow				= new InfoWindow(frameWidth - w, h);

        // Initialisation du panneaux lateral haut
        this.controlWindow			= new ControlWindow(frameWidth - w, frameHeight - h);

        // Placer le separateur de fenetres horizontal
        this.frameOrganizer1	= new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, infoWindow, controlWindow);
        this.frameOrganizer1.setDividerLocation(h);
        this.frameOrganizer1.setDividerSize(0);

        // Placer le separateur de fenetres vertical
        this.frameOrganizer2	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, mainWindow, frameOrganizer1);
        this.frameOrganizer2.setDividerLocation(w);
        this.frameOrganizer2.setDividerSize(0);

        // Incluer l'ensemble des pan dans la fenetre
        this.frame.add(frameOrganizer2);
	}

// ------------------------------------
// Accesseur
// ------------------------------------	
	public JFrame 			getFrame()				{return frame;}
	public MainWindow 		getMainWindow() 		{return mainWindow;}
	public InfoWindow		getInfoWindow() 		{return infoWindow;}
	public ControlWindow	getControlWindow()		{return controlWindow;}
	public GraphicGround	getGraphicGround()		{return gg;}
	public int				getMainPanelWidth() 	{return mainPanelWidth;}
	public int				getMainPanelHeight() 	{return mainPanelHeight;}

// ------------------------------------
// Methodes locales
// ------------------------------------	
	public void resizeFrame(int width, int height)
	{
		if ((width <= 0) || (height <= 0)) throw new RuntimeException("Minimal parametre riched");

		this.frameWidth		= width;
		this.frameHeight	= height;

		int w = (int)(partitionW * frameWidth);			// Largeur du panneau principal
	    int h = (int)(partitionH * frameHeight);		// Hauteur du panneau lateral principale

        // Dimensionement de la fenetre graphique
		this.frame.setSize(frameWidth, frameHeight);

		// Dimensionement du terrain graphique
		gg.resize(w, frameHeight);

		// Dimensionement du paneau pricipale
		this.mainPanelWidth			= w;
		this.mainPanelHeight		= frameHeight - frameSide;

		// Placer les separateurs de fenetres
        this.frameOrganizer1.setDividerLocation(h);
        this.frameOrganizer2.setDividerLocation(w);
	}
// ------------------------------------
// Methodes Auxiliaire
// ------------------------------------	
	private boolean allDataAreOk(int xMin, int xMax, int yMin, int yMax, int frameWidth, int frameHeight, double partitionW, double partitionH)
	{
		if (xMax <= xMin)		return false;
		if (yMax <= yMin)		return false;
		if (frameWidth <= 0)	return false;
		if (frameHeight <= 0)	return false;
		if (partitionW > 1)		return false;
		if (partitionW < 0)		return false;
		if (partitionH > 1)		return false;
		if (partitionH < 0)		return false;
		return true;
	}
}