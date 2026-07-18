package gui;

import client.ChatClient;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    private JTextField nicknameField;
    private JButton connectButton;

    public LoginWindow() {

        setTitle("Java Socket Chat");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("JAVA SOCKET CHAT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        panel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        nicknameField = new JTextField();
        nicknameField.setFont(new Font("Arial", Font.PLAIN, 16));

        connectButton = new JButton("Connect");
        connectButton.setFont(new Font("Arial", Font.BOLD, 15));

        centerPanel.add(nicknameField);
        centerPanel.add(connectButton);

        panel.add(centerPanel, BorderLayout.CENTER);

        add(panel);

        connectButton.addActionListener(e -> connect());

        nicknameField.addActionListener(e -> connect());
    }

    private void connect() {

        String nickname = nicknameField.getText().trim();

        if (nickname.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a nickname.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        ChatClient client = new ChatClient();

        boolean connected = client.connect(nickname);

        if (!connected) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to connect to server.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        dispose();

        new ChatWindow(client, nickname);

    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(LoginWindow::new);

    }

}