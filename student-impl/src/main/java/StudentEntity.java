import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import commands.StudentCommand;
import events.StudentEvent;
import states.StudentStates;

import java.time.LocalDateTime;
import java.util.Optional;

public class StudentEntity extends PersistentEntity<StudentCommand, StudentEvent, StudentStates> {
    /**
     * @param snapshotState
     * @return
     */
    @Override
    public Behavior initialBehavior(Optional<StudentStates> snapshotState) {

        // initial behaviour of movie
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(
                StudentStates.builder().student(Optional.empty())
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(StudentCommand.CreateStudent.class, (cmd, ctx) ->
                ctx.thenPersist(StudentEvent.StudentCreated.builder().student(cmd.getStudent())
                        .entityId(entityId()).build(), evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(StudentEvent.StudentCreated.class, evt ->
                StudentStates.builder().student(Optional.of(evt.getStudent()))
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(StudentCommand.UpdateStudent.class, (cmd, ctx) ->
                ctx.thenPersist(StudentEvent.StudentUpdated.builder().student(cmd.getStudent()).entityId(entityId()).build()
                        , evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(StudentEvent.StudentUpdated.class, evt ->
                StudentStates.builder().student(Optional.of(evt.getStudent()))
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setCommandHandler(StudentCommand.DeleteStudent.class, (cmd, ctx) ->
                ctx.thenPersist(StudentEvent.StudentDeleted.builder().student(cmd.getStudent()).entityId(entityId()).build(),
                        evt -> ctx.reply(Done.getInstance()))
        );

        behaviorBuilder.setEventHandler(StudentEvent.StudentDeleted.class, evt ->
                StudentStates.builder().student(Optional.empty())
                        .timestamp(LocalDateTime.now().toString()).build()
        );

        behaviorBuilder.setReadOnlyCommandHandler(StudentCommand.StudentCurrentState.class, (cmd, ctx) ->
                ctx.reply(state().getStudent())
        );

        return behaviorBuilder.build();
    }
}
