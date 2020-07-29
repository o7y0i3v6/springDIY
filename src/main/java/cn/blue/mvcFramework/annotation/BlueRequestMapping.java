package cn.blue.mvcFramework.annotation;

import java.lang.annotation.*;

/**
 * @author blue
 * @ClassName BlueController
 * @Description @Target({ElementType.TYPE})表示在类上使用，
 * ElementType.METHOD 表示在方法上使用
 * 这个注解只能作用在class上，@Retention 表示是运行时阶段。
 *
 * @date 2020/7/16 20:18
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BlueRequestMapping {
    String value() default "";
}
