public class Server extends Thread
{
    private java.net.ServerSocket serverSocket = null;
    private java.net.Socket socket = null;
    private java.io.ObjectInputStream inStream = null;
    private TetrisMain w;
    private int port = 23374;
    public Server(TetrisMain w, int port){
        this.w = w;
        this.port = port;
        try {
            this.serverSocket = new java.net.ServerSocket(this.port);
            serverSocket.setReuseAddress(true);
            System.out.println("SERVER RUNNING ON PORT " + this.port);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Not able to open the server", e);
        }
    }
    
    public void changeTarget(TetrisMain w){
        this.w=w;
    }

    public Server(TetrisMain w){
        this.w = w;
        while(true){
            port = (int)(Math.random()*(65536-49152+1))+49152;
            try {
                this.serverSocket = new java.net.ServerSocket(this.port);
                serverSocket.setReuseAddress(true);
                System.out.println("SERVER RUNNING ON PORT " + this.port);
                break;
            } catch (java.io.IOException e) {
                System.out.println("Port "+port+" is occupied.");
            }
        }
    }

    public int getPort(){
        return port;
    }

    public String getIP(){
        if(serverSocket!=null){
            try{
                return serverSocket.getInetAddress().getLocalHost().toString();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        return "";
    }

    public void run(){
        try{
            socket = serverSocket.accept();
        }
        catch(java.io.IOException e){
            e.printStackTrace();
        }
        while(true){
            try {
                inStream = new java.io.ObjectInputStream(socket.getInputStream());
                String o = (String) inStream.readObject();
                System.out.println(o);
                if(o.equals("Server")){
                    TetrisWorldServer t = (TetrisWorldServer)w;
                    t.addClient((String)inStream.readObject(),(Integer)inStream.readObject());
                }
                else if(o.equals("Grid")){
                    w.updateGrid((Integer)inStream.readObject(), (java.util.ArrayList<java.util.ArrayList<Integer>>)inStream.readObject());
                }
                else if(o.equals("ID")){
                    TetrisWorldClient t = (TetrisWorldClient)w;
                    t.setID((Integer)inStream.readObject());
                }
                else if(o.equals("START")){
                    TetrisWorldClient t = (TetrisWorldClient)w;
                    t.start((Integer)inStream.readObject());
                }
                else if(o.equals("Piece")){
                    w.updateCur((Integer)inStream.readObject(), (Piece)inStream.readObject());
                }
                else if(o.equals("Score")){
                    w.updateScore((Integer)inStream.readObject(), (Integer)inStream.readObject());
                }
            }
            catch(java.net.SocketException se){
                System.exit(0);
            }
            catch(java.io.IOException e){
                e.printStackTrace();
            }
            catch(ClassNotFoundException cn){
                cn.printStackTrace();
            }
        }
    }
}
