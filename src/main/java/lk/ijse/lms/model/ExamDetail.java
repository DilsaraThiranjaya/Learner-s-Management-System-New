package lk.ijse.lms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class ExamDetail {
    private String examId;
    private String studentId;
    private String result;
}
