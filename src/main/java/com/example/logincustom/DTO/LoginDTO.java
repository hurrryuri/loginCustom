package com.example.logincustom.DTO;

import com.example.logincustom.Constant.RoleType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LoginDTO {
    private Integer id;
    private String loginid;
    private String password;
    private String username;
    private RoleType roleType;
}
