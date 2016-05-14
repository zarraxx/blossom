package cn.net.xyan.blossom.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zarra on 16/5/13.
 */
//@Controller
//@RequestMapping("/root")
public class RootController {
    //@RequestMapping("/")
    public @ResponseBody String indexPage(){
        return "index";
    }
}
