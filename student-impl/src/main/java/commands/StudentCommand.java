package commands;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.StudentInfo;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

public interface StudentCommand extends Jsonable {

    @Value
    @Builder
    @JsonDeserialize
    @AllArgsConstructor
    final class CreateStudent implements StudentCommand, PersistentEntity.ReplyType<Done> {
        StudentInfo student;
    }

    @Value
    @Builder
    @JsonDeserialize
    @AllArgsConstructor
    final class UpdateStudent implements StudentCommand, PersistentEntity.ReplyType<Done> {
        StudentInfo student;
    }

    @Value
    @Builder
    @JsonDeserialize
    @AllArgsConstructor
    final class DeleteStudent implements StudentCommand, PersistentEntity.ReplyType<Done> {
        StudentInfo student;
    }

    @JsonDeserialize
    final class StudentCurrentState implements StudentCommand, PersistentEntity.ReplyType<Optional<StudentInfo>> {
    }
}
