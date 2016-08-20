package cn.net.xyan.blossom.platform.entity.log;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by zarra on 16/8/19.
 */
@Entity
@Table(name = "sys_request_log")
public class RequestLog {
    String uuid;
    Date date;
    String remoteIP;

    String targetClassName;
    String targetMethodName;

    String content;

    String type;

    public RequestLog(){
        this(null,null);
    }

    public RequestLog(Object target,Method method){
        if (target!=null)
            setTargetClassName(target.getClass().getName());
        if (method!=null)
            setTargetMethodName(method.getName());
        setDate(new Date());
    }

    @Column(name = "content",length = Integer.MAX_VALUE)
    @Lob
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public void setTargetMethodName(String targetMethodName) {
        this.targetMethodName = targetMethodName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Id
    @GeneratedValue(
            generator = "system-uuid"
    )
    @GenericGenerator(
            name = "system-uuid",
            strategy = "uuid"
    )
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


}
