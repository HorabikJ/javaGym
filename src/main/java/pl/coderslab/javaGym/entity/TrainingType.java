package pl.coderslab.javaGym.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    @NotBlank(message = "*Training name can not be empty.")
    private String name;

    @Column(length = 1000)
    @Size(min = 1, max = 1000, message = "*Description can not be empty and can not be longer that 1000 signs.")
    private String description;

}