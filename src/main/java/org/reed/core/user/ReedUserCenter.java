package org.reed.core.user;


import org.reed.bootup.EnableAPMAnalysis;
import org.reed.bootup.SpringBootBootup;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

@EnableAPMAnalysis(server = "127.0.0.1:11800")
public class ReedUserCenter extends SpringBootBootup {

    private final static String REED_USER_CENTER = "REED_USER_CENTER";

    public static void main(String[] args) {
        new ReedUserCenter().start(args);
    }
    @Override
    protected void beforeStart() {

    }

    @Override
    protected void afterStart(SpringApplication application, ApplicationContext context) {

    }

    @Override
    public String getModuleName() {
        return REED_USER_CENTER;
    }
}
