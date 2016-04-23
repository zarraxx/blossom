package cn.net.xyan.blossom.core.jpa.utils.sequence;

import java.io.Serializable;

/**
 * Created by xiashenpin on 16/1/20.
 */
final  public class DoNothingSequenceFormat implements ISequenceFormat {
    //此类型不可创建 不可继承
    private DoNothingSequenceFormat(){

    }

    @Override
    public Serializable formatSequence(Class<? extends Serializable> returnClass, Long sequence, Object[] propertyStates, String[] propertyNames, String propertyName) {
        return null;
    }
}
