import com.google.inject.AbstractModule;
import com.knoldus.StudentService;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class StudentModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(StudentService.class,StudentServiceImpl.class));
    }
}

