package Tamanegiseoul.comeet;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@EnableWebMvc
//@Transactional
@Slf4j
@ActiveProfiles("dev")
public class utilTest {

    @Value("${security.jwt.token.secret-key}")
    String test;
    @Test
    public void LocalDateTime_test() {
        LocalDateTime time = LocalDateTime.now();
        log.warn(time.toString());

        LocalDateTime custom = LocalDateTime.of(2022, 10, 23, 22, 14, 30);
        log.warn(custom.toString());

        LocalDateTime fromPattern = LocalDateTime.parse("2022-10-23 22:14:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.warn(fromPattern.toString());



    }

}
