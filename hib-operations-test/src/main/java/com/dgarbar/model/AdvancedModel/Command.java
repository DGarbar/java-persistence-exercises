package com.dgarbar.model.AdvancedModel;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@Entity
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

//    @Setter(AccessLevel.PRIVATE)
    @OneToMany(orphanRemoval = true, mappedBy = "command", cascade = CascadeType.PERSIST)
    private List<Captain> captains = new ArrayList<>();

//    @Setter(AccessLevel.PRIVATE)
//    @OneToMany(orphanRemoval = true, mappedBy = "command",cascade = CascadeType.PERSIST)
//    private List<Member> members = new ArrayList<>();

    public void addCaptain(Captain captain) {
        captain.setCommand(this);
        captains.add(captain);
    }

    public void removeCaptain(Captain captain) {
        captain.setCommand(null);
        captains.remove(captain);
    }

//    public void addMember(Member member) {
//        member.setCommand(this);
//        members.add(member);
//    }
//
//    public void removeMember(Member member) {
//        member.setCommand(null);
//        members.remove(member);
//    }
}
