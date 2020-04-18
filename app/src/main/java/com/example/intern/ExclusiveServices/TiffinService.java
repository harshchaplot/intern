package com.example.intern.ExclusiveServices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intern.R;
import com.example.intern.mainapp.MainApp;

public class TiffinService extends AppCompatActivity {

    ImageView back,home_btn;
    TextView secPara,thrPara,forPara;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiffin_service);

        back = findViewById(R.id.back);
        home_btn = findViewById(R.id.home_btn);

        secPara = findViewById(R.id.secPara);
        thrPara = findViewById(R.id.thrPara);
        forPara = findViewById(R.id.forPara);
        submit=findViewById(R.id.submitTiffin);


        String sourceString ="<b>"+"1) "+"</b>"+ " Our Tiffin is prepared by "+"<b>" + "well trained and expert cook, "+ "</b>"+" with proper care of " +"<b>" + "hygiene." + "</b> ";
        secPara.setText(Html.fromHtml(sourceString));

        String sourceString2 = "<b>"+"2) "+"</b>"+" Menu is designed in a way to provide "+"<b>" + "sufficient and balanced nutritions, "+"</b>"+ "for daily need.";
        thrPara.setText(Html.fromHtml(sourceString2));

        String sourceString3 = "<b>"+"3) "+"</b>"+" Usesof oil and spices as per "+"<b>" + "dieticians guidance "+"</b>"+ "makes it a perfect meal.";
        forPara.setText(Html.fromHtml(sourceString3));

        back.setOnClickListener(view -> onBackPressed());

        home_btn.setOnClickListener(view -> {
            Intent intent = new Intent(TiffinService.this, MainApp.class);
            startActivity(intent);
            finish();
        });
        final Context context = this;
        submit.setOnClickListener(v -> {
            //TODO :
            new AlertDialog.Builder(context).setIcon(R.drawable.pslogotrimmed).setTitle("Thank You")
                    .setMessage("We will get back to you shortly").setPositiveButton("OK", null)
                    .setOnDismissListener(dialog -> onBackPressed()).show();
        });
    }
}
