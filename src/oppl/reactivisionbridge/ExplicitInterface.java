package oppl.reactivisionbridge;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import reactivision.tuio.TuioClient;
import reactivision.tuio.TuioListener;

public class ExplicitInterface extends JFrame implements TuioListener {

	
	private static String BMP_PATH = new String(
			"/Users/oppl/Documents/Dokumente/Ausbildung/Diss/System/temp");
	private static int LEFTMARKER = 86;
 	private static int RIGHTMARKER = 87;
		
	private Map<Integer, BufferedImage> captions;
	private boolean endMarkerAvailable = false;
	private Point endPoint = null;
	private UserInputCollector parent = null;
	private boolean picTaken = false;
	private boolean startMarkerAvailable = false;
	private Point startPoint = null;
	
	private boolean waiting = true;
	
	public ExplicitInterface() {
		captions = Collections.synchronizedMap(new HashMap<Integer, BufferedImage>());

		TuioClient tuio_client = new TuioClient(3334);

		tuio_client.addTuioListener(this);
		tuio_client.connect();

	}

	public void addTuioCur(long session_id) {
		// TODO Auto-generated method stub

	}
	
	public void addTuioObj(long session_id, int fiducial_id) {
		if (ReactivisionConstants.isArtifact(fiducial_id)) {
			parent.createArtifact(fiducial_id);
			parent.processArtifact(fiducial_id, -1, -1);
		} 
		if (ReactivisionConstants.isBasicBuildingBlock(fiducial_id)) {
			System.out.println("Specify semantics ...");
			//parent.askForSemantics(fiducial_id);
		} 

	}
	
	/* (non-Javadoc)
	 * @see oppl.reactivision.helpers.CaptionContainer#getCaption(int)
	 */
	public BufferedImage getCaption(int id) {
		return captions.get(new Integer(id));
	}

	  public String getTextCaption(int id) {
		return null;
	}

		  public void refresh() {
			// TODO Auto-generated method stub

		}

		  public void removeTuioCur(long session_id) {
			// TODO Auto-generated method stub
		}
	
	public void removeTuioObj(long session_id, int fiducial_id) {
		if (fiducial_id == LEFTMARKER) startMarkerAvailable = false;
		if (fiducial_id == RIGHTMARKER) endMarkerAvailable = false;		
		if (!startMarkerAvailable && !endMarkerAvailable && picTaken) picTaken = false;	
		if (ReactivisionConstants.isArtifact(fiducial_id)) {
			parent.closeArtifact(fiducial_id);
		} 

	}

	  public void reset() {
		captions = Collections.synchronizedMap(new HashMap<Integer, BufferedImage>());		
		waiting = true;
		startMarkerAvailable = false;
		endMarkerAvailable = false;
		picTaken = false;
		startPoint = null;
		endPoint = null;
	}
	
	public void setParent(UserInputCollector parent) {
		this.parent = parent;
	}

	public void updateTuioCur(long session_id, float xpos, float ypos,
			float x_speed, float y_speed, float m_accel) {
		// TODO Auto-generated method stub

	}

	public void updateTuioObj(long session_id, int fiducial_id, float xpos,
			float ypos, float angle, float x_speed, float y_speed,
			float r_speed, float m_accel, float r_accel) {
		int x, y;
		BufferedImage img = null;
		x = 640 - Math.round(xpos * 640);
		y = Math.round(ypos * 480);
		if (fiducial_id == RIGHTMARKER) {
			endMarkerAvailable = true;
			endPoint = new Point(x,y);
		}
		if (fiducial_id == LEFTMARKER) {
			startMarkerAvailable = true;		
			startPoint = new Point(x,y);
		}
		if (startMarkerAvailable && endMarkerAvailable && !picTaken) {
			img = takePicture();
			if (img == null) return;
			img = process(img);
			int latest = -1;
			if (parent != null) latest = parent.getLatestBlock();
			if (latest != -1) {
				captions.put(new Integer(latest), img);
			}
			System.out.println("Picture taken! "+startPoint.x+","+startPoint.y+" - "+endPoint.x+","+endPoint.y);
			picTaken = true;
			parent.updateCaption(latest);
		}
		
	}

	private BufferedImage cropImage(BufferedImage src) {
	    BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
	    AffineTransform transform = new AffineTransform();
	    int dx = -startPoint.x - ((int) Math.round(Math.sqrt(Math.pow(endPoint.y-startPoint.y,2)+Math.pow(endPoint.x-startPoint.x,2))))/4;
	    int dy = -startPoint.y + ((int) Math.round(Math.sqrt(Math.pow(endPoint.y-startPoint.y,2)+Math.pow(endPoint.x-startPoint.x,2))))/6;
	    transform.translate(dx,dy);
	    AffineTransformOp op = new AffineTransformOp(transform,AffineTransformOp.TYPE_BILINEAR);
	    op.filter(src,dest);
	    return dest;
	  }

	private BufferedImage increaseContrast(BufferedImage src) {
        int min = 255; 
        int max = 0;
        int area = 30;
		for (int x=src.getWidth()/2-area; x<src.getWidth()/2+area; x++) {
            for (int y=src.getHeight()/2-area; y<src.getHeight()/2+area; y++) {
              int val = src.getRaster().getSample(x,y,2);
              min = Math.min(min, val);
              max = Math.max(max, val);
            }
		}
		System.out.println(min+" "+max);
    	for (int x=0; x<src.getWidth(); x++) {
    		for (int y=0; y<src.getHeight(); y++) {
              int val = src.getRaster().getSample(x,y,2);
              val = val - min + Math.round(255.0f*(val-min)/(max-min));
              if (val<0) val = 0;
              if (val>255) val = 255;
        	  src.getRaster().setSample(x,y,0,val);
        	  src.getRaster().setSample(x,y,1,val);
        	  src.getRaster().setSample(x,y,2,val);
              
    		}
    	}
 	    return src;
	}

	private BufferedImage process(BufferedImage img) {
		img = rotateImage(img);
		img = cropImage(img);
		img = scaleImage(img);
		img = increaseContrast(img);
		return img;
	}

	private BufferedImage rotateImage(BufferedImage src) {
		    BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
		    AffineTransform transform = new AffineTransform();
		    transform.rotate(Math.asin(-1.0*(endPoint.y-startPoint.y)/(endPoint.x-startPoint.x)),startPoint.x,startPoint.y);
		    AffineTransformOp op = new AffineTransformOp(transform,AffineTransformOp.TYPE_BILINEAR);
		    op.filter(src,dest);
		    return dest;
		  }
	
	private BufferedImage scaleImage(BufferedImage src) {
	    BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
	    AffineTransform transform = new AffineTransform();
	    double scaleFactor = 2.0*src.getWidth()/Math.sqrt(Math.pow(endPoint.y-startPoint.y,2)+Math.pow(endPoint.x-startPoint.x,2));
	    System.out.println("scale-factor: "+scaleFactor);
	    transform.scale(scaleFactor,scaleFactor);
	    AffineTransformOp op = new AffineTransformOp(transform,AffineTransformOp.TYPE_BILINEAR);
	    op.filter(src,dest);
	    return dest;
	  }

	private BufferedImage subtract(BufferedImage src1, BufferedImage src2) {
		    BufferedImage dest;
		    int val;
		    dest = new BufferedImage(src1.getWidth(), src1.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		    for (int i=0; i<src1.getHeight(); i++) {
		      for (int a=0; a<src1.getWidth(); a++) {
		        val = src1.getRaster().getSample(a,i,0) - (src2.getRaster().getSample(a,i,0)*src2.getRaster().getSample(a,i,0)/255);
		        if (val<40) val = 0;
		        else val = 255;
		        dest.getRaster().setSample(a,i,0,val);
		      }
		    }
		    return dest;
		  }

	public BufferedImage takePicture() {
		String filename = new String();
		String[] files = new String[0];
		BufferedImage img = null;
		try {
			Runtime.getRuntime().exec("osascript "+ReactivisionConstants.BASEPATH+"takePicture.scpt");
		} catch (Exception e) {
			return null;
		}
		waiting = true;
		int watchdog = 0;
		while (waiting) {
			files = (new File(BMP_PATH)).list(new BMPFileFilter());
			if (files.length != 0) {
				waiting = false;
			}
			else {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					// TODO: handle exception
				}
				System.out.print(".");
				watchdog++;
				if (watchdog==20) return null;
			}
		}
		try {
			Thread.sleep(200); // wait until BMP is written safely
		} catch (Exception e) {
			// TODO: handle exception
		}
		Toolkit.getDefaultToolkit().beep();
		try {
			img = ImageIO.read(new File(BMP_PATH, files[0]));
			for (int i = 0; i < files.length; i++)
				(new File(BMP_PATH, files[i])).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}

	private class BMPFileFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.endsWith(".bmp");
		}

	}

}
