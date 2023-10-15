import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {

        final ServerSocket serverSocket;
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);

        try {
            serverSocket = new ServerSocket(5000);
            // severSocket is used by the server to listen to the connection request from client
            // use accept to wait for a request from the client
            clientSocket = serverSocket.accept();
            // out is responsible for sending data to the client
            out = new PrintWriter(clientSocket.getOutputStream());
            // in is used to read from clientSocket
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Sender thread: server send messages to client
            Thread sender = new Thread(new Runnable() {
                String msg; // the data written by user
                @Override
                public void run() {
                    while (true) {
                        msg = sc.nextLine(); // read data from user's keyboard
                        out.println(msg); // write data stored in msg in the clientSocket
                        out.flush(); // forces the sending of data
                    }
                }
            });
            sender.start();

            // receive thread: read data sent from client using readLine()
            Thread receive = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    try {
                        msg = in.readLine(); // read data from the clientSocket
                        // while the client is still connected to the server
                        while (msg != null) {
                            System.out.println("Client:" + msg);
                            msg = in.readLine();
                        }
                        // if msg == null that means the client is not connected any more
                        System.out.println("Client disconnected");
                        // close the sockets and streams
                        out.close();
                        clientSocket.close();
                        serverSocket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            receive.start();
        }catch (IOException e) {
            e.printStackTrace();
        }


    }
}
