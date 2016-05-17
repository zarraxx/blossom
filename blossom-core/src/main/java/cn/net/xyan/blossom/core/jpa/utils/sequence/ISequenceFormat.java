package cn.net.xyan.blossom.core.jpa.utils.sequence;

import java.io.Serializable;

/**
 * Created by xiashenpin on 16/1/20.
 */
public interface ISequenceFormat {
    Serializable formatSequence(Class<? extends Serializable> returnClass, Object entity,Long sequence, Object[] propertyStates, String[] propertyNames, String propertyName);
}
