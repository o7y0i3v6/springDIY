package cn.blue.mvcFramework.annotation;

import javax.xml.bind.Element;
import java.lang.annotation.*;

/**
 * @author blue
 * @ClassName BlueController
 * @Description @Target({ElementType.TYPE})表示
 * 这个注解只能作用在class上，@Retention(RetentionPolicy.RUNTIME) 表示是运行时阶段可用。
 *
 * @date 2020/7/16 20:18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BlueController {
    String value() default "";
}
