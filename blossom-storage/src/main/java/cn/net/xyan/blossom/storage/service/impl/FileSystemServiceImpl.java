package cn.net.xyan.blossom.storage.service.impl;

import cn.net.xyan.blossom.platform.service.InstallerAdaptor;
import cn.net.xyan.blossom.storage.entity.DirectoryNode;
import cn.net.xyan.blossom.storage.service.FileSystemService;
import cn.net.xyan.blossom.storage.service.StorageService;
import cn.net.xyan.blossom.storage.support.SlashPath;
import cn.net.xyan.blossom.storage.ui.component.Directory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zarra on 2016/10/20.
 */
public class FileSystemServiceImpl extends InstallerAdaptor implements FileSystemService {

    @Autowired
    StorageService storageService;

    @Override
    public void beforeSetup() {
        SlashPath home = SlashPath.fromString("/home");
        SlashPath admin = SlashPath.fromString("/home/admin");
        SlashPath sys = SlashPath.fromString("/sys");


        if (!storageService.exist(home)){

            storageService.mkdir(null,home.getLastName().toString());
        }

        if (!storageService.exist(sys)){
            System.out.println(sys.getLastName().toString());
            storageService.mkdir(null,sys.getLastName().toString());
        }

        if (!storageService.exist(admin)){
            DirectoryNode homeDir = (DirectoryNode) storageService.find(home);
            storageService.mkdir(homeDir,admin.getLastName().toString());
        }

    }
}
