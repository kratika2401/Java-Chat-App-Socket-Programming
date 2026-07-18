package server;

import common.Constants;
import util.ChatLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private ServerSocket serverSocket;

    private final List<ClientHandler> clients = new ArrayList<>();

    public void startServer() {

        try {

            serverSocket = new ServerSocket(Constants.SERVER_PORT);

            System.out.println("=================================");
            System.out.println(" JAVA SOCKET CHAT SERVER");
            System.out.println("=================================");
            System.out.println("Server Started...");
            System.out.println("Host : " + Constants.SERVER_HOST);
            System.out.println("Port : " + Constants.SERVER_PORT);
            System.out.println("Waiting for clients...");
            System.out.println("---------------------------------");

            ChatLogger.log("Server Started");

            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();

                System.out.println("Client Connected : " + socket.getInetAddress());

                ChatLogger.log("Client Connected : " + socket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(socket, this);

                synchronized (clients) {
                    clients.add(clientHandler);
                }

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast normal chat message
    public void broadcastMessage(String message) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                client.sendMessage(message);

            }

        }

    }

    // Broadcast online users list
    public void broadcastUserList() {

        StringBuilder builder = new StringBuilder("#USERS#");

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client.getNickname() != null) {

                    builder.append(client.getNickname()).append(",");

                }

            }

            String userList = builder.toString();

            for (ClientHandler client : clients) {

                client.sendMessage(userList);

            }

        }

    }

    public void removeClient(ClientHandler client) {

        synchronized (clients) {

            clients.remove(client);

        }

    }

    public List<String> getOnlineUsers() {

        List<String> users = new ArrayList<>();

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client.getNickname() != null) {

                    users.add(client.getNickname());

                }

            }

        }

        return users;

    }

    public boolean isNicknameTaken(String nickname) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                String existing = client.getNickname();

                if (existing != null &&
                        existing.equalsIgnoreCase(nickname)) {

                    return true;

                }

            }

        }

        return false;

    }

    public ClientHandler getClientByNickname(String nickname) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client.getNickname() != null &&
                        client.getNickname().equalsIgnoreCase(nickname)) {

                    return client;

                }

            }

        }

        return null;

    }

    public void closeServer() {

        try {

            if (serverSocket != null) {

                ChatLogger.log("Server Stopped");

                serverSocket.close();

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public static void main(String[] args) {

        ChatServer server = new ChatServer();

        server.startServer();

    }

}