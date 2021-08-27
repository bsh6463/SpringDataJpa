package study.datajpa.entity;

import lombok.*;


import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "userName", "age"})
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String userName;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String userName) {
        this.userName = userName;
    }

    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if(team!=null) {
            this.changeTeam(team);
        }
    }

    public void changeUserName(String userName){
        this.userName = userName;
    }

    public void changeTeam(Team team){
        this.setTeam(team);
        team.getMembers().add(this);
    }

}
