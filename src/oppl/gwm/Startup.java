package oppl.gwm;

import oppl.reactivisionbridge.ExplicitInterface;
import oppl.reactivisionbridge.UserInputCollector;
import reactivision.tuio.TuioClient;

public class Startup {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TuioClient tuio_client = new TuioClient(3333);
		ScreenApplication mainViewer = new ScreenApplication();
		ProjectorApplication proj = new ProjectorApplication();
		
		UserInputCollector l = new UserInputCollector((GWMControllerInterface) mainViewer.view(), new ExplicitInterface());
		tuio_client.addTuioListener(l);
		tuio_client.connect();
		
		mainViewer.setResetListener(l);
		l.addViewer((GWMControllerInterface) proj.view());
	
	}

}
