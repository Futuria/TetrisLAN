import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class MyWorld here.
 * 
 * @author Nathan Lo, Joey Ma
 * @version June 15, 2017
 */
public class TetrisWorldServer extends TetrisMain{
    private ArrayList<Server> s;
    private ArrayList<Client> c;
    private ArrayList<ArrayList<ArrayList<Integer>>> grids;
    private ArrayList<Piece> curs;
    private ArrayList<Integer> scores;
    private Button1 start;
    private boolean started, tetris;
    private GreenfootImage thingy;

    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public TetrisWorldServer(boolean tetris){
        super(TetrisMain.PLAYER_SERVER, tetris?TetrisMain.PLAYER_TETRIS:TetrisMain.PLAYER_PENTA, true);
        this.tetris=tetris;
        s = new ArrayList<Server>();
        s.add(new Server(this));
        s.get(0).start();
        start = new Button1();
        addObject(start,400,300);
        thingy = new GreenfootImage(800,600);
        thingy.setColor(java.awt.Color.WHITE);
        thingy.fill();
        thingy.setColor(java.awt.Color.BLACK);
        thingy.drawString("Connected:",5,15);
        thingy.drawString("Your IP: "+s.get(0).getIP().split("/")[1],5,584);
        thingy.drawString("Your Port: "+s.get(0).getPort(),5,599);
        setBackground(thingy);
        c = new ArrayList<Client>();
    }
    
    public TetrisWorldServer(boolean tetris, ArrayList<Client> c, ArrayList<Server> s){
        super(TetrisMain.PLAYER_SERVER, tetris?TetrisMain.PLAYER_TETRIS:TetrisMain.PLAYER_PENTA,100*(c.size()+1),500);
        this.tetris=tetris;
        this.c = c;
        this.s = s;
        for(Client cc: c){
            cc.begin(c.size());
        }
        for(Server ss: this.s){
            ss.changeTarget(this);
        }
        ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> temp2 = new ArrayList<Integer>();
        for(int j = 0; j < 10; j++){
            temp2.add(-1);
        }
        for(int i = 0; i < 23; i++){
            temp.add(temp2);
        }
        grids = new ArrayList<ArrayList<ArrayList<Integer>>>();
        for(int i = 0; i <= c.size(); i++){
            grids.add(temp);
        }
        curs = new ArrayList<Piece>();
        for(int i = 0; i <= c.size(); i++){
            curs.add(null);
        }
        scores = new ArrayList<Integer>();
        for(int i = 0; i <= c.size(); i++){
            scores.add(0);
        }
        started=true;
    }

    public void addClient(String ip, int port){
        System.out.println("CREATING CONNECTION");
        System.out.println(ip.split("/")[1]);
        Client cc = new Client(ip.split("/")[1],port);
        c.add(cc);
        cc.sendNum(c.size());
        thingy.setColor(java.awt.Color.BLACK);
        thingy.drawString(ip.split("/")[1]+":"+port,5,15*c.size()+20);
        thingy.setColor(java.awt.Color.WHITE);
        thingy.drawString("Your Port: "+s.get(s.size()-1).getPort(),5,599);
        thingy.setColor(java.awt.Color.BLACK);
        s.add(new Server(this));
        s.get(s.size()-1).start();
        thingy.drawString("Your Port: "+s.get(s.size()-1).getPort(),5,599);
        setBackground(thingy);
    }

    private void draw() {
        image = new GreenfootImage(800, 600);
        image.setColor(java.awt.Color.WHITE);
        image.fill();
        for(int i = 0; i < grids.size(); i++){
            drawGrid(grids.get(i),75+300*i,450);
            drawGhost(curs.get(i),grids.get(i),75+300*i,450);
            drawCurrent(curs.get(i),75+300*i,450);
            drawScore(i,75+300*i,450);
        }
        drawNext();
        drawHold();
        setBackground(image);
    }

    private void drawScore(int i, int x, int y){
        image.drawImage(new GreenfootImage("Score: "+scores.get(i)*10, 30, java.awt.Color.BLACK, java.awt.Color.WHITE),x,y+50);
    }

    public void act(){
        if(!started){
            if(Greenfoot.mouseClicked(start)){
                Greenfoot.setWorld(new TetrisWorldServer(tetris,c,s));
            }
        }
        else{
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
                grids.set(0,temp);
                curs.set(0,new Piece(currentPiece));
                scores.set(0,(rows+bonus)*10);
                for(Client cc: c){
                    cc.sendGrid(0,grids.get(0));
                    cc.sendPiece(0,currentPiece);
                    cc.sendScore(0,(rows+bonus)*10);
                }
            }
            draw();
        }
    }

    public void updateGrid(int i, ArrayList<ArrayList<Integer>> grid){
        grids.set(i,grid);
        for(int j = 1; j < grids.size(); j++){
            if(j==i) continue;
            Client client = c.get(j-1);
            client.sendGrid(i, grid);
        }
    }

    public void updateCur(int i, Piece cur){
        curs.set(i,cur);
        for(int j = 1; j < curs.size(); j++){
            if(j==i) continue;
            Client client = c.get(j-1);
            client.sendPiece(i, cur);
        }
    }

    public void updateScore(int i, int score){
        scores.set(i,score);
        for(int j = 1; j < scores.size(); j++){
            if(j==i) continue;
            Client client = c.get(j-1);
            client.sendScore(i, score);
        }
    }
}
