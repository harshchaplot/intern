package com.example.intern.ExclusiveServices;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intern.NewsAndUpdatesACT;
import com.example.intern.R;
import com.example.intern.database.FireStoreUtil;
import com.example.intern.mainapp.MainApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TiffinService extends AppCompatActivity {

    ImageView back,home_btn;
    TextView secPara,thrPara,forPara;
    ImageView arrow1;
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
        arrow1 = findViewById(R.id.arrow1);

        //arrow1.setText("<b>"+"&#8594"+"</b>"+" ");
        String sourceString =/*"<b>"+"&#8594"+"</b>"+*/ getString(R.string.our_tiffin_is_prepared)+"<b>" + getString(R.string.well_trained_cook)+ "</b>"+getString(R.string.with_proper_care) +"<b>" + getString(R.string.hygiene) + "</b> ";
        secPara.setText(Html.fromHtml(sourceString));

        String sourceString2 =/*"<b>"+"&#8594 "+"</b>"+*/getString(R.string.menu_is_designed_in_a_way)+"<b>" + getString(R.string.sufficeint_and_balanced_nutrition)+"</b>"+ getString(R.string.for_daily_need);
        thrPara.setText(Html.fromHtml(sourceString2));

        String sourceString3 = /*"<b>"+"&#8594 "+"</b>"+*/getString(R.string.use_of_oil_and_spices)+"<b>" + getString(R.string.dietician_guidance)+"</b>"+ getString(R.string.makes_it_perfect);
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
	        ProgressDialog dialog = new ProgressDialog(this);
	        dialog.setTitle("Please wait");
	        dialog.setIcon(R.drawable.pslogotrimmed);
	        dialog.show();
	        Map<String, Object> data = new HashMap<>();
	        data.put("uid", FirebaseAuth.getInstance().getUid());
	        FirebaseFirestore.getInstance().collection(FireStoreUtil.EXCLUSIVE_SERVICES_COLLECTION_NAME)
			        .document(FireStoreUtil.TIFFIN_SERVICES_SERVICES)
			        .collection(FireStoreUtil.TIFFIN_SERVICES_SERVICES).add(data)
			        .addOnSuccessListener(documentReference -> {
				        dialog.dismiss();
				        new AlertDialog.Builder(context).setIcon(R.drawable.pslogotrimmed).setTitle("Thank You")
						        .setMessage("We will get back to you shortly").setPositiveButton("OK", null)
						        .setOnDismissListener(alertDialog -> onBackPressed()).show();
			        });
        });
        findViewById(R.id.notifi).setOnClickListener(v -> {
	        Intent intent = new Intent(this, NewsAndUpdatesACT.class);
	        startActivity(intent);
        });
    }
}
