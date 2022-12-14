package Tamanegiseoul.comeet.repository;

import Tamanegiseoul.comeet.domain.Member;
import Tamanegiseoul.comeet.domain.StackRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Long remove(Member member) {
        Long removedMemberId = member.getMemberId();
        em.remove(member);
        return removedMemberId;
    }

    public int removeByMemberId(Long memberId) {
        return em.createQuery("delete from Member m where m.memberId = :memberId")
                .setParameter("memberId", memberId)
                .executeUpdate();
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public Member findMemberByNickname(String nickname) {
        return em.createQuery("select m from Member m where m.nickname = :nickname", Member.class)
                .setParameter("nickname", nickname)
                .getResultList().stream().findFirst().orElse(null);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<StackRelation> findPreferredStacks(Long memberId) {
        return em.createQuery("select sr from StackRelation sr where sr.member.memberId = :memberId", StackRelation.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public Member findMemberByEmail(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList().stream().findFirst().orElse(null);
    }


}
