package ru.t1.t1school;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.t1.t1school.integration.TestContainersConfig;

@SpringBootTest
class T1SchoolApplicationTests extends TestContainersConfig {

    @Test
    @DisplayName("Тест инициализации контекста")
    void contextLoads() {
    }
}
