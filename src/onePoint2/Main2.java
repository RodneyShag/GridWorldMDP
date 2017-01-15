package onePoint2;

import onePoint1.DrawingBoard;
import onePoint1.GridWorld;

public class Main2 {
	/* Parameters to tune: 
	 * Discount factor (3rd parameter to constructor): use 0.7 to compare with instructor results. Use 0.99 for report
	 * Threshold       (4th parameter to constructor): Currently 500
	 */
	public static void main (String [] args){
		GridWorld worldFrom1Point1 = new GridWorld(true, 50, 0.99); // This uses GridWorld from 1.1 so we can use it for RMSE with 1.2
        worldFrom1Point1.establishValueIterationUtilities(); 
        
        GridWorld_Q world_Q = null;
        for (int numIterations = 1; numIterations <= 10000000; numIterations *= 2){
        	world_Q = new GridWorld_Q(true, numIterations, 0.99, 500, worldFrom1Point1.grid);
			world_Q.establish_Q_Utilities();
			System.out.println("numIterations = " + numIterations + "\tRMSE = " + world_Q.RMSE());
        }
        
        DrawingBoard d = new DrawingBoard(world_Q);
        
        d.draw(false);   // Use one of these (draws Utilities)
        //d.draw(true);    // Use one of these (draws Policy)
	}
}