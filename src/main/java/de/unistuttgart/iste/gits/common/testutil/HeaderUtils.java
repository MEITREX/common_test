package de.unistuttgart.iste.gits.common.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.util.Collections;
import java.util.UUID;

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

    /**
     * Converts the given user to a json string.
     *
     * @param user the user
     * @return the json string
     * @implNote We cannot use the {@link ObjectMapper} here because it cannot correctly convert the OffsetDateTime
     */
    private static String getJson(final LoggedInUser user) {

        final StringBuilder courseMemberships = new StringBuilder().append("[");

        for (int i = 0; i < user.getCourseMemberships().size(); i++) {
            final LoggedInUser.CourseMembership courseMembership = user.getCourseMemberships().get(i);

            courseMemberships.append("{")
                    .append("\"courseId\": \"").append(courseMembership.getCourseId()).append("\",")
                    .append("\"role\": \"").append(courseMembership.getRole()).append("\",")
                    .append("\"published\": ").append(courseMembership.isPublished()).append(",")
                    .append("\"startDate\": \"").append(courseMembership.getStartDate()).append("\",")
                    .append("\"endDate\": \"").append(courseMembership.getEndDate()).append("\"")
                    .append("}");

            if (i < user.getCourseMemberships().size() - 1) {
                courseMemberships.append(",");
            }
        }

        courseMemberships.append("]");

        return """
                {
                  "id": "%s",
                  "userName": "%s",
                  "firstName": "%s",
                  "lastName": "%s",
                  "courseMemberships": %s
                }
                """
                .formatted(user.getId(),
                        user.getUserName(),
                        user.getFirstName(),
                        user.getLastName(),
                        courseMemberships.toString());
    }
}
