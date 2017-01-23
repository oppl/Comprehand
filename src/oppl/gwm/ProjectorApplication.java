package oppl.gwm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JMenuBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.jhotdraw.application.DrawApplication;
import org.jhotdraw.framework.Drawing;
import org.jhotdraw.framework.DrawingView;

public class ProjectorApplication extends DrawApplication {

	private ProjectorDrawingView dv = null;
	
	public ProjectorApplication() {
		super("");
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gdevs = ge.getScreenDevices();
		GraphicsDevice gs = null;
		if (gdevs.length < 2) {
			System.out.println("Only one screen found - selecting default");
			gs = ge.getDefaultScreenDevice();
		}
		else {
			System.out.println(gdevs.length+" screens found - selecting secondary screen");
			gs = gdevs[1];
		}
	    if (gs.isFullScreenSupported()) {
	        System.out.println("Fullscreen supported");
	    } else {
	        System.out.println("Fullscreen not supported");
	    }
	    GraphicsConfiguration gc = gs.getDefaultConfiguration();
		this.dispose();
		this.setUndecorated(true);
		this.pack();
		if (gdevs.length >= 2) this.open();
	    try {
	        // Enter full-screen mode
			this.setLocation(gc.getBounds().getLocation());
			this.setSize(gc.getBounds().width,gc.getBounds().height);
	        // ...
	    } finally {
	        // Exit full-screen mode
	       // gs.setFullScreenWindow(null);
	    }

	}

	public ProjectorDrawingView getDrawingView() {
		return dv;
	}

	public void reset() {
		dv.setDrawing(this.createDrawing());
	}
	
	@Override
	protected DrawingView createDrawingView(Drawing newDrawing) {
		Dimension d = getDrawingViewSize();
		DrawingView newDrawingView = new ProjectorDrawingView(this, d.width, d.height);
		newDrawingView.setDrawing(newDrawing);
		newDrawingView.setBackground(Color.BLACK);
		// notify listeners about created view when the view is added to the desktop
		//fireViewCreatedEvent(newDrawingView);
		dv = (ProjectorDrawingView) newDrawingView;
		return newDrawingView;
	}

	@Override
	protected void createMenus(JMenuBar mb) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected JTextField createStatusLine() {
		JTextField field = new JTextField("", 0);
		field.setBackground(Color.BLACK);
		field.setEditable(false);
		return field;
	}
	
	@Override
	protected void createTools(JToolBar palette) {
		
	}

	@Override
	protected void open(final DrawingView newDrawingView) {
		super.open(newDrawingView);
		getContentPane().remove(getStatusLine());
	}
		
	
}
