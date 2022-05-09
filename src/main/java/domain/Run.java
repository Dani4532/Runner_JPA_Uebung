package domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter



@Entity
public class Run {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Double distanceInKm;

    private Integer minutes;

    @ManyToOne
    @JoinColumn(name = "runner_run_id", nullable = false)
    private Runner runner;
}
