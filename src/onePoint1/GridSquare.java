package onePoint1;

public class GridSquare {

    private double reward;
    private boolean terminal;
    private boolean wall;
    private int xPos;
    private int yPos;
    public double utility;

    /* Added for part 1.2 */
    //q values for the 4 possible actions
	public double qValueLeft;
	public double qValueRight;
	public double qValueUp;
	public double qValueDown;
	
	//counters for the number of times a given action has been taken
	public int actionCounterLeft;
	public int actionCounterRight;
	public int actionCounterUp;
	public int actionCounterDown;
	
    public GridSquare(double reward, boolean terminal, boolean wall, int x, int y) {
        this.terminal = terminal;
        this.reward = reward;
        this.xPos = x;
        this.yPos = y;
        this.wall = wall;
        this.utility = 0;
        
        /* For part 1.2 */
    	this.qValueLeft = 0;
    	this.qValueRight = 0;
    	this.qValueUp = 0;
    	this.qValueDown = 0;
    	
    	this.actionCounterLeft = 0;
    	this.actionCounterRight = 0;
    	this.actionCounterUp = 0;
    	this.actionCounterDown = 0;
    }
    
    public boolean isTerminal() {
        return terminal;
    }

    public double getReward() {
        return reward;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public boolean isWall() {
        return wall;
    }
    
    /**********************/
    /* Added for part 1.2 */	
    /**********************/
    
	public void updateUtility(){
		utility = Math.max(qValueLeft, Math.max(qValueRight, Math.max(qValueUp, qValueDown)));
	}
	
	public Direction highestUtilityDirection(){
		if (qValueLeft > Math.max(qValueRight, Math.max(qValueUp, qValueDown)))
			return Direction.LEFT;
		else if (qValueRight > Math.max(qValueUp, qValueDown))
			return Direction.RIGHT;
		else if (qValueUp > qValueDown)
			return Direction.UP;
		else
			return Direction.DOWN;
	}
	
	public Direction leastTriedDirection(){
		if (actionCounterLeft < Math.min(actionCounterRight, Math.min(actionCounterUp, actionCounterDown)))
			return Direction.LEFT;
		else if (actionCounterRight < Math.min(actionCounterUp, actionCounterDown))
			return Direction.RIGHT;
		else if (actionCounterUp < actionCounterDown)
			return Direction.UP;
		else
			return Direction.DOWN;
	}
}
