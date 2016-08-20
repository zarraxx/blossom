package cn.net.xyan.blossom.platform.controller;

import cn.net.xyan.blossom.platform.dao.I18NStringDao;
import cn.net.xyan.blossom.platform.dao.LanguageDao;
import cn.net.xyan.blossom.platform.dao.StatusDao;
import cn.net.xyan.blossom.platform.entity.dict.UserStatus;
import cn.net.xyan.blossom.platform.entity.i18n.I18NString;
import cn.net.xyan.blossom.platform.entity.i18n.Language;
import cn.net.xyan.blossom.platform.intercept.annotation.NeedLogInterceptor;
import cn.net.xyan.blossom.platform.service.DictService;
import cn.net.xyan.blossom.platform.service.UISystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * Created by zarra on 16/5/13.
 */
@Controller
@RequestMapping("/")
public class RootController implements InitializingBean {


    @Autowired
    DictService dictService;

    Logger logger = LoggerFactory.getLogger(RootController.class);


    @RequestMapping("/")
    @NeedLogInterceptor
    public String indexPage(){
        String redirectPath = dictService.getVariable(DictService.KeyROOTPage);
        return String.format("redirect:%s",redirectPath);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dictService.setupVariable(DictService.KeyROOTPage,"/ui/root");
    }
}
