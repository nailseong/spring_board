package toyproject.board.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import toyproject.board.domain.member.Member;
import toyproject.board.domain.member.MemberRepository;
import toyproject.board.dto.member.command.MemberRequestDto;
import toyproject.board.dto.member.query.MemberQueryDto;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    /**
     * 회원가입 테스트
     * memberService.join();
     */
    @Tag("join")
    @Test
    void 회원가입_성공() throws Exception {
        // give
        MemberRequestDto dto = MemberRequestDto.builder()
                .username("test")
                .password("12341234")
                .build();

        // when
        Long memberId = memberService.join(dto);

        // then
        Member result = em.find(Member.class, memberId);

        assertThat(result.getUsername()).isEqualTo("test");
    }

    @Tag("join")
    @Test
    void 회원가입_같은_이름의_회원이_존재_1() throws Exception {
        // give
        MemberRequestDto dto = MemberRequestDto.builder()
                .username("test")
                .password("12341234")
                .build();

        MemberRequestDto sameUsernameDto = MemberRequestDto.builder()
                .username("TEST")
                .password("43214321")
                .build();

        // when
        memberService.join(dto);

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> memberService.join(sameUsernameDto))
                .withMessage("이미 존재하는 이름입니다.");
    }

    @Tag("join")
    @Test
    void 회원가입_같은_이름의_회원이_존재_2() throws Exception {
        // give
        MemberRequestDto dto = MemberRequestDto.builder()
                .username("test")
                .password("12341234")
                .build();

        MemberRequestDto sameUsernameDto = MemberRequestDto.builder()
                .username("  TEST  ")
                .password("43214321")
                .build();

        // when
        memberService.join(dto);

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> memberService.join(sameUsernameDto))
                .withMessage("이미 존재하는 이름입니다.");
    }

    @Tag("join")
    @Test
    void 회원가입_같은_이름의_회원이_존재_3() throws Exception {
        // give
        MemberRequestDto dto = MemberRequestDto.builder()
                .username("te st")
                .password("12341234")
                .build();

        MemberRequestDto sameUsernameDto = MemberRequestDto.builder()
                .username("  TE_ST  ")
                .password("43214321")
                .build();

        // when
        memberService.join(dto);

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> memberService.join(sameUsernameDto))
                .withMessage("이미 존재하는 이름입니다.");
    }

    @Tag("join")
    @Test
    void 회원가입_암호화() throws Exception {
        // give
        MemberRequestDto dto = MemberRequestDto.builder()
                .username("test")
                .password("12345678")
                .build();

        // when
        Long memberId = memberService.join(dto);

        // then
        Member result = em.find(Member.class, memberId);

        assertThat(result.getPassword()).isNotEqualTo("12345678");
    }

    /**
     * 로그인 테스트
     * memberService.login()
     */
    @Tag("login")
    @Test
    void 로그인_성공() throws Exception {
        // give
        Member member = Member.builder()
                .username("test")
                .password(BCrypt.hashpw("12341234", BCrypt.gensalt(10)))
                .build();
        em.persist(member);

        // when
        MemberRequestDto loginDto = MemberRequestDto.builder()
                .username("test")
                .password("12341234")
                .build();

        Member result = memberService.login(loginDto);

        // then
        assertThat(result.getUsername()).isEqualTo("test");
    }

    @Tag("login")
    @Test
    void 로그인_실패_이름() throws Exception {
        // give

        // when
        MemberRequestDto loginDto = MemberRequestDto.builder()
                .username("test")
                .password("12341234")
                .build();

        // memberService.login(loginDto);

        // then
        assertThatThrownBy(() -> memberService.login(loginDto))
                .hasMessage("이름을 다시 확인해주세요.");

    }

    @Tag("login")
    @Test
    void 로그인_실패_비밀번호() throws Exception {
        // give
        Member member = Member.builder()
                .username("test")
                .password(BCrypt.hashpw("12341234", BCrypt.gensalt(10)))
                .build();
        em.persist(member);

        // when
        MemberRequestDto loginDto = MemberRequestDto.builder()
                .username("test")
                .password("43214321")
                .build();

        // memberService.login(loginDto);

        // then
        assertThatThrownBy(() -> memberService.login(loginDto))
                .hasMessage("비밀번호를 다시 확인해주세요.");

    }

    /**
     * 회원 탈퇴 테스트
     * memberService.withdrawal()
     */
    @Tag("withdrawal")
    @Test
    void 탈퇴_성공() throws Exception {
        // give
        Member member = Member.builder()
                .username("test")
                .password(BCrypt.hashpw("12341234", BCrypt.gensalt(10)))
                .build();
        em.persist(member);

        // when
        boolean result = memberService.withdrawal(member.getId());

        // then
        assertThat(result).isTrue();
    }

    @Tag("withdrawal")
    @Test
    void 탈퇴_예외() throws Exception {
        // give
        Member member = Member.builder()
                .username("test")
                .password(BCrypt.hashpw("12341234", BCrypt.gensalt(10)))
                .build();
        em.persist(member);

        // when
        // memberService.withdrawal(12121L);

        // then
        assertThatThrownBy(() -> memberService.withdrawal(12121L))
                .hasMessage("유저를 찾을 수 없습니다.");
    }

    /**
     * 회원 상세 조회 테스트
     * memberService.getMember()
     */
    @Tag("getMember")
    @Test
    void 상세_조회_성공() throws Exception {
        // give
        Member member = Member.builder()
                .username("test")
                .password(BCrypt.hashpw("12341234", BCrypt.gensalt(10)))
                .build();
        em.persist(member);

        // when
        MemberQueryDto result = memberService.getMember(member.getId());

        // then
        assertThat(result.getUsername()).isEqualTo("test");
        assertThat(result.getMemberId()).isEqualTo(member.getId());

    }

    @Tag("getMember")
    @Test
    void 상세_조회_성공2() throws Exception {
        // give
        Member member1 = Member.builder()
                .username("test1")
                .password(BCrypt.hashpw("12341234", BCrypt.gensalt(10)))
                .build();
        em.persist(member1);

        Member member2 = Member.builder()
                .username("test2")
                .password(BCrypt.hashpw("12341234", BCrypt.gensalt(10)))
                .build();
        em.persist(member2);

        // when
        MemberQueryDto result1 = memberService.getMember(member1.getId());
        MemberQueryDto result2 = memberService.getMember(member2.getId());

        // then
        assertThat(result1.getUsername()).isEqualTo("test1");
        assertThat(result2.getUsername()).isEqualTo("test2");


    }

    @Tag("getMember")
    @Test
    void 상세_조회_예외() throws Exception {
        // give

        // when
        // memberService.getMember(12L);

        // then
        assertThatThrownBy(() -> memberService.getMember(12L))
                .hasMessage("유저를 찾을 수 없습니다.");

    }

}