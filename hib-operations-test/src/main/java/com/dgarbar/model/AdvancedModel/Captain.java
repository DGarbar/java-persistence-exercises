package com.dgarbar.model.AdvancedModel;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Captain {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "command_id")
    private Command command;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "captain",cascade = CascadeType.PERSIST)
    private List<Job> jobs = new ArrayList<>();

    public void addJob(Job job) {
        job.setCaptain(this);
        jobs.add(job);
    }

    public void removeJob(Job job) {
        job.setCaptain(null);
        jobs.remove(job);
    }

}
