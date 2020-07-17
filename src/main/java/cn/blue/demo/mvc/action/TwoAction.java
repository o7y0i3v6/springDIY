package cn.blue.demo.mvc.action;

import cn.blue.demo.service.impl.IDemoService;
import cn.blue.mvcFramework.annotation.BlueAutowired;
import cn.blue.mvcFramework.annotation.BlueController;
import cn.blue.mvcFramework.annotation.BlueRequestMapping;
import cn.blue.mvcFramework.annotation.BlueRequestParam;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author blue
 * @ClassName TwoAction
 * @Description TODO
 * @date 2020/7/17 15:44
 */
@BlueController
@BlueRequestMapping("/web")
public class TwoAction {
    @BlueAutowired
    private IDemoService demoService;

    @BlueRequestMapping("/edit.json")
    public void edit(HttpServletRequest req, HttpServletResponse resp,
                     @BlueRequestParam("name") String name){

        String result = demoService.get(name);

        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
