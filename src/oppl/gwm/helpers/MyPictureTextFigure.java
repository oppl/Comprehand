package oppl.gwm.helpers;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.jhotdraw.framework.Figure;

public class MyPictureTextFigure extends MyTextFigure {
	
	private boolean displayImage = false;
	private BufferedImage image = null;

	public MyPictureTextFigure(Figure container) {
		super(container);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Rectangle displayBox() {
		if (image == null || !displayImage) return super.displayBox();
		else {
			Rectangle box = super.displayBox();
			float scaleFactor = getScaleFactor();
			return (new Rectangle(box.x-Math.round(image.getWidth()*scaleFactor/2), box.y,Math.round(image.getWidth()*scaleFactor),Math.round(image.getHeight()*scaleFactor)));
		}
	}
	
	public void displayImage() {
		displayImage = true;
	}

	public void displayText() {
		displayImage = false;
	}
	
	@Override
	public void draw(Graphics g) {
		if (image != null && displayImage) {
			Rectangle box = this.displayBox();
			float scaleFactor = getScaleFactor();
			Image scaledImage = image.getScaledInstance(Math.round(image.getWidth()*scaleFactor),Math.round(image.getHeight()*scaleFactor), Image.SCALE_DEFAULT);
			g.drawImage(scaledImage, box.x, box.y, null);
		}
		else super.draw(g);
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public boolean isImage() {
		return displayImage;
	}
	
	public void setImage(BufferedImage image) {
		displayImage = true;
		this.image = image;
	}
	
	@Override
	public void setText(String newText) {
		displayImage = false;
		super.setText(newText);
	}	

	private float getScaleFactor() {
//		Rectangle box = super.displayBox();
		return 100.0f / image.getWidth();
	}
}
