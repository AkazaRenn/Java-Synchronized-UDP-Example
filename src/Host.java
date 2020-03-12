import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Host connecting client and server and transfers packets between them
 */
public class Host {

    public final String SERVER_ADDRESS = "localhost";
    public final int RECV_PORT = 23;
    public final int SERVER_PORT = 69;
    public final int CLIENT_DATA_SIZE = 1024;
    public final int SERVER_DATA_SIZE = 4;

    private DatagramSocket recvSocket;
    private DatagramSocket sendRecvSocket;
    
    public Host() throws SocketException, UnknownHostException {
        recvSocket = new DatagramSocket(RECV_PORT);
        sendRecvSocket = new DatagramSocket();
    }
    
    /**
     * Forward the packet received to the corresponding destination
     * @throws IOException if failed sending or receiving the packet
     */
    public void forwardPacket() throws IOException {
        // receive packet from client
        byte[] clientData = new byte[CLIENT_DATA_SIZE];
        DatagramPacket clientPacket = new DatagramPacket(clientData, CLIENT_DATA_SIZE);
        recvSocket.receive(clientPacket);
        System.out.println("Host received packet from client:");
        Utils.printPacketDetails(clientPacket, true);
        
        // build a packet to be sent to server
        DatagramPacket serverPacket = new DatagramPacket(clientPacket.getData(), CLIENT_DATA_SIZE);
        serverPacket.setAddress(InetAddress.getByName(SERVER_ADDRESS));
        serverPacket.setPort(SERVER_PORT);
        System.out.println("Host sent packet to server:");
        Utils.printPacketDetails(serverPacket, true);
        sendRecvSocket.send(serverPacket);
        
        // receive response from server
        byte[] serverData = new byte[SERVER_DATA_SIZE];
        serverPacket.setData(serverData);
        sendRecvSocket.receive(serverPacket);
        System.out.println("Host received packet from server and forwarded to client:");
        Utils.printPacketDetails(serverPacket, false);
        
        // forward the response to client
        clientPacket.setData(serverPacket.getData());
        sendRecvSocket.send(clientPacket);        
    }
    
    public static void main(String[] args) throws Throwable {
        Host host = new Host();
        while(true) {
            host.forwardPacket();
        }
    }
}
