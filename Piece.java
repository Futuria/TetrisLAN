
/**
 * A Tetris or Pentrix piece
 * 
 * @author Nathan Lo 
 * @version May 31, 2017
 */
public class Piece implements java.io.Serializable {
    
    // Enumeration for Tetris pieces
    public static final int TETRIS_START = 0,
                            TETRIS_I = 0, 
                            TETRIS_O = 1, 
                            TETRIS_T = 2,
                            TETRIS_S = 3, 
                            TETRIS_Z = 4,
                            TETRIS_J = 5, 
                            TETRIS_L = 6,
                            TETRIS_END = 7;
    
    // The coloured block image to use for each block type
    public static final int[] colourArray = {0, 1, 2, 3, 4, 5, 6, 
        0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 12};

    // Enumeration for Pentrix pieces
    public static final int PENTA_START = 8,
                            PENTA_O = 8,
                            PENTA_P1 = 9,
                            PENTA_P2 = 10,
                            PENTA_Q1 = 11,
                            PENTA_Q2 = 12,
                            PENTA_R1 = 13,
                            PENTA_R2 = 14,
                            PENTA_S1 = 15,
                            PENTA_S2 = 16,
                            PENTA_T = 17,
                            PENTA_U = 18,
                            PENTA_V = 19,
                            PENTA_W = 20,
                            PENTA_X = 21,
                            PENTA_Y1 = 22,
                            PENTA_Y2 = 23,
                            PENTA_Z1 = 24,
                            PENTA_Z2 = 25,
                            PENTA_END = 26;
                      
    // Coordinates for tetris pieces, (x, y) The pieces pivot around 0, 0
    public static final int[][][] tetras = {
        {{0, -1}, {0,  0}, {0, 1}, {0, 2}}, // I
        {{0,  0}, {1,  0}, {0, 1}, {1, 1}}, // O
        {{0, -1}, {0,  0}, {0, 1}, {1, 0}}, // T
        {{0, -1}, {0,  0}, {1, 0}, {1, 1}}, // S
        {{1, -1}, {1,  0}, {0, 0}, {0, 1}}, // Z
        {{0, -1}, {1, -1}, {0, 0}, {0, 1}}, // J
        {{0, -1}, {0,  0}, {0, 1}, {1, 1}}, // L
    };
    
    // Coordinates for pentrix pieces, (x, y) The pieces pivot around 0, 0
    public static final int[][][] pentas = {
        {{0, -2}, {0, -1}, {0,  0}, {0,  1}, {0,   2}}, // O
        {{0, -1}, {0,  0}, {0,  1}, {1,  0}, {1,   1}}, // P1
        {{0, -1}, {0,  0}, {0,  1}, {-1, 0}, {-1,  1}}, // P2
        {{0,  0}, {0, -1}, {0, -2}, {0,  1}, {-1,  1}}, // Q1
        {{0,  0}, {0, -1}, {0, -2}, {0,  1}, {1,   1}}, // Q2
        {{0,  0}, {0, -1}, {0,  1}, {-1, 0}, {1,   1}}, // R1
        {{0,  0}, {0, -1}, {0,  1}, {1,  0}, {-1,  1}}, // R2
        {{0,  0}, {0, -1}, {0, -2}, {-1, 0}, {-1,  1}}, // S1
        {{0,  0}, {0, -1}, {0, -2}, {1,  0}, {1,   1}}, // S2
        {{0,  0}, {-1, 0}, {1,  0}, {0, -1}, {0,  -2}}, // T
        {{0,  0}, {1,  0}, {-1, 0}, {1,  1}, {-1,  1}}, // U
        {{0,  0}, {0,  1}, {0,  2}, {1,  0}, {2,   0}}, // V
        {{0,  0}, {1,  0}, {1,  1}, {0, -1}, {-1, -1}}, // W
        {{0,  0}, {-1, 0}, {1,  0}, {0, -1}, {0,   1}}, // X
        {{0,  0}, {0, -1}, {0, -2}, {0,  1}, {-1,  0}}, // Y1
        {{0,  0}, {0, -1}, {0, -2}, {0,  1}, {1,   0}}, // Y2
        {{0,  0}, {0, -1}, {0,  1}, {1,  1}, {-1, -1}}, // Z1
        {{0,  0}, {0, -1}, {0,  1}, {-1, 1}, {1,  -1}}, // Z2
    };
    

    private int blockType, x, y, blockX[], blockY[], numBlocks, colour;
    
    // These methods are straight-forward
    // NOTE: Javadocs are not included for these public methods 
    // because the method names are self-explanatory

    public Piece rotateRight() {
        Piece block = copyBlock(this);
        for (int i = 0; i < numBlocks; i++) {
            block.blockX[i] = +blockY[i];
            block.blockY[i] = -blockX[i];
        }
        return block;
    }
    
    // NOTE: Not used
    public Piece rotateLeft() {
        Piece block = copyBlock(this);
        for (int i = 0; i < numBlocks; i++) {
            block.blockX[i] = -blockY[i];
            block.blockY[i] = +blockX[i];
        }
        return block;
    }

    public Piece right() {
        Piece block = copyBlock(this);
        block.x++;
        return block;
    }
    
    public Piece left() {
        Piece block = copyBlock(this);
        block.x--;
        return block;
    }
    
    public Piece down() {
        Piece block = copyBlock(this);
        block.y--;
        return block;
    }
    
    private Piece(int blockType, int x, int y, int[] blockX, int[] blockY, int numBlocks, int colour) {
        this.blockType = blockType;
        this.x = x;
        this.y = y;
        this.numBlocks = numBlocks;
        this.colour = colour;
        this.blockX = new int[numBlocks];
        this.blockY = new int[numBlocks];
        for (int i = 0; i < numBlocks; i++) {
            this.blockX[i] = blockX[i];
            this.blockY[i] = blockY[i];
        }
    }
    
    /**
     *  Main constructor for Piece
     *  @param blockType        The block type in the enumeration above
     *   @param x                The x-coordinate the piece should start at
     *  @param y                The y-coordinate the piece should start at
     */
    public Piece(int blockType, int x, int y) {
        this.x = x;
        this.y = y;
        this.colour = colourArray[blockType]; // TODO: Change to actual colour array
        if (TETRIS_START <= blockType && blockType < TETRIS_END) {
            this.blockX = new int[4];
            this.blockY = new int[4];
            numBlocks = 4;
            for (int i = 0; i < 4; i++) {
                blockX[i] = tetras[blockType - TETRIS_START][i][0];
                blockY[i] = tetras[blockType - TETRIS_START][i][1];
            }
            // 4 block piece
        } else if (PENTA_START <= blockType && blockType < PENTA_END) {
            this.blockX = new int[5];
            this.blockY = new int[5];
            numBlocks = 5;
            for (int i = 0; i < 5; i++) {
                blockX[i] = pentas[blockType - PENTA_START][i][0];
                blockY[i] = pentas[blockType - PENTA_START][i][1];
            }
            // 5 block piece
        } else {
            System.err.println("BAD BLOCK TYPE" + blockType);
            System.out.println("Defaulting to empty block");
            this.colour = -1;
        }
    }
    
    /**
     *   Default constructor for pieces which spawn at the top
     */
    public Piece(int blockType) {
        this(blockType, 5, 20);
    }
    
    // NOTE: Javadocs are not included for these methods, as they are setters and getters
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getBlockX(int i) { return blockX[i]; }
    public int getBlockY(int i) { return blockY[i]; }
    public int getNumBlocks() { return numBlocks; }
    public int getColour() { return colour; }
    
    /**
     *   Returns a deep copy of a piece
     *   @param b        The piece to copy
     *   @result Piece   A deep copy of b
    */
    public static Piece copyBlock(Piece b) {
        return new Piece(b.blockType, b.x, b.y, b.blockX, b.blockY, b.numBlocks, b.colour);
    }
    
    /**
     *  A constructor wrapper for the copyBlock method
     *  @param old      The piece to copy
     */
    public Piece(Piece old){
        this(old.blockType, old.x, old.y, old.blockX, old.blockY, old.numBlocks, old.colour);
    }
}