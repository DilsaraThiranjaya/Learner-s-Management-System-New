package lk.ijse.lms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Employee {
    private String id;
    private String fname;
    private String lname;
    private String dOb;
    private String gender;
    private String joinDate;
    private String nIC;
    private String address;
    private String cNo;
    private String email;
    private String role;

}
