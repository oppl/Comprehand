package oppl.gwm;

import oppl.reactivisionbridge.ExplicitInterface;
import oppl.reactivisionbridge.TablelessUserInputCollector;
import oppl.reactivisionbridge.UserInputCollector;
import reactivision.tuio.TuioClient;

public class TablelessStartup {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TuioClient tuio_client = new TuioClient(3333);
		ScreenApplication mainViewer = new ScreenApplication();
		
		UserInputCollector l = new TablelessUserInputCollector((GWMControllerInterface) mainViewer.view(), new ExplicitInterface());
		tuio_client.addTuioListener(l);
		tuio_client.connect();
		
		mainViewer.setResetListener(l);
	
	}

}
