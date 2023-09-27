package de.unistuttgart.iste.gits.common.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Utility class for adding the current user header to a {@link HttpGraphQlTester}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderUtils {

    /**
     * Adds the current user header to the tester. This user will be used for all requests.
     *
     * @param tester the tester
     * @param user   the user
     * @return the tester
     */
    public static HttpGraphQlTester addCurrentUserHeader(final HttpGraphQlTester tester, final LoggedInUser user) {
        return tester.mutate()
                .header("CurrentUser", getJson(user))
                .build();
    }

    /**
     * Adds a current user header to the tester. This user will be used for all requests.
     * The user will have the given id and the name "test", lastName "testLastName" and firstName "testFirstName".
     * It will have no course memberships.
     *
     * @param tester the tester to mutate
     * @param userId the user id to use
     * @return the tester
     */
    public static HttpGraphQlTester addCurrentUserHeader(final HttpGraphQlTester tester, final UUID userId) {
        final LoggedInUser user = new LoggedInUser(userId, "test", "testFirstName", "testLastName", Collections.emptyList());

        return addCurrentUserHeader(tester, user);
    }

    private static String getJson(final LoggedInUser user) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(user);
        } catch (final Exception e) {
            fail("Could not convert user to json", e);
        }
        return null;
    }
}
