package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{

    public List<Member> findMemberByUserNameAndAgeGreaterThan(String userName, int age);

    @Query(name = "Member.findByUserName")
    List<Member> findByUserName(@Param("userName") String userName);

    @Query("select m from Member m where m.userName = :userName and m.age = :age")
    List<Member> findUser(@Param("userName") String userName, @Param("age") int age);

    @Query("select m.userName from Member m")
    List<String> findUserNameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUserName(String userName);
    Member findMemberByUserName(String userName);
    Optional<Member> findOptionalByUserName(String userName);

    //Page<Member> findByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m left join m.team t",countQuery = "select count(m.userName) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) //얘가 있어야 jpa의 .executeUpdate()실행됨. 없으면 resultRist or SingleResult 반환.
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlug(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();


    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //org.hibernate.readOnly를 hiberante가 열어놨다!
    //readOnly가 적용되면 영속성 컨텍스트에 스냅샷 안만들어 놓음.
    @QueryHints(value= @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String userName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUserName(String userName);


    List<NestedClosedProjections> findProjectionsByUserName(@Param("userName") String userName);

    @Query(value = "select * from member where user_name = ?", nativeQuery = true)
    Member findByNativeQuery(String userName);

    @Query(value = "select m.member_id as id, m.userName, t.name as teamName " +
            "from member m left join team t",
    countQuery = "select count(*) from member",
    nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
