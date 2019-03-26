package com.example.prashantgajera.tallyerpsystem.CommenUtilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.prashantgajera.tallyerpsystem.R;


public class DialogHelper {

    public static Dialog showdialog(  Context c){
        final Dialog dialog=new Dialog(c);
        dialog.setContentView(R.layout.alertview);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }
    public static Dialog showdialog(  Context c,String text){
        final Dialog dialog=new Dialog(c);
        dialog.setContentView(R.layout.alertview);
        dialog.setCancelable(true);
        TextView wait=dialog.findViewById(R.id.wait);
        wait.setText(text);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }
    public static Dialog showAlert(final Activity c, String text){
        final Dialog dialog=new Dialog(c);
        dialog.setContentView(R.layout.alertdata);
        dialog.setCancelable(true);
        Button exit=dialog.findViewById(R.id.exit);
        TextView text1=dialog.findViewById(R.id.text);
        text1.setText(text);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                c.finish();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }



}
