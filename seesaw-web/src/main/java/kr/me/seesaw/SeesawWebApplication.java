package kr.me.seesaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class SeesawWebApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SeesawWebApplication.class);
        ApplicationPidFileWriter writer = new ApplicationPidFileWriter();
        application.addListeners(writer);
        application.run(args);
    }

}
