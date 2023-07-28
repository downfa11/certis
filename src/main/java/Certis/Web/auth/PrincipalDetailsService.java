package Certis.Web.auth;


import Certis.Web.entity.User;
import Certis.Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User byUsername = userRepository.findByUsername(username);
        if(byUsername != null){
//            byUsername = User.userDetailRegister()
//                    .username(username).password(password).email(email).role(role)
//                            .build();
//            userRepository.save(byUsername);
            return new PrincipalDetails(byUsername);
        }
        return null;
    }
}

