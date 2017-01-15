package onePoint1;

import java.awt.Font;
import java.text.DecimalFormat;

public class DrawingBoard {
    private int numRows;
    private int numCols;

    private GridWorld gridWorld;

    private Font valueFont;

    /* Constructor */
    public DrawingBoard(GridWorld grid){
        gridWorld = grid;
        this.numRows = grid.rows;
        this.numCols = grid.columns;
        createCanvas();

        int style = Font.BOLD | Font.ITALIC;
        this.valueFont = new Font ("Garamond", style , 36);
    }

    private void createCanvas(){
        int canvasScale = 200;
        int maxCanvasSize = 900;
        while(this.numCols * canvasScale > maxCanvasSize || this.numRows * canvasScale > maxCanvasSize){
            canvasScale = canvasScale - 5;
            if(canvasScale <= 0){
                canvasScale = 1;
                break;
            }
        }
        StdDraw.setCanvasSize(this.numCols * canvasScale, this.numRows * canvasScale);
        StdDraw.setXscale(0, this.numCols);
        StdDraw.setYscale(0, this.numRows);
    }
    
    /**
     * Draws a grid with either 1) Utilities, or 2) Policy
     * @param drawPolicy	TRUE if you want arrows to be drawn. FALSE if u want utilities to be drawn.
     */
    public void draw(boolean drawPolicy){
        StdDraw.clear();
        this.drawBlankGrid();
        this.drawAllGridValues(drawPolicy);
        StdDraw.show(5);
    }
    
    private void drawBlankGrid(){
        for(int row = 0; row < this.numRows; row++){
            for(int col = 0; col < this.numCols; col++){
                this.drawGridSpace(row, col);
            }
        }
    }
    
    private void drawGridSpace(int row, int col){
        StdDraw.setPenRadius();

        GridSquare square = gridWorld.getGridSquare(col, row);
        if (square.isWall())
            StdDraw.setPenColor(StdDraw.GRAY);
        else if (square == gridWorld.start)
            StdDraw.setPenColor(StdDraw.RED);
        else if (square.getReward() == -0.04)
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        else if (square.getReward() < 0)
            StdDraw.setPenColor(StdDraw.ORANGE);
        else if (square.getReward() > 0)
            StdDraw.setPenColor(StdDraw.GREEN);

        StdDraw.filledSquare(col+0.5, (this.numRows-1-row)+0.5, .45 );
    }
    
    private void drawAllGridValues(boolean drawPolicy) {
        for(int row = 0; row < this.numRows; row++){
            for(int col = 0; col < this.numCols; col++){
                this.drawSingleGridValue(row, col, drawPolicy);
            }
        }
    }

    private void drawSingleGridValue(int row, int col, boolean drawPolicy) {
        StdDraw.setFont(this.valueFont);
        StdDraw.setPenColor(StdDraw.BLACK);
        double value = this.gridWorld.getGridSquare(col, row).utility;
        DecimalFormat df = new DecimalFormat("#.####");
        String val = df.format(value);

        GridSquare square = gridWorld.getGridSquare(col, row);
        if (square.isWall())
            StdDraw.text(col+0.5, (this.numRows-1-row)+0.5, "WALL");
        else{
        	if (drawPolicy){
	        	if (gridWorld.directionPolicy[row][col] == Direction.LEFT)
	        		StdDraw.picture(col+0.5, (this.numRows-1-row)+0.5, "../images/leftArrow.png");
	        	else if (gridWorld.directionPolicy[row][col] == Direction.RIGHT)
	        		StdDraw.picture(col+0.5, (this.numRows-1-row)+0.5, "../images/rightArrow.png");
	        	else if (gridWorld.directionPolicy[row][col] == Direction.DOWN)
	        		StdDraw.picture(col+0.5, (this.numRows-1-row)+0.5, "../images/downArrow.png");
	        	else if (gridWorld.directionPolicy[row][col] == Direction.UP)
	        		StdDraw.picture(col+0.5, (this.numRows-1-row)+0.5, "../images/upArrow.png");
	        	else
	        		StdDraw.text(col+0.5, (this.numRows-1-row)+0.5, val);
        	}
        	else
        		StdDraw.text(col+0.5, (this.numRows-1-row)+0.5, ""+ val);
        }
    }
}