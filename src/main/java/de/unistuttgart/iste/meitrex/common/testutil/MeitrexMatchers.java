package de.unistuttgart.iste.meitrex.common.testutil;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.springframework.graphql.ResponseError;

import java.util.Collection;
import java.util.function.Function;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;

/**
 * Collection of useful Hamcrest matchers.
 */
public class MeitrexMatchers {
    private MeitrexMatchers() {
    }

    /**
     * Matcher that checks if a collection of {@link ResponseError}s contains exactly
     * one error that matches the given matcher.
     *
     * @param errorMatcher the matcher for the response error to check against
     * @return the matcher
     */
    public static Matcher<Iterable<? extends ResponseError>> containsError(Matcher<ResponseError> errorMatcher) {
        return contains(errorMatcher);
    }

    /**
     * Matcher for response errors that checks if the error is caused by a specific exception.
     *
     * @param exceptionClass the exception class
     * @return the matcher
     * @see #containsError(Matcher)
     */
    public static Matcher<ResponseError> causedBy(Class<? extends Throwable> exceptionClass) {
        return hasFeature("exception",
                ResponseError::getExtensions,
                hasEntry("exception", exceptionClass.getSimpleName()));
    }

    /**
     * Matcher for response errors that checks if the error has a specific message.
     *
     * @param messageMatcher the message
     * @return the matcher
     */
    public static Matcher<ResponseError> withMessage(Matcher<String> messageMatcher) {
        return hasFeature("message", ResponseError::getMessage, messageMatcher);
    }

    /**
     * Matcher that accesses a function of the actual object and checks if the result matches the given matcher.
     * This is useful, e.g., for checking if a certain field of an object has a specific value.
     *
     * @param accessor the function to access the feature
     * @param matcher  the matcher for the feature
     * @param <T>      type of the actual object
     * @param <F>      type of the feature
     * @return the matcher
     */
    public static <T, F> Matcher<T> hasFeature(Function<T, F> accessor, Matcher<F> matcher) {
        return hasFeature("feature", accessor, matcher);
    }

    /**
     * Like {@link #hasFeature(Function, Matcher)}, but with a custom feature name.
     */
    public static <T, R> Matcher<T> hasFeature(String featureName, Function<T, R> accessor, Matcher<R> matcher) {
        return new FeatureMatcher<>(matcher, "has " + featureName, featureName) {
            @Override
            protected R featureValueOf(T actual) {
                return accessor.apply(actual);
            }
        };
    }

    /**
     * Matcher that matches a collection of objects against a collection of matchers.
     *
     * @param collection      the collection to match
     * @param matcherFunction the function to create a matcher for each element
     * @param <T>             the type of the collection elements
     * @param <E>             the type of the matchers
     * @return the array of matchers
     */
    @SuppressWarnings("unchecked")
    public static <T, E> Matcher<E>[] each(Collection<T> collection, Function<T, Matcher<E>> matcherFunction) {
        return (Matcher<E>[]) collection.stream()
                .map(matcherFunction)
                .toArray(Matcher[]::new);
    }
}
