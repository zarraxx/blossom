package cn.net.xyan.blossom.platform.controller;

import cn.net.xyan.blossom.platform.dao.I18NStringDao;
import cn.net.xyan.blossom.platform.dao.LanguageDao;
import cn.net.xyan.blossom.platform.dao.StatusDao;
import cn.net.xyan.blossom.platform.entity.dict.UserStatus;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.i18n.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * Created by zarra on 16/5/13.
 */
@Controller
//@RequestMapping("/root")
public class RootController {

    @Autowired
    StatusDao statusDao;

    @Autowired
    LanguageDao langDao;

    @Autowired
    I18NStringDao stringDao;

    @PostConstruct
    public void setup(){
        langDao.saveAndFlush(new Language(Locale.US));
        langDao.saveAndFlush(new Language(Locale.SIMPLIFIED_CHINESE));
        langDao.saveAndFlush(new Language(Locale.TRADITIONAL_CHINESE));

        I18NString string = new I18NString("string.test","test");

        string.putValue(Locale.SIMPLIFIED_CHINESE,"测试");

        stringDao.saveAndFlush(string);


        statusDao.saveAndFlush(new UserStatus(1,"active"));


    }

    //@RequestMapping("/")
    public @ResponseBody String indexPage(){
        return "index";
    }
}
