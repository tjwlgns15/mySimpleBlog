package com.jihun.mysimpleblog.board.service;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.Role;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import com.jihun.mysimpleblog.board.entity.Category;
import com.jihun.mysimpleblog.board.entity.Post;
import com.jihun.mysimpleblog.board.entity.dto.post.*;
import com.jihun.mysimpleblog.board.repository.CategoryRepository;
import com.jihun.mysimpleblog.board.repository.PostRepository;
import com.jihun.mysimpleblog.global.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.jihun.mysimpleblog.global.constant.GlobalConstant.POST_PAGE_SIZE;
import static com.jihun.mysimpleblog.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<PostResponse> findAll() {
        return postRepository.findAll().stream().map(PostResponse::fromEntity).collect(Collectors.toList());
    }

    public Page<PostResponse> searchPosts(PostSearchDto searchDto, int page) {
        log.info("Searching posts. title: {}, author: {}, page: {}", searchDto.getTitle(), searchDto.getAuthorName(), page);
        PageRequest pageable = getPageRequest(page);
        Page<Post> posts = postRepository.findAllBySearchCondition(
                searchDto.getTitle(),
                searchDto.getAuthorName(),
                pageable
        );

        return posts.map(PostResponse::fromEntity);
    }

    public Page<PostResponse> findAllByAuthorId(Long authorId, int page) {
        PageRequest pageable = getPageRequest(page);
        Page<Post> posts = postRepository.findAllByAuthorId(authorId, pageable);
        return posts.map(PostResponse::fromEntity);
    }

    @Transactional
    public PostResponse getPost(Long id, HttpServletRequest request, HttpServletResponse response) {

        Post post = postRepository.findByIdWithAuthorAndCategory(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        // todo: 추후에 Redis 공부하면서 Redis + IP 방식으로 변경 ( 조회수 관련 )

        // 쿠키 이름 생성 (post_{id}_view)
        String cookieName = "post_" + id + "_view";
        Cookie[] cookies = request.getCookies();
        boolean hasViewCookie = false;

        // 쿠키 확인
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    hasViewCookie = true;
                    break;
                }
            }
        }

        // 쿠키가 없으면 조회수 증가 및 쿠키 생성
        if (!hasViewCookie) {
            // 조회수 증가
            post.incrementViewCount();

            // 쿠키 생성 (24시간 유효)
            Cookie viewCookie = new Cookie(cookieName, "true");
            viewCookie.setMaxAge(24 * 60 * 60);
            viewCookie.setPath("/");
            response.addCookie(viewCookie);
        }

        return PostResponse.fromEntity(post);
    }


    @Transactional
    public PostResponse create(@Valid PostFormDto dto, CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new CustomException(NOT_AUTHORIZED);
        }

        log.info("Creating new post. title: {}, author: {}", dto.getTitle(), userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXIST));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .category(category)
                .build();

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully. id: {}", savedPost.getId());

        return PostResponse.fromEntity(savedPost);
    }

    @Transactional
    public PostResponse update(@Valid PostUpdateDto dto, CustomUserDetails userDetails) {
        log.info("Updating post. id: {}, author: {}", dto.getId(), userDetails.getUsername());

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXIST));

        Post post = postRepository.findById(dto.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        // 작성자 검증
        if (!post.getAuthor().equals(user)) {
            throw new CustomException(NOT_AUTHORIZED);
        }

        post.update(category, dto.getTitle(), dto.getContent());
        log.info("Post updated successfully. id: {}", post.getId());

        return PostResponse.fromEntity(post);
    }

    public void delete(Long id, CustomUserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        // 권한 검증
        if (!post.getAuthor().equals(user) && !user.getRole().equals(Role.ADMIN)) {
            throw new CustomException(NOT_AUTHORIZED);
        }

        postRepository.delete(post);
        log.info("Post deleted. id: {}, deleted by: {}", id, user.getEmail());
    }


    private static PageRequest getPageRequest(int page) {
        return PageRequest.of(page, POST_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
