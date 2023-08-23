/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package gui;

import entity.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.WhiteBoardServerService;
import shape.*;
import shape.Rectangle;
import shape.Shape;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.rmi.RemoteException;
import java.security.Key;
import java.util.Base64;
import java.util.List;

public class WhiteBoardGUI extends JFrame {
    private static String userName;
    private static DrawingCanvas canvas;
    private static JPanel userPanel;
    private static JTextArea chatTextArea;
    private static WhiteBoardServerService serverService;
    private static List<User> userList;
    private static List<String> userNameList;
    public static boolean isManager;
    public static BufferedWriter writer;
    private static JButton selectedShapeButton;
    private static JComboBox<String> fontComboBox, styleComboBox;
    private static JComboBox<Integer> sizeComboBox;
    private static File currentJsonFile;

    public WhiteBoardGUI(String userName, WhiteBoardServerService whiteBoardServerService, List<User> userList, List<String> userNameList, List<Shape> shapeList, boolean isManager, BufferedWriter writer) {

        super(isManager ? "White Board -- " + userName + " [Manager View]" : "White Board -- " + userName + " [User View]");
        WhiteBoardGUI.userName = userName;
        serverService = whiteBoardServerService;
        WhiteBoardGUI.userList = userList;
        WhiteBoardGUI.userNameList = userNameList;
        WhiteBoardGUI.isManager = isManager;
        WhiteBoardGUI.writer = writer;

        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.noButtonText", "No");

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel canvasPanel = new JPanel();

        canvas = new DrawingCanvas(shapeList);
        canvasPanel.add(canvas);

        // controlPanel on the top
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));
        controlPanel.setPreferredSize(new Dimension(getWidth(), 60));
        controlPanel.setMaximumSize(new Dimension(getWidth(), 60));

        // show current color
        JPanel currentColorPanel = new JPanel(new BorderLayout());
        currentColorPanel.setBackground(Color.BLACK);
        currentColorPanel.setMinimumSize(new Dimension(25, 25));
        currentColorPanel.setPreferredSize(new Dimension(25, 25));
        currentColorPanel.setMaximumSize(new Dimension(25, 25));
        controlPanel.add(Box.createHorizontalStrut(25));
        controlPanel.add(currentColorPanel);
        controlPanel.add(Box.createHorizontalStrut(15));

        // Add color picker to the left
        Color[] colorList = {
                Color.BLACK, Color.GRAY, new Color(255, 102, 102), new Color(255, 178, 102),
                new Color(255, 255, 102), new Color(178, 255, 102), new Color(102, 255, 102), new Color(102, 255, 178),
                new Color(102, 255, 255), new Color(102, 178, 255), new Color(178, 102, 255), new Color(255, 102, 255),
                new Color(255, 51, 0), new Color(255, 153, 51), new Color(0, 153, 51), new Color(153, 51, 255)
        };
        JPanel colorPanel = new JPanel(new GridLayout(2, 8, 5, 5));
        colorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Colors"));
        for (Color c: colorList) {
            JButton button = new JButton();
            button.setBackground(c);
            button.setMinimumSize(new Dimension(15, 15));
            button.setPreferredSize(new Dimension(15, 15));
            button.setMaximumSize(new Dimension(15, 15));
            button.addActionListener(e -> {
                currentColorPanel.setBackground(button.getBackground());
                if (WhiteBoardGUI.getSelectedShapeButton() != null) {
                    WhiteBoardGUI.getSelectedShapeButton().getModel().setPressed(true);
                }
            });
            colorPanel.add(button);
        }
        colorPanel.setPreferredSize(new Dimension(180, 60));
        colorPanel.setMaximumSize(new Dimension(180, 60));
        controlPanel.add(colorPanel);
        controlPanel.add(Box.createHorizontalStrut(15));

        // Add shape buttons to the center
        JPanel shapePanel = new JPanel(new GridLayout(1, 5, 10, 0));
        shapePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "shape"));

        JButton lineButton = new JButton();
        ImageIcon lineIcon = new ImageIcon(getClass().getResource("/image/Line.png"));
        Image lineImage = lineIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        lineButton.setIcon(new ImageIcon(lineImage));
        lineButton.setMinimumSize(new Dimension(25, 25));
        lineButton.setPreferredSize(new Dimension(25, 25));
        lineButton.setMaximumSize(new Dimension(25, 25));

        JButton circleButton = new JButton();
        ImageIcon circleIcon = new ImageIcon(getClass().getResource("/image/Circle.png"));
        Image circleImage = circleIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        circleButton.setIcon(new ImageIcon(circleImage));
        circleButton.setMinimumSize(new Dimension(25, 25));
        circleButton.setPreferredSize(new Dimension(25, 25));
        circleButton.setMaximumSize(new Dimension(25, 25));

        JButton rectangleButton = new JButton();
        ImageIcon rectangleIcon = new ImageIcon(getClass().getResource("/image/Rectangle.png"));
        Image rectangleImage = rectangleIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        rectangleButton.setIcon(new ImageIcon(rectangleImage));
        rectangleButton.setMinimumSize(new Dimension(25, 25));
        rectangleButton.setPreferredSize(new Dimension(25, 25));
        rectangleButton.setMaximumSize(new Dimension(25, 25));

        JButton ovalButton = new JButton();
        ImageIcon ovalIcon = new ImageIcon(getClass().getResource("/image/Oval.png"));
        Image ovalImage = ovalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ovalButton.setIcon(new ImageIcon(ovalImage));
        ovalButton.setMinimumSize(new Dimension(25, 25));
        ovalButton.setPreferredSize(new Dimension(25, 25));
        ovalButton.setMaximumSize(new Dimension(25, 25));

        JButton textButton = new JButton();
        ImageIcon textIcon = new ImageIcon(getClass().getResource("/image/Text.png"));
        Image textImage = textIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        textButton.setIcon(new ImageIcon(textImage));
        textButton.setMinimumSize(new Dimension(25, 25));
        textButton.setPreferredSize(new Dimension(25, 25));
        textButton.setMaximumSize(new Dimension(25, 25));

        shapePanel.add(lineButton);
        shapePanel.add(circleButton);
        shapePanel.add(rectangleButton);
        shapePanel.add(ovalButton);
        shapePanel.add(textButton);
        shapePanel.setPreferredSize(new Dimension(200, 55));
        shapePanel.setMaximumSize(new Dimension(200, 55));
        controlPanel.add(shapePanel);
        controlPanel.add(Box.createHorizontalStrut(15));

        // Add stroke slider to the right
        JSlider strokeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 3);
        strokeSlider.setPreferredSize(new Dimension(130, 55));
        strokeSlider.setMaximumSize(new Dimension(130, 55));
        strokeSlider.setPaintTicks(true);
        strokeSlider.setMinorTickSpacing(1);
        strokeSlider.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Stroke"));
        strokeSlider.addChangeListener(e -> {
            if (WhiteBoardGUI.getSelectedShapeButton() != null) {
                WhiteBoardGUI.getSelectedShapeButton().getModel().setPressed(true);
            }
        });
        controlPanel.add(strokeSlider);
        controlPanel.add(Box.createHorizontalStrut(15));

        // create font combo box
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontComboBox = new JComboBox<>(fontNames);
        fontComboBox.setPreferredSize(new Dimension(100, 30));
        fontComboBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Font"));
        fontComboBox.setSelectedItem("Arial");
        fontComboBox.setVisible(false);
        fontComboBox.addActionListener(e -> {
            if (WhiteBoardGUI.getSelectedShapeButton() != null) {
                WhiteBoardGUI.getSelectedShapeButton().getModel().setPressed(true);
            }
        });

        // create style combo box
        String[] styles = {"Plain", "Bold", "Italic", "Bold Italic"};
        styleComboBox = new JComboBox<>(styles);
        styleComboBox.setPreferredSize(new Dimension(100, 30));
        styleComboBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Style"));
        styleComboBox.setSelectedItem("Plain");
        styleComboBox.setVisible(false);
        styleComboBox.addActionListener(e -> {
            if (WhiteBoardGUI.getSelectedShapeButton() != null) {
                WhiteBoardGUI.getSelectedShapeButton().getModel().setPressed(true);
            }
        });

        // create size combo box
        Integer[] sizes = {8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48};
        sizeComboBox = new JComboBox<>(sizes);
        sizeComboBox.setPreferredSize(new Dimension(100, 30));
        sizeComboBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Size"));
        sizeComboBox.setSelectedItem(12);
        sizeComboBox.setVisible(false);
        sizeComboBox.addActionListener(e -> {
            if (WhiteBoardGUI.getSelectedShapeButton() != null) {
                WhiteBoardGUI.getSelectedShapeButton().getModel().setPressed(true);
            }
        });

        JPanel textOptionPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        textOptionPanel.add(fontComboBox);
        textOptionPanel.add(styleComboBox);
        textOptionPanel.add(sizeComboBox);
        textOptionPanel.setPreferredSize(new Dimension(320, 45));
        textOptionPanel.setMaximumSize(new Dimension(320, 45));
        textOptionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        controlPanel.add(textOptionPanel);
        controlPanel.add(Box.createHorizontalGlue());

        // labels of buttons
        JLabel statusLabel = new JLabel();
        statusLabel.setFont(new Font("Serif", Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setVerticalAlignment(JLabel.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(255, 255, 192, 255));
        Border border = BorderFactory.createLineBorder(new Color(226, 226, 218, 255), 2, true);
        statusLabel.setBorder(border);
        statusLabel.setVisible(false);
        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.add(statusLabel, JLayeredPane.POPUP_LAYER);

        // draw line button
        lineButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON1 && !lineButton.getModel().isPressed()) {
                    WhiteBoardGUI.setTextComboBoxVisible(false);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    if (selectedShapeButton != null) {
                        selectedShapeButton.getModel().setPressed(false);
                        selectedShapeButton.getModel().setArmed(false);
                        WhiteBoardGUI.setSelectedShapeButton(null);
                    }
                    lineButton.getModel().setPressed(true);
                    WhiteBoardGUI.setSelectedShapeButton(lineButton);
                    for (MouseListener listener : canvas.getMouseListeners()) {
                        canvas.removeMouseListener(listener);
                    }
                    for (MouseMotionListener listener : canvas.getMouseMotionListeners()) {
                        canvas.removeMouseMotionListener(listener);
                    }
                    canvas.addMouseListener(new DrawLineMouseListener((Graphics2D) canvas.getGraphics(), canvas, currentColorPanel, strokeSlider, lineButton, serverService));
                    canvas.addMouseMotionListener(new DrawLineMouseListener((Graphics2D) canvas.getGraphics(), canvas, currentColorPanel, strokeSlider, lineButton, serverService));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                statusLabel.setText("Line");
                Point lineButtonPoint = new Point(lineButton.getLocationOnScreen().x + lineButton.getWidth(),
                        lineButton.getLocationOnScreen().y + lineButton.getHeight());
                SwingUtilities.convertPointFromScreen(lineButtonPoint, getContentPane());
                int x = lineButtonPoint.x - 36;
                int y = lineButtonPoint.y + 30;
                statusLabel.setBounds(x, y, 44, 28);
                statusLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                statusLabel.setText("");
                statusLabel.setVisible(false);
            }
        });

        // draw circle button
        circleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON1 && !circleButton.getModel().isPressed()) {
                    WhiteBoardGUI.setTextComboBoxVisible(false);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    if (selectedShapeButton != null) {
                        selectedShapeButton.getModel().setPressed(false);
                        selectedShapeButton.getModel().setArmed(false);
                        WhiteBoardGUI.setSelectedShapeButton(null);
                    }
                    circleButton.getModel().setPressed(true);
                    WhiteBoardGUI.setSelectedShapeButton(circleButton);
                    for (MouseListener listener : canvas.getMouseListeners()) {
                        canvas.removeMouseListener(listener);
                    }
                    for (MouseMotionListener listener : canvas.getMouseMotionListeners()) {
                        canvas.removeMouseMotionListener(listener);
                    }
                    canvas.addMouseListener(new DrawCircleMouseListener((Graphics2D) canvas.getGraphics(), canvas, currentColorPanel, strokeSlider, circleButton, serverService));
                    canvas.addMouseMotionListener(new DrawCircleMouseListener((Graphics2D) canvas.getGraphics(), canvas, currentColorPanel, strokeSlider, circleButton, serverService));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                statusLabel.setText("Circle");
                Point circleButtonPoint = new Point(circleButton.getLocationOnScreen().x + circleButton.getWidth(),
                        circleButton.getLocationOnScreen().y + circleButton.getHeight());
                SwingUtilities.convertPointFromScreen(circleButtonPoint, getContentPane());
                int x = circleButtonPoint.x - 38;
                int y = circleButtonPoint.y + 30;
                statusLabel.setBounds(x, y, 50, 28);
                statusLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                statusLabel.setText("");
                statusLabel.setVisible(false);
            }
        });

        // draw rectangle button
        rectangleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON1 && !rectangleButton.getModel().isPressed()) {
                    WhiteBoardGUI.setTextComboBoxVisible(false);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    if (selectedShapeButton != null) {
                        selectedShapeButton.getModel().setPressed(false);
                        selectedShapeButton.getModel().setArmed(false);
                        WhiteBoardGUI.setSelectedShapeButton(null);
                    }
                    rectangleButton.getModel().setPressed(true);
                    WhiteBoardGUI.setSelectedShapeButton(rectangleButton);
                    for (MouseListener listener : canvas.getMouseListeners()) {
                        canvas.removeMouseListener(listener);
                    }
                    for (MouseMotionListener listener : canvas.getMouseMotionListeners()) {
                        canvas.removeMouseMotionListener(listener);
                    }

                    canvas.addMouseListener(new DrawRectangleMouseListener((Graphics2D) canvas.getGraphics(), canvas, currentColorPanel, strokeSlider, rectangleButton, serverService));
                    canvas.addMouseMotionListener(new DrawRectangleMouseListener((Graphics2D) canvas.getGraphics(), canvas, currentColorPanel, strokeSlider, rectangleButton, serverService));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                statusLabel.setText("Rectangle");
                Point rectangleButtonPoint = new Point(rectangleButton.getLocationOnScreen().x + rectangleButton.getWidth(),
                        rectangleButton.getLocationOnScreen().y + rectangleButton.getHeight());
                SwingUtilities.convertPointFromScreen(rectangleButtonPoint, getContentPane());
                int x = rectangleButtonPoint.x - 45;
                int y = rectangleButtonPoint.y + 30;
                statusLabel.setBounds(x, y, 74, 28);
                statusLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                statusLabel.setText("");
                statusLabel.setVisible(false);
            }
        });

        // draw oval button
        ovalButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON1 && !ovalButton.getModel().isPressed()) {
                    WhiteBoardGUI.setTextComboBoxVisible(false);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    if (selectedShapeButton != null) {
                        selectedShapeButton.getModel().setPressed(false);
                        selectedShapeButton.getModel().setArmed(false);
                        WhiteBoardGUI.setSelectedShapeButton(null);
                    }
                    ovalButton.getModel().setPressed(true);
                    WhiteBoardGUI.setSelectedShapeButton(ovalButton);
                    for (MouseListener listener : canvas.getMouseListeners()) {
                        canvas.removeMouseListener(listener);
                    }
                    for (MouseMotionListener listener : canvas.getMouseMotionListeners()) {
                        canvas.removeMouseMotionListener(listener);
                    }
                    canvas.addMouseListener(new DrawOvalMouseListener((Graphics2D) canvas.getGraphics(), canvas, currentColorPanel, strokeSlider, ovalButton, serverService));
                    canvas.addMouseMotionListener(new DrawOvalMouseListener((Graphics2D) canvas.getGraphics(), canvas, currentColorPanel, strokeSlider, ovalButton, serverService));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                statusLabel.setText("Oval");
                Point ovalButtonPoint = new Point(ovalButton.getLocationOnScreen().x + ovalButton.getWidth(),
                        ovalButton.getLocationOnScreen().y + ovalButton.getHeight());
                SwingUtilities.convertPointFromScreen(ovalButtonPoint, getContentPane());
                int x = ovalButtonPoint.x - 36;
                int y = ovalButtonPoint.y + 30;
                statusLabel.setBounds(x, y, 44, 28);
                statusLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                statusLabel.setText("");
                statusLabel.setVisible(false);
            }
        });

        // draw text button
        textButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                statusLabel.setText("");
                statusLabel.setVisible(false);
                for (MouseListener listener : canvas.getMouseListeners()) {
                    canvas.removeMouseListener(listener);
                }
                for (MouseMotionListener listener : canvas.getMouseMotionListeners()) {
                    canvas.removeMouseMotionListener(listener);
                }
                if (e.getButton() == MouseEvent.BUTTON1 && !textButton.getModel().isPressed()) {
                    if (selectedShapeButton != null) {
                        selectedShapeButton.getModel().setPressed(false);
                        selectedShapeButton.getModel().setArmed(false);
                        WhiteBoardGUI.setSelectedShapeButton(null);
                    }
                    String text = JOptionPane.showInputDialog(canvas, "Please enter text: ", "Text Input Dialog", JOptionPane.PLAIN_MESSAGE);
                    textButton.getModel().setPressed(true);
                    if (text != null && !text.equals("")) {
                        WhiteBoardGUI.setTextComboBoxVisible(true);
                        WhiteBoardGUI.setSelectedShapeButton(textButton);
                        canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                        canvas.addMouseListener(new DrawTextFieldMouseListener((Graphics2D) canvas.getGraphics(), canvas, text, currentColorPanel, fontComboBox, styleComboBox, sizeComboBox, textButton, serverService));
                        canvas.addMouseMotionListener(new DrawTextFieldMouseListener((Graphics2D) canvas.getGraphics(), canvas, text, currentColorPanel, fontComboBox, styleComboBox, sizeComboBox, textButton, serverService));
                    }
                    else {
                        canvas.setCursor(Cursor.getDefaultCursor());
                        textButton.getModel().setPressed(false);
                        textButton.getModel().setArmed(false);
                        WhiteBoardGUI.setTextComboBoxVisible(false);
                        WhiteBoardGUI.setSelectedShapeButton(null);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                statusLabel.setText("Text");
                Point textButtonPoint = new Point(textButton.getLocationOnScreen().x + textButton.getWidth(),
                        textButton.getLocationOnScreen().y + textButton.getHeight());
                SwingUtilities.convertPointFromScreen(textButtonPoint, getContentPane());
                int x = textButtonPoint.x - 36;
                int y = textButtonPoint.y + 30;
                statusLabel.setBounds(x, y, 44, 28);
                statusLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                statusLabel.setText("");
                statusLabel.setVisible(false);
            }
        });

        // userPanel and userScrollPane on the right up
        userPanel= new JPanel();
        userPanel.setBackground(Color.WHITE);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        JScrollPane userScrollPane = new JScrollPane(userPanel);
        userScrollPane.setBorder(BorderFactory.createTitledBorder("Current User List"));
        userScrollPane.setPreferredSize(new Dimension(200, 200));
        printUserNameList();

        // chatTextArea and chatScrollPane on the right center
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        chatTextArea.setBorder(BorderFactory.createTitledBorder("Chat room"));
        JScrollPane chatScrollPane = new JScrollPane(chatTextArea);



        // chatInputPanel on the right bottom
        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.setBorder(BorderFactory.createEtchedBorder());
        JTextField chatInputField = new JTextField(10);
        JButton chatSendButton = new JButton("Send");
        chatSendButton.addActionListener(e -> {
            String currentMessage = chatInputField.getText();
            if (currentMessage != null && !currentMessage.equals("")){
                String chatMessage = getUserName() + ": " + currentMessage + "\n";
                if (isManager) {
                    printChatMessage(chatMessage);
                }
                else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("request", "addChatMessage");
                    jsonObject.put("chatMessage", chatMessage);
                    sendEncrypted(jsonObject.toJSONString(), writer);
                }
                try {
                    serverService.addChatMessage(chatMessage);
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
                chatInputField.setText("");
            }
        });

        chatInputPanel.add(chatInputField, BorderLayout.CENTER);
        chatInputPanel.add(chatSendButton, BorderLayout.EAST);

        // chatPanel contains user/chat/chatInput panel
        JPanel chatPanel = new JPanel(new BorderLayout());
        JSplitPane userchatSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, userScrollPane, chatScrollPane);
        userchatSplitPane.setDividerSize(4);
        chatPanel.add(userchatSplitPane, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        // split pane of canvas and chat
        JSplitPane canvaschatSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasPanel, chatPanel);
        canvaschatSplitPane.setDividerSize(6);
        canvaschatSplitPane.setResizeWeight(0.99);
        BasicSplitPaneUI ui = (BasicSplitPaneUI) canvaschatSplitPane.getUI();
        ui.getDivider().setBackground(Color.LIGHT_GRAY);
        ui.getDivider().setBorder(null);

        // mainPanel add
        mainPanel.add(canvaschatSplitPane, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        setContentPane(mainPanel);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem saveAsMenuItem = new JMenuItem("Save As ...");
        JMenuItem closeMenuItem = new JMenuItem("Close");

        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(WhiteBoardGUI.this, "Are you sure you want to create a new white board?", "Confirm create", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    currentJsonFile = null;
                    shapeList.clear();
                    canvasRepaint();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("response", "newWhiteBoard");
                    for (User user : userList) {
                        sendEncrypted(jsonObject.toJSONString(), user.getWriter());
                    }
                }
            }
        });

        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Open");
                fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
                if (fileChooser.showOpenDialog(WhiteBoardGUI.this) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try (FileReader fileReader = new FileReader(selectedFile)) {
                        JSONArray jsonArray = (JSONArray) new JSONParser().parse(fileReader);
                        shapeList.clear();
                        for (Object obj : jsonArray) {
                            Shape shape = parseShape((JSONObject) obj);
                            if (shape != null) {
                                shapeList.add(shape);
                            }
                        }
                        canvasRepaint();
                        currentJsonFile = selectedFile;

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("response", "openWhiteBoard");
                        for (User user : userList) {
                            sendEncrypted(jsonObject.toJSONString(), user.getWriter());
                        }
                    } catch (IOException | ParseException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentJsonFile == null) {
                    saveAsMenuItem.doClick();
                }
                else {
                    try (FileWriter fileWriter = new FileWriter(currentJsonFile)) {
                        JSONArray jsonArray = new JSONArray();
                        for (Shape shape : shapeList) {
                            jsonArray.add(shape.toJSONObject());
                        }
                        fileWriter.write(jsonArray.toJSONString());

                        File pngFile = new File(currentJsonFile.getParent(), currentJsonFile.getName().replace(".json", ".png"));
                        BufferedImage image = canvas.createCanvasImage();
                        ImageIO.write(image, "png", pngFile);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        });

        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("new.json"));
                fileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".json");
                    }

                    @Override
                    public String getDescription() {
                        return "JSON (*.json)";
                    }
                });
                if (fileChooser.showSaveDialog(WhiteBoardGUI.this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File jsonFile = fileChooser.getSelectedFile();
                File pngFile = new File(jsonFile.getParent(), jsonFile.getName().replace(".json", ".png"));
                BufferedImage image = canvas.createCanvasImage();
                try (FileWriter fileWriter = new FileWriter(jsonFile)) {
                    // write a json file
                    JSONArray jsonArray = new JSONArray();
                    for (Shape shape : shapeList) {
                        jsonArray.add(shape.toJSONObject());
                    }
                    fileWriter.write(jsonArray.toJSONString());
                    currentJsonFile = jsonFile;
                    // write an image file
                    ImageIO.write(image, "png", pngFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        closeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(WhiteBoardGUI.this, "Are you sure you want to quit?", "Confirm quit", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    for (User user : userList) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("response", "quit");
                        sendEncrypted(jsonObject.toJSONString(), user.getWriter());
                    }
                    System.exit(0);
                }
            }
        });

        if (!isManager) {
            newMenuItem.setEnabled(false);
            openMenuItem.setEnabled(false);
            saveMenuItem.setEnabled(false);
            saveAsMenuItem.setEnabled(false);
            closeMenuItem.setEnabled(false);
        }

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(closeMenuItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);


        setSize(1300, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(WhiteBoardGUI.this, "Are you sure you want to quit?", "Confirm quit", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    if (isManager) {
                        for (User user : userList) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("response", "quit");
                            sendEncrypted(jsonObject.toJSONString(), user.getWriter());
                        }
                    }
                    else {
                        try {
                            serverService.removeUserName(WhiteBoardGUI.getUserName());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("request", "quit");
                            sendEncrypted(jsonObject.toJSONString(), writer);
                        } catch (RemoteException re) {
                            re.printStackTrace();
                        }
                    }
                    System.exit(0);
                }
            }
        });
        setVisible(true);
    }

    public static void setSelectedShapeButton(JButton button) {
        selectedShapeButton = button;
    }

    public static JButton getSelectedShapeButton() {
        return selectedShapeButton;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setTextComboBoxVisible(boolean visible) {
        fontComboBox.setVisible(visible);
        styleComboBox.setVisible(visible);
        sizeComboBox.setVisible(visible);
    }

    public synchronized void canvasRepaint() {
        canvas.repaint();
    }


    public void clearCanvas() {
        Graphics2D g2d = (Graphics2D) canvas.getGraphics();
        g2d.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public synchronized void printUserNameList() {
        userPanel.removeAll();
        userPanel.revalidate();

        int userCount = 0;
        for (String userName : userNameList) {
            JPanel userRowPanel = new JPanel();
            userRowPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE),
                    BorderFactory.createEmptyBorder(5, 0, 5, 0)
            ));
            userRowPanel.setBackground(Color.LIGHT_GRAY);
            userRowPanel.setLayout(new BoxLayout(userRowPanel, BoxLayout.X_AXIS));
            userRowPanel.setPreferredSize(new Dimension(150, 35));
            userRowPanel.setMaximumSize(new Dimension(3000, 35));

            JLabel userNameLabel = new JLabel(userName);
            userRowPanel.add(userNameLabel);

            if (userCount == 0) {
                JLabel managerLabel = new JLabel("[Manager]");
                userRowPanel.add(Box.createHorizontalGlue());
                userRowPanel.add(managerLabel);
                userRowPanel.add(Box.createHorizontalStrut(10));
            }

            if(isManager && userCount > 0) {
                JButton viewButton = new JButton("View");
                Insets insets = new Insets(2, 1, 2, 1);
                viewButton.setMargin(insets);
                viewButton.setPreferredSize(new Dimension(36, 20));
                viewButton.setMaximumSize(new Dimension(36, 20));
                viewButton.addActionListener(e -> {
                    String userNameLabelText = userNameLabel.getText();
                    for (User user : userList) {
                        if (user.getUserName().equals(userNameLabelText)) {
                            JOptionPane.showMessageDialog(null, "User name:                 " + user.getUserName() + System.lineSeparator() + "User IP address:       "+ user.getIPAddress() + System.lineSeparator() + "User port number:    "+ user.getPort(),
                                    "View User Information", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                    }
                });

                JButton kickButton = new JButton("Kick");
                kickButton.setMargin(insets);
                kickButton.setPreferredSize(new Dimension(36, 20));
                kickButton.setMaximumSize(new Dimension(36, 20));
                kickButton.addActionListener(e -> {
                    String userNameLabelText = userNameLabel.getText();
                    String[] buttonLabels = {"Yes", "No"};
                    int option = JOptionPane.showOptionDialog(null, "Are you sure you want to kick this user?", "Confirm Kick",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttonLabels, buttonLabels[0]);
                    if (option == JOptionPane.YES_OPTION) {
                        User toRemovedUser = null;
                        for (User user : userList) {
                            if (user.getUserName().equals(userNameLabelText)) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("response", "kick");
                                sendEncrypted(jsonObject.toJSONString(), user.getWriter());
                                toRemovedUser = user;
                            }
                        }
                        try {
                            serverService.removeUserName(userNameLabelText);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                        if (toRemovedUser != null) {
                            userList.remove(toRemovedUser);
                        }
                        printUserNameList();
                    }
                });

                userRowPanel.add(Box.createHorizontalGlue());
                userRowPanel.add(viewButton);
                userRowPanel.add(Box.createHorizontalStrut(5));
                userRowPanel.add(kickButton);
                userRowPanel.add(Box.createHorizontalStrut(10));
            }
            userPanel.add(userRowPanel);
            userCount++;
        }
        userPanel.repaint();
        userPanel.revalidate();
    }

    public synchronized void printChatMessage(String newChatMessage) {
        chatTextArea.append(newChatMessage);
        chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
    }

    private static Shape parseShape(JSONObject shapeObject) {
        String shapeType = (String) shapeObject.get("type");
        return switch (shapeType) {
            case "Line" ->
                    new Line(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(),
                            ((Long) shapeObject.get("x2")).intValue(), ((Long) shapeObject.get("y2")).intValue(),
                            new Color(((Long) shapeObject.get("colorValue")).intValue()), ((Long) shapeObject.get("strokeValue")).intValue());
            case "Circle" ->
                    new Circle(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(), ((Long) shapeObject.get("diameter")).intValue(),
                            new Color(((Long) shapeObject.get("colorValue")).intValue()), ((Long) shapeObject.get("strokeValue")).intValue());
            case "Oval" ->
                    new Oval(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(),
                            ((Long) shapeObject.get("width")).intValue(), ((Long) shapeObject.get("height")).intValue(),
                            new Color(((Long) shapeObject.get("colorValue")).intValue()), ((Long) shapeObject.get("strokeValue")).intValue());
            case "Rectangle" ->
                    new Rectangle(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(),
                            ((Long) shapeObject.get("width")).intValue(), ((Long) shapeObject.get("height")).intValue(),
                            new Color(((Long) shapeObject.get("colorValue")).intValue()), ((Long) shapeObject.get("strokeValue")).intValue());
            case "Text" ->
                    new Text(((Long) shapeObject.get("x1")).intValue(), ((Long) shapeObject.get("y1")).intValue(),
                            (String) shapeObject.get("text"), new Color(((Long) shapeObject.get("colorValue")).intValue()),
                            (String) shapeObject.get("fontName"), ((Long) shapeObject.get("fontStyle")).intValue(), ((Long) shapeObject.get("fontSize")).intValue());
            default -> null;
        };
    }

    public static void sendEncrypted(String message, BufferedWriter writer){
        String key = "5v8y/B?D(G+KbPeS";
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(message.getBytes("UTF-8"));
            String encodedText = Base64.getEncoder().encodeToString(encrypted);
            writer.write(encodedText + System.lineSeparator());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

