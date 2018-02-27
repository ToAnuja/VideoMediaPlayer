/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MediaPlayer;

import java.io.IOException;

/**
 *
 * @author root
 */
public class CommFunction {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CommFunction.class);

    public String frameNoToTime(int frameNo, int fps) {
        int totalTime = frameNo / fps;
        int seconds = totalTime;
        
        

        String timeStr = "00";
        if (totalTime >= 3600) {
            int hrs = (int) totalTime / 3600;
            seconds = (totalTime % 3600);

            if (hrs > 9) {
                timeStr = "" + hrs;
            } else {
                timeStr = "0"+hrs;
            }

        }

        if (seconds >= 60) {
            int min = (int) seconds / 60;
            seconds = (totalTime % 60);
            if (!timeStr.isEmpty()) {
                
                if (min > 9) {
                    timeStr = timeStr + ":" + min;
                } else {
                    timeStr = timeStr + ":0" + min;
                }

            } else {
                if (min > 9) {
                    timeStr = "00:" + min;
                } else {
                    timeStr = timeStr + ":0" + min;
                }

            }
        }

        if (!timeStr.isEmpty()) {
            if (seconds > 9) {
                timeStr = timeStr + ":" + seconds;
            } else {
                timeStr = timeStr + ":0" + seconds;
            }

        } else {
            if (seconds > 9) {
                timeStr = "00:00:" + seconds;
            } else {
                timeStr = "00:00:0" + seconds;
            }

        }
        return timeStr;
    }

    public boolean exeCommand(String cmd) {
        logger.info("CommFunction is called ==" + cmd);
        try {
            Runtime r = Runtime.getRuntime();

            Process p = r.exec(cmd);
            p.waitFor();

            r.freeMemory();
            p.destroy();

            return true;
        } catch (IOException | InterruptedException ex) {

            return false;
        }
    }

}
