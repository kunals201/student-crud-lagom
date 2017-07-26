package states;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.knoldus.StudentInfo;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.util.Optional;

@Value
@Builder
@JsonDeserialize
@AllArgsConstructor
public class StudentStates implements CompressedJsonable {
    Optional<StudentInfo> student;
    String timestamp;
}
