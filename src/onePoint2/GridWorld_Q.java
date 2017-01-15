package onePoint2;

import onePoint1.Direction;
import onePoint1.GridSquare;
import onePoint1.GridWorld;

public class GridWorld_Q extends GridWorld{
	
	int threshold;
	GridSquare[][] solutionGrid;
	
	public GridWorld_Q(boolean rewardsTerminal, int iterations, double discountFactor, int threshold, GridSquare[][] solutionGrid) {
		super(rewardsTerminal, iterations, discountFactor);
		this.threshold = threshold;
		this.solutionGrid = solutionGrid;
	}
	
	public boolean notConverged(){
		System.out.println(getGridSquare(0,0).utility);
		return Math.abs(getGridSquare(0,0).utility) - 0.0893 < 0.1; // can make better criteria for convergence
	}
	
	/* Lecture 17 Slide 20 has pseudocode. Piazza @528 has more detailed pseudocode */
    public void establish_Q_Utilities() {
    	//while(notConverged()){ // can try this instead of the for loop line below
    	for (int i = 1; i <= numIterations; i++){
	    	GridSquare currentState = start;
	    	GridSquare intendedSuccessorState = null;
	    	GridSquare actualSuccessorState = null;
	    	while (true){
	    		if(currentState.isTerminal()){
	    			currentState.utility = currentState.getReward();
	    			break;
	    		}
		        intendedSuccessorState = selectAction(currentState);//this is an action
		        Direction intendedDirection = getDirection(currentState, intendedSuccessorState);
		        actualSuccessorState = getSuccessorState(currentState, intendedSuccessorState, intendedDirection);
		       	TD_Update(currentState, actualSuccessorState, intendedDirection);
		       	updateOtherVariables(currentState, intendedDirection);
		    	currentState = actualSuccessorState;
	    	}
    	}
    	
        policyImprovement();
        generateDirectionPolicy();
    }
    
    //select an action that yields the maximum return value of the exploration 
    //function operating on the succesor state
    public GridSquare selectAction(GridSquare currentState){
        int x = currentState.getxPos();
        int y = currentState.getyPos();
        
        /* Get adjacent GridSquares */
		GridSquare leftSquare  = getGridSquare(x - 1, y);
		GridSquare rightSquare = getGridSquare(x + 1, y);
		GridSquare downSquare  = getGridSquare(x, y + 1);
		GridSquare upSquare  = getGridSquare(x, y - 1);
		
		if ( ! isValidLocation(leftSquare))
			leftSquare = new GridSquare(0, true, true, x - 1, y); // off of Grid
		if ( ! isValidLocation(rightSquare))
			rightSquare = new GridSquare(0, true, true, x + 1, y); // off of Grid
		if ( ! isValidLocation(downSquare))
			downSquare = new GridSquare(0, true, true, x, y + 1); // off of Grid
		if ( ! isValidLocation(upSquare))
			upSquare = new GridSquare(0, true, true, x, y - 1); // off of Grid
		
		/* Explore: will always try each direction at least "threshold" number of times. No Need for R+ */
		if (needToExplore(currentState)){
			Direction leastTriedDir = currentState.leastTriedDirection();
	    	switch(leastTriedDir){
		    	case LEFT:
		    		return leftSquare;
		    	case RIGHT:
		    		return rightSquare;
		    	case DOWN:
		    		return downSquare;
		    	case UP:
		    		return upSquare;
		    	default: 
		    		return null;//should never execute
	    	}
		}
		
		/* Exploit */
		Direction dir = currentState.highestUtilityDirection();
    	switch(dir){
	    	case LEFT:
	    		return leftSquare;
	    	case RIGHT:
	    		return rightSquare;
	    	case DOWN:
	    		return downSquare;
	    	case UP:
	    		return upSquare;
	    	default: 
	    		return null;//should never execute
    	}
    }
    
    /* 80% chance of going in intended direction */
    public GridSquare getSuccessorState(GridSquare currentState, GridSquare intendedSuccessorState, Direction intendedDirection) {    	
        int x = currentState.getxPos();
        int y = currentState.getyPos();
        
        GridSquare ninetyDegreesLeft  = null;
        GridSquare ninetyDegreesRight = null;
    	
        switch(intendedDirection){
        	case LEFT:
        		ninetyDegreesLeft  = getGridSquare(x, y + 1);
                ninetyDegreesRight = getGridSquare(x, y - 1); 
        		break;
        	case RIGHT:
        		ninetyDegreesLeft  = getGridSquare(x, y - 1);
                ninetyDegreesRight = getGridSquare(x, y + 1);
        		break;
        	case DOWN:
        		ninetyDegreesLeft  = getGridSquare(x + 1, y);
                ninetyDegreesRight = getGridSquare(x - 1, y);
        		break;
        	case UP:
        		ninetyDegreesLeft  = getGridSquare(x - 1, y);
                ninetyDegreesRight = getGridSquare(x + 1, y);
        		break;
        	default: 
        		break;
        }
        
        double diceRoll = Math.random() * 10;
        //80% chance to take the intended action
        if (diceRoll < 8){
        	if (isValidLocation(intendedSuccessorState) && ! intendedSuccessorState.isWall())
        		return intendedSuccessorState;
        	else
        		return currentState;
        }
        //10% chance to take the action 90degrees to the left
        else if (diceRoll < 9){
        	if (isValidLocation(ninetyDegreesLeft) && ! ninetyDegreesLeft.isWall())
        		return ninetyDegreesLeft;
        	else
        		return currentState;
        }
        //10% chance to take the action 90degrees to the right
        else{
        	if (isValidLocation(ninetyDegreesRight) && ! ninetyDegreesRight.isWall())
        		return ninetyDegreesRight;
        	else
        		return currentState;
        }
    }
    
    /* Formula from bottom of Lecture 17, Slide 20 */
    private void TD_Update(GridSquare currentState, GridSquare successorState, Direction dir){ //also updates N(s, a')
    	if (dir == Direction.LEFT)
    		currentState.qValueLeft += getAlphaTiedToQ(currentState, dir) * (currentState.getReward() + (discountFactor * successorState.utility) - currentState.qValueLeft);
    	else if (dir == Direction.RIGHT)
    		currentState.qValueRight += getAlphaTiedToQ(currentState, dir) * (currentState.getReward() + (discountFactor * successorState.utility) - currentState.qValueRight);
    	else if (dir == Direction.DOWN)
    		currentState.qValueDown += getAlphaTiedToQ(currentState, dir) * (currentState.getReward() + (discountFactor * successorState.utility) - currentState.qValueDown);
    	else if (dir == Direction.UP)
    		currentState.qValueUp += getAlphaTiedToQ(currentState, dir) * (currentState.getReward() + (discountFactor * successorState.utility) - currentState.qValueUp);
    }
    
    private void updateOtherVariables(GridSquare currentState, Direction dir){
    	/* Update N(s, a') */
    	if (dir == Direction.LEFT)
    		currentState.actionCounterLeft++;
    	else if (dir == Direction.RIGHT)
    		currentState.actionCounterRight++;
    	else if (dir == Direction.DOWN)
    		currentState.actionCounterDown++;
    	else if (dir == Direction.UP)
    		currentState.actionCounterUp++;
    	
       	currentState.updateUtility();
    }
    
    /* Lecture Slides are incorrect. "t" should be tied to Q (confirmed by TA) */
    public double getAlphaTiedToQ(GridSquare currentState, Direction dir){
    	int t = 0;
    	if (dir == Direction.LEFT)
    		t = currentState.actionCounterLeft;
    	else if (dir == Direction.RIGHT)
    		t = currentState.actionCounterRight;
    	else if (dir == Direction.DOWN)
    		t = currentState.actionCounterDown;
    	else if (dir == Direction.UP)
    		t = currentState.actionCounterUp;
    	//return (double) 60 / (59 + t);
    	//return (double) 600 / (599 + t);
    	return (double) 6000 / (5999 + t);
    }
    
    /* Returns number of times we've chosen a given direction from the current state
     * (Function defined in Lecture 17 Slide 15) */
    public int N(GridSquare currentState, Direction dir){
    	if (dir == Direction.LEFT)
    		return currentState.actionCounterLeft;
    	else if (dir == Direction.RIGHT)
    		return currentState.actionCounterRight;
    	else if (dir == Direction.DOWN)
    		return currentState.actionCounterDown;
    	else if (dir == Direction.UP)
    		return currentState.actionCounterUp;
    	return -1; // should never execute
 	}
	
    public Direction getDirection(GridSquare origin, GridSquare destination){
	  	if (origin.getxPos() - 1 == destination.getxPos())
	  		return Direction.LEFT;
	  	else if (origin.getxPos() + 1 == destination.getxPos())
	  		return Direction.RIGHT;
	  	else if (origin.getyPos() + 1 == destination.getyPos())
	  		return Direction.DOWN;
	  	else if (origin.getyPos() - 1 == destination.getyPos())
	  		return Direction.UP;
	  	return null; // should never execute
	}
	
	public boolean needToExplore(GridSquare currentState){
		return (N(currentState, Direction.LEFT) < threshold || N(currentState, Direction.RIGHT) < threshold
		     || N(currentState, Direction.DOWN) < threshold || N(currentState, Direction.UP) < threshold);
	}
	
	public double RMSE(){
		double sum = 0;
		for (int row = 0; row < rows; row++){
			for (int col = 0; col < columns; col++){
				sum += Math.pow(grid[row][col].utility - solutionGrid[row][col].utility, 2);
			}
		}
		return Math.sqrt((double) (sum) / numIterations);
	}
}
