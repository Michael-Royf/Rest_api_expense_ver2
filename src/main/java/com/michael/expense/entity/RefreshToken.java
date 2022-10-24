package com.michael.expense.entity;

import lombok.*;

import javax.persistence.*;

import java.util.Date;


@NoArgsConstructor
@Setter
@Getter

@Entity
@Table(name = "tbl_refresh_token")
public class RefreshToken {
    @Id
    @SequenceGenerator(
            name = "refresh_token_sequence",
            sequenceName = "refresh_token_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "refresh_token_sequence"
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Date expiredDate;

    public RefreshToken(User user, String token, Date expiredDate) {
        this.user = user;
        this.token = token;
        this.expiredDate = expiredDate;
    }
}
