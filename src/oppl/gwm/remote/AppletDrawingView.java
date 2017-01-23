package oppl.gwm.remote;

import oppl.gwm.ScreenDrawingView;

import org.jhotdraw.framework.DrawingEditor;

public class AppletDrawingView extends ScreenDrawingView implements
		RemoteGWMControllerInterface {

	public AppletDrawingView(DrawingEditor editor) {
		super(editor);
		init();
	}

	public AppletDrawingView(DrawingEditor editor, int width, int height) {
		super(editor, width, height);
		init();
	}

}
