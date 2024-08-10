package lk.ijse.lms.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class CourseTm {
    private String id;
    private String name;
    private String description;
    private String duration;
    private String price;
}
