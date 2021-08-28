package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;


    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public void delete(Member member){
        em.remove(member);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id){
        Member findMember = em.find(Member.class, id);
        return Optional.ofNullable(findMember); //null일수도 있고 아닐수도 있고 ㅋㅋ
    }

    /**
     * Member의 수 count
     */
    public long count(){
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }


    public Member find(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findMemberByUserNameAndAgeGreaterThan(String userName, int age){
        return em.createQuery("select m from Member m where m.userName = :userName and m.age > :age")
                .setParameter("userName", userName)
                .setParameter("age", age)
                .getResultList();
    }

}
