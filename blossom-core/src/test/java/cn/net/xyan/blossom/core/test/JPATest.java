package cn.net.xyan.blossom.core.test;

import cn.net.xyan.blossom.core.jpa.utils.JPA;
import cn.net.xyan.blossom.core.jpa.utils.query.QueryConditionModel;
import cn.net.xyan.blossom.core.test.dao.EntityADao;
import cn.net.xyan.blossom.core.test.dao.EntityBDao;
import cn.net.xyan.blossom.core.test.entity.TestEntityA;
import cn.net.xyan.blossom.core.test.entity.TestEntityB;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zarra on 16/4/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TESTApplication.class)
@WebAppConfiguration
public class JPATest {

    @Autowired
    EntityADao entityADao;

    @Autowired
    EntityBDao entityBDao;

    Logger logger = LoggerFactory.getLogger(JPATest.class);

    @Test
    @Transactional
    public void contextLoads() throws Exception {
        for (int i=0;i<10;i++){
            TestEntityA a = new TestEntityA();
            TestEntityB b = new TestEntityB();
            String v = String.valueOf(i);
            a.setName(v);
            a.setValue(v);

            b.setColPK(v);
            b.setColA("a"+v);
            b.setColB("b"+v);
            b.setColC(i);

            b = entityBDao.saveAndFlush(b);

            a.setEntityB(b);

            a =entityADao.saveAndFlush(a);






            //a.setEntityB(b);
            //entityADao.saveAndFlush(a);


        }

        QueryConditionModel queryConditionModel = new QueryConditionModel("value","1");
        List<TestEntityA> result;
        List<TestEntityB> resultB;
//        queryConditionModel.setOperator(JPA.Operator.GreaterOrEqual);
//        List<TestEntityA> result = entityADao.findAllForModel(Arrays.asList(queryConditionModel));
//
//        for (TestEntityA a : result){
//            logger.info("\t"+a.getValue());
//        }

//        queryConditionModel = new QueryConditionModel("colC",1);
//        resultB = entityBDao.findAllForModel(Arrays.asList(queryConditionModel));
//        for (TestEntityB b : resultB){
//            logger.info("\t"+b.getColC());
//        }

        queryConditionModel = new QueryConditionModel("entityB.colC",1);
        result = entityADao.findAllForModel(Arrays.asList(queryConditionModel));
        for (TestEntityA a : result){
            logger.info("\t"+a.getValue());
        }
    }
}
