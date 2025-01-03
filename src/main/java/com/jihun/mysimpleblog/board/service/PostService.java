package com.jihun.mysimpleblog.board.service;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.Role;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.repository.UserRepository;
import com.jihun.mysimpleblog.board.entity.Category;
import com.jihun.mysimpleblog.board.entity.Post;
import com.jihun.mysimpleblog.board.entity.dto.post.PostFormDto;
import com.jihun.mysimpleblog.board.entity.dto.post.PostResponse;
import com.jihun.mysimpleblog.board.entity.dto.post.PostSearchDto;
import com.jihun.mysimpleblog.board.entity.dto.post.PostUpdateDto;
import com.jihun.mysimpleblog.board.repository.CategoryRepository;
import com.jihun.mysimpleblog.board.repository.PostRepository;
import com.jihun.mysimpleblog.global.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<PostResponse> searchPosts(PostSearchDto searchDto, int page) {
        log.info("Searching posts. title: {}, author: {}, page: {}", searchDto.getTitle(), searchDto.getAuthorName(), page);

        PageRequest pageable = PageRequest.of(page, POST_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> posts = postRepository.findAllBySearchCondition(
                searchDto.getTitle(),
                searchDto.getAuthorName(),
                pageable
        );

        return posts.map(PostResponse::fromEntity);
    }


    @Transactional
    public PostResponse create(@Valid PostFormDto dto, CustomUserDetails userDetails) {
        if (userDetails == null) {

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
}
