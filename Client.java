public class Client 
{
    private java.net.Socket socket = null;
    private java.io.ObjectInputStream inputStream = null;
    private java.io.ObjectOutputStream outputStream = null;
    private String host = null;
    private int port = 23374;
    
    public Client(String host, int port){
        this.host=host;
        this.port=port;
        try{
            socket = new java.net.Socket(host, port);
            System.out.println("Connected");
        }
        catch(java.net.SocketException se){
            se.printStackTrace();
            throw new RuntimeException("Not able to connect", se);
            // System.exit(0);
        }
        catch(java.io.IOException e){
            e.printStackTrace();
        }
        catch(Exception ee){
            ee.printStackTrace();
        }
    }
    
    public void sendGrid(int i, java.util.ArrayList<java.util.ArrayList<Integer>> grid){
        try{
            outputStream = new java.io.ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject("Grid");
            outputStream.writeObject(i);
            outputStream.writeObject(grid);
            outputStream.flush();
        }
        catch(java.io.IOException e){
            e.printStackTrace();
        }
    }
    
    public void sendServer(Server s){
        try{
            outputStream = new java.io.ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject("Server");
            outputStream.writeObject(s.getIP());
            outputStream.writeObject(s.getPort());
            outputStream.flush();
        }
        catch(java.io.IOException e){
            e.printStackTrace();
        }
    }
    
    public void sendPiece(int i, Piece p){
        try{
            outputStream = new java.io.ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject("Piece");
            outputStream.writeObject(i);
            outputStream.writeObject(p);
            outputStream.flush();
        }
        catch(java.io.IOException e){
            e.printStackTrace();
        }
    }
    
    public void sendScore(int i, int s){
        try{
            outputStream = new java.io.ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject("Score");
            outputStream.writeObject(i);
            outputStream.writeObject(s);
            outputStream.flush();
        }
        catch(java.io.IOException e){
            e.printStackTrace();
        }
    }
    
    public void begin(int num){
        try{
            outputStream = new java.io.ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject("START");
            outputStream.writeObject(num);
            outputStream.flush();
        }
        catch(java.io.IOException e){
            e.printStackTrace();
        }
    }
    
    public void sendNum(int i){
        try{
            outputStream = new java.io.ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject("ID");
            outputStream.writeObject(i);
            outputStream.flush();
        }
        catch(java.io.IOException e){
            e.printStackTrace();
        }
    }
}
