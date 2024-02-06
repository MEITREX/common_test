package de.unistuttgart.iste.gits.common.testutil;

import lombok.NoArgsConstructor;
import org.springframework.graphql.ResponseError;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * This class contains asserts for authorization.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthorizationAsserts {

    /**
     * Asserts that the given graphqlErrors contain only a missing user role error.
     *
     * @param graphqlErrors the graphql errors
     */
    public static void assertIsMissingUserRoleError(final List<ResponseError> graphqlErrors) {
        assertThat(graphqlErrors, hasSize(1));
        assertThat(graphqlErrors.getFirst().getExtensions().get("exception"), is("NoAccessToCourseException"));
        assertThat(graphqlErrors.getFirst().getMessage(), containsString("User does not have the required role to access this data of the course."));
    }
}
