package com.jihun.mysimpleblog.service;

import com.jihun.mysimpleblog.domain.User;
import com.jihun.mysimpleblog.domain.UserRole;
import com.jihun.mysimpleblog.dto.mapper.PostMapper;
import com.jihun.mysimpleblog.infra.model.CustomPageDto.CustomPageRequest;
import com.jihun.mysimpleblog.infra.model.CustomPageDto.CustomPageResponse;
import com.jihun.mysimpleblog.repository.UserRepository;
import com.jihun.mysimpleblog.domain.Category;
import com.jihun.mysimpleblog.domain.Post;
import com.jihun.mysimpleblog.repository.CategoryRepository;
import com.jihun.mysimpleblog.repository.PostRepository;
import com.jihun.mysimpleblog.infra.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.jihun.mysimpleblog.dto.PostDto.*;
import static com.jihun.mysimpleblog.infra.constant.GlobalConstant.POST_PAGE_SIZE;
import static com.jihun.mysimpleblog.infra.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final PostMapper postMapper;

    @Transactional
    public PostResponse createPost(@Valid PostRequest request, Long userId) {
        User user = checkLoginUser(userId);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXIST));

        Post post = postMapper.toEntity(request, user, category);
        Post savedPost = postRepository.save(post);

        log.info("Created new post. title: {}, author: {}", request.getTitle(), user.getUsername());

        return postMapper.toResponse(savedPost);
    }

    public List<PostResponse> findAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CustomPageResponse<PostResponse> searchPosts(PostSearchForm searchDto, CustomPageRequest customPageRequest) {
        Pageable pageable = customPageRequest.toPageRequest();

        Page<PostResponse> postResponses = postRepository.findAllBySearchCondition(
                searchDto.getTitle(),
                searchDto.getAuthorName(),
                pageable
        ).map(postMapper::toResponse);

        log.info("Searching posts. title: {}, author: {}", searchDto.getTitle(), searchDto.getAuthorName());
        return CustomPageResponse.fromPage(postResponses);
    }

    @Transactional
    public PostResponse getPost(Long id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        Post post = postRepository.findByIdWithAuthorAndCategory(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        // todo: 추후에 Redis 공부하면서 Redis + IP 방식으로 변경 ( 조회수 관련 )

        // 쿠키 이름 생성 (post_{id}_view)
        String cookieName = "post_" + id + "_view";
        Cookie[] cookies = httpRequest.getCookies();
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
            httpResponse.addCookie(viewCookie);
        }

        return postMapper.toResponse(post);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_EXIST));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        // 작성자 검증
        if (!post.getAuthor().equals(user)) {
            throw new CustomException(NOT_AUTHORIZED);
        }

        postMapper.updateEntity(post, request, category);

        log.info("Post updated successfully. id: {}", post.getId());

        return postMapper.toResponse(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        // 권한 검증
        if (!post.getAuthor().equals(user) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new CustomException(NOT_AUTHORIZED);
        }

        postRepository.delete(post);

        log.info("Post deleted. postId: {}, deleted by: {}", postId, user.getUsername());
    }

    public CustomPageResponse<PostResponse> findAllByAuthorId(CustomPageRequest customPageRequest, Long userId) {
        Pageable pageRequest = customPageRequest.toPageRequest();
        Page<PostResponse> postResponses = postRepository.findAllByAuthorId(userId, pageRequest)
                .map(postMapper::toResponse);

        return CustomPageResponse.fromPage(postResponses);
    }

    private User checkLoginUser(Long userId) {
        if (userId == null) {
            throw new CustomException(NOT_AUTHORIZED);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    private void validateAdminUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER));

        if (!user.isAdmin()) {
            throw new CustomException(NOT_AUTHORIZED);
        }
    }
}
