package Builder;

import Runnables.ConsoleWriter;
import Runnables.SocketReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler {
    private final Socket clientSocket;
    public ConnectionHandler(Socket socket){
        this.clientSocket=socket;
    }
    public void construct() throws IOException , InterruptedException {
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        ConsoleWriter writer=new ConsoleWriter(reader,printWriter,clientSocket);
        Thread writerThread= new Thread(writer);
        writerThread.setDaemon(true);
        BufferedReader inputReader=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        SocketReader socketReader=new SocketReader(inputReader,clientSocket);
        Thread readerThread=new Thread(socketReader);
        readerThread.start();
        writerThread.start();
        readerThread.join();
    }
}
