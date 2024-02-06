package de.unistuttgart.iste.meitrex.common.testutil;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.annotation.*;

/**
 * This annotation is a combination of {@link SpringBootTest} and the test extensions
 * {@link GraphQlTesterParameterResolver}, {@link MeitrexPostgresSqlContainer} and {@link ClearDatabase}.
 */
@ExtendWith(GraphQlTesterParameterResolver.class)
@ExtendWith(MeitrexPostgresSqlContainer.class)
@Testcontainers
@ExtendWith(ClearDatabase.class)
// set allow-bean-definition-overriding to true to allow overriding of spring beans in tests
@SpringBootTest({"spring.main.allow-bean-definition-overriding=true"})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GraphQlApiTest {
}
