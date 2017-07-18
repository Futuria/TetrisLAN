import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class TetrisWorldClient here.
 * 
 * @author Nathan Lo, Joey Ma
 * @version June 15, 2017
 */
public class TetrisWorldClient extends TetrisMain {
    private Client c;
    private Server s;
    private int ID, cnt;
    private ArrayList<ArrayList<ArrayList<Integer>>> grids;
    private ArrayList<Piece> curs;
    private ArrayList<Integer> scores;
    private boolean started, tetris;

    /**
     * Constructor for objects of class TetrisWorldClient.
     * 
     */
    public TetrisWorldClient(boolean tetris){
        super(TetrisMain.PLAYER_CLIENT, tetris?TetrisMain.PLAYER_TETRIS:TetrisMain.PLAYER_PENTA, 100, 500);
        s = new Server(this);
        started = false;
        this.tetris=tetris;
        setupConnection();
    }
    
    public TetrisWorldClient(boolean tetris, Client c, Server s, int ID, int cnt){
        super(TetrisMain.PLAYER_CLIENT, tetris?TetrisMain.PLAYER_TETRIS:TetrisMain.PLAYER_PENTA, 100*cnt+100, 500);
        this.tetris=tetris;
        this.c = c;
        this.s = s;
        this.s.changeTarget(this);
        started = true;
        this.ID = ID;
        this.cnt = cnt;
        ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> temp2 = new ArrayList<Integer>();
        for(int j = 0; j < 10; j++){
            temp2.add(-1);
        }
        for(int i = 0; i < 23; i++){
            temp.add(temp2);
        }
        grids = new ArrayList<ArrayList<ArrayList<Integer>>>();
        for(int i = 0; i <= cnt; i++){
            grids.add(temp);
        }
        curs = new ArrayList<Piece>();
        for(int i = 0; i <= cnt; i++){
            curs.add(null);
        }
        scores = new ArrayList<Integer>();
        for(int i = 0; i <= cnt; i++){
            scores.add(0);
        }
    }

    private void draw() {
        image = new GreenfootImage(800, 600);
        image.setColor(java.awt.Color.WHITE);
        image.fill();
        drawGrid();
        drawGhost();
        drawCurrent();
        drawScore();
        for(int i = 0; i < grids.size(); i++){
            if(i==ID) continue;
            drawGrid(grids.get(i),75+300*(i+(i<ID?1:0)),450);
            drawGhost(curs.get(i),grids.get(i),75+300*(i+(i<ID?1:0)),450);
            drawCurrent(curs.get(i),75+300*(i+(i<ID?1:0)),450);
            drawScore(i,75+300*(i+(i<ID?1:0)),450);
        }
        drawNext();
        drawHold();
        setBackground(image);
    }

    private void drawScore(int i, int x, int y){
        image.drawImage(new GreenfootImage("Score: "+scores.get(i)*10, 30, java.awt.Color.BLACK, java.awt.Color.WHITE),x,y+50);
    }

    private void setupConnection(){
        while(true){
            try{
                String host = javax.swing.JOptionPane.showInputDialog("Input host's IP Address: ");
                int port;
                while(true){
                    try{
                        port = Integer.parseInt(javax.swing.JOptionPane.showInputDialog("Input host's Port: "));
                        break;
                    }catch(java.lang.NumberFormatException e){
                    }
                }
                c = new Client(host,port);
                c.sendServer(s);
                s.start();
                break;
            }
            catch(java.lang.RuntimeException e){
            }
        }
    }

    public void act(){
        if(started){
            super.act();
            if(moved){
                ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
                for(int i = 0; i < grid.length; i++){
                    ArrayList<Integer> temp2 = new ArrayList<Integer>();
                    for(int j = 0; j < grid[i].length; j++){
                        temp2.add(grid[i][j]);
                    }
                    temp.add(temp2);
                }
                grids.set(ID,temp);
                curs.set(ID,new Piece(currentPiece));
                scores.set(ID,(rows+bonus)*10);
                c.sendPiece(ID,new Piece(currentPiece));
                c.sendGrid(ID,temp);
                c.sendScore(ID,(rows+bonus)*10);
            }
            draw();
        }
    }

    public void setID(int id){
        ID=id;
        System.out.println("I AM NUMBER "+ID);
    }

    public void updateGrid(int i, ArrayList<ArrayList<Integer>> grid){
        grids.set(i,grid);
    }

    public void updateCur(int i, Piece cur){
        curs.set(i,cur);
    }

    public void updateScore(int i, int score){
        scores.set(i,score);
    }

    public void start(int num){
        Greenfoot.setWorld(new TetrisWorldClient(tetris, c, s, ID, num));
        cnt = num;
    }
}
