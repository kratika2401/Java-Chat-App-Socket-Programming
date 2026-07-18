package server;

import common.Constants;
import util.ChatLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;

    private BufferedReader reader;
    private PrintWriter writer;

    private String nickname;
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a");

    private String getCurrentTime() {

        return "[" +
                LocalTime.now().format(TIME_FORMAT) +
                "]";

    }
    public ClientHandler(Socket socket, ChatServer server) {

        this.socket = socket;
        this.server = server;

    }

    @Override
    public void run() {

        try {

            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            writer = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            while (true) {

                writer.println("Enter your nickname:");

                String enteredNickname = reader.readLine();

                if (enteredNickname == null) {
                    return;
                }

                enteredNickname = enteredNickname.trim();

                if (enteredNickname.isEmpty()) {

                    writer.println("Nickname cannot be empty.");
                    continue;

                }

                if (server.isNicknameTaken(enteredNickname)) {

                    writer.println("Nickname already taken. Try another.");
                    continue;

                }

                nickname = enteredNickname;
                break;

            }

            String joinMessage =
                    "🟢 " +
                            getCurrentTime() +
                            " " +
                            nickname +
                            " joined the chat";

            server.broadcastMessage(joinMessage);
            server.broadcastUserList();

            ChatLogger.log(nickname + " joined the chat");

            writer.println("Type /help to see available commands.");

            String message;

            while ((message = reader.readLine()) != null) {

                message = message.trim();

                if (message.isEmpty()) {
                    continue;
                }

                if (message.equalsIgnoreCase(Constants.EXIT_COMMAND)) {
                    break;
                }

                if (message.equalsIgnoreCase(Constants.HELP_COMMAND)) {

                    writer.println("========== COMMANDS ==========");
                    writer.println("/help  - Show commands");
                    writer.println("/list  - Show online users");
                    writer.println("/quit  - Exit chat");
                    writer.println("@username message - Private Message");
                    writer.println("==============================");

                    continue;

                }

                if (message.equalsIgnoreCase(Constants.LIST_COMMAND)) {

                    List<String> users = server.getOnlineUsers();

                    writer.println("Online Users:");

                    for (String user : users) {

                        writer.println("- " + user);

                    }

                    continue;

                }

                if (message.startsWith(Constants.PRIVATE_PREFIX)) {

                    int firstSpace = message.indexOf(" ");

                    if (firstSpace == -1) {

                        writer.println("Usage: @username message");
                        continue;

                    }

                    String targetName =
                            message.substring(1, firstSpace);

                    String privateMessage =
                            message.substring(firstSpace + 1);

                    ClientHandler targetClient =
                            server.getClientByNickname(targetName);

                    if (targetClient == null) {

                        writer.println(
                                "User '" + targetName + "' not found."
                        );

                        continue;

                    }

                    targetClient.sendMessage(
                            "🔒 " +
                                    getCurrentTime() +
                                    " [Private] " +
                                    nickname +
                                    ": " +
                                    privateMessage
                    );

                    writer.println(
                            "🔒 " +
                                    getCurrentTime() +
                                    " [Private to " +
                                    targetName +
                                    "] " +
                                    privateMessage
                    );

                    ChatLogger.log(
                            "[Private] " +
                                    nickname +
                                    " -> " +
                                    targetName +
                                    " : " +
                                    privateMessage
                    );

                    continue;

                }

                server.broadcastMessage(
                        getCurrentTime() +
                                " " +
                                nickname +
                                ": " +
                                message
                );

                ChatLogger.log(
                        nickname + ": " + message
                );

            }

        }

        catch (IOException e) {

            System.out.println(nickname + " disconnected.");

        }

        finally {

            server.removeClient(this);

            if (nickname != null) {

                server.broadcastMessage(
                        "🔴 " +
                                getCurrentTime() +
                                " " +
                                nickname +
                                " left the chat"
                );
                server.broadcastUserList();

                ChatLogger.log(
                        nickname + " left the chat"
                );

            }

            try {

                socket.close();

            }

            catch (IOException e) {

                e.printStackTrace();

            }

        }

    }

    public void sendMessage(String message) {

        writer.println(message);

    }

    public String getNickname() {

        return nickname;

    }

}