package client;

import common.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {

    private Socket socket;
    private BufferedReader serverReader;
    private PrintWriter serverWriter;

    private Consumer<String> messageListener;
    private Consumer<String[]> userListListener;

    public boolean connect(String nickname) {

        try {

            socket = new Socket(
                    Constants.SERVER_HOST,
                    Constants.SERVER_PORT
            );

            serverReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            serverWriter = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            // Read server prompt
            serverReader.readLine();

            // Send nickname
            serverWriter.println(nickname);

            startReceiverThread();

            return true;

        } catch (IOException e) {

            return false;

        }

    }

    private void startReceiverThread() {

        Thread receiverThread = new Thread(() -> {

            try {

                String message;

                while ((message = serverReader.readLine()) != null) {

                    if (message.startsWith("#USERS#")) {

                        String users = message.substring(7);

                        String[] onlineUsers = users.isEmpty()
                                ? new String[0]
                                : users.split(",");

                        if (userListListener != null) {

                            userListListener.accept(onlineUsers);

                        }

                    } else {

                        if (messageListener != null) {

                            messageListener.accept(message);

                        }

                    }

                }

            } catch (IOException ignored) {

            }

        });

        receiverThread.setDaemon(true);
        receiverThread.start();

    }

    public void sendMessage(String message) {

        if (serverWriter != null) {

            serverWriter.println(message);

        }

    }

    public void disconnect() {

        try {

            if (socket != null) {

                socket.close();

            }

        } catch (IOException ignored) {

        }

    }

    public void setMessageListener(Consumer<String> listener) {

        this.messageListener = listener;

    }
    public void setUserListListener(Consumer<String[]> listener) {

        this.userListListener = listener;

    }

    // ----------------------------
    // Console Mode (Old Behaviour)
    // ----------------------------

    public void startConsoleMode() {

        try {

            BufferedReader keyboardReader =
                    new BufferedReader(
                            new InputStreamReader(System.in)
                    );

            System.out.println("====================================");
            System.out.println(" JAVA SOCKET CHAT CLIENT");
            System.out.println("====================================");
            System.out.println();

            System.out.print("Enter Nickname : ");

            String nickname = keyboardReader.readLine();

            boolean connected = connect(nickname);

            if (!connected) {

                System.out.println("Unable to connect to server.");

                return;

            }

            setMessageListener(System.out::println);

            String userMessage;

            while ((userMessage = keyboardReader.readLine()) != null) {

                if (userMessage.trim().isEmpty()) {

                    continue;

                }

                sendMessage(userMessage);

                if (userMessage.equalsIgnoreCase(Constants.EXIT_COMMAND)) {

                    break;

                }

            }

            disconnect();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public static void main(String[] args) {

        new ChatClient().startConsoleMode();

    }

}