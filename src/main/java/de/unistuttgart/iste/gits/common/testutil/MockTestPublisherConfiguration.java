package de.unistuttgart.iste.gits.common.testutil;

import de.unistuttgart.iste.gits.common.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.common.event.ContentProgressedEvent;
import de.unistuttgart.iste.gits.common.event.CrudOperation;
import de.unistuttgart.iste.gits.common.event.UserProgressUpdatedEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

/**
 * This test configuration lets you test methods with a topicPublisher.
 Usage:
 * <pre>
 *     &#64;ContextConfiguration(classes = MockTopicPublisherConfiguration.class)
 *     public class Test {
 * </pre>
 *
 */

@TestConfiguration
public class MockTestPublisherConfiguration {
    @Primary
    @Bean
    public TopicPublisher getTestTopicPublisher() {
        final TopicPublisher mockPublisher = mock(TopicPublisher.class);
        doNothing().when(mockPublisher).notifyCourseChanges(any(UUID.class), any(CrudOperation.class));
        doNothing().when(mockPublisher).notifyChapterChanges(any(), any(CrudOperation.class));
        doNothing().when(mockPublisher).notifyContentChanges(any(), any(CrudOperation.class));
        doNothing().when(mockPublisher).notifyUserWorkedOnContent(any(ContentProgressedEvent.class));
        doNothing().when(mockPublisher).notifyUserProgressUpdated(any(UserProgressUpdatedEvent.class));
        return mockPublisher;
    }

}
