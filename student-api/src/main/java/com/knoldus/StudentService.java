package com.knoldus;


import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.Service;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;
import static com.lightbend.lagom.javadsl.api.transport.Method.*;

public interface StudentService extends Service {

    /**
     * @param id
     * @return
     */
    ServiceCall<NotUsed, Optional<StudentInfo>> getStudent(String id);

    /**
     * @return
     */
    ServiceCall<StudentInfo, Done> createStudent();

    /**
     * @param id
     * @return
     */
    ServiceCall<StudentInfo, Done> updateStudent(String id);

    /**
     * @param id
     * @return
     */
    ServiceCall<NotUsed, Done> deleteStudent(String id);

    @Override
    default Descriptor descriptor() {

        return named("movie").withCalls(
                restCall(GET, "/api/student/:id", this::getStudent),
                restCall(POST, "/api/create-student", this::createStudent),
                restCall(PUT, "/api/update-student/:id", this::updateStudent),
                restCall(DELETE, "/api/delete-student/:id", this::deleteStudent)
                //  restCall(GET, "/api/user/get-all-movie", this::getAllMovie)
        ).withAutoAcl(true);
    }


}
