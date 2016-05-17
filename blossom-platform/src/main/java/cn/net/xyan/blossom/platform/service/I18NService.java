package cn.net.xyan.blossom.platform.service;

import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.i18n.Language;

import java.util.List;
import java.util.Locale;

/**
 * Created by zarra on 16/5/14.
 */
public interface I18NService {
    void setupLanguage(Locale locale);
    void disableLanguage(Language language);
    void enableLanguage(Language language);

    I18NString setupMessage(String key, String defaultValue);

    void setLocaleMessage(I18NString message,Language language,String value);
    void setLocaleMessage(String key,Locale locale,String value);

    List<I18NString> allMessage();

    String i18nMessage(String key,Locale locale);
}
