package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        Assertions.assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        Assertions.assertThat(findMember.getUserName()).isEqualTo(savedMember.getUserName());
        Assertions.assertThat(findMember).isEqualTo(member);

    }


    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        //단건조회 검증
        Assertions.assertThat(findMember1.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(findMember1.getUserName()).isEqualTo(member1.getUserName());
        Assertions.assertThat(findMember2.getId()).isEqualTo(member2.getId());
        Assertions.assertThat(findMember2.getUserName()).isEqualTo(member2.getUserName());

        //리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);
        long count = memberJpaRepository.count();
        Assertions.assertThat(all.size()).isEqualTo(count);

        //삭제
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        long deleteCount = memberJpaRepository.count();
        Assertions.assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUserNameAndAgeGreaterThan(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> findMembers = memberJpaRepository.findMemberByUserNameAndAgeGreaterThan("bbb", 10);

        Assertions.assertThat(findMembers.get(0).getUserName()).isEqualTo(member2.getUserName());
        Assertions.assertThat(findMembers.get(0).getAge()).isEqualTo(member2.getAge());

    }

    @Test
    public void paging(){
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset =0;
        int limit = 3;
        //when
        List<Member> members = memberJpaRepository.findbyPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        //then
        Assertions.assertThat(members.size()).isEqualTo(3);
        Assertions.assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void bulkUpdate(){
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        //then
        Assertions.assertThat(resultCount).isEqualTo(3);

    }


}