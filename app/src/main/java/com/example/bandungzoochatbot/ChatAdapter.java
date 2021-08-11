package com.example.bandungzoochatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bandungzoochatbot.ChatMessage;

import java.util.List;

import static android.app.ProgressDialog.show;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {
    private static final  int MY_MESSAGE = 0;
    private static final  int BOT_MESSAGE = 1;

    public ChatAdapter(@NonNull Chatbot context, List<ChatMessage> data) {
        super(context, R.layout.user_chat_layout, data);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);
        if (item.isMine()){
            return MY_MESSAGE;
        }
        else {
            return BOT_MESSAGE;
        }
    }
    //TODO: asking location untuk BOT
    public boolean isLocation (int position){
        ChatMessage item = getItem(position);
        if(item.getLocation()){
            return true;
        }
        return false;
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == MY_MESSAGE){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_chat_layout, parent, false);
            TextView textView = convertView.findViewById(R.id.text);
            textView.setText((getItem(position).getContext()));
        }
        else if(viewType == BOT_MESSAGE){
            if(isLocation(position)){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chatbot_location_layout, parent, false);
                TextView textView = convertView.findViewById(R.id.text);
                textView.setText((getItem(position).getContext()));
            }
            else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chatbot_chat_layout, parent, false);
                TextView textView = convertView.findViewById(R.id.text);
                textView.setText((getItem(position).getContext()));
            }
        }

        convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
