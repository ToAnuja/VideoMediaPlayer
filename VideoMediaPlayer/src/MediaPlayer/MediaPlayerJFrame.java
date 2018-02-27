/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MediaPlayer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.ui.RefineryUtilities;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 *
 * @author root
 */
public class MediaPlayerJFrame extends javax.swing.JFrame {

    private VideoDisplayThread currThread;
    private final String pathNAS;
    private VideoCapture capture = null;
    private Size s = null;
    private String inputVideoPath;
    private int rowCount = -1;

    /**
     * Creates new form MediaPlayerJFrame
     *
     * @param filePath
     * @param _pathNAS
     */
    public MediaPlayerJFrame(String filePath, String _pathNAS, String playListFlg) {
        initComponents();

        pathNAS = _pathNAS;
        inputVideoPath = filePath;

        jPrevButton.setText("");
        jNextButton.setText("");
        jRunningButton.setText("");
        jStopButton.setText("");
        jCutButton.setText("");
        jErrorLabel.setText("");
        jPlayListButton.setText("");

        jPrevButton.setToolTipText("Stepped Down");
        jNextButton.setToolTipText("Stepped Up");
        jRunningButton.setToolTipText("Play/Pause");
        jStopButton.setToolTipText("Stop");
        jCutButton.setToolTipText("Video Cutter");
        jPrevPlayButton.setToolTipText("Previous");
        jNextPlayButton.setToolTipText("Next");
        jPlayListButton.setToolTipText("View Play List");

        jListMenuItem.setIcon(new javax.swing.ImageIcon("Icons/playList.png"));
        jExitMenuItem.setIcon(new javax.swing.ImageIcon("Icons/exit.png"));

        jPrevButton.setIcon(new javax.swing.ImageIcon("Icons/left.png"));
        jNextButton.setIcon(new javax.swing.ImageIcon("Icons/right.png"));
        jRunningButton.setIcon(new javax.swing.ImageIcon("Icons/run.jpg"));
        jStopButton.setIcon(new javax.swing.ImageIcon("Icons/stop.png"));
        jCutButton.setIcon(new javax.swing.ImageIcon("Icons/cut.png"));
        jPrevPlayButton.setIcon(new javax.swing.ImageIcon("Icons/prev.png"));
        jNextPlayButton.setIcon(new javax.swing.ImageIcon("Icons/next.png"));
        jPlayListButton.setIcon(new javax.swing.ImageIcon("Icons/playList.png"));

        jStartTextField.addKeyListener(keyListener);
        jEndTextField.addKeyListener(keyListener);

        if (playListFlg.equals("1")) {
            readPlayListFile();
            jPlayListButton.setEnabled(true);
        } else {
            jPlayListButton.setEnabled(false);
        }
        onVideoFileLoad();
    }

    private void readPlayListFile() {

        File outfile = new File("playList.txt");
        try (Scanner opnScanner = new Scanner(outfile)) {

            int cnt = 1;
            DefaultTableModel model = (DefaultTableModel) jPlayListTable.getModel();
            while (opnScanner.hasNext()) {
                Vector<String> fileList = new Vector();
                String cntStr = "  " + cnt;
                fileList.add(cntStr);

                String line = opnScanner.nextLine();
                fileList.add(line);

                model.addRow(fileList);
                cnt++;
            }
            opnScanner.close();
        } catch (FileNotFoundException ex) {

        }
    }

    private void onVideoFileLoad() {

        jNextButton.setEnabled(false);
        jPrevButton.setEnabled(false);

        if (capture != null && currThread != null) {

            while (!currThread.getState().name().equals("TERMINATED")) {
                currThread.setResetFlag();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MediaPlayerJFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            currThread = null;
            capture = null;
        }

        File f = new File(inputVideoPath);

        if (f.exists()) {
            jFilePathLabel.setText(f.getName());
            capture = new VideoCapture(inputVideoPath);

            int width = jImageLabel.getWidth();
            int height = jImageLabel.getHeight();
            s = new Size(width, height);

            int fps = (int) capture.get(Videoio.CAP_PROP_FPS);
            int code = (int) capture.get(Videoio.CV_CAP_PROP_FOURCC);
            int total = (int) capture.get(Videoio.CAP_PROP_FRAME_COUNT);
            int curFrameNo = (int) capture.get(Videoio.CV_CAP_PROP_POS_FRAMES);

            String fourcc;
            if (code != 0) {
                fourcc = fourCCStringFromCode(code).toUpperCase();
            } else {
                fourcc = extnOfSelImgFile(inputVideoPath).toUpperCase();
            }

            jFourccValLabel.setText(fourcc);
            jFPSValLabel.setText("" + fps);
            jTotalFramesLabel.setText("" + total);
            jCurFrameLabel.setText("" + curFrameNo);
            jSeekSlider.setMaximum(total);

            CommFunction commObj = new CommFunction();
            String TimeStr = commObj.frameNoToTime(total, fps);
            jVideoLenLabel.setText(TimeStr);

            playButtonClicked();
        } else {
            jErrorLabel.setText("Input video is not available");
        }
    }

    private String extnOfSelImgFile(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i > 0) {
            return fileName.substring(i + 1);
        }
        return "";
    }

    private final KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!((c >= '0' && c <= '9') || (c == KeyEvent.VK_BACK_SLASH)
                    || (c == KeyEvent.VK_DELETE))) {
                getToolkit().beep();
                e.consume();
            }
        }
    };

    private String fourCCStringFromCode(int code) {
        //return format("%c%c%c%c", fourcc & 255, (fourcc >> 8) & 255, (fourcc >> 16) & 255, (fourcc >> 24) & 255);

        byte[] fourCC = new byte[5];
        fourCC[0] = (byte) (code & 255);
        fourCC[1] = (byte) ((code >> 8) & 255);
        fourCC[2] = (byte) ((code >> 16) & 255);
        fourCC[3] = (byte) ((code >> 24) & 255);

        return new String(fourCC).trim();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPlayListFrame = new javax.swing.JFrame();
        jListScrollPanel = new javax.swing.JScrollPane();
        jPlayListTable = new javax.swing.JTable();
        jPrevPlayButton = new javax.swing.JButton();
        jNextPlayButton = new javax.swing.JButton();
        jImageLabel = new javax.swing.JLabel();
        jSeekSlider = new javax.swing.JSlider();
        jToolPanel = new javax.swing.JPanel();
        jRunningButton = new javax.swing.JButton();
        jPrevButton = new javax.swing.JButton();
        jNextButton = new javax.swing.JButton();
        jStopButton = new javax.swing.JButton();
        jPlayListButton = new javax.swing.JButton();
        jFourccTitleLabel = new javax.swing.JLabel();
        jFourccValLabel = new javax.swing.JLabel();
        jFPSTitleLabel = new javax.swing.JLabel();
        jFPSValLabel = new javax.swing.JLabel();
        jTotalFrameTitleLabel = new javax.swing.JLabel();
        jTotalFramesLabel = new javax.swing.JLabel();
        jVideoFileLabel = new javax.swing.JLabel();
        jFilePathLabel = new javax.swing.JLabel();
        jCurrFrameTitleLabel = new javax.swing.JLabel();
        jCurFrameLabel = new javax.swing.JLabel();
        jStatusLabel = new javax.swing.JLabel();
        jStatusTxtLabel = new javax.swing.JLabel();
        jVideoLenTitleLabel = new javax.swing.JLabel();
        jVideoLenLabel = new javax.swing.JLabel();
        jStartFrameLabel = new javax.swing.JLabel();
        jStartTextField = new javax.swing.JTextField();
        jEndFrameTitleLabel = new javax.swing.JLabel();
        jEndTextField = new javax.swing.JTextField();
        jCutButton = new javax.swing.JButton();
        jErrorLabel = new javax.swing.JLabel();
        jMainMenuBar = new javax.swing.JMenuBar();
        jViewMenu = new javax.swing.JMenu();
        jListMenuItem = new javax.swing.JMenuItem();
        jCloseMenu = new javax.swing.JMenu();
        jExitMenuItem = new javax.swing.JMenuItem();

        jPlayListFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPlayListFrame.setTitle("PlayList"); // NOI18N
        jPlayListFrame.setMinimumSize(new java.awt.Dimension(594, 370));
        jPlayListFrame.setPreferredSize(new java.awt.Dimension(594, 370));
        jPlayListFrame.setResizable(false);

        jPlayListTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPlayListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SNo.", "VideoFile  Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jPlayListTable.setToolTipText("Video Play List Table");
        jPlayListTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPlayListTableMouseClicked(evt);
            }
        });
        jListScrollPanel.setViewportView(jPlayListTable);
        int width[] = {10, 200};
        for(int col=0; col<jPlayListTable.getColumnCount();col++){
            jPlayListTable.getColumnModel().getColumn(col).setPreferredWidth(width[col]);
        }

        jPrevPlayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPrevPlayButtonActionPerformed(evt);
            }
        });

        jNextPlayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNextPlayButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPlayListFrameLayout = new javax.swing.GroupLayout(jPlayListFrame.getContentPane());
        jPlayListFrame.getContentPane().setLayout(jPlayListFrameLayout);
        jPlayListFrameLayout.setHorizontalGroup(
            jPlayListFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jListScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
            .addGroup(jPlayListFrameLayout.createSequentialGroup()
                .addGap(239, 239, 239)
                .addComponent(jPrevPlayButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNextPlayButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPlayListFrameLayout.setVerticalGroup(
            jPlayListFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPlayListFrameLayout.createSequentialGroup()
                .addComponent(jListScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayListFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jNextPlayButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPrevPlayButton, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Play Video");
        setResizable(false);

        jSeekSlider.setMajorTickSpacing(1);
        jSeekSlider.setPaintTicks(true);
        jSeekSlider.setValue(0);
        jSeekSlider.setPreferredSize(new java.awt.Dimension(424, 64));
        jSeekSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSeekSliderStateChanged(evt);
            }
        });
        jSeekSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSeekSliderMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSeekSliderMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jSeekSliderMouseClicked(evt);
            }
        });

        jRunningButton.setText("Play");
        jRunningButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRunningButtonActionPerformed(evt);
            }
        });

        jPrevButton.setText("Prev");
        jPrevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPrevButtonActionPerformed(evt);
            }
        });

        jNextButton.setText("Next");
        jNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNextButtonActionPerformed(evt);
            }
        });

        jStopButton.setText("Stop");
        jStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStopButtonActionPerformed(evt);
            }
        });

        jPlayListButton.setText("PlayList");
        jPlayListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPlayListButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jToolPanelLayout = new javax.swing.GroupLayout(jToolPanel);
        jToolPanel.setLayout(jToolPanelLayout);
        jToolPanelLayout.setHorizontalGroup(
            jToolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jToolPanelLayout.createSequentialGroup()
                .addComponent(jPrevButton, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRunningButton, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPlayListButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jNextButton, jPlayListButton, jPrevButton, jRunningButton, jStopButton});

        jToolPanelLayout.setVerticalGroup(
            jToolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jToolPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jToolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRunningButton)
                    .addComponent(jNextButton)
                    .addComponent(jPrevButton)
                    .addComponent(jStopButton)
                    .addComponent(jPlayListButton))
                .addContainerGap())
        );

        jToolPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jNextButton, jPlayListButton, jPrevButton, jRunningButton, jStopButton});

        jFourccTitleLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jFourccTitleLabel.setText("FOURCC:");
        jFourccTitleLabel.setToolTipText("Video Format");

        jFourccValLabel.setText("H264");

        jFPSTitleLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jFPSTitleLabel.setText("FPS:");
        jFPSTitleLabel.setToolTipText("Frame Per Second");

        jFPSValLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jFPSValLabel.setText("0");

        jTotalFrameTitleLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jTotalFrameTitleLabel.setText("Total Frames:");

        jTotalFramesLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jTotalFramesLabel.setText("0");

        jVideoFileLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jVideoFileLabel.setText("Video File:");

        jFilePathLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jFilePathLabel.setForeground(new java.awt.Color(5, 15, 169));
        jFilePathLabel.setText("File to play");

        jCurrFrameTitleLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jCurrFrameTitleLabel.setText("Current Frame:");

        jCurFrameLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jCurFrameLabel.setText("0");

        jStatusLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jStatusLabel.setText("Status:");

        jStatusTxtLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jStatusTxtLabel.setText("Stopped");

        jVideoLenTitleLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jVideoLenTitleLabel.setText("Video Length:");
        jVideoLenTitleLabel.setToolTipText("Total time of video play");

        jVideoLenLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jVideoLenLabel.setText("0");

        jStartFrameLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jStartFrameLabel.setText("Start Frame:");

        jStartTextField.setText("0");

        jEndFrameTitleLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jEndFrameTitleLabel.setText("End Frame:");

        jEndTextField.setText("0");

        jCutButton.setText("Cut");
        jCutButton.setToolTipText("Video Cut ");
        jCutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCutButtonActionPerformed(evt);
            }
        });

        jErrorLabel.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jErrorLabel.setForeground(new java.awt.Color(226, 14, 11));
        jErrorLabel.setText("ERROR:");

        jViewMenu.setText("View");
        jViewMenu.setToolTipText("View Video Playlist");
        jViewMenu.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N

        jListMenuItem.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jListMenuItem.setText("PlayList");
        jListMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jListMenuItemActionPerformed(evt);
            }
        });
        jViewMenu.add(jListMenuItem);

        jMainMenuBar.add(jViewMenu);

        jCloseMenu.setText("Close");
        jCloseMenu.setToolTipText("Close Video Media Player");
        jCloseMenu.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N

        jExitMenuItem.setFont(new java.awt.Font("Cantarell", 1, 15)); // NOI18N
        jExitMenuItem.setText("Exit");
        jCloseMenu.add(jExitMenuItem);

        jMainMenuBar.add(jCloseMenu);

        setJMenuBar(jMainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jImageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeekSlider, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jVideoFileLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFilePathLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jStartFrameLabel)
                                .addGap(2, 2, 2)
                                .addComponent(jStartTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jEndFrameTitleLabel)
                                .addGap(2, 2, 2)
                                .addComponent(jEndTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(293, 293, 293)
                                        .addComponent(jToolPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jTotalFrameTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jCurrFrameTitleLabel))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jCurFrameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTotalFramesLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jStatusLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jStatusTxtLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(70, 70, 70)
                                .addComponent(jErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jVideoLenTitleLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(jVideoLenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jFourccTitleLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(jFourccValLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jFPSTitleLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jFPSValLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addGap(6, 6, 6))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jEndTextField, jStartTextField});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeekSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCurrFrameTitleLabel)
                            .addComponent(jCurFrameLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTotalFrameTitleLabel)
                            .addComponent(jTotalFramesLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jStatusLabel)
                            .addComponent(jStatusTxtLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jFPSValLabel)
                            .addComponent(jFPSTitleLabel)
                            .addComponent(jFourccValLabel)
                            .addComponent(jFourccTitleLabel))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jVideoLenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jVideoLenTitleLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToolPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jStartTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jStartFrameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jEndFrameTitleLabel)
                        .addComponent(jEndTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jErrorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jVideoFileLabel)
                        .addComponent(jFilePathLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jEndTextField, jStartTextField});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRunningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRunningButtonActionPerformed
        // TODO add your handling code here:
        playButtonClicked();
    }//GEN-LAST:event_jRunningButtonActionPerformed

    private void playButtonClicked() {

        if (currThread != null && jStatusTxtLabel.getText().equals("Stopped")) {
            currThread.setRestartPlay();
            jRunningButton.setIcon(new javax.swing.ImageIcon("Icons/pause.jpg"));
            jRunningButton.setToolTipText("Pause");
            jStatusTxtLabel.setText("Running");
            jPrevButton.setEnabled(false);
            jNextButton.setEnabled(false);
            return;
        }

        if (currThread != null && jStatusTxtLabel.getText().equals("Running")) {
            jRunningButton.setIcon(new javax.swing.ImageIcon("Icons/run.jpg"));
            jRunningButton.setToolTipText("Play");

            jStatusTxtLabel.setText("Paused");

            jPrevButton.setEnabled(true);
            jNextButton.setEnabled(true);

            currThread.setPauseFlag(true);
            return;
        }

        jRunningButton.setIcon(new javax.swing.ImageIcon("Icons/pause.jpg"));
        jRunningButton.setToolTipText("Pause");
        jStatusTxtLabel.setText("Running");

        if (currThread == null) {
            currThread = new VideoDisplayThread(capture, s, jSeekSlider, jImageLabel,
                    jCurFrameLabel, jStatusTxtLabel, jRunningButton);
            currThread.start();
        } else {
            if (currThread.getState().name().equals("TIMED_WAITING")) {
                currThread.setPauseFlag(false);
            }
        }
    }
    private void jPrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPrevButtonActionPerformed
        // TODO add your handling code here:
        String status = jStatusTxtLabel.getText().trim();
        if (!status.equals("Stopped") && !status.equals("Running")) {
            int currFrame = Integer.parseInt(jCurFrameLabel.getText().trim());
            if (currFrame != 0) {
                jStatusTxtLabel.setText("Stepped Down");
                if (currThread != null) {
                    currThread.downCurrFrameNo();
                }
            }
        }
    }//GEN-LAST:event_jPrevButtonActionPerformed

    private void jNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNextButtonActionPerformed
        // TODO add your handling code here:
        String status = jStatusTxtLabel.getText().trim();
        if (!status.equals("Stopped") && !status.equals("Running")) {
            int currFrame = Integer.parseInt(jCurFrameLabel.getText().trim());
            if (currFrame != 0) {
                jStatusTxtLabel.setText("Stepped Up");
                if (currThread != null) {
                    currThread.upCurrFrameNo();
                }
            }
        }
    }//GEN-LAST:event_jNextButtonActionPerformed

    private void jStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStopButtonActionPerformed
        // TODO add your handling code here:     
        int currFrame = Integer.parseInt(jCurFrameLabel.getText().trim());
        if (currFrame != 0) {
            if (currThread != null) {
                currThread.setStopFlag();
            } else {
                jStatusTxtLabel.setText("Stopped");
                jRunningButton.setIcon(new javax.swing.ImageIcon("Icons/run.jpg"));
                jRunningButton.setToolTipText("Play");
            }
            jPrevButton.setEnabled(false);
            jNextButton.setEnabled(false);
        }

    }//GEN-LAST:event_jStopButtonActionPerformed

    private void jSeekSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSeekSliderStateChanged
        // TODO add your handling code here:
        int val = jSeekSlider.getValue();
        if (currThread != null) {
            currThread.setSeekBarValToCapture(val);
        }
    }//GEN-LAST:event_jSeekSliderStateChanged

    private void jSeekSliderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeekSliderMouseClicked
        // TODO add your handling code here:
        onMouseEventsAction();
    }//GEN-LAST:event_jSeekSliderMouseClicked

    private void jSeekSliderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeekSliderMousePressed
        // TODO add your handling code here:
        onMouseEventsAction();
    }//GEN-LAST:event_jSeekSliderMousePressed

    private void jSeekSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeekSliderMouseReleased
        // TODO add your handling code here:
        playButtonClicked();
    }//GEN-LAST:event_jSeekSliderMouseReleased

    private void jCutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCutButtonActionPerformed
        // TODO add your handling code here:
        if (capture == null) {
            return;
        }
        jErrorLabel.setText("");

        String startFrameNoStr = jStartTextField.getText().trim();
        String endFrameNoStr = jEndTextField.getText().trim();
        String totalFramesStr = jTotalFramesLabel.getText().trim();

        String playVideoPath = jFilePathLabel.getText().trim();
        if (playVideoPath.isEmpty()) {
            jErrorLabel.setText("Video file is empty");
            return;
        }

        if (totalFramesStr.isEmpty()) {
            jErrorLabel.setText("Total frame is empty");
            return;
        }

        if (startFrameNoStr.isEmpty() || startFrameNoStr.equals("0") || startFrameNoStr.length() > 9) {
            jErrorLabel.setText("Start frame number is not valid");
            return;
        }

        if (endFrameNoStr.isEmpty() || endFrameNoStr.equals("0") || endFrameNoStr.length() > 9) {
            jErrorLabel.setText("End frame number is not valid");
            return;
        }

        String fpsStr = jFPSValLabel.getText().trim();
        if (fpsStr.isEmpty() || fpsStr.equals("0")) {
            jErrorLabel.setText("Video fps value is not valid");
            return;
        }

        int startFrameNo = Integer.parseInt(startFrameNoStr);
        int endFrameNo = Integer.parseInt(endFrameNoStr);
        int fps = Integer.parseInt(fpsStr);
        int totalFrames = Integer.parseInt(totalFramesStr);

        if (startFrameNo > endFrameNo) {
            jErrorLabel.setText("End frame number is less than start frame");
            return;
        }

        if (startFrameNo > totalFrames) {
            jErrorLabel.setText("Start frame number is greater than total frame");
            return;
        }

        if (endFrameNo > totalFrames) {
            jErrorLabel.setText("End frame number is greater than total frame");
            return;
        }

        if (startFrameNo == endFrameNo) {
            jErrorLabel.setText("Start and End frame number is same");
            return;
        }

        String cutFileName = new StringBuilder(pathNAS).append("/").append(startFrameNo).append("_").
                append(endFrameNo).append("_").append(inputVideoPath).toString();

        File f1 = new File(cutFileName);
        if (f1.exists()) { //Same video frame length already exists
            jErrorLabel.setText("Same frame number video cut is available");
            return;
        }

        VideoCutterThread videoThread = new VideoCutterThread(jCutButton, jErrorLabel, startFrameNo, endFrameNo, fps, inputVideoPath, f1.getPath());
        videoThread.start();


    }//GEN-LAST:event_jCutButtonActionPerformed

    private void jListMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jListMenuItemActionPerformed
        // TODO add your handling code here:        
        displayPlayList();
    }//GEN-LAST:event_jListMenuItemActionPerformed

    private void jPlayListTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPlayListTableMouseClicked
        // TODO add your handling code here:

        DefaultTableModel model = (DefaultTableModel) jPlayListTable.getModel();
        if (model.getRowCount() != 0) {
            int row = jPlayListTable.getSelectedRow();
            if (row != -1) {
                inputVideoPath = (String) model.getValueAt(row, 1);
                onVideoFileLoad();
                rowCount = row;
            }
        }
    }//GEN-LAST:event_jPlayListTableMouseClicked

    private void jPrevPlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPrevPlayButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) jPlayListTable.getModel();

        if (model.getRowCount() != 0 && rowCount > 0) {
            --rowCount;
            inputVideoPath = (String) model.getValueAt(rowCount, 1);
            jPlayListTable.changeSelection(rowCount, rowCount, false, false);
            onVideoFileLoad();
        }
    }//GEN-LAST:event_jPrevPlayButtonActionPerformed

    private void jNextPlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNextPlayButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) jPlayListTable.getModel();
        if (model.getRowCount() != 0) {
            ++rowCount;
            inputVideoPath = (String) model.getValueAt(rowCount, 1);
            jPlayListTable.changeSelection(rowCount, rowCount, false, false);
            onVideoFileLoad();
        }
    }//GEN-LAST:event_jNextPlayButtonActionPerformed

    private void jPlayListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlayListButtonActionPerformed
        // TODO add your handling code here:
        displayPlayList();
    }//GEN-LAST:event_jPlayListButtonActionPerformed

    private void displayPlayList() {
        DefaultTableModel model = (DefaultTableModel) jPlayListTable.getModel();
        if (model.getRowCount() != 0) {
            jPlayListFrame.setVisible(true);
            RefineryUtilities.centerFrameOnScreen(jPlayListFrame);
        }else{
             JOptionPane.showMessageDialog(this, "Play List is empty", "ERROR", JOptionPane.ERROR_MESSAGE, new ImageIcon("Icons/error.png"));
        }
        
    }

    private void onMouseEventsAction() {
        if (currThread != null && jStatusTxtLabel.getText().equals("Running")) {
            jRunningButton.setIcon(new javax.swing.ImageIcon("Icons/run.jpg"));
            jRunningButton.setToolTipText("Play");
            jStatusTxtLabel.setText("Paused");
            currThread.setPauseFlag(true);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jCloseMenu;
    private javax.swing.JLabel jCurFrameLabel;
    private javax.swing.JLabel jCurrFrameTitleLabel;
    private javax.swing.JButton jCutButton;
    private javax.swing.JLabel jEndFrameTitleLabel;
    private javax.swing.JTextField jEndTextField;
    private javax.swing.JLabel jErrorLabel;
    private javax.swing.JMenuItem jExitMenuItem;
    private javax.swing.JLabel jFPSTitleLabel;
    private javax.swing.JLabel jFPSValLabel;
    private javax.swing.JLabel jFilePathLabel;
    private javax.swing.JLabel jFourccTitleLabel;
    private javax.swing.JLabel jFourccValLabel;
    private javax.swing.JLabel jImageLabel;
    private javax.swing.JMenuItem jListMenuItem;
    private javax.swing.JScrollPane jListScrollPanel;
    private javax.swing.JMenuBar jMainMenuBar;
    private javax.swing.JButton jNextButton;
    private javax.swing.JButton jNextPlayButton;
    private javax.swing.JButton jPlayListButton;
    private javax.swing.JFrame jPlayListFrame;
    private javax.swing.JTable jPlayListTable;
    private javax.swing.JButton jPrevButton;
    private javax.swing.JButton jPrevPlayButton;
    private javax.swing.JButton jRunningButton;
    private javax.swing.JSlider jSeekSlider;
    private javax.swing.JLabel jStartFrameLabel;
    private javax.swing.JTextField jStartTextField;
    private javax.swing.JLabel jStatusLabel;
    private javax.swing.JLabel jStatusTxtLabel;
    private javax.swing.JButton jStopButton;
    private javax.swing.JPanel jToolPanel;
    private javax.swing.JLabel jTotalFrameTitleLabel;
    private javax.swing.JLabel jTotalFramesLabel;
    private javax.swing.JLabel jVideoFileLabel;
    private javax.swing.JLabel jVideoLenLabel;
    private javax.swing.JLabel jVideoLenTitleLabel;
    private javax.swing.JMenu jViewMenu;
    // End of variables declaration//GEN-END:variables

}
