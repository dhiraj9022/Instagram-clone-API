package com.instagram.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.instagram.dto.PostDto;
import com.instagram.exception.NotFoundException;
import com.instagram.model.Post;
import com.instagram.model.User;
import com.instagram.repo.PostRepository;
import com.instagram.repo.UserRepository;

@Service
public class PostService {

	private static final Logger logger = LoggerFactory.getLogger(PostService.class);

	@Autowired
	private PostRepository postRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepo;

	public Post submitPost(@Valid PostDto postDto) {

		User user = userService.checkUsernameAvailable(postDto.getUser().getUsername());

		Post post = new Post();
		post.setCaption(postDto.getCaption());
		post.setLocation(postDto.getLocation());
		post.setLikeCount(postDto.getLikeCount());
		post.setUser(user);

		logger.info("Post submitted successfully");
		return postRepo.save(post);
	}

	public List<PostDto> getAllPost() {
		List<PostDto> postDtos = new ArrayList<>();

		postRepo.findAll().forEach(p -> {

			PostDto postDto = new PostDto();
			postDto.setCaption(p.getCaption());
			postDto.setComments(p.getComments());
			postDto.setLikeCount(p.getLikeCount());
//			postDto.setUser(p.getUser());

			postDtos.add(postDto);
		});
		return postDtos;
	}

	public Post getPost(int postId) {
		Optional<Post> postOpt = postRepo.findById(postId);
		if (!postOpt.isPresent()) {
			throw new NotFoundException("Post " + postId + " is not found");
		}
		return postOpt.get();
	}

	public Post updatePost(@Valid PostDto postDto, int postId) {

		Post updatePost = getPost(postId);

		updatePost.setCaption(postDto.getCaption());
		updatePost.setLocation(postDto.getLocation());
		updatePost.setLikeCount(postDto.getLikeCount());

		logger.info("Post updated submitted successfully");

		return postRepo.save(updatePost);
	}

	public void deletePost(int userId) {

		User user = userService.getUser(userId);
		postRepo.deleteById(user.getUserId());

		logger.info("Post deleted successfully");

	}

}
