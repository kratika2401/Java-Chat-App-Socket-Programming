package gui;

import client.ChatClient;
import common.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatWindow extends JFrame {

    private final ChatClient client;
    private final String nickname;

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    public ChatWindow(ChatClient client, String nickname) {

        this.client = client;
        this.nickname = nickname;

        setTitle("Java Chat  |  " + nickname);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initComponents();
        registerEvents();

        setVisible(true);

        messageField.requestFocusInWindow();
    }

    private void initComponents() {

        getContentPane().setBackground(new Color(245,245,245));
        setLayout(new BorderLayout(10,10));

        ((JComponent)getContentPane())
                .setBorder(new EmptyBorder(10,10,10,10));

        JLabel title = new JLabel("Java Chat");
        title.setFont(new Font("Segoe UI", Font.BOLD,22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        add(title, BorderLayout.NORTH);

        chatArea = new JTextArea();

        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        chatArea.setBackground(Color.WHITE);

        chatArea.setFont(new Font("Segoe UI", Font.PLAIN,16));

        chatArea.setMargin(new Insets(10,10,10,10));

        JScrollPane scrollPane = new JScrollPane(chatArea);

        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210,210,210)));

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10,10));

        bottomPanel.setBackground(new Color(245,245,245));

        messageField = new JTextField();

        messageField.setFont(new Font("Segoe UI", Font.PLAIN,16));

        messageField.setPreferredSize(new Dimension(0,42));

        sendButton = new JButton("Send");

        sendButton.setFont(new Font("Segoe UI", Font.BOLD,15));

        sendButton.setPreferredSize(new Dimension(110,42));

        bottomPanel.add(messageField, BorderLayout.CENTER);

        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

    }

    private void registerEvents() {

        client.setMessageListener(message -> {

            SwingUtilities.invokeLater(() -> {

                chatArea.append(message + "\n");

                chatArea.setCaretPosition(
                        chatArea.getDocument().getLength()
                );

            });

        });

        sendButton.addActionListener(e -> sendMessage());

        messageField.addActionListener(e -> sendMessage());

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                client.sendMessage(Constants.EXIT_COMMAND);

                client.disconnect();

                dispose();

            }

        });

    }

    private void sendMessage() {

        String message = messageField.getText().trim();

        if(message.isEmpty()) {
            return;
        }

        client.sendMessage(message);

        messageField.setText("");

        messageField.requestFocusInWindow();

    }

}