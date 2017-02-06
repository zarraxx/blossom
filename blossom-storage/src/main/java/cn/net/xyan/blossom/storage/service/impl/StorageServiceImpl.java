package cn.net.xyan.blossom.storage.service.impl;

import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import cn.net.xyan.blossom.core.utils.RequestUtils;
import cn.net.xyan.blossom.core.utils.StringUtils;
import cn.net.xyan.blossom.platform.entity.security.Group;
import cn.net.xyan.blossom.platform.entity.security.Permission;
import cn.net.xyan.blossom.platform.entity.security.User;
import cn.net.xyan.blossom.platform.service.SecurityService;
import cn.net.xyan.blossom.storage.dao.BinaryDataDao;
import cn.net.xyan.blossom.storage.dao.NodeDao;
import cn.net.xyan.blossom.storage.dao.NodeSpecification;
import cn.net.xyan.blossom.storage.entity.ArchiveNode;
import cn.net.xyan.blossom.storage.entity.BinaryDataHolder;
import cn.net.xyan.blossom.storage.entity.DirectoryNode;
import cn.net.xyan.blossom.storage.entity.Node;
import cn.net.xyan.blossom.storage.service.StorageService;
import cn.net.xyan.blossom.storage.support.SlashPath;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by zarra on 2016/10/18.
 */
public class StorageServiceImpl implements StorageService {

    @Autowired
    NodeDao nodeDao;

    @Autowired
    BinaryDataDao dataDao;

    @Autowired
    SecurityService securityService;

    public Permission adminPermission(){
        return securityService.findPermission(SecurityService.PermissionAdmin);
    }

    @Override
    public boolean isOwn(Node node, User user) {
        return node.getOwner().equals(user);
    }

    @Override
    public boolean isSameGroup(Node node, Group group) {
        return node.getOwnerGroup().equals(group);
    }

    @Override
    public boolean canRead(Node node, User user) {
        if (securityService.checkPermissionForUser(user,adminPermission())){
            return true;
        }
        if (node == null)
            return false;
        else if(isOwn(node,user)){
            return true;
        }else if ( (node.getPermissionAll() & Node.READ) !=0){
            return true;
        }

        for (Group group:user.getGroups()){
            if (isSameGroup(node,group)){
                if ( (node.getPermissionGroup() & Node.READ)!=0){
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean canWrite(Node node, User user) {
        if (securityService.checkPermissionForUser(user,adminPermission())){
            return true;
        }
        if (node == null)
            return false;
        else if(isOwn(node,user)){
            return true;
        }else if ( (node.getPermissionAll() & Node.WRITE) !=0){
            return true;
        }

        for (Group group:user.getGroups()){
            if (isSameGroup(node,group)){
                if ( (node.getPermissionGroup() & Node.WRITE)!=0){
                    return true;
                }
            }
        }
        return false;
    }


    public void promiseCanRead(Node node ,User user){
        if (!canRead(node,user)){
            throw new StatusAndMessageError(-10,"not allow read");
        }
    }

    public void promiseCanWrite(Node node, User user){
        if (!canWrite(node,user)){
            throw new StatusAndMessageError(-10,"not allow write");
        }
    }


    public User currentUser(){
        HttpServletRequest httpServletRequest = RequestUtils.httpServletRequest();
        if (httpServletRequest!=null)
            return securityService.currentUser();
        else
            return securityService.findUser(SecurityService.USERAdmin);
    }

    @Override
    @Transactional
    public Node rename(Node node,String newName) {
        promiseCanWrite(node,currentUser());
        node.setTitle(newName);
        node.setModifyDate(new Date());
        return nodeDao.saveAndFlush(node);
    }

    @Override
    @Transactional
    public void delete(Node node) {
        promiseCanWrite(node,currentUser());
        if (node instanceof ArchiveNode){
            ArchiveNode archiveNode = (ArchiveNode) node;
            dataDao.delete(archiveNode.getBinaryDataHolder());
        }
        else if (node instanceof DirectoryNode){
            DirectoryNode directoryNode = (DirectoryNode) node;
            for (Node n : directoryNode.getChildren()){
                delete(n);
            }
        }

        nodeDao.delete(node);
    }



    @Override
    @Transactional
    public Node move(Node node, DirectoryNode dest) {
        promiseCanWrite(node.getParent(),currentUser());
        promiseCanWrite(dest,currentUser());
        node.setParent(dest);
        node.setModifyDate(new Date());
        return nodeDao.saveAndFlush(node);
    }

    public <T extends Node> T newNode(DirectoryNode parent,String name,Class<T> nodeCls){
        if (StringUtils.isEmpty(name))
            throw new NullPointerException();

        Node old = find(parent,name);

        if (old != null){
            throw new StatusAndMessageError(-8,""+name+" exist!");
        }

        try {
            T node = nodeCls.newInstance();

            node.setParent(parent);

            node.setTitle(name);

            node.setCreateDate(new Date());
            node.setModifyDate(node.getCreateDate());
            node.setVisitDate(node.getCreateDate());


            User user = currentUser();
            Group group = null;

            for (Group g : user.getGroups()){
                group = g;
                break;
            }

            if (group == null)
                group = securityService.findGroup(SecurityService.GroupAdmin);
            node.setOwner(user);
            node.setOwnerGroup(group);

            node.setPermissionOwn(Node.READ+Node.WRITE);
            node.setPermissionGroup(Node.READ);
            node.setPermissionAll(Node.READ);

            return nodeDao.saveAndFlush(node);

        } catch (Throwable e) {
            throw new StatusAndMessageError(-9,e);
        }

    }

    public ArchiveNode newArchive(DirectoryNode parent,String name){

        ArchiveNode archive = newNode(parent,name,ArchiveNode.class);

        BinaryDataHolder binaryDataHolder = new BinaryDataHolder();
        binaryDataHolder.setData(new byte[0]);

        binaryDataHolder = dataDao.saveAndFlush(binaryDataHolder);

        archive.setBinaryDataHolder(binaryDataHolder);

        return nodeDao.saveAndFlush(archive);


    }

    @Override
    @Transactional
    public ArchiveNode touch(DirectoryNode parent, String name) {
        promiseCanWrite(parent,currentUser());
        Node node =find(parent,name);
        if (node == null){
            node = newArchive(parent,name);
        }else if (node instanceof ArchiveNode){
            Date current = new Date();
            node.setCreateDate(current);
            node.setModifyDate(current);
            node.setVisitDate(current);

            node = nodeDao.saveAndFlush(node);
        }else{
            throw new StatusAndMessageError(-9,String.format("%s is not file",name));
        }

        ArchiveNode archiveNode = (ArchiveNode) node;

        return archiveNode;

    }

    @Override
    @Transactional
    public ArchiveNode save(DirectoryNode parent, String name, InputStream in) {
        promiseCanWrite(parent,currentUser());
        Node node = find(parent,name);
        if (node == null){
            node = newArchive(parent,name);
        }
        else if (node instanceof DirectoryNode){
            throw new StatusAndMessageError(-9,String.format("%s is exist and is directory",name));
        }

        ArchiveNode archiveNode = (ArchiveNode) node;
        promiseCanWrite(archiveNode,currentUser());
        try {

            BinaryDataHolder data = archiveNode.getBinaryDataHolder();
            data.setData(StreamUtils.copyToByteArray(in));
            Date current = new Date();
            archiveNode.setModifyDate(current);
            archiveNode.setVisitDate(current);
            dataDao.saveAndFlush(data);
            return nodeDao.saveAndFlush(archiveNode);
        }catch (IOException ioE){
            throw  new StatusAndMessageError(-9,ioE);
        }


    }

    @Override
    @Transactional
    public DirectoryNode mkdir(DirectoryNode parent, String name) {
        promiseCanWrite(parent,currentUser());

        if (parent!=null) {
            Date current = new Date();
            parent.setModifyDate(current);
            parent.setVisitDate(current);
            nodeDao.saveAndFlush(parent);
        }
        DirectoryNode directory = newNode(parent,name,DirectoryNode.class);
        return directory;
    }

    @Override
    public Node find(DirectoryNode parent, String name) {
        promiseCanRead(parent,currentUser());
        if (parent!=null) {
            parent.setVisitDate(new Date());
            nodeDao.saveAndFlush(parent);
        }
        return nodeDao.findByParentAndTitle(parent,name);
    }

    @Override
    public Node find(SlashPath slashPath) {
        SlashPath sp = slashPath.normalize();
        Node node = null;
        if (sp.equals(SlashPath.ROOT)){

        }
        else if (sp.isAbsolute()){
            int size = sp.getNameCount();
            for (int i=0;i<size;i++){
                SlashPath s = sp.getName(i);
                if (node == null || node instanceof DirectoryNode){
                    DirectoryNode directoryNode = (DirectoryNode) node;
                    node = find(directoryNode,s.toString());
                }

                else {
                    throw new StatusAndMessageError(-9,"invalid path:"+slashPath.toString());
                }
            }


        }else{
            throw new UnsupportedOperationException("only support absolute path");
        }

        return node;
    }

    @Override
    public DirectoryNode userHome() {
        Node home = find(null,"home");
        User user = currentUser();
        DirectoryNode userHome = (DirectoryNode) find((DirectoryNode) home,user.getLoginName());
        return userHome;
    }

    @Override
    public boolean exist(SlashPath slashPath) {
        try{
            return  find(slashPath)!=null;
        }catch (Throwable e){
            return false;
        }
    }

    @Override
    public List<Node> findAll(DirectoryNode parent) {
        promiseCanRead(parent,currentUser());
        if (parent!=null) {
            parent.setVisitDate(new Date());
            nodeDao.saveAndFlush(parent);
        }
        Specifications<Node> w = Specifications.where(NodeSpecification.specificationByParent(parent));
        return nodeDao.findAll(w);
    }

    @Override
    public Page<Node> findAll(DirectoryNode parent, Pageable pageable) {
        promiseCanRead(parent,currentUser());
        if (parent!=null) {
            parent.setVisitDate(new Date());
            nodeDao.saveAndFlush(parent);
        }
        Specifications<Node> w = Specifications.where(NodeSpecification.specificationByParent(parent));

        return nodeDao.findAll(w,pageable);
    }

    @Override
    public InputStream open(DirectoryNode parent, String name) {
        ArchiveNode node = (ArchiveNode) find(parent,name);
        if (node == null)
            throw new StatusAndMessageError(-1,String.format("%s not found",name));

        promiseCanRead(node,currentUser());
        if (node!=null) {
            node.setVisitDate(new Date());
            nodeDao.saveAndFlush(node);
        }
        return new ByteArrayInputStream(node.getBinaryDataHolder().getData());
    }

}
