package cn.net.xyan.blossom.storage.service;

import cn.net.xyan.blossom.platform.entity.security.Group;
import cn.net.xyan.blossom.platform.entity.security.User;
import cn.net.xyan.blossom.storage.entity.ArchiveNode;
import cn.net.xyan.blossom.storage.entity.DirectoryNode;
import cn.net.xyan.blossom.storage.entity.Node;
import cn.net.xyan.blossom.storage.support.SlashPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;

/**
 * Created by zarra on 2016/10/18.
 */
public interface StorageService {
    boolean isOwn(Node node, User user);
    boolean isSameGroup(Node node, Group group);
    boolean canRead(Node node, User user);
    boolean canWrite(Node node, User user);

    Node rename(Node node,String newName);
    void delete(Node node);

    Node move(Node node,DirectoryNode dest);


    ArchiveNode touch(DirectoryNode parent,String name);
    ArchiveNode save(DirectoryNode parent,String name,InputStream in);

    DirectoryNode mkdir(DirectoryNode parent,String name);
    Node find(DirectoryNode parent,String name);
    Node find(SlashPath slashPath);
    boolean exist(SlashPath slashPath);
    List<Node> findAll(DirectoryNode parent);

    Page<Node> findAll(DirectoryNode parent, Pageable pageable);

    InputStream open(DirectoryNode parent,String name);



}
