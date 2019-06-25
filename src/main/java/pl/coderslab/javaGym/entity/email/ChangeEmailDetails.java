package pl.coderslab.javaGym.entity.email;

import lombok.*;
import pl.coderslab.javaGym.entity.user.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
public class ChangeEmailDetails implements ConfirmationEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @Column
    private String param;

    @Column
    private ZonedDateTime sendTime;

    @Column
    private String newEmail;

    @Column
    private Integer minutesExpirationTime;

    public ChangeEmailDetails(User user, String param, ZonedDateTime sendTime,
                              String newEmail, Integer minutesExpirationTime) {
        this.user = user;
        this.param = param;
        this.sendTime = sendTime;
        this.newEmail = newEmail;
        this.minutesExpirationTime = minutesExpirationTime;
    }
}