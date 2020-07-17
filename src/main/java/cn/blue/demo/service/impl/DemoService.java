package cn.blue.demo.service.impl;

/**
 * @author blue
 * @ClassName DemoService
 * @Description TODO
 * @date 2020/7/17 17:49
 */
public class DemoService implements IDemoService {
    @Override
    public String get(String name) {
        return "My name is" + name;
    }
}
