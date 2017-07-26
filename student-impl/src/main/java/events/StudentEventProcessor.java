package events;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import events.StudentEvent.*;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;


public class StudentEventProcessor extends ReadSideProcessor<StudentEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentEventProcessor.class);

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement writeStudents;
    private PreparedStatement deleteStudents;

    /**
     * @param session
     * @param readSide
     */
    @Inject
    public StudentEventProcessor(final CassandraSession session, final CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    /**
     * @return
     */
    @Override
    public PSequence<AggregateEventTag<StudentEvent>> aggregateTags() {
        LOGGER.info(" aggregateTags method ... ");
        return TreePVector.singleton(StudentEventTag.INSTANCE);
    }

    /**
     * @return
     */
    @Override
    public ReadSideHandler<StudentEvent> buildHandler() {
        LOGGER.info(" buildHandler method ... ");
        return readSide.<StudentEvent>builder("Student_offset")
                .setGlobalPrepare(this::createTable)
                .setPrepare(evtTag -> prepareWriteStudent()
                        .thenCombine(prepareDeleteStudent(), (d1, d2) -> Done.getInstance())
                )
                .setEventHandler(StudentEvent.StudentCreated.class, this::processPostAdded)
                .setEventHandler(StudentEvent.StudentUpdated.class, this::processPostUpdated)
                .setEventHandler(StudentEvent.StudentDeleted.class, this::processPostDeleted)
                .build();
    }

    /**
     * @return
     */
    // Execute only once while application is start
    private CompletionStage<Done> createTable() {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS student ( " +
                        "id TEXT, name TEXT, gender TEXT, totalMarks TEXT, PRIMARY KEY(id))"
        );
    }

    /*
    * START: Prepare statement for insert Student values into Students table.
    * This is just creation of prepared statement, we will map this statement with our event
    */

    /**
     * @return
     */
    private CompletionStage<Done> prepareWriteStudent() {
        return session.prepare(
                "INSERT INTO student (id, name, gender, totalMarks) VALUES (?, ?, ?, ?)"
        ).thenApply(ps -> {
            setWriteStudents(ps);
            return Done.getInstance();
        });
    }

    /**
     * @param statement
     */
    private void setWriteStudents(PreparedStatement statement) {
        this.writeStudents = statement;
    }

    // Bind prepare statement while StudentCreate event is executed

    /**
     * @param event
     * @return
     */
    private CompletionStage<List<BoundStatement>> processPostAdded( StudentEvent.StudentCreated event) {
        BoundStatement bindWriteStudent = writeStudents.bind();
        bindWriteStudent.setString("id", event.getStudent().getId());
        bindWriteStudent.setString("name", event.getStudent().getName());
        bindWriteStudent.setString("gender", event.getStudent().getGender());
        bindWriteStudent.setString("totalmarks", event.getStudent().getTotalMarks());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteStudent));
    }
    /* ******************* END ****************************/

    /* START: Prepare statement for update the data in Students table.
    * This is just creation of prepared statement, we will map this statement with our event
    */

    /**
     * @param event
     * @return
     */
    private CompletionStage<List<BoundStatement>> processPostUpdated(StudentEvent.StudentUpdated event) {
        BoundStatement bindWriteStudent = writeStudents.bind();
        bindWriteStudent.setString("id", event.getStudent().getId());
        bindWriteStudent.setString("name", event.getStudent().getName());
        bindWriteStudent.setString("gender", event.getStudent().getGender());
        bindWriteStudent.setString("totalmarks", event.getStudent().getTotalMarks());

        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteStudent));
    }
    /* ******************* END ****************************/

    /* START: Prepare statement for delete the the Student from table.
    * This is just creation of prepared statement, we will map this statement with our event
    */

    /**
     * @return
     */
    private CompletionStage<Done> prepareDeleteStudent() {
        return session.prepare(
                "DELETE FROM student WHERE id=?"
        ).thenApply(ps -> {
            setDeleteStudents(ps);
            return Done.getInstance();
        });
    }

    /**
     * @param deleteStudents
     */
    private void setDeleteStudents(PreparedStatement deleteStudents) {
        this.deleteStudents = deleteStudents;
    }

    /**
     * @param event
     * @return
     */
    private CompletionStage<List<BoundStatement>> processPostDeleted(StudentEvent.StudentDeleted event) {
        BoundStatement bindWriteStudent = deleteStudents.bind();
        bindWriteStudent.setString("id", event.getStudent().getId());
        return CassandraReadSide.completedStatements(Arrays.asList(bindWriteStudent));
    }
    /* ******************* END ****************************/
}

