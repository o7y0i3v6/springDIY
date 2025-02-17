package cn.blue.mvcFramework.servlet;

import cn.blue.mvcFramework.annotation.BlueAutowired;
import cn.blue.mvcFramework.annotation.BlueController;
import cn.blue.mvcFramework.annotation.BlueRequestMapping;
import cn.blue.mvcFramework.annotation.BlueService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author blue
 * @ClassName BlueDispatcherServlet
 * @Description TODO
 * @date 2020/7/17 18:18
 */
public class  BlueDispatcherServlet extends HttpServlet {

    private Properties p = new Properties();

    private List<String> classNames = new ArrayList<String>();

    private Map<String,Object> ioc = new HashMap<String,Object>();

    private Map<String, Method> handlerMapping  = new HashMap<String,Method>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    //6.等待请求，进入运行阶段
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //用户请求得url
        String url = req.getRequestURI();
        //获得一个相对路径
        String contextPath = req.getContextPath();

        System.out.println("url: "+url);
        System.out.println("contextPath: "+contextPath);


        url.replace(contextPath,"").replaceAll("/+","/");

        if(!handlerMapping.containsKey(url)){
            resp.getWriter().write("404 not Found!!");
            return;
        }

        Method m = handlerMapping.get(url);
        /*
            反射的方法
            需要两个参数，第一个拿到这个method的insstance，第二个参数，要拿到实参
            从request中取值
         */
        //m.invoke( )
        //System.out.println("=========="+m);
    }

    /**
     * @Description 初始化阶段调用的方法
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.加载配置文件
         doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2.根据配置文件扫描所有相关的类
         doScanner(p.getProperty("scanPackage"));
        //3.初始化所有的相关类的实例，并且将其放入到IOC容器中，也就是Map中
         doInstance();
        //4.实现自动依赖注入
         doAutowried();
        //5.初始化HandlerMapping
        initHandlerMapping();
        
    }

    private void initHandlerMapping() {
        if(ioc.isEmpty()){return;};

        for(Map.Entry<String,Object> entry:ioc.entrySet()){
           Class<?> clazz = entry.getValue().getClass();

           if(!clazz.isAnnotationPresent(BlueController.class)){continue;};

           String baseUrl = "";
           //去找被RequestMapping注解修饰的类
           if(clazz.isAnnotationPresent(BlueRequestMapping.class)){
               //拿到类上的注解
               BlueRequestMapping blueRequestMapping =  clazz.getAnnotation(BlueRequestMapping.class);
               //如果给注解传参数了，拿到参数值,和controller注解搭配RequestMapping注解一定有值。
               baseUrl = blueRequestMapping.value();
           }
            //把所有方法全部获取到
            Method[] methods = clazz.getMethods();
            for(Method method:methods){
                if(!method.isAnnotationPresent(BlueRequestMapping.class)){continue;}
                //拿到方法上的注解
                BlueRequestMapping blueRequestMapping =  method.getAnnotation(BlueRequestMapping.class);

                String  url = (baseUrl+blueRequestMapping.value()).replaceAll("/+","/");
//                System.out.println("baseUrl:"+baseUrl);
//                System.out.println("value():"+blueRequestMapping.value());
                handlerMapping.put(url,method);

                System.out.println("Mapping:"+url+",method:"+method);


            }
        }
    }

    private void doAutowried() {
        if(ioc.isEmpty()){
            return;
        }
        //Map ioc中所有的对象都是Entry,
        for(Map.Entry<String,Object> entry:ioc.entrySet()){

            /*
                类只有一个，但字段有很多，所以要用数组装。
                拿到所有字段，不管是private还是protected还是default都要强制注入
             */
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            //遍历字段数组
            for(Field field:fields){
                if(!field.isAnnotationPresent(BlueAutowired.class)){
                    continue;
                }
                //拿到Autowired字段
                BlueAutowired autowired  = field.getAnnotation(BlueAutowired.class);
                //拿到指定字段的字段名
                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                //打开...强制授权访问
                field.setAccessible(true);

                try {
                    //给被自动装配注解修饰的字段注入值,实现依赖注入
                    field.set(entry.getValue(),ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }

            }
        }
    }

    private void doInstance() {
       if(classNames.isEmpty()){
            return;
       }

       try{
            for(String className:classNames){

                Class<?> clazz = Class.forName(className);
                //接下来进入bean的实例化阶段，初始化IOC容器

                /*
                        IOC容器规则
                        1.key默认用类名首字母小写
                        2.如果用户自定义名字，那么要先选择用自定义名字
                        3.
                 */
                //判断是否被Controller注解修饰
                Boolean isController =  clazz.isAnnotationPresent(BlueController.class);
                //判断是否被Service注解修饰
                Boolean iService =  clazz.isAnnotationPresent(BlueService.class);
                //注意这里不能写成！(isController&iService)，不是一个意思
                Boolean NotControllerAndService = !isController&!iService;

                if(isController){

                    String beanName = clazz.getSimpleName();

                    ioc.put(beanName,clazz.newInstance());
                }
                /*
                    在这里的代码使得被修饰的实现类和接口都能被扫描到。
                    因为受判断控制，没有注解修饰的化，扫描接口的代码就不走
                 */
                if(iService){
                    //2. 如果用户自定义名字，那么要先选择自定义名字
                    BlueService service = clazz.getAnnotation(BlueService.class);
                    //用value()拿到自定义的值,一会作为key
                    String beanName = service.value();
                    if("".equals(beanName.trim())){
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName,instance);

                    /*
                        3. 如果是接口的话，我们可以用巧妙的接口类型作为key，那前面存进去的就是null
                     */
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> i:interfaces){
                        //将接口的类型作为key
                        ioc.put(i.getName(),instance);

                    }
                }
                if(NotControllerAndService){
                    continue;
                }
            }
       }catch(Exception e){
            e.printStackTrace();
       }

    }
    //这个方法的作用是拿到字符串，把开头转换成小写
    private String lowerFirstCase(String str){
        //把字符串转换为字符数组
        char[] chars = str.toCharArray();
        //拿到开头转换为小写，
        chars[0] += 32;
        //然后在放回去
        return String.valueOf(chars);
    }

    private void doScanner(String packageName) {

        URL url =  this.getClass().getClassLoader()
                .getResource( packageName.replaceAll("\\.","/"));

       //拿到目录名
       File classDir = new File(url.getFile());


        //进行递归扫描原理比较简单，是文件夹就再扫
       for(File file:classDir.listFiles()){
            if(file.isDirectory()){
                doScanner(packageName+"."+file.getName());
            }else{
                String className =
                        packageName+"."+file.getName().replace(".class","");

                classNames.add(className);
            }
       }
       //通过这里的打桩可以看到，不管加不加注解，在指定包下的类都可以扫描到。
        System.out.println(classNames);
    }

    private void doLoadConfig(String location) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location);

        try{
            p.load(is);
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(null!=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
