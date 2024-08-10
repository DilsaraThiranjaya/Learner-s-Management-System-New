package lk.ijse.lms.util;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.paint.Paint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static boolean isTextFieldValid(TextField textField, String text){
        String field = "";

        switch (textField){
            case ID :
                field = "\\b[A-Z]{1,2}\\d{3,}\\b";
                break;
            case NAME :
                field = "\\b[A-Z][a-zA-Z\\s]+\\b";
                break;
            case EMAIL :
                field = "^([A-z])([A-z0-9.]){1,}[@]([A-z0-9]){1,10}[.]([A-z]){2,5}$";
                break;
            case PHONE :
                field = "^([+]94{1,3}|[0])([1-9]{2})([0-9]){7}$";
                break;
            case ADDRESS :
                field = "(?:\\d+[A-Z]?,\\s*)?.+\\s*,\\s*.+";
                break;
            case NIC :
                field = "^([0-9]{9}[x|X|v|V]|[0-9]{12})$";
                break;
            case DOUBLE :
                field = "^([0-9]){1,}[.]([0-9]){1,}$";
                break;
            case INT :
                field = "^\\d+$";
                break;
            case DURATION :
                field = "\\d+\\s+(Years|Months|Days)";
                break;
            case TIME :
                field = "\\b(?:0?[1-9]|1[0-2]):[0-5][0-9]\\s(?:AM|PM)\\b";
                break;
            case MONTHOFPAY :
                field = "^(?:January|February|March|April|May|June|July|August|September|October|November|December)\\s\\d{4}$";
                break;
            case OTP :
                field = "^\\d{6}$";
                break;
            case NOTE :
                field = "^.{0,32}$";
                break;
        }

        Pattern pattern = Pattern.compile(field);

        if(text != null){
            if(text.trim().isEmpty()){
                return false;
            }
        } else {
            return false;
        }

        Matcher matcher = pattern.matcher(text);

        if(matcher.matches()){
            return true;
        }
        return false;
    }

    public static boolean setTextColor(TextField location, JFXTextField field){
        if (Regex.isTextFieldValid(location,field.getText())){
            field.setFocusColor(Paint.valueOf("Green"));
            field.setUnFocusColor(Paint.valueOf("Green"));
            return true;
        }else {
            field.setFocusColor(Paint.valueOf("Red"));
            field.setUnFocusColor(Paint.valueOf("Red"));
            return false;
        }
    }
}
