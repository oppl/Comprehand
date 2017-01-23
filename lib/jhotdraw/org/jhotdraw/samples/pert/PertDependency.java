/*
 * @(#)PertDependency.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.samples.pert;

import java.awt.Color;
import java.util.List;

import org.jhotdraw.figures.ArrowTip;
import org.jhotdraw.figures.LineConnection;
import org.jhotdraw.figures.PolyLineFigure;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.framework.HandleEnumeration;
import org.jhotdraw.standard.HandleEnumerator;
import org.jhotdraw.standard.NullHandle;

/**
 * @version <$CURRENT_VERSION$>
 */
public class PertDependency extends LineConnection {
	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -7959500008698525009L;
	private int pertDependencySerializedDataVersion = 1;

	public PertDependency() {
		setEndDecoration(new ArrowTip());
		setStartDecoration(null);
	}

	public void handleConnect(Figure start, Figure end) {
		PertFigure source = (PertFigure)start;
		PertFigure target = (PertFigure)end;
		if (source.hasCycle(target)) {
			setAttribute(FigureAttributeConstant.FRAME_COLOR, Color.red);
		}
		else {
			target.addPreTask(source);
			source.addPostTask(target);
			source.notifyPostTasks();
		}
	}

	public void handleDisconnect(Figure start, Figure end) {
		PertFigure source = (PertFigure)start;
		PertFigure target = (PertFigure)end;
		if (target != null) {
			target.removePreTask(source);
			target.updateDurations();
		}
		if (source != null) {
			source.removePostTask(target);
		}
   }

	public boolean canConnect(Figure start, Figure end) {
		return ((start instanceof PertFigure) && (end instanceof PertFigure));
	}

	public HandleEnumeration handles() {
		List handles = super.handles().toList();
		// don't allow to reconnect the starting figure
		handles.set(0, new NullHandle(this, PolyLineFigure.locator(0)));
		return new HandleEnumerator(handles);
	}
}
