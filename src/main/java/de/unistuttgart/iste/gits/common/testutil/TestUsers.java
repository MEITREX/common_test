package de.unistuttgart.iste.gits.common.testutil;


import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Util class containing test users to use in tests.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TestUsers {

    /**
     * Creates a user with a valid membership in a course with the given id.
     * The course is published and the course is active at the current time.
     *
     * @param courseId the id of the course
     * @param role     the role of the user in the course
     * @return a user with a valid membership in a course with the given id
     */
    public static LoggedInUser userWithMembershipInCourseWithId(final UUID courseId, final LoggedInUser.UserRoleInCourse role) {
        return userWithMemberships(membershipBuilder()
                .courseId(courseId)
                .role(role)
                .build());
    }

    /**
     * Creates a user with valid membership(s) in a course with the given id.
     *
     * @param courseMemberships the memberships of the user
     * @return a user with a valid membership in a course with the given id
     */
    public static LoggedInUser userWithMemberships(final LoggedInUser.CourseMembership... courseMemberships) {
        return LoggedInUser.builder()
                .userName("userWithMemberships")
                .id(UUID.randomUUID())
                .firstName("firstName")
                .lastName("lastName")
                .courseMemberships(List.of(courseMemberships))
                .realmRoles(Collections.emptySet())
                .build();
    }

    /**
     * Creates a user with valid membership(s) & realm roles in a course with the given id.
     *
     * @param courseMemberships the memberships of the user
     * @param realmRoles set of realm roles a user possesses
     * @return a user with a valid membership in a course with the given id
     */
    public static LoggedInUser userWithMembershipsAndRealmRoles(final Set<LoggedInUser.RealmRole> realmRoles, final LoggedInUser.CourseMembership... courseMemberships) {
        return LoggedInUser.builder()
                .userName("userWithMemberships")
                .id(UUID.randomUUID())
                .firstName("firstName")
                .lastName("lastName")
                .courseMemberships(List.of(courseMemberships))
                .realmRoles(realmRoles)
                .build();
    }

    private static LoggedInUser.CourseMembership.CourseMembershipBuilder membershipBuilder() {
        return LoggedInUser.CourseMembership.builder()
                .published(true)
                .startDate(OffsetDateTime.now().minusDays(1))
                .endDate(OffsetDateTime.now().plusDays(1));
    }
}
