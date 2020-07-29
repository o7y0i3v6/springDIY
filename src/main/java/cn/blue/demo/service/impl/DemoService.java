package cn.blue.demo.service.impl;

import cn.blue.demo.service.IDemoService;
import cn.blue.mvcFramework.annotation.BlueService;

/**
 * @author blue
 * @ClassName DemoService
 * @Description TODO
 * @date 2020/7/17 17:49
 */
@BlueService
public class DemoService implements IDemoService {
    @Override
    public String get(String name) {
        return "My name is" + name;
    }
}
