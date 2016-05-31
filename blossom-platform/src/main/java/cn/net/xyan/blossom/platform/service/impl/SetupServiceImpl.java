package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.platform.service.UISystemService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by zarra on 16/5/31.
 */
@Service
public class SetupServiceImpl implements InitializingBean {

    @Autowired
    UISystemService uiSystemService;

    @Override
    public void afterPropertiesSet() throws Exception {
        uiSystemService.setup();
    }


}
