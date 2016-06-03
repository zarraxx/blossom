package cn.net.xyan.blossom.core.i18n;

import java.util.Locale;

/**
 * Created by zarra on 16/6/3.
 */
public interface I18NTools {

    void setLocaleMessage(String key,Locale locale,String value);
    String i18nMessage(String key,Locale locale);

    void setupDefaultMessage(String key,String defaultValue);
}
