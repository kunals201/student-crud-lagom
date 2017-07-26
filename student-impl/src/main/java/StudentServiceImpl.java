import akka.Done;
import akka.NotUsed;
import com.knoldus.StudentInfo;
import com.knoldus.StudentService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import commands.StudentCommand;
import events.StudentEventProcessor;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class StudentServiceImpl implements StudentService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession session;

    /**
     * @param registry
     * @param readSide
     * @param session
     */

    @Inject
    public StudentServiceImpl(final PersistentEntityRegistry registry, ReadSide readSide, CassandraSession session) {
        this.persistentEntityRegistry = registry;
        this.session = session;

        persistentEntityRegistry.register(StudentEntity.class);
        readSide.register(StudentEventProcessor.class);
    }

    @Override
    public ServiceCall<NotUsed, Optional<StudentInfo>> getStudent(String id) {
        return request -> {
            CompletionStage<Optional<StudentInfo>> studentFuture =
                    session.selectAll("SELECT * FROM student WHERE id = ?", id)
                            .thenApply(rows ->
                                    rows.stream()
                                            .map(row -> StudentInfo.builder().id(row.getString("id"))
                                                    .name(row.getString("name")).gender(row.getString("genre"))
                                                    .totalMarks(row.getString("genre")).build()
                                            )
                                            .findFirst()
                            );
            return studentFuture;
        };
    }

    @Override
    public ServiceCall<StudentInfo, Done> createStudent() {
        return student -> {
            PersistentEntityRef<StudentCommand> ref = studentEntityRef(student);
            return ref.ask(StudentCommand.CreateStudent.builder().student(student).build());
        };
    }

    @Override
    public ServiceCall<StudentInfo, Done> updateStudent(String id) {
        return student -> {
            PersistentEntityRef<StudentCommand> ref = studentEntityRef(student);
            return ref.ask(StudentCommand.UpdateStudent.builder().student(student).build());
        };
    }

    @Override
    public ServiceCall<NotUsed, Done> deleteStudent(String id) {
        return request -> {
            StudentInfo student = StudentInfo.builder().id(id).build();
            PersistentEntityRef<StudentCommand> ref = studentEntityRef(student);
            return ref.ask(StudentCommand.DeleteStudent.builder().student(student).build());
        };
    }

    private PersistentEntityRef<StudentCommand> studentEntityRef(StudentInfo student) {
        return persistentEntityRegistry.refFor(StudentEntity.class, student.getId());
    }

}
