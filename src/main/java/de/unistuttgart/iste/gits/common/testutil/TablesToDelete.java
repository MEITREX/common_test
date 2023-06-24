package de.unistuttgart.iste.gits.common.testutil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotation in test together with {@link ClearDatabase} to
 * set which tables should be deleted.
 * This is useful if you want to delete tables in a specific order.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TablesToDelete {

    String[] value();
}
