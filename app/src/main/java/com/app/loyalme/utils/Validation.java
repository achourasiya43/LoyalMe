package com.app.loyalme.utils;

import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dharmraj on 2/27/2016.
 */
public class Validation {

    private final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    private final String FULLNAME_PATTERN = "^[\\p{L} .'-]+$";
    private final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";

    private Pattern pattern;
    private Matcher matcher;

    private String getString(TextView textView){
        String getValue = textView.getText().toString().trim();
        return getValue;
    }

    @Deprecated // "use isEmpty instead of the isNullValue"
    public boolean isNullValue(TextView textView){
        if(getString(textView).isEmpty()){
           // textView.setError("Field cannot be empty");
            return false;
        }
        textView.setError(null);
        return true;
    }

    public boolean isEmpty(TextView textView){
        if(getString(textView).isEmpty()){
            //textView.setError("field can't be empty");
            textView.requestFocus();
            return true;
        }
        return false;
    }

    public boolean isEmptyN(TextView textView){
        if(getString(textView).isEmpty()){
            textView.requestFocus();
            return true;
        }
        return false;
    }

    public boolean isUserNameValid(TextView textView){
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(getString(textView));
        boolean bool = matcher.matches();
        if(!bool){
            textView.setError("enter valid userName");
            textView.requestFocus();
        }
        return bool;
    }

    public boolean isFullNameValid(TextView textView){
        Pattern pattern = Pattern.compile(FULLNAME_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(getString(textView));
        return matcher.find();
    }


    public boolean isPasswordValid(TextView textView) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(getString(textView));
        boolean bool = matcher.matches();
        if(!bool){
            textView.setError("At least 4 characters required");
            textView.requestFocus();
        }
        return bool;
    }

    public boolean isEmailValid(TextView textView) {
        boolean bool = android.util.Patterns.EMAIL_ADDRESS.matcher(getString(textView)).matches();
        if(!bool){
           // textView.setError("enter valid email");
            textView.requestFocus();
        }
        return bool;
    }

}
