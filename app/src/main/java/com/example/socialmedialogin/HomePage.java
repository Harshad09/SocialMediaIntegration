package com.example.socialmedialogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class HomePage extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    TextView user,email,gender,birthday;
    ImageView profile_photo;
    Button log_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        user = findViewById(R.id.user_name);
        email = findViewById(R.id.email);
        profile_photo = findViewById(R.id.profile_photo);
        gender = findViewById(R.id.gender);
        birthday = findViewById(R.id.birthday);
        log_out = findViewById(R.id.log_out);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            user.setText(personName);
            email.setText(personEmail);
            Picasso.get().load(String.valueOf(personPhoto)).into(profile_photo);
        }

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AccessToken.isCurrentAccessTokenActive()){
                LoginManager.getInstance().logOut();
                startActivity(new Intent(HomePage.this,MainActivity.class));
                }
                else{
                    signOut();
                }

            }
        });

        if(AccessToken.isCurrentAccessTokenActive()) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        user.setText(object.getString("name"));
                        String pic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                        Picasso.get().load(pic).into(profile_photo);
                        gender.setText(object.getString("gender"));
                        birthday.setText(object.getString("birthday"));
                        email.setText(object.getString("email"));
                        Log.d("d", object.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle bundle = new Bundle();
            bundle.putString("fields", "gender,name,id,first_name,last_name,email,picture.width(400).height(400),birthday");
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();
        }
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(HomePage.this,MainActivity.class));
                    }
                });
    }
}