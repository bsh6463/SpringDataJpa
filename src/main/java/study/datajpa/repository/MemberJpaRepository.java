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

    public List<Member> findByUserName(String userName){
        return em.createNamedQuery("Member.findByUserName", Member.class)
                .setParameter("userName", "member1")
                .getResultList();
    }

    public List<Member> findbyPage(int age, int offset, int limit){
        return em.createQuery("select m from Member m where m.age = :age order by m.userName desc")
                .setParameter("age", age)
                .setFirstResult(offset) //몇 번째부터?
                .setMaxResults(limit) // 몇 개?
                .getResultList();
    }

    /**
     * total count가져오기.
     */
    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    public int bulkAgePlus(int age){
        return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                    .setParameter("age", age)
                    .executeUpdate(); //Returns: the number of entities updated or deleted
    }
}
