package cn.net.xyan.blossom.platform.dao;

import cn.net.xyan.blossom.core.jpa.utils.EasyJpaRepository;
import cn.net.xyan.blossom.platform.entity.i18n.Language;

import java.util.List;

/**
 * Created by zarra on 16/5/14.
 */
public interface LanguageDao extends EasyJpaRepository<Language,String> {
    List<Language> findByAvailable(boolean available);
}
