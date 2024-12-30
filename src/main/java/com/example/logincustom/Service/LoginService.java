package com.example.logincustom.Service;

import com.example.logincustom.Constant.RoleType;
import com.example.logincustom.DTO.LoginDTO;
import com.example.logincustom.Entity.LoginEntity;
import com.example.logincustom.Repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.pulsar.PulsarAutoConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
//UserDetailsService : 로그인서비스를 사용자 정의로 오버라이드
public class LoginService implements UserDetailsService {
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;  //회원가입시 암호를 암호화
    private final ModelMapper modelMapper;

    //로그인 처리
    //메소드명도 변경 불가
    //내용만 로그인에 필요한 정보로 재 작성
    //try~catch : 프로그램에 일부분에 오류가 발생할 때 예외처리
    //throws : 메소드 전체에 오류가 발생할 때 예외처리
    //메소드에 throws를 적용하면 메소드를 호출한 곳에도 동일하게 throws을 작성
    @Override
    public UserDetails loadUserByUsername(String loginid)
        throws UsernameNotFoundException {
        //입력한 id가 존재하는지 조회
        Optional<LoginEntity> loginEntity = loginRepository.findByLoginid(loginid);

        //Optional로 받은 값은 .get() 로 불러와야 한다
        //List로 저장된 목록에서 하나씩 호출할 때도 .get()

        //조회한 아이디가 존재하면
        if(loginEntity.isPresent()) {
            //조회한 정보를 security에 전달해서 로그인처리를 할수 있음
            //아이디, 비밀번호, 권한을 전달해야함
            return User.withUsername(loginEntity.get().getLoginid())
                            .password(loginEntity.get().getPassword())
                                    .roles(loginEntity.get().getRoleType().name())
                    .build();
        }else { // 입력한 아이디가 존재하지 않으면 오류발생(예외처리)
            throw new UsernameNotFoundException(loginid+"오류!!!");
        }
    }

    //회원가입(가입폼 ->DTO -> Entity -> 저장)
    //아이디는 중복 불가능
    public void saveUser(LoginDTO loginDTO){
        Optional<LoginEntity> read = loginRepository.findByLoginid(loginDTO.getLoginid());

        if(read.isPresent()){ //해당 ID의 존재여부 판단
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

        //해당ID가 존재하지 않으면
        String password = passwordEncoder.encode(loginDTO.getPassword());
        LoginEntity loginEntity = modelMapper.map(loginDTO, LoginEntity.class);
        //password에 암호화한 비밀번호로 다시 저장
        loginEntity.setPassword(password);
        //회원가입시 기본권한(USER) 설정 -> 차후 관리자페이지에서 관리자 등록 재조정
        //회원수가 0이면 관리자로 등록, 1이상이면 일반 사용자 등록하게 활용
        loginEntity.setRoleType(RoleType.ADMIN);

        loginRepository.save(loginEntity);

    }
}

