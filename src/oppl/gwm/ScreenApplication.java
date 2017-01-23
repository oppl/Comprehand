package oppl.gwm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MenuShortcut;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import mane.DMManager;
import mane.metamodel.Association;
import mane.metamodel.Concept;
import mane.metamodel.Manager;
import mane.metamodel.Manager.RoleConcept;
import oppl.gwm.elements.BasicBuildingBlock;
import oppl.gwm.elements.Blue;
import oppl.gwm.elements.Connection;
import oppl.gwm.elements.Red;
import oppl.gwm.elements.Yellow;
import oppl.gwm.helpers.LabelingTool;
import oppl.reactivisionbridge.UserInputCollector;

import org.jhotdraw.application.DrawApplication;
import org.jhotdraw.framework.Drawing;
import org.jhotdraw.framework.DrawingView;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.framework.Tool;
import org.jhotdraw.standard.AbstractCommand;
import org.jhotdraw.standard.StandardDrawingView;
import org.jhotdraw.util.Command;
import org.jhotdraw.util.CommandMenu;

public class ScreenApplication extends DrawApplication {

	private ScreenDrawingView dv = null;
	private UserInputCollector resetListener = null;

	public ScreenApplication() {
		super("SUNML Viewer");
		// reg = new TuioMessageDistributorRegistrator("localhost",3589);
		// reg.register("localhost", port);
		this.open();
	}
	
	public void setResetListener(UserInputCollector l) {
		resetListener = l;
	}
	
	private void reset() {
		if (resetListener != null) resetListener.reset();
	}

	protected Dimension defaultSize() {
		return new Dimension(1000,700);
	}
	
	private void save() {
		if (dv == null)
			return;
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			/* generate code 4 twm.xml */
			DMManager dmM = new DMManager("domainModels/xmlSpec", "src-gen");
			// dmM.deleteGeneratedModel("twm");//delete existing model code for
			// test purposes
			dmM.generateModel("twm.xml");

			/* get model manager 4 twm model */
			Manager twm = dmM.getModelManager("twm");
			twm.newModelInstance("GWM Model", "", "");

			synchronized (dv.basicBuildingBlocks) {
				for (BasicBuildingBlock b : dv.basicBuildingBlocks.values()) {
					Concept bc = null;
					if (b instanceof Red)
						bc = twm.generateConcept("Process", b.getTextCaption());
					if (b instanceof Blue)
						bc = twm.generateConcept("Role", b.getTextCaption());
					if (b instanceof Yellow)
						bc = twm.generateConcept("Data", b.getTextCaption());
					bc.addRealisation("XAxis", new Integer(b.getDrawingPosition().x)
							.toString());
					bc.addRealisation("YAxis", new Integer(b.getDrawingPosition().y)
							.toString());
				}
			}
			synchronized (dv.connections) {
				for (Connection c : dv.connections.values()) {
					Association a = null;
					Concept end1 = twm.getConcepts().get(
							c.getFrom().getTextCaption()).iterator().next();
					Concept end2 = twm.getConcepts().get(
							c.getTo().getTextCaption()).iterator().next();
					Set<RoleConcept> rCS = new HashSet<RoleConcept>();
					if (c.getFrom() instanceof Red
							&& c.getTo() instanceof Red) {
						rCS.add(new RoleConcept("before", end1));
						rCS.add(new RoleConcept("after", end2));
						a = twm.generateAssociation("Strictlybefore", rCS);
					}
					if (c.getFrom() instanceof Blue
							&& c.getTo() instanceof Blue) {
						rCS.add(new RoleConcept("involves", end1));
						rCS.add(new RoleConcept("consults", end2));
						a = twm.generateAssociation("InvolesConsults", rCS);
					}
					if (c.getFrom() instanceof Yellow
							&& c.getTo() instanceof Yellow) {
						rCS.add(new RoleConcept("determines", end1));
						rCS.add(new RoleConcept("determinedBy", end2));
						a = twm.generateAssociation("Determines", rCS);
					}
					if (c.getFrom() instanceof Red
							&& c.getTo() instanceof Blue) {
						rCS.add(new RoleConcept("owned", end1));
						rCS.add(new RoleConcept("responsible", end2));
						a = twm.generateAssociation("Responsible", rCS);
					}
					if (c.getFrom() instanceof Blue
							&& c.getTo() instanceof Red) {
						rCS.add(new RoleConcept("owned", end2));
						rCS.add(new RoleConcept("responsible", end1));
						a = twm.generateAssociation("Responsible", rCS);
					}
					if (c.getFrom() instanceof Red
							&& c.getTo() instanceof Yellow) {
						rCS.add(new RoleConcept("creates", end1));
						rCS.add(new RoleConcept("createdBy", end2));
						a = twm.generateAssociation("Creates", rCS);
					}
					if (c.getFrom() instanceof Yellow
							&& c.getTo() instanceof Red) {
						rCS.add(new RoleConcept("input", end1));
						rCS.add(new RoleConcept("processor", end2));
						a = twm.generateAssociation("IsInputFor", rCS);
					}
				}
			}
			try {
				File f = new File(fc.getSelectedFile()
						.getCanonicalPath());
				f.createNewFile();
				dmM.storeTopicMapAsXTM(twm.toTopicMap(), fc.getSelectedFile()
						.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void savePNG() {
		if (dv == null)
			return;
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			BufferedImage b = new BufferedImage(dv.getWidth(),dv.getHeight(),BufferedImage.TYPE_INT_RGB); /* change sizes of course */
			Graphics2D g = b.createGraphics();
			dv.paint(g);
			System.out.println("Captured Model in png");
			try{ImageIO.write(b,"png",new File(fc.getSelectedFile()
					.getCanonicalPath()));}catch (Exception e) {}
		}
	}
	
	private void saveHistoryPNG() {
		if (dv == null)
			return;
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Vector<ScreenDrawingView> sdv = dv.getHistoryImages();
			Vector<BufferedImage> images = new Vector<BufferedImage>();
			
			for (ScreenDrawingView s: sdv){
				BufferedImage b = new BufferedImage(dv.getWidth(),dv.getHeight(),BufferedImage.TYPE_INT_RGB); /* change sizes of course */
				Graphics2D g = b.createGraphics();
				s.paint(g);	
				images.add(b);
			}
			images.remove(0);
			int x = (int) Math.round(Math.sqrt(images.size()));
			int y = (int) Math.round(Math.ceil(Math.sqrt(images.size())));
			BufferedImage complete = new BufferedImage(dv.getWidth()*x, dv.getHeight()*y,BufferedImage.TYPE_INT_RGB);
			int cntX = 0;
			int cntY = 0;
			Graphics2D g = complete.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,dv.getWidth()*x, dv.getHeight()*y);
			for (BufferedImage b:images) {
				
				g.drawImage(b,dv.getWidth()*cntX,dv.getHeight()*cntY,null);
				g.setColor(Color.black);
				g.drawRect(dv.getWidth()*cntX,dv.getHeight()*cntY,dv.getWidth(),dv.getHeight());
				cntX++;
				if (cntX == x) {
					cntY++;
					cntX=0;
				}
			}
			System.out.println("Captured History in png");
			try{ImageIO.write(complete,"png",new File(fc.getSelectedFile()
					.getCanonicalPath()));}catch (Exception e) {}
		}
	}

	private void saveHierarchyPNG() {
		if (dv == null)
			return;
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Vector<ScreenDrawingView> sdv = dv.getEmbeddedImages();
			Vector<BufferedImage> images = new Vector<BufferedImage>();
			
			for (ScreenDrawingView s: sdv){
				BufferedImage b = new BufferedImage(dv.getWidth(),dv.getHeight(),BufferedImage.TYPE_INT_RGB); /* change sizes of course */
				Graphics2D g = b.createGraphics();
				s.paint(g);	
				images.add(b);
			}
			int x = (int) Math.round(Math.sqrt(images.size()));
			int y = (int) Math.round(Math.ceil(Math.sqrt(images.size())));
			BufferedImage complete = new BufferedImage(dv.getWidth()*x, dv.getHeight()*y,BufferedImage.TYPE_INT_RGB);
			int cntX = 0;
			int cntY = 0;
			Graphics2D g = complete.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,dv.getWidth()*x, dv.getHeight()*y);
			for (BufferedImage b:images) {
				
				g.drawImage(b,dv.getWidth()*cntX,dv.getHeight()*cntY,null);
				g.setColor(Color.black);
				g.drawRect(dv.getWidth()*cntX,dv.getHeight()*cntY,dv.getWidth(),dv.getHeight());
				cntX++;
				if (cntX == x) {
					cntY++;
					cntX=0;
				}
			}
			System.out.println("Captured History in png");
			try{ImageIO.write(complete,"png",new File(fc.getSelectedFile()
					.getCanonicalPath()));}catch (Exception e) {}
		}
	}

	private void saveHierarchicalHistoryPNG() {
		if (dv == null)
			return;
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Vector<ScreenDrawingView> sdv = dv.getHistoryImages();
			Vector<BufferedImage> images = new Vector<BufferedImage>();
			for (ScreenDrawingView s: sdv){
				Vector<ScreenDrawingView> embedded = s.getEmbeddedImages();
				for (ScreenDrawingView s2: embedded) {
					BufferedImage b = new BufferedImage(dv.getWidth(),dv.getHeight(),BufferedImage.TYPE_INT_RGB); /* change sizes of course */
					Graphics2D g = b.createGraphics();
					s2.paint(g);	
					images.add(b);
				}
			}
			images.remove(0);
			int x = (int) Math.round(Math.sqrt(images.size()));
			int y = (int) Math.round(Math.ceil(Math.sqrt(images.size())));
			BufferedImage complete = new BufferedImage((dv.getWidth()+1)*x, (dv.getHeight()+1)*y,BufferedImage.TYPE_INT_RGB);
			int cntX = 0;
			int cntY = 0;
			Graphics2D g = complete.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,dv.getWidth()*x, dv.getHeight()*y);
			for (BufferedImage b:images) {
				
				g.drawImage(b,(dv.getWidth()+1)*cntX,(dv.getHeight()+1)*cntY,null);
				g.setColor(Color.black);
				g.drawRect(dv.getWidth()*cntX,dv.getHeight()*cntY,dv.getWidth(),dv.getHeight());
				cntX++;
				if (cntX == x) {
					cntY++;
					cntX=0;
				}
			}
			System.out.println("Captured History in png");
			try{ImageIO.write(complete,"png",new File(fc.getSelectedFile()
					.getCanonicalPath()));}catch (Exception e) {}
		}
	}
	
	@Override
	protected JMenu createAlignmentMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JMenu createArrowMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JMenu createAttributesMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JMenu createColorMenu(String title,
			FigureAttributeConstant attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JMenu createDebugMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DrawingView createDrawingView(Drawing newDrawing) {
		Dimension d = getDrawingViewSize();
		dv = new ScreenDrawingView(this, d.width, d.height);
		dv.setDrawing(newDrawing);
		// notify listeners about created view when the view is added to the
		// desktop
		// fireViewCreatedEvent(newDrawingView);
		return dv;
	}

	@Override
	protected JMenu createEditMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JMenu createFileMenu() {
		CommandMenu menu = new CommandMenu("File");
		Command cmd = new AbstractCommand("Print...", this, true) {
			@Override
			public void execute() {
				print();
			}
		};
		Command cmd2 = new AbstractCommand("Save as XTM...", this, true) {
			@Override
			public void execute() {
				save();
			}
		};
		Command cmd3 = new AbstractCommand("Save as PNG...", this, true) {
			@Override
			public void execute() {
				savePNG();
			}
		};
		Command cmd4 = new AbstractCommand("Save History as PNG...", this, true) {
			@Override
			public void execute() {
				saveHistoryPNG();
			}
		};
		Command cmd5 = new AbstractCommand("Save Hierarchy as PNG...", this, true) {
			@Override
			public void execute() {
				saveHierarchyPNG();
			}
		};

		Command cmd6 = new AbstractCommand("Save Hierarchical History as PNG...", this, true) {
			@Override
			public void execute() {
				saveHierarchicalHistoryPNG();
			}
		};
		
		Command cmd7 = new AbstractCommand("Reset Application", this, true) {
			@Override
			public void execute() {
				reset();
			}
		};

		menu.add(cmd, new MenuShortcut('p'));
		menu.add(cmd2, new MenuShortcut('s'));
		menu.add(cmd3, new MenuShortcut('p'));
		menu.add(cmd4, new MenuShortcut('h'));
		menu.add(cmd5, new MenuShortcut('i'));
		menu.add(cmd6, new MenuShortcut('y'));
		menu.add(cmd7, new MenuShortcut('r'));
		menu.addSeparator();

		cmd = new AbstractCommand("Exit", this, true) {
			@Override
			public void execute() {
				endApp();
			}
		};
		menu.add(cmd);
		return menu;
	}

	@Override
	protected JMenu createFontMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JMenu createFontSizeMenu() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected JMenu createFontStyleMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createTools(JToolBar palette) {
		super.createTools(palette);

		Tool tool = new LabelingTool(this, new BasicBuildingBlock());
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));
	}

	@Override
	protected void destroy() {
		// reg.deregister();
	}
	
}
