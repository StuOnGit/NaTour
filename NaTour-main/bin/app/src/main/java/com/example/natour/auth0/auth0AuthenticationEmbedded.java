package com.example.natour.auth0;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.example.natour.data.User;
import com.example.natour.interfacce.FailListener;

import java.util.function.Consumer;

public class auth0AuthenticationEmbedded {


    private static final String TAG = "auth0Authentication";
    private final String auth0ClientId;
    private final String auth0Domain;
    private Auth0 auth0Account;
    private final AuthenticationAPIClient authenticationAPIclientAuth0;
    private String databaseConnections;
    private String userToken;

    public auth0AuthenticationEmbedded(String auth0ClientId, String auth0Domain, String databaseConnections){
        this.auth0ClientId = auth0ClientId;
        this.auth0Domain = auth0Domain;
        this.databaseConnections = databaseConnections;
        auth0Account = new Auth0(this.auth0ClientId, this.auth0Domain);
        authenticationAPIclientAuth0 = new AuthenticationAPIClient(auth0Account);
    }

    //Da rivedere
    public void loginFacebook(Context context, Consumer<User> onSuccess, FailListener onError) {
        WebAuthProvider
                .login(auth0Account)
                .withScheme("demo")
                .withConnection("facebook")
                .start(context , new Callback<Credentials, AuthenticationException>() {
                    @Override
                    public void onSuccess(Credentials credentials) {
                        userToken = credentials.getAccessToken();
                        AsyncTask.execute(()->{
                            String email = getEmailUserSigned();
                            String name = getNameUserSigned();
                            String username = getUsernameUserSigned();
                            String givenName = getGivenName();

                            User user = new User(username,name, givenName, email);
                            onSuccess.accept(user);
                        });

                        Log.d(TAG, "onSuccess: " + "login with facebook ok");
                    }

                    @Override
                    public void onFailure(AuthenticationException e) {
                        onError.onAuthFail(e.getMessage());
                        Log.d(TAG, "onFailure: " + "login with facebook failed");
                    }
                });
    }

    //Da rivedere
    public void loginGoogle(Context context, Consumer<User> onSuccess, FailListener onError){
        WebAuthProvider.login(auth0Account)
                .withScheme("demo")
                .withConnection("google-oauth2")
                .start(context , new Callback<Credentials, AuthenticationException>() {

                    @Override
                    public void onSuccess(Credentials credentials) {
                        userToken = credentials.getAccessToken();
                        AsyncTask.execute(()->{
                            String email = getEmailUserSigned();
                            String name = getNameUserSigned();
                            String username = getUsernameUserSigned();
                            String givenName = getGivenName();

                            User user = new User(username,name, givenName, email);
                            onSuccess.accept(user);
                        });
                        Log.d(TAG, "onSuccess: " + "login with google ok");
                    }

                    @Override
                    public void onFailure(AuthenticationException e) {
                        onError.onAuthFail(e.getMessage());
                        Log.d(TAG, "onFailure: " + "login with google failed");
                    }
                });
    }

    public void login2 (String email, String password, Context context, Consumer<String> onSuccess, FailListener onError){

        authenticationAPIclientAuth0.login(email, password, databaseConnections).start(new Callback<Credentials, AuthenticationException>() {
            @Override
            public void onSuccess(Credentials credentials) {
                Log.d(TAG, "onSuccess: "+"login Success");
                userToken = credentials.getAccessToken();
                AsyncTask.execute(()->{
                    onSuccess.accept(getEmailUserSigned());
                });

            }
            @Override
            public void onFailure(AuthenticationException e) {
                Log.d(TAG, "onFailure: " + e.toString());
                onError.onAuthFail("Password o Email errate, verifica la connessione e riprova");
            }

        });
    }

    public void signup2(String email, String password,String username,String name,  String surname, Consumer<User> onSuccess, FailListener onError){


        authenticationAPIclientAuth0.signUp(email, password, databaseConnections).start(new Callback<Credentials, AuthenticationException>() {
            @Override
            public void onSuccess(Credentials credentials) {
                Log.d(TAG,"onSuccess: ");
                userToken = credentials.getAccessToken();
                onSuccess.accept(new User(username, name, surname, email));
            }

            @Override
            public void onFailure(AuthenticationException e) {
                Log.e(TAG,"OnFailure: " + e.toString());
                onError.onAuthFail(e.getMessage());
            }
        });

    }

    private String getEmailUserSigned(){
        return authenticationAPIclientAuth0.userInfo(userToken).execute().getEmail();
    }

    private String getUsernameUserSigned(){
        return authenticationAPIclientAuth0.userInfo(userToken).execute().getNickname();
    }

    private String getNameUserSigned(){
        return authenticationAPIclientAuth0.userInfo(userToken).execute().getName();
    }

    private String getGivenName(){
        return authenticationAPIclientAuth0.userInfo(userToken).execute().getGivenName();
    }
}
