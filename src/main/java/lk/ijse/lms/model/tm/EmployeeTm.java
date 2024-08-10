package lk.ijse.lms.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class EmployeeTm {
    private String id;
    private String role;
    private String name;
    private String dOB;
    private String age;
    private String gender;
    private String joinDate;
    private String nIC;
    private String address;
    private String cNo;
    private String email;
}
