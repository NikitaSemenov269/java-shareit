import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan(basePackages = "ru.practicum.shareit")
class ShareItTests {

    @Test
    void contextLoads() {
    }
}
