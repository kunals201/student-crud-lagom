package events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.StudentInfo;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;


public interface StudentEvent extends Jsonable, AggregateEvent<StudentEvent> {

    @Override
    default AggregateEventTagger<StudentEvent> aggregateTag() {
        return StudentEventTag.INSTANCE;
    }

    @Value
    @Builder
    @JsonDeserialize
    @AllArgsConstructor
    final class StudentCreated implements StudentEvent, CompressedJsonable {
        StudentInfo student;
        String entityId;
    }

    @Value
    @Builder
    @JsonDeserialize
    @AllArgsConstructor
    final class StudentUpdated implements StudentEvent, CompressedJsonable {
        StudentInfo student;
        String entityId;
    }

    @Value
    @Builder
    @JsonDeserialize
    @AllArgsConstructor
    final class StudentDeleted implements StudentEvent, CompressedJsonable {
        StudentInfo student;
        String entityId;
    }
}
