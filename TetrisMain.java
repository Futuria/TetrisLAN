import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.util.ArrayList;

/**
 * The main Tetris / Pentrix backend mechanics, Clients and Server classes extend this
 * 
 * @author Nathan Lo, Joey Ma
 * @version June 15, 2017
 */
public class TetrisMain extends World {
    protected int[][] grid; // The 2d grid of block types, central data structure
    protected int playerType; // Server or Client?
    protected int gameMode; // Tetris or Pentrix?
    protected int rows; // Number of rows cleared
    protected int bonus; // Number of bonus points (for more than 1 line at a time)
    protected Piece currentPiece, holdPiece; // Current and held pieces
    protected Piece[] nextPieces; // Pieces in queue
    protected boolean running; // Is the game still running or did the player lose?
    protected int x, y; // The x and y position of the game on screen
    protected int fallTimer, lockTimer; // Timer to fall and lock piece into grid
    protected static int MAX_FALL_TIMER = 15, MAX_LOCK_TIMER = 20; // Maximums for said timers
    protected boolean grounded = false, held = false, moved = false; // Is the piece touching something, already held, and moved since last frame?

    public static GreenfootImage[] images; // Images of coloured blocks
    protected GreenfootImage image; // The actual actor's image
    private GreenfootImage bg = new GreenfootImage("RipOff.png"); // Background image

     static { // Initialize the images
        images = new GreenfootImage[22];
        GreenfootImage tmp;
        for (int i = 0; i < images.length; i++) {
            try {
                tmp = new GreenfootImage("res/" + i + ".png"); // If this doesn't work, default to grayscale image
            } catch (Exception e) {
                tmp = new GreenfootImage(20, 20);
                tmp.setColor(new Color(i * 9, i * 9, i * 9));
                tmp.fill();
            }
            images[i] = tmp;
        }
    }

    public static final int PLAYER_CLIENT = 0, PLAYER_SERVER = 1; // Constants for passing into constructor
    public static final int PLAYER_TETRIS = 2, PLAYER_PENTA = 3;

    // Takes the current piece and locks it into grid
    private boolean lockCurrentPiece() {
        for (int i = 0; i < currentPiece.getNumBlocks(); i++) {
            int tx = currentPiece.getX() + currentPiece.getBlockX(i);
            int ty = currentPiece.getY() + currentPiece.getBlockY(i);
            // If the block is out of bounds or occupied, player lost
            if (grid[ty][tx] != -1 || ty >= 19) {
                running = false;
                break;
            }
        }
        if (!running) return false; // If the player has lost, don't update grid
        // Update grid
        for (int i = 0; i < currentPiece.getNumBlocks(); i++) {
            int tx = currentPiece.getX() + currentPiece.getBlockX(i);
            int ty = currentPiece.getY() + currentPiece.getBlockY(i);
            grid[ty][tx] = currentPiece.getColour();
        }
        return true;
    }

    // Clear lines
    private void clearLines() {
        int num = 0;
        // Clear all full lines, count number of rows cleared
        for(int i = 0; i < grid.length; i++) {
            while(fullLine(i)){
                shift(i);
                rows++;
                num++;
            }
        }
        // Bonuses are given for anything above a single line
        if(num != 1) bonus += num;
        draw();
    }
    
    // Is the row full? (Classic algorithm, no explanation needed)
    private boolean fullLine(int row){
        for (int i = 0; i < grid[row].length; i++) {
            if (grid[row][i] == -1) { 
                return false;
            }
        }
        return true;
    }
    
    // Shifts all rows down
    private void shift(int row){
        while (row < grid.length - 1) {
            for (int i = 0; i < grid[row].length; i++) {
                grid[row][i] = grid[row + 1][i];
            }
            row++;
        }
        for (int i = 0; i < grid[row].length; i++) {
            grid[row][i] = -1;
        }
    }

    private void getNextPiece() {
        held = false;
        currentPiece = nextPieces[0];
        // Shift the next pieces down
        for (int i = 0; i < 4; i++) {
            nextPieces[i] = nextPieces[i + 1];
        }
        // Get a new piece
        if (gameMode == PLAYER_TETRIS) {
            nextPieces[4] = new Piece(TRandom.getTetris());
        } else {
            nextPieces[4] = new Piece(TRandom.getPenta());
        }
    }

    private void hold() {
        if (held) return; // Can't hold twice within the same block
        if (holdPiece == null) { // No held piece, just place it in
            holdPiece = currentPiece;
            getNextPiece();
        } else { // Held piece, swap current with held
            Piece temp = currentPiece;
            currentPiece = holdPiece;
            holdPiece = temp;
            currentPiece.setX(5);
            currentPiece.setY(20);
        }
        // Make sure the held piece is at y = 0 (for reasons)
        holdPiece.setX(0);
        holdPiece.setY(0);
        held = true;
    }

    private void handleInput() {
        String key = Greenfoot.getKey();
        if (key == null) return; // No keys pressed
        if (key.equals("space")) { // Space = instant drop
            while(isOk(currentPiece.down())) currentPiece = currentPiece.down();
            if (!lockCurrentPiece()) return; // lockCurrentPiece returns false if the game is over, so return if the game is over
            moved = true;
            clearLines();
            getNextPiece();
            grounded = false;
            fallTimer = MAX_FALL_TIMER-rows/5;
        } else if (key.equals("left")) { // Move left if possible
            if (isOk(currentPiece.left())) {
                moved = true;
                currentPiece = currentPiece.left();
                lockTimer = (MAX_LOCK_TIMER+lockTimer)/2;
            }
        } else if (key.equals("right")) { // Move right if possible
            if (isOk(currentPiece.right())) {
                moved = true;
                currentPiece = currentPiece.right();
                lockTimer = (MAX_LOCK_TIMER+lockTimer)/2;
            }
        } else if (key.equals("up")) { // Rotate CW if possible
            if (isOk(currentPiece.rotateRight())) {
                moved = true;
                currentPiece = currentPiece.rotateRight();
            }
        } else if (key.equals("down")) { // Move down if possible
            if (isOk(currentPiece.down())) {
                moved = true;
                currentPiece = currentPiece.down();
            }
        } else if (key.equals("c") || key.equals("shift")) { // Hold
            moved = true;
            hold();
        }
    }

    public void act() {
        if (!running) endGame(); // The game is over
        moved = false;
        handleInput();
        draw();
        if (grounded) { // The piece is touching something from below
            if(isOk(currentPiece.down())){ // Move down if possible, updated grounded
                currentPiece = currentPiece.down();
                moved = true;
                grounded = false;
            }
            // Lock after a certain amount of time
            lockTimer--;
            if (lockTimer <= 0) {
                if (!lockCurrentPiece()) return;
                clearLines();
                getNextPiece();
                grounded = false;
                moved = true;
                fallTimer = MAX_FALL_TIMER - rows / 10;
            }
        } else {
            // If not grounded, just fall every so often (delay gets shorter the more rows you clear)
            fallTimer--;
            if (fallTimer <= 0) {
                if (isOk(currentPiece.down())) {
                    moved = true;
                    currentPiece = currentPiece.down();
                } else {
                    grounded = true;
                    lockTimer = MAX_LOCK_TIMER;
                }
                fallTimer = MAX_FALL_TIMER - rows / 10;
            }
        }
        draw();
    }

    /**
     * Constructor for objects of class TetrisMain.
     * @param playerType    Either TetrisMain.PLAYER_SERVER or TetrisMain.PLAYER_CLIENT
     * @param gameMode      Either TetrisMain.PLAYER_TETRIS or TetrisMain.PLAYER_PENTA
     * @param x             The x-coordinate on the actual screen
     * @param y             The y-coordinate on the actual screen
     */
    public TetrisMain(int playerType, int gameMode, int x, int y) {
        // Create a variable sized world
        super(400+3*(x-100), 550, 1);
        // Initialize all the variables!
        
        this.playerType = playerType;
        assert(playerType == PLAYER_CLIENT || playerType == PLAYER_SERVER); // Sanity checks
        this.gameMode = gameMode;
        assert(gameMode == PLAYER_TETRIS || gameMode == PLAYER_PENTA); // Sanity checks
        this.x = 75;
        this.y = 450;
        this.running = true;
        // Initialize and clear grid
        this.grid = new int[23][10];
        for (int i = 0; i < 23; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = -1;
            }
        }
        
        // Get current piece and next pieces, hold piece is always empty to start
        if (gameMode == PLAYER_TETRIS) currentPiece = new Piece(TRandom.getTetris());
        else currentPiece = new Piece(TRandom.getPenta());
        nextPieces = new Piece[5];
        for (int i = 0; i < 5; i++) {
            if (gameMode == PLAYER_TETRIS) nextPieces[i] = new Piece(TRandom.getTetris());
            else nextPieces[i] = new Piece(TRandom.getPenta());
        }
        holdPiece = null;
        
        // Initializes timers and score
        this.grounded = false;
        this.moved = false;
        this.lockTimer = MAX_LOCK_TIMER;
        this.fallTimer = MAX_FALL_TIMER;
        this.rows = 0;
        this.bonus = 0;
    }

    // This is the protected constructor Joey copied for the singleplayer mode 
    protected TetrisMain(int playerType, int gameMode, boolean a) {
        // Create a new world with 800x600 cells with a cell size of 1x1 pixels.
        super(800,600,1);
        bonus = 0;
        this.playerType = playerType;
        assert(playerType == PLAYER_CLIENT || playerType == PLAYER_SERVER);
        this.gameMode = gameMode;
        assert(gameMode == PLAYER_TETRIS || gameMode == PLAYER_PENTA);
        this.x = 75;
        this.y = 450;
        this.running = true;
        this.grid = new int[23][10];
        for (int i = 0; i < 23; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = -1;
            }
        }
        if (gameMode == PLAYER_TETRIS) currentPiece = new Piece(TRandom.getTetris());
        else currentPiece = new Piece(TRandom.getPenta());
        
        nextPieces = new Piece[5];
        for (int i = 0; i < 5; i++) {
            if (gameMode == PLAYER_TETRIS) nextPieces[i] = new Piece(TRandom.getTetris());
            else nextPieces[i] = new Piece(TRandom.getPenta());
        }
        holdPiece = null;

        grounded = false;
        moved = false;
        lockTimer = MAX_LOCK_TIMER;
        fallTimer = MAX_FALL_TIMER;
        rows = 0;
    }

    // Draw all the things
    private void draw() {
        image = new GreenfootImage(800, 600);
        image.setColor(Color.WHITE);
        image.fill();
        drawGrid();
        drawGhost();
        drawCurrent();
        drawNext();
        drawHold();
        drawScore();
        setBackground(image);
    }
    
    protected void drawGrid() {
        // Draw blocks in the grid
        for (int i = 0; i < 23; i++) {
            final int drawY = y - 20 * i + 10;
            for (int j = 0; j < 10; j++) {
                final int drawX = x + 20 * j + 10;
                drawBlock(drawX, drawY, grid[i][j]);
            }
        }
        image.setColor(new Color(100, 100, 255));
        // Draw the grid lines
        for (int i = 0; i <= 23; i++) {
            if(i == 19){
                image.setColor(new Color(0,0,0));
                image.drawLine(x + 10, y - 20 * i + 29, x + 20 * 10 + 10, y - 20 * i + 29);
                image.drawLine(x + 10, y - 20 * i + 31, x + 20 * 10 + 10, y - 20 * i + 31);
                image.setColor(new Color(100, 100, 255));
            }
            image.drawLine(x + 10, y - 20 * i + 30, x + 20 * 10 + 10, y - 20 * i + 30);
        }
        for (int i = 0; i <= 10; i++) {
            image.drawLine(x + 20 * i + 10, y + 30, x + 20 * i + 10, y + 30 - 20 * 23);
        }
    }
    
    protected void drawGhost(){
        // Move the ghost all the way down then draw it
        Piece ghost = new Piece(currentPiece);
        while(isOk(ghost.down())) ghost = ghost.down();
        drawPiece(ghost, x, y, true);
    }

    private void drawBlock(int drawX, int drawY, int colour) {
        // If not empty block, draw the corresponding block from the images array
        if (colour != -1) {
            image.drawImage(images[colour], drawX, drawY);
        }
    }

    public boolean isOk(Piece b) {
        // If the piece fits in the grid, return true, else return false
        for (int i = 0; i < b.getNumBlocks(); i++) {
            final int tx = b.getX() + b.getBlockX(i);
            final int ty = b.getY() + b.getBlockY(i);
            if (tx < 0 || tx >= 10 || ty < 0 || ty >= 23 || grid[ty][tx] != -1) return false;
        }
        return true;
    }
    
    private void endGame() {
        // This is called when the game ends
        Greenfoot.setWorld(new Title());
    }
    
    // Used by server and client classes
    protected void updateGrid(int i, ArrayList<ArrayList<Integer>> grid) {}
    protected void updateCur(int i, Piece cur) {}
    protected void updateScore(int i, int score) {}

    protected void drawScore(){
        image.drawImage(new GreenfootImage("Score: "+(rows+bonus)*10, 30, Color.BLACK, Color.WHITE),x,y+50);
    }
    
    protected void drawCurrent(Piece cur, int x, int y) {
        // Draw current piece
        if (cur != null) drawPiece(cur, x, y, false);
    }

    protected void drawGhost(Piece cur, ArrayList<ArrayList<Integer>> grid, int x, int y){
        // Draw ghost piece
        if(cur == null) return;
        Piece ghost = new Piece(cur);
        while(isOk(ghost.down(), grid)) ghost = ghost.down();
        drawPiece(ghost, x, y, true);
    }

    protected boolean isOk(Piece b, ArrayList<ArrayList<Integer>> grid) {
        // Same code as above but for ArrayLists instead
        for (int i = 0; i < b.getNumBlocks(); i++) {
            int tx = b.getX() + b.getBlockX(i);
            int ty = b.getY() + b.getBlockY(i);
            if (tx < 0 || tx >= 10 || ty < 0 || ty >= 23 || grid.get(ty).get(tx) != -1) {
                return false;
            }
        }
        return true;
    }

    protected void drawGrid(ArrayList<ArrayList<Integer>> arr, int x, int y) {
        // Same code as above but for ArrayLists instead
        for (int i = 0; i < 23; i++) {
            int drawY = y - 20 * i + 10;
            for (int j = 0; j < 10; j++) {
                int drawX = x + 20 * j + 10;
                drawBlock(drawX, drawY, arr.get(i).get(j));
            }
        }
        image.setColor(new Color(100, 100, 255));
        for (int i = 0; i <= 23; i++) {
            if(i == 19){
                image.setColor(new Color(0, 0, 0));
                image.drawLine(x + 10, y - 20 * i + 29, x + 20 * 10 + 10, y - 20 * i + 29);
                image.drawLine(x + 10, y - 20 * i + 31, x + 20 * 10 + 10, y - 20 * i + 31);
                image.setColor(new Color(100, 100, 255));
            }
            image.drawLine(x + 10, y - 20 * i + 30, x + 20 * 10 + 10, y - 20 * i + 30);
        }
        for (int i = 0; i <= 10; i++) {
            image.drawLine(x + 20 * i + 10, y + 30, x + 20 * i + 10, y + 30 - 20 * 23);
        }
    }

    /* =============== These draw methods are all self-explanatory ================= */
    protected void drawNext() {
        drawPiece(nextPieces[0], x + 120, y + 50, false);
        drawPiece(nextPieces[1], x + 120, y + 150, false);
        drawPiece(nextPieces[2], x + 120, y + 250, false);
        drawPiece(nextPieces[3], x + 120, y + 350, false);
    }

    protected void drawCurrent() {
        drawGhost(currentPiece);
        drawPiece(currentPiece, x, y, false);
    }

    protected void drawGhost(Piece cur){
        Piece ghost = new Piece(cur);
        while(isOk(ghost.down())) ghost = ghost.down();
        drawPiece(ghost, x, y, true);
    }

    protected void drawHold() {
        drawPiece(holdPiece, x - 60, y, false);
    }

    protected void drawPiece(Piece b, int x, int y, boolean isGhost) {
        if (b == null) return;
        int colour = isGhost ? 21 : b.getColour();
        for (int i = 0; i < b.getNumBlocks(); i++) {
            int tx = b.getX() + b.getBlockX(i);
            int ty = b.getY() + b.getBlockY(i);
            int drawX = x + 20 * tx + 10;
            int drawY = y - 20 * ty + 10;
            drawBlock(drawX, drawY, colour);
        }
    }
}
