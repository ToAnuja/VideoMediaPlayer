# VideoMediaPlayer
Java based Video Player                                                                                                              

Platform OS: Linux 

Screen Resolution: 1920X1080

Language: java version "1.8.0_91"

Environment: opencv-3.3.0 must installed before running this application

Lib: jcommon.jar, log4j-1.2.17.jar, opencv-330.jar

IDE: NetBeans IDE 8.2

-Djava.library.path="<path_of_opencv>/opencv-3.3.0/build/lib/" add as VM option in netbeans to compile and run the application from netbeans program                                                                                                                      

Configuratuion: playList.txt is used to play the list of videos by media player

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:<path_of_opencv>/opencv-3.4.0/build/lib/

Run Command: java -jar dist/VideoMediaPlayer.jar <videoFile>.avi 
  
