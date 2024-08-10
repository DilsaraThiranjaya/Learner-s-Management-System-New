package lk.ijse.lms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class LessonSchedule {
    private String lessonId;
    private String date;
    private String time;
    private String employeeId;
    private String courseId;
}
