package cn.net.xyan.blossom.core.i18n;

import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.vaadin.spring.i18n.I18N;

import java.util.Locale;

/**
 * Created by zarra on 16/5/13.
 */
public class TR {


    final public static String Login = "ui.button.Login";

    final public static String Logout = "ui.button.Logout";

    final public static String Add = "ui.button.add";

    final public static String New = "ui.button.new";

    final public static String Edit = "ui.button.edit";

    final public static String Save = "ui.button.save";

    final public static String Remove = "ui.button.remove";

    final public static String Delete = "ui.button.delete";

    final public static String Filter = "ui.button.filter";

    final public static String OK = "ui.button.ok";

    final public static String Cancel = "ui.button.cancel";

    final public static String Close = "ui.button.close";

    final public static String View = "ui.action.view";

    final public static String Confirm = "ui.view.title.confirm";

    private static I18NMessageProvider messageSource;

    private static I18NTools tools;

    public static I18NMessageProvider getI18N(){
        if (messageSource == null) {
            messageSource = ApplicationContextUtils.getBean(I18NMessageProvider.class);
        }
        return messageSource;
    }

    public static I18NTools getTools(){
        if (tools == null) {
            tools = ApplicationContextUtils.getBean(I18NTools.class);
        }
        return tools;
    }

    public static String m(String key){
        return m(key,key);
        //return defaultValue;
    }

    public static String m(String key,String defaultValue,Object ... objs){
        Locale locale = LocaleContextHolder.getLocale();
        getTools().setupDefaultMessage(key,defaultValue);
        return getI18N().getMessage(key,objs,defaultValue,locale);
        //return defaultValue;
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
