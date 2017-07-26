package events;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

public class StudentEventTag {

    public static final AggregateEventTag<StudentEvent> INSTANCE = AggregateEventTag.of(StudentEvent.class);
}
