package Certis.Web.dto;

import Certis.Web.entity.MemberEntity;
import Certis.Web.entity.Role;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberDTO {
    private long id;
    private String email;
    private String password;
    private String username;
    private Role role;

    public static MemberDTO toMemberDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setEmail(memberEntity.getEmail());
        memberDTO.setPassword(memberEntity.getPassword());
        memberDTO.setUsername(memberEntity.getUsername());
        memberDTO.setRole(memberEntity.getRole());

        return memberDTO;
    }
}
