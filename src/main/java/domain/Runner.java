package domain;

import lombok.*;


import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter

@Entity
public class Runner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Gender gender;

    @OneToMany(mappedBy = "runner")
    private List<Run> runs = new ArrayList<>();


    public Runner(Long id, String name, Gender gender, List<Run> runs) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        runs.forEach(this::addRuns);
    }

    public Runner(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }


    public void addRuns(Run run) {
        this.runs.add(run);
        run.setRunner(this);
        }



}
