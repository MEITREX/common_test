package de.unistuttgart.iste.gits.common.testutil;

import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;

import java.lang.annotation.*;
import java.util.UUID;

/**
 * Use this annotation on a field of type {@link UUID} or {@link LoggedInUser} in combination
 * with {@link GraphQlTesterParameterResolver} to automatically add the current user header to the tester.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectCurrentUserHeader {
}
