package de.unistuttgart.iste.gits.common.testutil;

import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

/**
 * This text extension lets you test the graphQL API with a {@link GraphQlTester}.
 * <p>
 * Usage:
 * <pre>
 *     &#64;ExtendWith(GraphQlIntegrationTestParameterResolver.class)
 *     public class GraphQlTest {
 * </pre>
 * or using the {@link GraphQlApiTest} annotation.
 * <p>
 * In the test methods, the {@link GraphQlTester} can be injected as a parameter.
 * <pre>
 *     &#64;Test
 *     public void test(GraphQlTester tester) {
 *        // ...
 * </pre>
 *
 * If the test class has a field annotated with {@link InjectCurrentUserHeader},
 * the user header will be automatically added to the tester.
 * The field must be of type {@link UUID} or {@link LoggedInUser}.
 * <pre>
 *     &#64;InjectCurrentUserHeader
 *     private UUID userId;
 *     // ...
 * </pre>
 */
public class GraphQlTesterParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(GraphQlTester.class)
                || parameterContext.getParameter().getType().equals(HttpGraphQlTester.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext) throws ParameterResolutionException {

        final WebApplicationContext context = (WebApplicationContext) SpringExtension.getApplicationContext(extensionContext);

        final WebTestClient webTestClient = MockMvcWebTestClient.bindToApplicationContext(context)
                .configureClient()
                .baseUrl("/graphql")
                .build();

        HttpGraphQlTester tester = HttpGraphQlTester.create(webTestClient);

        tester = injectCurrentUserHeaderIfNecessary(tester, extensionContext);

        return tester;
    }

    @SneakyThrows
    private HttpGraphQlTester injectCurrentUserHeaderIfNecessary(final HttpGraphQlTester tester,
                                                                 final ExtensionContext extensionContext) {

        final Optional<Class<?>> testClass = extensionContext.getTestClass();

        if (testClass.isEmpty()) {
            return tester;
        }

        // check if test class has a field annotated with InjectCurrentUserHeader
        for (final Field field : testClass.get().getDeclaredFields()) {
            if (!field.isAnnotationPresent(InjectCurrentUserHeader.class)) {
                continue;
            }

            final Class<?> fieldType = field.getType();

            if (UUID.class.equals(fieldType)) {
                return HeaderUtils.addCurrentUserHeader(tester, UUID.randomUUID());
            } else if (LoggedInUser.class.equals(fieldType)) {
                return HeaderUtils.addCurrentUserHeader(tester, (LoggedInUser) field.get(extensionContext.getTestInstance()));
            } else {
                throw new ParameterResolutionException("Field annotated with InjectCurrentUserHeader must be of type UUID or LoggedInUser");
            }
        }

        return tester;
    }

}
