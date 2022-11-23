package Tamanegiseoul.comeet.service;
import Tamanegiseoul.comeet.domain.Posts;
import Tamanegiseoul.comeet.domain.enums.TechStack;
import Tamanegiseoul.comeet.domain.exception.ResourceNotFoundException;
import Tamanegiseoul.comeet.dto.post.request.UpdatePostRequest;
import Tamanegiseoul.comeet.repository.CommentRepository;
import Tamanegiseoul.comeet.repository.PostRepository;
import Tamanegiseoul.comeet.repository.StackRelationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final StackRelationService stackRelationService;

    private final CommentRepository commentRepository;
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
    public void updatePost(Long id, UpdatePostRequest updatedPost) {
        Posts findPost = postRepository.findOne(id);
        findPost.updatePost(updatedPost);
        findPost.initDesignateStack();
        findPost.updateDesignateStack(updatedPost.getDesignatedStacks());
        findPost.updateModifiedDate();
    }

    @Transactional
    public int removePostByPostId(Long postId) {
        Posts findPost = postRepository.findOne(postId);
        // first, remove child entity
        stackRelationRepository.removeRelatedStacksByPost(postId);
        commentRepository.removeCommentByPostId(postId);
        // then, remove parent entity
        return postRepository.removePostByPostId(postId);

    }

    @Transactional
    public void removePostByPosterId(Long userId) {
        List<Posts> findPosts = postRepository.findPostByUserId(userId);
        // first, remove child entity
        for(Posts p : findPosts) {
            stackRelationRepository.removeRelatedStacksByPost(p.getPostId());
            commentRepository.removeCommentByPostId(p.getPostId());
        }

        // then, remove parent entity
        postRepository.removePostByPosterId(userId);
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
    public List<Posts> findPostByUserId(Long userId) {
        return postRepository.findPostByUserId(userId);
    }


    /***********************
     * POST UPDATE METHODS *
     ***********************/
    @Transactional
    public void updateDesignateStacks(Long postId, TechStack...techStacks) {
        Posts findPost = postRepository.findOne(postId);
        findPost.initDesignateStack();
        for(TechStack stack : techStacks) {
            findPost.addDesignateStack(stack);
        }
    }

}
