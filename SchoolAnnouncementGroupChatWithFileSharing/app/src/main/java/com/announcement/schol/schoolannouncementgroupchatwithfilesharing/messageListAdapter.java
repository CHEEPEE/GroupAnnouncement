package com.announcement.schol.schoolannouncementgroupchatwithfilesharing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Keji's Lab on 05/10/2017.
 */

public class messageListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<messageChatModel> messageChatModels = new ArrayList<>();

    private static LayoutInflater inflater=null;
    public messageListAdapter(Context c, ArrayList<messageChatModel >mMessageChatModel){
        this.context = c;
        this.messageChatModels = mMessageChatModel;
        inflater = (LayoutInflater)c.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return messageChatModels.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }



    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = inflater.inflate(R.layout.message_list_single_item,null);
        TextView user,message,iconText,timestamp;
        ImageView downloadicon = (ImageView) rootView.findViewById(R.id.download_icon);
        if (messageChatModels.get(position).getDownloadURL()!=null){
            downloadicon.setVisibility(View.VISIBLE);
        }else {

        }
        timestamp = (TextView) rootView.findViewById(R.id.date);
        timestamp.setText(messageChatModels.get(position).getTimestamp());
        user = (TextView) rootView.findViewById(R.id.user_name);
        message = (TextView) rootView.findViewById(R.id.text_message);
        iconText = (TextView) rootView.findViewById(R.id.icon_text);
        iconText.setText(messageChatModels.get(position).getUsername().substring(0,1).toUpperCase());

            user.setText(messageChatModels.get(position).getUsername());
            message.setText(messageChatModels.get(position).getMessage());



        return rootView;
    }
}
