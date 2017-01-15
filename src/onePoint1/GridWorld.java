package onePoint1;
import java.util.ArrayList;
import java.util.List;

/* Important: (0, 0) is the TOP-LEFT corner of grid */
public class GridWorld {
    public int numIterations;
    public double discountFactor;

    public int rows = 6;
    public int columns = 6;
    
    public GridSquare start;
    public GridSquare grid[][];

    // Nodes are considered actions. A node we will move to is indicative of the action we are trying to take
    public GridSquare policy[][];
    public Direction directionPolicy[][];

    /* Constructor: Constructs the GridWorld as defined in the MP4 Specification */
    public GridWorld(boolean rewardsTerminal, int iterations, double discountFactor) {
    	this.numIterations = iterations;
    	this.discountFactor = discountFactor;
    	
        grid = new GridSquare[rows][columns];
        policy = new GridSquare[rows][columns];
        directionPolicy = new Direction[rows][columns];
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                // Setting up the walls, rewards, and negative rewards.
                if (x == 1 && y == 0) {
                	grid[y][x] = new GridSquare(-1, rewardsTerminal, false, x, y); // rewardSquare
                } else if (x == 3 && ((y >= 1 && y <= 3) || y == 5)) {
                	grid[y][x] = new GridSquare(0, false, true, x, y); // walls
                } else if (x == 0 && y == 5) {
                	grid[y][x] = new GridSquare(1, rewardsTerminal, false, x, y); // rewardSquare
                } else if (x == 1 && y == 5) {
                	grid[y][x] = new GridSquare(-1, rewardsTerminal, false, x, y); // rewardSquare
                } else if (x == 4 && y == 1) {
                	grid[y][x] = new GridSquare(-1, rewardsTerminal, false, x, y); // rewardSquare
                } else if ((x == 4 || x == 5) && y == 5) {
                	grid[y][x] = new GridSquare(-1, rewardsTerminal, false, x, y); // rewardSquares
                } else if (x == 5 && y == 2) {
                	grid[y][x] = new GridSquare(3, rewardsTerminal, false, x, y); // rewardSquare
                } else {
                	grid[y][x] = new GridSquare(-0.04, false, false, x, y); // default to -.04 square
                }
            }
        }
        start = grid[3][1];
    }

    public Direction getDirection(int row, int column, GridSquare destination){
    	if (destination == null)
    		return null;
	  	if (column - 1 == destination.getxPos())
	  		return Direction.LEFT;
	  	else if (column + 1 == destination.getxPos())
	  		return Direction.RIGHT;
	  	else if (row + 1 == destination.getyPos())
	  		return Direction.DOWN;
	  	else if (row - 1 == destination.getyPos())
	  		return Direction.UP;
	  	return null; // should never execute
	}
    
    
    public GridSquare getGridSquare(int x, int y) {
    	if (!isValidLocation(x, y))
    		return null;
        return grid[y][x];
    }
    
    /* Trying to move into walls is actually a valid move. */
    public boolean isValidLocation(int x, int y) {
        return x >= 0 && y >= 0 && x < columns && y < rows;
    }
    
    public boolean isValidLocation(GridSquare square){
    	return (square != null) && (isValidLocation(square.getxPos(), square.getyPos()));
    }

    /* Returns a list of 4 adjacent GridSquares (including walls, and GridSquares off of grid) */
    public List<GridSquare> getValidAdjacentSquares(GridSquare square) {
        int x = square.getxPos();
        int y = square.getyPos();

        ArrayList<GridSquare> validActions = new ArrayList<GridSquare>();
        if (square.isTerminal()) {
            return validActions;
        }

        /* Get adjacent GridSquares */
        GridSquare leftSquare  = getGridSquare(x - 1, y);
        GridSquare rightSquare = getGridSquare(x + 1, y);
        GridSquare downSquare  = getGridSquare(x, y + 1);
        GridSquare upSquare    = getGridSquare(x, y - 1);

		if ( ! isValidLocation(leftSquare))
			leftSquare  = new GridSquare(0, true, true, x - 1, y); // off of Grid
		if ( ! isValidLocation(rightSquare))
			rightSquare = new GridSquare(0, true, true, x + 1, y); // off of Grid
		if ( ! isValidLocation(downSquare))
			downSquare  = new GridSquare(0, true, true, x, y + 1); // off of Grid
		if ( ! isValidLocation(upSquare))
			upSquare    = new GridSquare(0, true, true, x, y - 1); // off of Grid
		
		validActions.add(leftSquare);
		validActions.add(rightSquare);
		validActions.add(downSquare);		
		validActions.add(upSquare);

        return validActions;
    }

    // Action here is defined as one gridspace to another
    public double getUtilityForAction(GridSquare currentState, GridSquare successorState) {
        if (currentState == null || successorState == null)
            return 0;

        double utility;
        
        int x = currentState.getxPos();
        int y = currentState.getyPos();
        
        GridSquare leftSquare  = getGridSquare(x - 1, y);
        GridSquare rightSquare = getGridSquare(x + 1, y);
        GridSquare downSquare  = getGridSquare(x, y + 1);
        GridSquare upSquare    = getGridSquare(x, y - 1);
        
        if (isValidLocation(successorState) && ! successorState.isWall())
        	utility = 0.8 * successorState.utility;
        else
        	utility = 0.8 * currentState.utility; // agent stays in same place as before.

        if (successorState.getxPos() == currentState.getxPos()) { // intended movement is vertical
            if (isValidLocation(leftSquare) && ! leftSquare.isWall())
                utility += 0.1 * leftSquare.utility;
            else
            	utility += 0.1 * currentState.utility; // agent stays in same place as before.

            if (isValidLocation(rightSquare) && ! rightSquare.isWall())
                utility += 0.1 * rightSquare.utility;
            else
            	utility += 0.1 * currentState.utility; // agent stays in same place as before.
        }
        else { // intended movement is horizontal
            if (isValidLocation(upSquare) && ! upSquare.isWall())
                utility += 0.1 * upSquare.utility;
            else
            	utility += 0.1 * currentState.utility; // agent stays in same place as before.
            
            if (isValidLocation(downSquare) && ! downSquare.isWall())
                utility += 0.1 * downSquare.utility;
            else
            	utility += 0.1 * currentState.utility; // agent stays in same place as before.
        }
        
        return utility;
    }

    // Calculates argmax a \in A(s) Sum_{s'} P(s' | s, a) U_{current} (s')
    public GridSquare maxUtilityAction(GridSquare s) {
        List<GridSquare> validNextSquares = getValidAdjacentSquares(s);
        if (validNextSquares.size() != 0) {
            GridSquare maxNode = validNextSquares.get(0);
            double maxUtility = Double.NEGATIVE_INFINITY;

            // For all valid directions to move, get the node with the expected max utility
            for (GridSquare s_prime : validNextSquares) {
                double utility = getUtilityForAction(s, s_prime);

                if (utility > maxUtility) {
                    maxNode = s_prime;
                    maxUtility = utility;
                }
            }

            return maxNode;
        } 
        else
            return null;
    }

    public double maxUtility(GridSquare s) {
        GridSquare maxAction = maxUtilityAction(s);

        if (maxAction != null)
        	return getUtilityForAction(s, maxAction);
        else
            return 0;
    }

    /* We must not write the utilities to cells until all 36 are calculated */
    public void establishValueIterationUtilities() {
        for (int i = 1; i <= numIterations; i++) {
        	double utilities[][] = new double[rows][columns];
        	/* 1st calculate all the utilities before writing to the cells */
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < columns; x++) {
                    GridSquare currentSquare = getGridSquare(x, y);
                    if ( ! currentSquare.isWall())
                    	utilities[y][x] = currentSquare.getReward() + discountFactor * maxUtility(currentSquare); 
                }
            }
            /* Now we can copy the utilities */
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < columns; x++) {
                	grid[y][x].utility = utilities[y][x];
                }
            }
        }
        
        policyImprovement();
        generateDirectionPolicy();
    }
    
    public void policyEvaluation() {
    	double utilities[][] = new double[rows][columns];
    	/* 1st calculate all the utilities before writing to the cells */
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                GridSquare currentSquare = getGridSquare(x, y);
                if ( ! currentSquare.isWall())
                	utilities[y][x] = currentSquare.getReward() + discountFactor * getUtilityForAction(currentSquare, policy[y][x]);
            }
        }
        /* Now we can copy the utilities */
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
            	grid[y][x].utility = utilities[y][x];
            }
        }
    }

    public void policyImprovement() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                policy[y][x] = maxUtilityAction(getGridSquare(x, y));
            }
        }
    }

    public void establishPolicyIterationUtilites() {
        // Setup initial policy (always go to first in adjacent list)
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                GridSquare currentSquare = getGridSquare(x, y);
                List<GridSquare> validAdjacentSquares = getValidAdjacentSquares(currentSquare);

                if (validAdjacentSquares.size() == 0 )
                    policy[y][x] = null;
                else
                    policy[y][x] = validAdjacentSquares.get(0); // policy starts off with all left-pointing arrows
            }
        }

        for (int i = 0; i < numIterations; i++) {
            policyEvaluation();
            policyImprovement();
        }
        generateDirectionPolicy();
    }
    
    /* Used to draw arrows for policy */
    public void generateDirectionPolicy(){
    	for (int row = 0; row < rows; row++){
    		for (int col = 0; col < columns; col++){
    			directionPolicy[row][col] = getDirection(row, col, policy[row][col]);
    		}
    	}
    }
    
    /* Added for RMSE (for 1.1 and 1.2) */
    public double sumOfUtilities(){
    	double sum = 0;
    	for (int row = 0; row < rows; row++){
    		for (int col = 0; col < columns; col++){
    			sum += grid[row][col].utility;
    		}
    	}
    	return sum;
    }
    

}
