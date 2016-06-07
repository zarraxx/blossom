package cn.net.xyan.blossom.platform.service;

import cn.net.xyan.blossom.core.i18n.I18NTools;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.i18n.Language;

import java.util.List;
import java.util.Locale;

/**
 * Created by zarra on 16/5/14.
 */
public interface I18NService extends I18NTools {
    Language setupLanguage(Locale locale);

    List<Language> allLanguage(boolean onlyEnable);

    void disableLanguage(Language language);
    void enableLanguage(Language language);

    I18NString setupMessage(String key, String defaultValue);

    String getDefaultMessage(String key);
    void setDefaultMessage(String key,String defaultValue);

    void setupLocaleMessage(I18NString message,Language language,String value);
    void setupLocaleMessage(String key,Language language,String value);

    void setLocaleMessage(I18NString message,Language language,String value);
    void setLocaleMessage(String key,Language language,String value);
    //void setLocaleMessage(String key,Locale locale,String value);

    List<I18NString> allMessage();

    String i18nMessage(String key);
    String i18nMessage(String key,Language language);
    //String i18nMessage(String key,Locale locale);
}
