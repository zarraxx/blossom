package cn.net.xyan.blossom.core.jpa.utils;


import cn.net.xyan.blossom.core.exception.StatusAndMessageError;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.Map;

/**
 * Created by xiashenpin on 16/1/17.
 */
public class JPA {

    public enum Operator{
        IsNull,NotNull,In,NotIn,Equal,NotEqual,Greater,GreaterOrEqual,Less,LessOrEqual,Between,Like,NotLike
    }

    public static Predicate oneEqualOneLogic(CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(criteriaBuilder.literal(1),1);
    }

    public static <T> Predicate advEqualLogic(Map<? extends Attribute ,Object> valueMap,Root<T> root, CriteriaBuilder criteriaBuilder,boolean trueIsAndFalseIsOr){
        Predicate p = null;
        for (Map.Entry<? extends Attribute,Object> entry:valueMap.entrySet()){
            Expression<?> expression = pathFromRoot(root,entry.getKey());
            Predicate inP = criteriaBuilder.equal(expression,entry.getValue());
            if (p == null){
                p = inP;
            }else{
                if (trueIsAndFalseIsOr)
                    p = criteriaBuilder.and(p,inP);
                else
                    p = criteriaBuilder.or(p,inP);
            }
        }
        return p;
    }


    public static <T> Predicate allEqualLogic(Map<? extends Attribute,Object> valueMap,Root<T> root, CriteriaBuilder criteriaBuilder){
        return advEqualLogic(valueMap,root,criteriaBuilder,true);
    }

    public static <T> Predicate anyOneEqualLogic(Map<? extends Attribute,Object> valueMap,Root<T> root, CriteriaBuilder criteriaBuilder){
        return advEqualLogic(valueMap,root,criteriaBuilder,false);
    }

    public static <V> Predicate simpleLogic(final Operator operator,Expression<V> path,V value,CriteriaBuilder criteriaBuilder){
        switch (operator) {
            case IsNull:
                return criteriaBuilder.isNull(path);
            case NotNull:
                return criteriaBuilder.isNotNull(path);
            case Equal:
                return criteriaBuilder.equal(path, value);
            case NotEqual:
                return criteriaBuilder.notEqual(path, value);
            default:
                throw new StatusAndMessageError(-99, "Unknow operator:" + operator);
        }
    }

    public static <V extends Comparable<? super V>> Predicate comparableLogic(final Operator operator,Expression<V> path,V value,CriteriaBuilder criteriaBuilder){
        switch (operator){
            case IsNull:
            case NotNull:
            case Equal:
            case NotEqual:
                return simpleLogic(operator,path,value,criteriaBuilder);
            case Greater:
                return criteriaBuilder.greaterThan(path,value);
            case GreaterOrEqual:
                return criteriaBuilder.greaterThanOrEqualTo(path,value);
            case Less:
                return criteriaBuilder.lessThan(path,value);
            case LessOrEqual:
                return criteriaBuilder.lessThanOrEqualTo(path,value);
            default:
                throw new StatusAndMessageError(-99, "Unknow operator:" + operator);
        }
    }

    public static <V extends Comparable<? super V>> Predicate comparableLogic(final Operator operator,Expression<V> path,V begin,V end,CriteriaBuilder criteriaBuilder){
        switch (operator){
            case Between:
                return criteriaBuilder.between(path,begin,end);
            default:
                throw new StatusAndMessageError(-99, "Unknow operator:" + operator);
        }
    }

    public static  Predicate stringLogic(final Operator operator,Expression<String> path,String value,CriteriaBuilder criteriaBuilder){
        if (operator == Operator.Like){
            return criteriaBuilder.like(path,value);
        }else if(operator == Operator.NotLike){
            return criteriaBuilder.notLike(path,value);
        }else{
            return comparableLogic(operator,path,value,criteriaBuilder);
        }
    }

    public static <C,V extends Collection<C>> Predicate collectionLogic(Operator operator,Expression<C> e,V value,CriteriaBuilder criteriaBuilder){
        switch (operator){
            case In:
                return e.in(value);
            case NotIn:
                return e.in(value).not();
            default:
                throw new StatusAndMessageError(-99, "Unknow operator:" + operator);
        }
    }

    public static <C> Predicate collectionLogic(Operator operator,Expression<C> e,Subquery<C> subquery,CriteriaBuilder criteriaBuilder){
        switch (operator){
            case NotIn:
                return e.in(subquery).not();
            case In:
                return e.in(subquery);
            default:
                throw new StatusAndMessageError(-99, "Unknow operator:" + operator);
        }
    }

    public static <T,U,A extends Attribute<T,U>> Expression<U> pathFromRoot(Root<T> root,A attribute){
        if (attribute instanceof SingularAttribute){
            SingularAttribute<T,U> sa = (SingularAttribute<T, U>) attribute;
            return root.get(sa);
        }else if (attribute instanceof PluralAttribute){
            PluralAttribute<T,? extends Collection,?> pa = (PluralAttribute<T, ? extends Collection, ?>) attribute;
            return (Expression<U>) root.get(pa);
        }

        throw new StatusAndMessageError(-90,"不支持的attribute类型:"+attribute.getClass().getCanonicalName());
    }

    public static <T,U> Expression<U> pathFromRoot(Root<T> root,String attribute){
        String[] attributes = attribute.split("\\.");
        Path<?> path = null;
        for (String a : attributes){
            if (path ==null)
                path = root.get(a);
            else
                path = path.get(a);
        }
        return (Expression<U>) path;
    }


    public static <T > Specification<T> oneEqualOne(){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return oneEqualOneLogic(criteriaBuilder);
            }
        };
    }

    public  static <T,V,A extends Attribute<T,V>> Specification<T> condition(final Operator operator, final A attribute, final V value){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Expression<V> path = pathFromRoot(root,attribute);
                return simpleLogic(Operator.Between,path,value,criteriaBuilder);

            }
        };
    }

    public  static <T,V extends Comparable<? super V>,A extends Attribute<T,V>> Specification<T> condition(final Operator operator, final A attribute, final V value){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Expression<V> path = pathFromRoot(root,attribute);
                return comparableLogic(operator,path,value,criteriaBuilder);
            }
        };
    }

    public  static <T,V extends Comparable<? super V>,A extends Attribute<T,V>> Specification<T> condition(final Operator operator, final A attribute, final V begin , final V end){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Expression<V> path = pathFromRoot(root,attribute);
                return comparableLogic(operator,path,begin,end,criteriaBuilder);
            }
        };
    }


    public  static <T,A extends Attribute<T,String>> Specification<T> condition(final Operator operator, final A attribute, final String value){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Expression<String> path = pathFromRoot(root,attribute);
                return stringLogic(operator,path,value,criteriaBuilder);
            }
        };
    }

    public  static <T,C,V extends Collection<C> ,A extends Attribute<T,C>> Specification<T> condition(final Operator operator, final A attribute, final V value){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Expression<C> path = pathFromRoot(root,attribute);
                return collectionLogic(operator,path,value,criteriaBuilder);
            }
        };
    }


    public  static <T,V> Specification<T> condition(final Operator operator, final String attribute, final V value){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Expression<V> path = pathFromRoot(root,attribute);
                return simpleLogic(operator,path,value,criteriaBuilder);
            }
        };
    }

    public  static <T,V extends Comparable<? super V>> Specification<T> condition(final Operator operator, final String attribute, final V value){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Expression<V> path = pathFromRoot(root,attribute);
                if (value instanceof String){
                    String stringValue = (String) value;
                    Expression<String> stringExpression = (Expression<String>) path;
                    return stringLogic(operator,stringExpression,stringValue,criteriaBuilder);
                }else {
                    return comparableLogic(operator, path, value, criteriaBuilder);
                }
            }
        };
    }

    public  static <T,V extends Comparable<? super V>> Specification<T> condition(final Operator operator, final String attribute, final V begin, final V end){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Expression<V> path = pathFromRoot(root,attribute);
                return comparableLogic(operator,path,begin,end,criteriaBuilder);
            }
        };
    }

    public  static <T,C,V extends Collection<C> > Specification<T> condition(final Operator operator, final String attribute, final V value){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Expression<C> path = pathFromRoot(root,attribute);
                return collectionLogic(operator,path,value,criteriaBuilder);
            }
        };
    }


    public static <T> Specifications<T> where(){
        return Specifications.where(JPA.<T>oneEqualOne());
    }

    public static <T> Specifications<T> where(Specification<T> spec){
        Specifications specs =  Specifications.where(JPA.<T>oneEqualOne());
        return  specs.and(spec);
    }


    public  static Pageable pageRequest(int pageIndex, int pageSize, Sort.Order... orders) {
        Sort sort = new Sort(orders);
        Pageable pageSpecification = new PageRequest(pageIndex, pageSize, sort);
        return pageSpecification;
    }

    public static Sort.Order order(String field, Sort.Direction direction) {
        return new Sort.Order(direction, field);
    }

    public static  <T,V,A extends Attribute<T,V>>  Sort.Order order(A attribute, Sort.Direction direction) {
        return order(attribute.getName(),direction);
    }

    public static  <T,V,A extends Attribute<T,V>>  Sort.Order asc(A attribute) {
        return order( attribute, Sort.Direction.ASC);
    }

    public static  <T,V,A extends Attribute<T,V>>  Sort.Order desc(A attribute) {
        return order( attribute, Sort.Direction.DESC);
    }

    public static  Sort.Order asc(String field) {
        return order( field, Sort.Direction.ASC);
    }

    public static  Sort.Order desc(String field) {
        return order( field, Sort.Direction.DESC);
    }


}
