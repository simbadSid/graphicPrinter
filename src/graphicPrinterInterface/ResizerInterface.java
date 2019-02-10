package graphicPrinterInterface;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import graphicPrinter.Application;
import graphicPrinterIhm.PanelManager;












public class ResizerInterface implements ComponentListener
{
// ------------------------------------
// Attributs
// ------------------------------------
	private Application		app;
	private PanelManager	panelManager;

// ------------------------------------
// Constructeur
// ------------------------------------	
		public ResizerInterface(Application	app, PanelManager panelManager)
		{
			this.app			= app;
			this.panelManager	= panelManager;
		}

// -----------------------------------
// Methodes Locales
// -----------------------------------
		public void componentResized(ComponentEvent e)
		{
			int width 	= e.getComponent().getWidth();
			int height 	= e.getComponent().getHeight();

			this.panelManager.resizeFrame(width, height);
			int w = panelManager.getMainPanelWidth();
			int h = panelManager.getMainPanelHeight();
			this.app.resetSize(w, h);
		}

		public void componentHidden(ComponentEvent arg0) 	{}
		public void componentMoved(ComponentEvent arg0) 	{}
		public void componentShown(ComponentEvent arg0) 	{}
}