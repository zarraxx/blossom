package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.platform.dao.I18NStringDao;
import cn.net.xyan.blossom.platform.dao.LanguageDao;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.i18n.Language;
import cn.net.xyan.blossom.platform.service.I18NService;
import cn.net.xyan.blossom.platform.service.InstallerAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Created by zarra on 16/5/30.
 */
public class I18NServiceImpl extends InstallerAdaptor implements I18NService {

    @Autowired
    LanguageDao languageDao;

    @Autowired
    I18NStringDao stringDao;

    @Override
    @Transactional
    public Language setupLanguage(Locale locale) {
        String tag = locale.toLanguageTag();
        Language language = languageDao.findOne(tag);
        if (language == null){
            language = new Language(locale,true);
            language = languageDao.saveAndFlush(language);
        }
        return language;
    }

    @Override
    public List<Language> allLanguage(boolean onlyEnable) {
        if (onlyEnable){
            return languageDao.findByAvailable(true);
        }else{
            return languageDao.findAll();
        }
    }

    @Override
    @Transactional
    public void disableLanguage(Language language) {
        language.setAvailable(false);
        languageDao.saveAndFlush(language);
    }

    @Override
    @Transactional
    public void enableLanguage(Language language) {
        language.setAvailable(true);
        languageDao.saveAndFlush(language);
    }

    @Override
    @Transactional
    public I18NString setupMessage(String key, String defaultValue) {
        I18NString string = stringDao.findOne(key);
        if (string == null){
            string = new I18NString(key,defaultValue);
            string = stringDao.saveAndFlush(string);
        }
        return string;
    }

    @Override
    public String getDefaultMessage(String key) {
        I18NString string = stringDao.findOne(key);
        return string.getDefaultValue();
    }

    @Override
    @Transactional
    public void setDefaultMessage(String key, String defaultValue) {
        I18NString string = stringDao.findOne(key);
        string.setDefaultValue(defaultValue);
        stringDao.saveAndFlush(string);
    }

    @Override
    @Transactional
    public void setLocaleMessage(I18NString message, Language language, String value) {
        message.putValue(language,value);
        stringDao.saveAndFlush(message);
    }

    @Override
    public void setLocaleMessage(String key, Language language, String value) {
        I18NString i18NString = setupMessage(key,value);
        setLocaleMessage(i18NString,language,value);
    }

    @Override
    public void setLocaleMessage(String key, Locale locale, String value) {
        Language language = setupLanguage(locale);
        setLocaleMessage(key,language,value);
    }

    @Override
    public List<I18NString> allMessage() {
        return stringDao.findAll();
    }

    @Override
    public String i18nMessage(String key, Language language) {
        I18NString string = stringDao.findOne(key);
        return string.value(language);
    }

    @Override
    public String i18nMessage(String key, Locale locale) {
        String tag = locale.toLanguageTag();
        I18NString string = stringDao.findOne(key);
        return string.value(locale);
    }

    @Override
    public void setupDefaultMessage(String key, String defaultValue) {
        setupMessage(key,defaultValue);
    }

    @Override
    public void beforeSetup() {
        setupLanguage(Locale.US);
        setupLanguage(Locale.SIMPLIFIED_CHINESE);
    }
}
