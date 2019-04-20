package com.dgarbar.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Account {

    @Id
    @GeneratedValue
    public Long id;

    @Column(unique = true)
    public String name;
}
