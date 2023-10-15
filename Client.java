import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        final Socket clientSocket; // socket used by client to send and receive data from server
        final BufferedReader in; // object to read data from socket
        final PrintWriter out; // object to write data into socket
        final Scanner sc = new Scanner(System.in); // object to read data from user's keyboard

        try {
            clientSocket = new Socket("127.0.0.1", 5000);
            out = new PrintWriter(clientSocket.getOutputStream());
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

            Thread receiver = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    try {
                        msg = in.readLine();
                        while (msg != null) {
                            System.out.println("Server: " + msg);
                            msg = in.readLine();
                        }
                        System.out.println("Server out of service");
                        out.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            receiver.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
