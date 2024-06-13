package com.infoplusvn.qrbankgateway.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "TRANSCUR")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransCurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_cur_id")
    private Long transCurId;

    @Column(unique = true, name = "string_code")
    private String stringCode;

    @Column(unique = true, name = "number_code")
    private String numberCode;

    @Column(name = "trans_cur_name")
    private String transCurName;
}
