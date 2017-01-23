package oppl.gwm;

import oppl.reactivisionbridge.ExplicitInterface;
import oppl.reactivisionbridge.UserInputCollector;
import reactivision.tuio.TuioClient;

public class StartupSimulator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ScreenApplication mainViewer = new ScreenApplication();
		
		UserInputCollector l = new UserInputCollector((GWMControllerInterface) mainViewer.view(), new ExplicitInterface());
		
		mainViewer.setResetListener(l);
		
		Simulator sim = new Simulator(l);
		
		sim.run();

	}

}
