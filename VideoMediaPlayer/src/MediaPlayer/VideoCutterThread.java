/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MediaPlayer;

import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author root
 */
public class VideoCutterThread extends Thread {

    private final javax.swing.JButton jCutButton;
    private final javax.swing.JLabel jErrorLabel;
  
    private final String inputVidPath;
    private final String cutFileName;
    private final int startFrameNo;
    private final int endFrameNo;
    private final int fps;

    VideoCutterThread(javax.swing.JButton _jCutButton, javax.swing.JLabel _jErrorLabel,
            int _startFrameNo, int _endFrameNo, int _fps,
            String _inputVidPath, String _cutFileName) {
        jCutButton = _jCutButton;
        jErrorLabel = _jErrorLabel;
        cutFileName = _cutFileName;
       
        inputVidPath = _inputVidPath;
        startFrameNo = _startFrameNo;
        endFrameNo = _endFrameNo;
        fps = _fps;
    }

    public void run() {
        jCutButton.setEnabled(false);
        jErrorLabel.setText("Wait for processing completion ...");

        CommFunction commObj = new CommFunction();
        String StartTime = commObj.frameNoToTime(startFrameNo, fps);
        String EndTime = commObj.frameNoToTime(endFrameNo, fps);

        

        String cmd = new StringBuilder("ffmpeg -i ").append(inputVidPath).append(" -ss ").append(StartTime).
                append(" -to ").append(EndTime).append(" -async 1 -strict -2 ").append(cutFileName).toString();
        
        commObj.exeCommand(cmd);

        jErrorLabel.setText("");

        jCutButton.setEnabled(true);
        
        JOptionPane.showMessageDialog(null, "Video cut process is completed", "Information", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("Icons/error.png"));
        Thread.currentThread().interrupt();
    }

}
