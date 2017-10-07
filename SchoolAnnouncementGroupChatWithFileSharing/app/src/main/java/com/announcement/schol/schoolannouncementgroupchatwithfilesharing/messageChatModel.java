package com.announcement.schol.schoolannouncementgroupchatwithfilesharing;

/**
 * Created by Keji's Lab on 05/10/2017.
 */

public class messageChatModel {
   private String username;
   private String message;
    private String timestamp;
    private String downloadURL;

    public String getTimestamp(){
        return timestamp;
    }
    public String getMessage() {
        return message;
    }
    public String getUsername(){
        return username;
    }
    public String getDownloadURL(){
        return downloadURL;
    }
    public void setDownloadURL(String dURL){
        this.downloadURL = dURL;
    }
    public void setUsername(String setName){
        this.username = setName;
    }
    public void setMessage(String setMsg){
        this.message = setMsg;
    }
    public void setTimestamp(String setTime){
        this.timestamp = setTime;
    }

}
