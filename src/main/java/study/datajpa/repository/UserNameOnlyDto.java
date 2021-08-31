package study.datajpa.repository;

import lombok.Getter;

@Getter
public class UserNameOnlyDto {

    private String userName;

    public UserNameOnlyDto(String userName) {
        this.userName = userName;
    }
}
