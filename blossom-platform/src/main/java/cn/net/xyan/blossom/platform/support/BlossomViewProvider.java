package cn.net.xyan.blossom.platform.support;

import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import cn.net.xyan.blossom.core.utils.StringUtils;
import cn.net.xyan.blossom.platform.entity.Module;
import cn.net.xyan.blossom.platform.entity.VaadinViewModule;
import cn.net.xyan.blossom.platform.service.UISystemService;
import com.vaadin.navigator.View;
import com.vaadin.spring.navigator.SpringViewProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

/**
 * Created by zarra on 16/7/2.
 */
public class BlossomViewProvider extends SpringViewProvider {

    public static final String HomeView = "home";

    Logger logger = LoggerFactory.getLogger(BlossomViewProvider.class);

    UISystemService uiSystemService;


    public BlossomViewProvider(ApplicationContext applicationContext, BeanDefinitionRegistry beanDefinitionRegistry) {
        super(applicationContext, beanDefinitionRegistry);
    }


    public void setUiSystemService(UISystemService uiSystemService) {
        this.uiSystemService = uiSystemService;
    }

    @Override
    public String getViewName(String viewAndParameters) {
        String viewName =  super.getViewName(viewAndParameters);
        if (viewName == null){
            if (StringUtils.isEmpty(viewAndParameters)){
                viewName = HomeView;
            }else{
                String[] split = viewAndParameters.split("/");
                viewName = split[0];
            }
        }
        return viewName;
    }

    @Override
    public View getView(String viewName) {
        View view =  super.getView(viewName);
        if (view == null){
            if (StringUtils.isEmpty(viewName))
                view = super.getView(HomeView);
            else{
                Module module = uiSystemService.moduleByCode(viewName);
                if (module instanceof VaadinViewModule){
                    VaadinViewModule viewModule = (VaadinViewModule) module;
                    String beanName = viewModule.getViewBeanName();
                    view = (View) ApplicationContextUtils.getBean(beanName);
                }
            }
        }

        return view;
    }
}
