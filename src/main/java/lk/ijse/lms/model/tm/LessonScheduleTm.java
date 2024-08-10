package lk.ijse.lms.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class LessonScheduleTm {
    private String lId;
    private String eId;
    private String employeeName;
    private String courseName;
    private String date;
    private String time;
}
