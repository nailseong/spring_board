package toyproject.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import toyproject.board.domain.member.Member;
import toyproject.board.dto.BasicResponseDto;
import toyproject.board.dto.member.MemberDto;
import toyproject.board.dto.member.MemberNoPw;
import toyproject.board.dto.member.MemberResponseDto;
import toyproject.board.dto.member.MemberSearchCondition;
import toyproject.board.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @ResponseStatus(CREATED)
    @PostMapping("/new")
    public BasicResponseDto join(@Valid @RequestBody MemberDto dto) {

        memberService.join(dto);

        return BasicResponseDto.builder()
                .httpStatus(CREATED)
                .build();
    }

    @PostMapping("/login")
    public BasicResponseDto login(@Valid @RequestBody MemberDto dto,
                                  HttpSession session,
                                  HttpServletRequest request) {

        Member member = memberService.login(dto);
        session.setAttribute("member", member);

        String userAgent = request.getHeader("User-Agent");
        session.setAttribute("userAgent", userAgent);

        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        session.setAttribute("ip", ip);

        return BasicResponseDto.builder()
                .httpStatus(OK)
                .build();
    }

    @PostMapping("/withdrawal")
    public BasicResponseDto withdrawal(HttpSession session) {

        Object attribute = session.getAttribute("member");
        Member member = (Member) attribute;

        memberService.withdrawal(member.getId());
        session.invalidate();

        return BasicResponseDto.builder()
                .httpStatus(OK)
                .build();
    }

    @GetMapping("/{memberId}")
    public BasicResponseDto getMember(@PathVariable(name = "memberId") Long memberId) {

        MemberNoPw member = memberService.getMember(memberId);

        return MemberResponseDto.builder()
                .httpStatus(OK)
                .member(member)
                .build();
    }

    @GetMapping("/search-page")
    public Page<MemberNoPw> searchV2(MemberSearchCondition condition, Pageable pageable) {
        return memberService.searchMemberPage(condition, pageable);
    }

}
