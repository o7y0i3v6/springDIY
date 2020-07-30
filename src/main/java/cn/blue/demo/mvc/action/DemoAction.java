package cn.blue.demo.mvc.action;

import cn.blue.demo.service.IDemoService;
import cn.blue.mvcFramework.annotation.BlueAutowired;
import cn.blue.mvcFramework.annotation.BlueController;
import cn.blue.mvcFramework.annotation.BlueRequestMapping;
import cn.blue.mvcFramework.annotation.BlueRequestParam;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author blue
 * @ClassName DemoAction
 * @Description 这里用了许多自己开发的注解代替原版注解，原版注解需要导包才能用
 *
 * @date 2020/7/16 18:44
 */
@BlueController
@BlueRequestMapping("/demo")
public class DemoAction   {

    @BlueAutowired private IDemoService demoService;

    @BlueRequestMapping("/query.json")
   public void query(HttpServletRequest req, HttpServletResponse resp,
                     @BlueRequestParam("name") String name)  {

       String result = demoService.get(name);

       try {
           resp.getWriter().write(result);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   @BlueRequestMapping("/edit.json")
   public void edit(HttpServletRequest req ,HttpServletResponse resp,
                    @BlueRequestParam("id") Integer id){

   }

   @BlueRequestMapping("/remove.json")
   public void remove(HttpServletRequest req ,HttpServletResponse resp,
                      @BlueRequestParam("id") Integer id){

   }
}
