package cz.kohlicek.bpini.service;


import android.content.Context;

import cz.kohlicek.bpini.model.Account;

public class BPINIClient {


    public static BPINIService getInstance(Context context) {
        Account account = Account.getLocalAccount(context);

        return ServiceGenerator.createService(BPINIService.class, account.getHost(), account.getUsername(), account.getPassword());
    }

//    public void getUserLoginInfo(UserLoginQuery userLoginQuery, final UserListener listener) {
//        mUserRestService.getUserLoginInfo(UserLoginQuery.Method, JSON, API_KEY, generateMD5(userLoginQuery.getSignature()), userLoginQuery.mUsername, userLoginQuery.mPassword, new Callback<UserLoginInfo>() {
//            @Override
//            public void success(UserLoginInfo userLoginInfo, Response response) {
//                Log.d("Logedin", userLoginInfo.mSession.mToken + " " + userLoginInfo.mSession.mUsername);
//                mUserSession = userLoginInfo.mSession;
//                mUserSession.update(context);
//                listener.userSuccess();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                listener.userInfoFailed();
//            }
//        });
//    }


}
