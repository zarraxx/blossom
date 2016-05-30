package cn.net.xyan.blossom.core.i18n;

import java.util.Locale;

/**
 * Created by zarra on 16/5/13.
 */
public class TR {


    final public static String Login = "ui.button.Login";

    final public static String Logout = "ui.button.Logout";

    public static String m(String key,String defaultValue,Object ... objs){
        return defaultValue;
    }

    public static String localeDisplayName(Locale locale){
        Locale english = Locale.ENGLISH;
        if (english.getLanguage().equals(locale.getLanguage())){
            return locale.getDisplayName(english);
        }else{
            return String.format("%s - %s",locale.getDisplayName(english),locale.getDisplayName(locale));
        }
    }
}
