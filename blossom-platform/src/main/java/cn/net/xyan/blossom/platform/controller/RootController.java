package cn.net.xyan.blossom.platform.controller;

import cn.net.xyan.blossom.platform.dao.I18NStringDao;
import cn.net.xyan.blossom.platform.dao.LanguageDao;
import cn.net.xyan.blossom.platform.dao.StatusDao;
import cn.net.xyan.blossom.platform.entity.dict.UserStatus;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.i18n.Language;
import cn.net.xyan.blossom.platform.service.UISystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    UISystemService uiSystemService;

    Logger logger = LoggerFactory.getLogger(RootController.class);

    @PostConstruct
    public void setup(){
        logger.info("abc");

    }

    @RequestMapping("/test")
    public @ResponseBody String indexPage(){
//        try {
//            //uiSystemService.deleteAllPage();
//        }catch (Throwable e){
//
//        }
        return "index";
    }
}
