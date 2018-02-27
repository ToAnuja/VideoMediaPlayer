# VideoMediaPlayer
Java based "Video Player Application" is to play video, list of video files and video cutter of played videos. Build the uploaded project in following environment before executing the application:                                                                                                           

Platform OS: Linux 

Screen Resolution: 1920X1080

Language: java version "1.8.0_91"

Environment: opencv-3.3.0 or any similar versions must installed before running this application

Lib: jcommon.jar, log4j-1.2.17.jar, opencv-330.jar

IDE: NetBeans IDE 8.2

-Djava.library.path="<path_of_opencv>/opencv-3.3.0/build/lib/" add as VM option in netbeans to compile and run the application from netbeans program                                                                                                                      

Configuratuion: playList.txt is used to play the list of videos by media player

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:<path_of_opencv>/opencv-3.4.0/build/lib/

Run Command: Three arguments shall be given to run videoApplication
1. videoFile = "/home/user/GunPosn.mpg";  [VideoFile to play]
2. pathOfStorage = "/home/user/STORAGE_DRIVE/"; [That path shall be used to store the videos after cutting]
3. listFlg = "0"; [1 -> For playing the video files from playList.txt, 0 -> For Not playing the video files from playList.txt]

java -jar dist/VideoMediaPlayer.jar videoFile.avi pathOfStorage listFlg
  
