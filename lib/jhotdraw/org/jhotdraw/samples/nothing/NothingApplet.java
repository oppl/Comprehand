/*
 * @(#)NothingApplet.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.samples.nothing;

import javax.swing.JPanel;

import org.jhotdraw.applet.DrawApplet;
import org.jhotdraw.contrib.PolygonTool;
import org.jhotdraw.figures.ElbowConnection;
import org.jhotdraw.figures.EllipseFigure;
import org.jhotdraw.figures.LineConnection;
import org.jhotdraw.figures.LineFigure;
import org.jhotdraw.figures.RectangleFigure;
import org.jhotdraw.figures.RoundRectangleFigure;
import org.jhotdraw.figures.TextFigure;
import org.jhotdraw.figures.TextTool;
import org.jhotdraw.framework.Tool;
import org.jhotdraw.standard.ConnectionTool;
import org.jhotdraw.standard.CreationTool;

/**
 * @version <$CURRENT_VERSION$>
 */
public class NothingApplet extends DrawApplet {

	//-- DrawApplet overrides -----------------------------------------

	protected void createTools(JPanel palette) {
		super.createTools(palette);

		Tool tool = new TextTool(this, new TextFigure());
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));

		tool = new CreationTool(this, new RectangleFigure());
		palette.add(createToolButton(IMAGES + "RECT", "Rectangle Tool", tool));

		tool = new CreationTool(this, new RoundRectangleFigure());
		palette.add(createToolButton(IMAGES + "RRECT", "Round Rectangle Tool", tool));

		tool = new CreationTool(this, new EllipseFigure());
		palette.add(createToolButton(IMAGES + "ELLIPSE", "Ellipse Tool", tool));

		tool = new CreationTool(this, new LineFigure());
		palette.add(createToolButton(IMAGES + "LINE", "Line Tool", tool));

		tool = new PolygonTool(this);
		palette.add(createToolButton(IMAGES + "POLYGON", "Polygon Tool", tool));

		tool = new ConnectionTool(this, new LineConnection());
		palette.add(createToolButton(IMAGES + "CONN", "Connection Tool", tool));

		tool = new ConnectionTool(this, new ElbowConnection());
		palette.add(createToolButton(IMAGES + "OCONN", "Elbow Connection Tool", tool));
	}
}
