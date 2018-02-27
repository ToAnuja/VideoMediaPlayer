/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videomediaplayer;

import MediaPlayer.MediaPlayerJFrame;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.ui.RefineryUtilities;
import org.opencv.core.Core;

/**
 *
 * @author root
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // TODO code application logic here

        String videoFile = "/root/Videos/GunPosn.mpg";
        String pathNAS = "/home/crl/EXTERNAL_DRIVE/";
        String listFlg = "0";
        
        if (args.length != 0) {
            videoFile = args[0];
            pathNAS = args[1];
            listFlg = args[2];
        }
        pathNAS = pathNAS + "/VIDEO_CUTTER/";
        File f = new File(pathNAS);
        if (!f.exists()) {
            try {
                Runtime.getRuntime().exec("mkdir " + pathNAS);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        MediaPlayerJFrame player = new MediaPlayerJFrame(videoFile, pathNAS, listFlg);
        player.setVisible(true);
        RefineryUtilities.centerFrameOnScreen(player);
    } 

}
