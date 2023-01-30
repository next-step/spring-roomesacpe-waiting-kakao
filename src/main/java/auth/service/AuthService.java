package auth.service;

import auth.dao.AuthDao;
import auth.domain.RoleTypes;
import auth.exception.MemberNameNotFoundException;
import auth.model.MemberDetails;
import lombok.RequiredArgsConstructor;
import auth.exception.AuthenticationException;
import auth.model.TokenRequest;
import auth.model.TokenResponse;
import auth.support.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthDao authDao;

    @Transactional
    public TokenResponse createToken(TokenRequest tokenRequest) {
        if (!isValidLogin(tokenRequest)) {
            throw new AuthenticationException();
        }

        String accessToken = jwtTokenProvider.createCredential(tokenRequest.getMemberName());
        return new TokenResponse(accessToken);
    }

    private boolean isValidLogin(TokenRequest request) {
        return authDao.findMemberDetailsByMemberName(request.getMemberName())
                .filter(member -> member.isValidPassword(request.getPassword()))
                .isPresent();
    }

    @Transactional(readOnly = true)
    public MemberDetails findMemberDetailsByMemberName(String memberName){
        MemberDetails memberDetails = authDao.findMemberDetailsByMemberName(memberName)
                .orElseThrow(MemberNameNotFoundException::new);

        RoleTypes roleTypes = findRoleByMemberName(memberName);
        memberDetails.addRole(roleTypes);
        return memberDetails;
    }

    @Transactional(readOnly = true)
    public RoleTypes findRoleByMemberName(String memberName){
        return authDao.findMemberRolesByMemberName(memberName);
    }
}