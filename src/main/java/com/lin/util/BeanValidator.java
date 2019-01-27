package com.lin.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lin.exception.ParamException;
import org.apache.commons.collections.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * Created by linziyu on 2019/1/22.
 *
 *参数校验工具
 *
 */
public class BeanValidator{

        //校验工厂
        private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        /*
        map中key对应字段 value对应字段的错误信息
         */
        public static <T> Map<String, String> validate(T t, Class... groups) {
            Validator validator = validatorFactory.getValidator();//获取校验工具
            Set validateResult = validator.validate(t, groups);//自动的获取校验的结果
            if (validateResult.isEmpty()) {//空的时候直接返回
                return Collections.emptyMap();
            } else {
                LinkedHashMap errors = Maps.newLinkedHashMap();//Google Java中的写法 也就是 new 一个LinkedHashMap
                Iterator iterator = validateResult.iterator();//迭代器
                while (iterator.hasNext()) {
                    //每个值都是校验的结果 封装在ConstraintViolation中
                    ConstraintViolation violation = (ConstraintViolation)iterator.next();
                    //把错误信息放入map中
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                }
                return errors;
            }
        }
        /*
        检验集合
         */
        public static Map<String, String> validateList(Collection<?> collection) {
            Preconditions.checkNotNull(collection);//判断是否为空  Google Java 提供的校验API
            Iterator iterator = collection.iterator();//迭代器
            Map errors;

            do {
                if (!iterator.hasNext()) {
                    return Collections.emptyMap();
                }
                Object object = iterator.next();
                errors = validate(object, new Class[0]);
            } while (errors.isEmpty());

            return errors;
        }

        /*
        检验对象
         */
        public static Map<String, String> validateObject(Object first, Object... objects) {
            if (objects != null && objects.length > 0) {//可变参数不为空
                return validateList(Lists.asList(first, objects));//包装成List进行处理
            } else {
                return validate(first, new Class[0]);//参数数组为空 直接处理前面的first
            }
        }
        /*
        参数对象校验
         */
        public static void check(Object param) throws ParamException {
            Map<String, String> map = BeanValidator.validateObject(param);
            if (MapUtils.isNotEmpty(map)) {//判断是否为空
                throw new ParamException(map.toString());
            }
        }
}
