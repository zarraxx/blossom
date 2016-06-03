package cn.net.xyan.blossom.platform.support;

import cn.net.xyan.blossom.core.i18n.I18NMessageProvider;
import cn.net.xyan.blossom.platform.service.I18NService;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Created by zarra on 16/6/3.
 */
public class I18NMessageProviderImpl extends I18NMessageProvider {


    I18NService i18NService;

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(I18NService i18NService) {
        this.i18NService = i18NService;
    }


    public MessageFormat resolveCode(String s, Locale locale) {
        String message = null;
        message = i18NService.i18nMessage(s,locale);
        if (message!=null) {
            return new MessageFormat(message, locale);
        }
        return null;
    }
}
