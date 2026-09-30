package Runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class SocketReader implements Runnable{
    private final BufferedReader socketInput;
    private final Socket Socket;
    public SocketReader(BufferedReader bufferedReader,Socket socket){
        this.socketInput=bufferedReader;
        this.Socket =socket;
    }

    @Override
    public void run() {
       try{
           String msg;
           while ((msg=socketInput.readLine())!=null){
               System.out.println(msg);

               if("./Exit".equals(msg)){
                   System.out.println("closing socket...");
                   Socket.close();
                   return;
               }

           }
       } catch (IOException e) {
           System.out.println("Reader Error: "+e.getMessage());
       }

    }
}

