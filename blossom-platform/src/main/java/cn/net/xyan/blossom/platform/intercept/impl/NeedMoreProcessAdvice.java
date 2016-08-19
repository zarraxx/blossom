package cn.net.xyan.blossom.platform.intercept.impl;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterReturningAdvice;
/**
 * Created by zarra on 16/8/19.
 */
public class NeedMoreProcessAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // TODO Auto-generated method stub

        System.out.println("method  name:" + methodInvocation.getMethod().getName());

       // System.out.println("method  arguments" + Arrays.toString(methodInvocation.getArguments()));

        System.out.println("Around  method : before ");

        try{

            Object result = methodInvocation.proceed();

            System.out.println("Around method : after ");
            return  result;

        }catch(IllegalArgumentException e){

            System.out.println("Around method : throw  an  exception ");
            throw  e;
        }
    }

}
