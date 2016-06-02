package cn.net.xyan.blossom.core.support;

import cn.net.xyan.blossom.core.utils.ApplicationContextUtils;
import com.vaadin.addon.jpacontainer.provider.CachingMutableLocalEntityProvider;
import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;

/**
 * Created by zarra on 16/6/2.
 */
public class SpringMutableLocalEntityProvider<T> extends MutableLocalEntityProvider<T> {

    private PlatformTransactionManager transactionManager;

    protected void init(){
        transactionManager = ApplicationContextUtils.getBean(PlatformTransactionManager.class);
    }

    public SpringMutableLocalEntityProvider(Class<T> entityClass) {
        super(entityClass);
        init();
    }

    public SpringMutableLocalEntityProvider(Class<T> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
        init();
    }

    @Override
    protected void runInTransaction(Runnable operation) {
        assert operation != null : "operation must not be null";
        if (isTransactionsHandledByProvider()) {
            TransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction( def );
            try{
                operation.run();
                transactionManager.commit( status );
            }catch (Throwable e){
                transactionManager.rollback(status);
                throw e;
            }
        }else {
            operation.run();
        }
    }
}
