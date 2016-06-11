package cn.net.xyan.blossom.platform.ui.view.sys;

import cn.net.xyan.blossom.platform.entity.dict.StatusAndType;
import cn.net.xyan.blossom.platform.service.UISystemService;
import cn.net.xyan.blossom.platform.ui.view.entity.EntityView;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import org.vaadin.spring.sidebar.annotation.FontAwesomeIcon;
import org.vaadin.spring.sidebar.annotation.SideBarItem;

/**
 * Created by zarra on 16/6/7.
 */
@SpringView(name = "sys.dict")
@SideBarItem(sectionId = UISystemService.CatalogSystem, caption = "Dictionary", order = 0)
@FontAwesomeIcon(FontAwesome.COG)
public class DictView extends EntityView<StatusAndType> {
    public DictView(){
        super("Dictionary");
    }

//    @Override
//    public void onClickAdd(Class<? extends StatusAndType> eClass ) {
//        //super.onClickAdd();
//    }
}
