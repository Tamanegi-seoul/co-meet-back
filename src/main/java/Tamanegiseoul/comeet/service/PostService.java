package Tamanegiseoul.comeet.service;
import Tamanegiseoul.comeet.domain.Member;
import Tamanegiseoul.comeet.domain.Posts;
import Tamanegiseoul.comeet.domain.enums.TechStack;
import Tamanegiseoul.comeet.domain.exception.ResourceNotFoundException;
import Tamanegiseoul.comeet.dto.post.request.UpdatePostRequest;
import Tamanegiseoul.comeet.dto.post.response.PostCompactDto;
import Tamanegiseoul.comeet.dto.post.response.UpdatePostResponse;
import Tamanegiseoul.comeet.repository.CommentRepository;
import Tamanegiseoul.comeet.repository.MemberRepository;
import Tamanegiseoul.comeet.repository.PostRepository;
import Tamanegiseoul.comeet.repository.StackRelationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final StackRelationService stackRelationService;

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final StackRelationRepository stackRelationRepository;

    @PersistenceContext
    EntityManager em;

    @Transactional
    public Long registerPost(Posts post) {
        log.info("[PostService:registerPost] register method init");
        postRepository.save(post);
        post.updateModifiedDate();
        post.updateCreatedDate();
        log.info("[PostService:registerPost] successfully registered");
        return post.getPostId();
    }

    /***********************
     * UPDATE POST METHODS *
     ***********************/

    @Transactional
    public UpdatePostResponse updatePost(UpdatePostRequest updatedPost) {
        Posts findPost = postRepository.findOne(updatedPost.getPostId());
        if(findPost == null) {
            throw new ResourceNotFoundException("post_id", "post id", updatedPost.getPostId());
        }
        findPost.updatePost(updatedPost);
        findPost.updateDesignateStack(updatedPost.getDesignatedStacks());
        findPost.updateModifiedDate();

        return UpdatePostResponse.toDto(findPost);
    }

    @Transactional
    public void removePostByPostId(Long postId) {
        Posts findPost = postRepository.findOne(postId);
        if(findPost == null) {
            throw new ResourceNotFoundException("post", "post_id", postId);
        }

        em.remove(findPost);

    }

    @Transactional
    public void removePostByPosterId(Long memberId) {
        List<Posts> findPosts = postRepository.findPostByMemberId(memberId);
        // first, remove child entity
        for(Posts p : findPosts) {
            stackRelationRepository.removeRelatedStacksByPost(p.getPostId());
            commentRepository.removeCommentByPostId(p.getPostId());
        }

        // then, remove parent entity
        postRepository.removePostByPosterId(memberId);
    }

    @Transactional
    public void increasePostHit(Long postId) {
        Posts findPost = postRepository.findOne(postId);
        findPost.increaseHits();
    }

    /***********************
     * SEARCH POST METHODS *
     ***********************/
    @Transactional(readOnly = true)
    public Posts findPostById(Long postId) {
        log.warn("[PostService:findPostById] find method init");
        Posts findPost = postRepository.findOne(postId);
        if(findPost == null) {
            log.warn("[PostService:findPostById] can't find post with given post id");
            throw new ResourceNotFoundException("Posts", "postId", postId);
        } else {
            log.warn("[PostService:findPostById] find post with given post id");
            return findPost;
        }
    }

    @Transactional(readOnly = true)
    public List<Posts> findAll() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Posts> findAll(int offset, int limit) {
        return postRepository.findAll(offset, limit);
    }

    @Transactional(readOnly = true)
    public List<Posts> findPostByMemberId(Long memberId) {
        Member findMember = memberRepository.findOne(memberId);
        if(findMember == null) {
            throw new ResourceNotFoundException("member_id", "memberId", memberId);
        }
        return postRepository.findPostByMemberId(memberId);
    }


    /***********************
     * POST UPDATE METHODS *
     ***********************/
    @Transactional
    public void updateDesignateStacks(Long postId, List<TechStack> techStacks) {
        Posts findPost = postRepository.findOne(postId);
        //findPost.initDesignateStack();
        findPost.getDesignatedStack().clear();
        for(TechStack stack : techStacks) {
            findPost.addDesignateStack(stack);
        }
    }

    /***********************
     * DTO TRANSFER METHODS *
     ***********************/

    public List<PostCompactDto> toCompactDtoList(List<Posts> postList) {
        List<PostCompactDto> list = new ArrayList<>();
        for(Posts post : postList) {
            PostCompactDto dto = PostCompactDto.toDto(post);
            List<TechStack> techStacks = stackRelationService.findTechStackByPostId(post.getPostId());
            log.warn("[PostApiController:toDtoList]"+techStacks.toString());
            dto.designatedStacks(techStacks);
            list.add(dto);
        }
        return list;
    }

}
