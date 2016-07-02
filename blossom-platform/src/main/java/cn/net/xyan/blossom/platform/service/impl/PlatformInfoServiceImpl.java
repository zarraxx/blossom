package cn.net.xyan.blossom.platform.service.impl;

import cn.net.xyan.blossom.core.utils.ExceptionUtils;
import cn.net.xyan.blossom.platform.service.DictService;
import cn.net.xyan.blossom.platform.service.InstallerAdaptor;
import cn.net.xyan.blossom.platform.service.PlatformInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Created by zarra on 16/6/7.
 */
public class PlatformInfoServiceImpl extends InstallerAdaptor implements PlatformInfoService {

    @Autowired
    DictService dictService;

    Map<String,ArtifactInfo> cache = new HashMap<>();


    public ArtifactInfo readArtifactInfoFile(Resource resource){
        try {
            InputStream inputStream = resource.getInputStream();
            Properties properties = new Properties();
            properties.load(inputStream);

            String name = properties.getProperty(PropertyName);
            String version = properties.getProperty(PropertyVersion);
            String timestamp = properties.getProperty(PropertyTimestamp);

            if (version == null) version = "unknow";

            if (version == null) timestamp = "unknow";

            if (name != null){
                return new ArtifactInfo(name,version,timestamp);
            }

        } catch (IOException e) {
            ExceptionUtils.errorString(e);
        }

        return null;
    }

    @Override
    public void beforeSetup() {
        try {

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(String.format("classpath*:%s",ArtifactInfo));
            if (resources !=null) {
                for (Resource resource : resources) {
                    ArtifactInfo artifactInfo = readArtifactInfoFile(resource);

                    if (artifactInfo!=null){
                        cache.put(artifactInfo.getArtifactId(),artifactInfo);
                    }
                }
            }

            dictService.setupVariable(KeyMainArtifactId,PlatformArtifactId);


        } catch (IOException e) {
            ExceptionUtils.errorString(e);
        }
    }

    @Override
    public Collection<ArtifactInfo> allArtifactInfo() {
        return new LinkedList<>(cache.values());
    }

    @Override
    public ArtifactInfo platformArtifactInfo() {

        String mainArtifactId = dictService.getVariable(KeyMainArtifactId);

        return cache.get(mainArtifactId);
    }
}
