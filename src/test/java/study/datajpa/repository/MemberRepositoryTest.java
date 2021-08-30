package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Optional<Member> byId = memberRepository.findById(savedMember.getId());
        Member findMember = byId.orElseGet(() -> new Member("nobody"));

        Assertions.assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        Assertions.assertThat(findMember.getUserName()).isEqualTo(savedMember.getUserName());
        Assertions.assertThat(findMember).isEqualTo(member);
    }


    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        //단건조회 검증
        Assertions.assertThat(findMember1.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(findMember1.getUserName()).isEqualTo(member1.getUserName());
        Assertions.assertThat(findMember2.getId()).isEqualTo(member2.getId());
        Assertions.assertThat(findMember2.getUserName()).isEqualTo(member2.getUserName());

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);
        long count = memberRepository.count();
        Assertions.assertThat(all.size()).isEqualTo(count);

        //삭제
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deleteCount = memberRepository.count();
        Assertions.assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUserNameAndAgeGreaterThan(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findMembers = memberRepository.findMemberByUserNameAndAgeGreaterThan("bbb", 10);

        Assertions.assertThat(findMembers.get(0).getUserName()).isEqualTo(member2.getUserName());
        Assertions.assertThat(findMembers.get(0).getAge()).isEqualTo(member2.getAge());

    }

    @Test
    public void findUserNameList(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> userNameList = memberRepository.findUserNameList();
        for (String userName : userNameList) {
            System.out.println("userName = " + userName);
        }

    }

    @Test
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("aaa", 10);
        member1.changeTeam(team);
        memberRepository.save(member1);

        List<MemberDto> memberDtos = memberRepository.findMemberDto();

        System.out.println("========================");
        for (MemberDto memberDto : memberDtos) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findByNames(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("aaa", "bbb"));
        System.out.println("========================");
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findListByUserName("aaa");
        Optional<Member> optionalMember = memberRepository.findOptionalByUserName("aaa");

       // Member findMember = memberRepository.findMemberByUserName("cddddddd");
        //System.out.println("findMember = " + findMember);

        /**
         * Collection을 조회하는데 결과가 없으면? 결과는 null이 아님!
         * 빈 컬렉션을 반환한다. .size() = 0
         */
        List<Member> sdsdfsdf = memberRepository.findListByUserName("sdsdfsdf");
        System.out.println("sdsdfsdf = " + sdsdfsdf);
        System.out.println("sdsdfsdf.size() = " + sdsdfsdf.size());
    }

    @Test
    public void paging(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        //Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUserName(), null));//map은 내부의 것을 바꿔서 다른 결과를 냄.

        //then
        List<Member> content = page.getContent(); //data 꺼내기
        long totalCount = page.getTotalElements(); //totalCount

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumber()).isEqualTo(0); //page 번호
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.isFirst()).isTrue();
        Assertions.assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlug(20);
        em.flush();
        em.clear();
        List<Member> result = memberRepository.findByUserName("member5");
        Member member5 = result.get(0);
        System.out.println("member5.getAge() = " + member5.getAge());


        //then
        Assertions.assertThat(resultCount).isEqualTo(3);

    }

    @Test
    public void findMemberLazy(){
        //given
        //member1 -> TeamA
        //member2 -> TeamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        System.out.println("===================");
        for (Member member : members) {
            System.out.println("member.getUserName() = " + member.getUserName());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
        }
    }

    @Test
    public void queryHint(){
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUserName("member1");
        findMember.setUserName("member2");

        em.flush();

    }

    @Test
    public void lock(){
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findLockByUserName("member1");


    }

}
