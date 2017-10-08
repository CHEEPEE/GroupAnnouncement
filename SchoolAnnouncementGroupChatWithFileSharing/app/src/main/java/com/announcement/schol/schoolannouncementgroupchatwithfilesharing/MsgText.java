package com.announcement.schol.schoolannouncementgroupchatwithfilesharing;

/**
 * Created by Keji's Lab on 06/10/2017.
 */



import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class MsgText {

    public String username;
    public String email;
    public String message;
    public String timeStamp;
    public String downloadURL;
    public String type;

    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public MsgText() {

    }

    public MsgText(String username, String email, String smg, String timeStamp,String url,String FileType) {
        this.username = username;
        this.email = email;
        this.message = smg;
        this.timeStamp = timeStamp;
        this.downloadURL = url;
        this.type = FileType;
    }
    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username",username);
        result.put("email",email);
        result.put("message", message);
        result.put("timeStamp",timeStamp);
        result.put("downloadURL", downloadURL);
        result.put("type",type);


        return result;
    }
    // [END post_to_map]

}