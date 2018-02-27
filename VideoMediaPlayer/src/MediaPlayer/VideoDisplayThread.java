/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MediaPlayer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 *
 * @author root
 */
public class VideoDisplayThread extends Thread {

    private final javax.swing.JLabel jImageLabel;

    private final javax.swing.JLabel jCurFrameLabel;
    private final javax.swing.JLabel jStatusTxtLabel;

    private final javax.swing.JSlider jSeekSlider;
    private final javax.swing.JButton jRunningButton;

    private boolean pauseFlg;
    private boolean stopFlg;
    private boolean resetFlg;
    private int curFrameNo;
    private final VideoCapture capture;
    private final Size sizeImg;

    VideoDisplayThread(VideoCapture _capture, Size _s, javax.swing.JSlider _jSeekSlider,
            javax.swing.JLabel _jImageLabel, javax.swing.JLabel _jCurFrameLabel,
            javax.swing.JLabel _jStatusTxtLabel, javax.swing.JButton _jRunningButton) {
        capture = _capture;
        sizeImg = _s;
        jImageLabel = _jImageLabel;
        jCurFrameLabel = _jCurFrameLabel;
        jSeekSlider = _jSeekSlider;
        jStatusTxtLabel = _jStatusTxtLabel;
        jRunningButton = _jRunningButton;
    }

    public void run() {
        playVideo();
    }

    void playVideo() {

        Mat matOrig = new Mat();
        int sleepTime = (int) capture.get(Videoio.CAP_PROP_FPS);
        if (sleepTime < 20) {
            sleepTime = 30;
        }
        while (true) {
            try {

                synchronized (capture) {
                    if (capture.isOpened()) {

                        if (resetFlg) {
                            if (curFrameNo != 0) {
                                stoppedButtonCall();
                            }
                            break;
                        }
                        if (stopFlg) {
                            if (curFrameNo != 0) {
                                stoppedButtonCall();
                            }
                            Thread.sleep((int) 1000);
                            continue;
                        }

                        if (!pauseFlg) {
                            curFrameNo = (int) capture.get(Videoio.CV_CAP_PROP_POS_FRAMES);
                            capture.read(matOrig);
                            updateVideoFrame(matOrig);
                        }
                    }
                }

                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(VideoDisplayThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        capture.release();
        Thread.currentThread().interrupt();
    }

    private void updateVideoFrame(Mat matOrig) {
        if (!matOrig.empty()) {
            Mat dst = new Mat();
            Imgproc.resize(matOrig, dst, sizeImg);

            BufferedImage dstImage = Mat2BufferedImage(dst);
            ImageIcon image = new ImageIcon(dstImage);
            jImageLabel.setIcon(image);
            jImageLabel.validate();
            jImageLabel.repaint();

            jCurFrameLabel.setText("" + curFrameNo);
            jSeekSlider.setValue(curFrameNo);
        } else {
            stoppedButtonCall();
        }
    }

    private void stoppedButtonCall() {
        jCurFrameLabel.setText("" + curFrameNo);
        jStatusTxtLabel.setText("Stopped");
        jRunningButton.setIcon(new javax.swing.ImageIcon("Icons/run.jpg"));
        jRunningButton.setToolTipText("Play");

        capture.set(Videoio.CV_CAP_PROP_POS_FRAMES, 0);
        curFrameNo = (int) capture.get(Videoio.CV_CAP_PROP_POS_FRAMES);
        jSeekSlider.setValue(0);
        jCurFrameLabel.setText("" + curFrameNo);

        jImageLabel.setIcon(null);
        stopFlg = true;
    }

    void downCurrFrameNo() {
        synchronized (capture) {
            if (capture != null && curFrameNo != 0) {
                curFrameNo = curFrameNo - 1;
                capture.set(Videoio.CV_CAP_PROP_POS_FRAMES, curFrameNo);

                Mat matOrig = new Mat();
                capture.read(matOrig);
                updateVideoFrame(matOrig);
                jCurFrameLabel.setText("" + curFrameNo);
            }
        }
    }

    void setSeekBarValToCapture(int val) {

        synchronized (capture) {
            if (capture != null && pauseFlg) {
                curFrameNo = val;
                capture.set(Videoio.CV_CAP_PROP_POS_FRAMES, curFrameNo);
                Mat matOrig = new Mat();
                capture.read(matOrig);
                updateVideoFrame(matOrig);
            }
        }
    }

    void upCurrFrameNo() {
        synchronized (capture) {
            int total = (int) capture.get(Videoio.CAP_PROP_FRAME_COUNT);
            if (capture != null && curFrameNo != total) {
                curFrameNo = curFrameNo + 1;
                capture.set(Videoio.CV_CAP_PROP_POS_FRAMES, curFrameNo);
                jCurFrameLabel.setText("" + curFrameNo);

                Mat matOrig = new Mat();
                capture.read(matOrig);
                updateVideoFrame(matOrig);
                jCurFrameLabel.setText("" + curFrameNo);
            }
        }
    }

    void setRestartPlay() {
        stopFlg = false;
        pauseFlg = false;
    }

    void setPauseFlag(boolean flg) {
        pauseFlg = flg;
    }

    void setStopFlag() {
        stopFlg = true;
    }

    void setResetFlag() {
        resetFlg = true;
    }

    private BufferedImage Mat2BufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage img = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return img;
    }

}
