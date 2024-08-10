package lk.ijse.lms.model;

import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Student {
    private String id;
    private String fname;
    private String lname;
    private String dOb;
    private String gender;
    private String admissionDate;
    private String nIC;
    private String address;
    private String cNo;
    private String email;
}
