package Runnables;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ConsoleWriter implements Runnable{
   private final PrintWriter out;
   private final BufferedReader in;
   private final Socket socket;
    public ConsoleWriter(BufferedReader bufferedReader,PrintWriter printWriter,Socket socket){
       this.in=bufferedReader;
       this.out=printWriter;
       this.socket=socket;
    }
    @Override
    public void run(){
     try{
        String msg;
        while((msg=in.readLine())!=null){
           out.println(msg);
            if("./Exit".equals(msg)){
                out.println("closing socket...");
                socket.close();
                return;}
        }
     }catch (IOException e){System.out.println("Writer Error: "+e.getMessage());}
    }
}
