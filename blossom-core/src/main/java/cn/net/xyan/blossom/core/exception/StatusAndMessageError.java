package cn.net.xyan.blossom.core.exception;

/**
 * Created by zarra on 16/4/23.
 */
public class StatusAndMessageError extends RuntimeException {
    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public StatusAndMessageError(int status, Throwable e){
        super(e);
        setStatus(status);
    }
    public StatusAndMessageError(int status, String message){
        super(message);
        setStatus(status);
    }

}
