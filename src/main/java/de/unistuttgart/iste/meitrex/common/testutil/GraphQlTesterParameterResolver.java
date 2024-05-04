package de.unistuttgart.iste.meitrex.common.testutil;

import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.graphql.test.tester.WebSocketGraphQlTester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

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
 * <p>
 * If the test class has a field annotated with {@link InjectCurrentUserHeader},
 * the user header will be automatically added to the tester.
 * The field must be of type {@link UUID} or {@link LoggedInUser}.
 * <pre>
 *     &#64;InjectCurrentUserHeader
 *     private UUID userId;
 *     // ...
 * </pre>
 *
 * This extension, by default, uses a {@link HttpGraphQlTester}, which can also be
 * explicitly requested by using a parameter of type {@link HttpGraphQlTester}.
 * To use a {@link WebSocketGraphQlTester}, the test method must have a parameter
 * of type {@link WebSocketGraphQlTester}.
 */
public class GraphQlTesterParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext) throws ParameterResolutionException {
        final var type = parameterContext.getParameter().getType();

        return type.equals(GraphQlTester.class)
                || type.equals(HttpGraphQlTester.class)
                || type.equals(WebSocketGraphQlTester.class)
                || type.equals(WebGraphQlTester.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext) throws ParameterResolutionException {

        if (parameterContext.getParameter().getType().equals(WebSocketGraphQlTester.class)) {
            return createWebSocketGraphQlTester(extensionContext);
        } else {
            return createHttpGraphQlTester(extensionContext);
        }
    }

    private HttpGraphQlTester createHttpGraphQlTester(final ExtensionContext extensionContext) {
        final WebApplicationContext context = (WebApplicationContext) SpringExtension.getApplicationContext(extensionContext);

        final WebTestClient webTestClient = MockMvcWebTestClient.bindToApplicationContext(context)
                .configureClient()
                .baseUrl(getHttpGraphQlRoute())
                .build();

        HttpGraphQlTester tester = HttpGraphQlTester.create(webTestClient);

        return (HttpGraphQlTester) injectCurrentUserHeaderIfNecessary(tester, extensionContext);
    }

    private WebSocketGraphQlTester createWebSocketGraphQlTester(final ExtensionContext extensionContext) {
        final String url = "ws://localhost:" + getPort() + getWebSocketGraphQlRoute();
        WebSocketClient client = new ReactorNettyWebSocketClient();
        WebSocketGraphQlTester tester = WebSocketGraphQlTester.builder(url, client).build();

        return (WebSocketGraphQlTester) injectCurrentUserHeaderIfNecessary(tester, extensionContext);
    }

    private String getPort() {
        return System.getProperty("server.port");
    }

    private String getHttpGraphQlRoute() {
        return System.getProperty("spring.graphql.path", "/graphql");
    }

    private String getWebSocketGraphQlRoute() {
        return System.getProperty("spring.graphql.websocket.path", "/graphql-ws");
    }

    @SneakyThrows
    @SuppressWarnings("java:S3011")
    private WebGraphQlTester injectCurrentUserHeaderIfNecessary(final WebGraphQlTester tester,
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

            field.setAccessible(true); // make private fields accessible

            if (UUID.class.equals(fieldType)) {
                UUID userId = (UUID) field.get(extensionContext.getTestInstance().orElseThrow());
                return HeaderUtils.addCurrentUserHeader(tester, userId);
            }
            if (LoggedInUser.class.equals(fieldType)) {
                LoggedInUser user = (LoggedInUser) field.get(extensionContext.getTestInstance().orElseThrow());
                return HeaderUtils.addCurrentUserHeader(tester, user);
            }
            throw new ParameterResolutionException("Field annotated with InjectCurrentUserHeader must be of type UUID or LoggedInUser");
        }

        return tester;
    }

}
