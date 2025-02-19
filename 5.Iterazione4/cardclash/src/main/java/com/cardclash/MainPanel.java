package com.cardclash;

import java.awt.BorderLayout;
import javax.swing.*;

public class MainPanel extends JPanel {
    private JTabbedPane tabbedPane;
    public PlayerPanel playerPanel;
    public OrganizerPanel organizerPanel;

    public MainPanel(CardClashGUI parentFrame) {
        setLayout(new BorderLayout());
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Esci");
        logoutItem.addActionListener(e -> parentFrame.cardLayout.show(parentFrame.containerPanel, "LOGIN"));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(menuBar, BorderLayout.NORTH);
        tabbedPane = new JTabbedPane();
        playerPanel = new PlayerPanel(parentFrame);
        organizerPanel = new OrganizerPanel(parentFrame);
        tabbedPane.addTab("Giocatore", playerPanel);
        tabbedPane.addTab("Organizzatore", organizerPanel);
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void setupRole(String role) {
        if ("user".equalsIgnoreCase(role)) {
            tabbedPane.setEnabledAt(0, true);
            tabbedPane.setEnabledAt(1, false);
            tabbedPane.setSelectedIndex(0);
        } else {
            tabbedPane.setEnabledAt(0, false);
            tabbedPane.setEnabledAt(1, true);
            tabbedPane.setSelectedIndex(1);
        }
    }

    
    public void updateEloLabel(Float elo) {
        playerPanel.updateElo(elo);
    }
}
