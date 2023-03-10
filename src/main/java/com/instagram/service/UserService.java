package com.instagram.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.instagram.Enum.Status;
import com.instagram.dto.AvatarDto;
import com.instagram.dto.PostDto;
import com.instagram.dto.UserDto;
import com.instagram.dto.UserInfoDto;
import com.instagram.exception.NotFoundException;
import com.instagram.model.User;
import com.instagram.repo.UserRepository;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;

	public User AddUser(@Valid UserDto userDto) {

		User user = new User();

		user.setUsername(userDto.getUsername());
		user.setFullName(userDto.getFullName());
		user.setBio(userDto.getBio());
		user.setStatus(Status.ONLINE);
		logger.info("User successfully added !!!");

		return userRepo.save(user);

	}

	public User uploadAvatar(@Valid AvatarDto avatarDto) {
		User user = new User();
		user.setAvatar(avatarDto.getAvatar());
		return userRepo.save(user);
	}

	public User updateAvatar(AvatarDto avatarDto, int avatarId) {

		User updateAvatar = new User();

		if (!userRepo.existsById(avatarId)) {
			throw new NotFoundException("Avatar id not matches");
		}
		updateAvatar.setAvatar(avatarDto.getAvatar());
		return userRepo.save(updateAvatar);
	}

	public User updateUsername(UserDto userDto, int userId) {

		User updateUsername = getUser(userId);
		updateUsername.setUsername(userDto.getUsername());

		logger.info("Username updated successfully !!!");

		return userRepo.save(updateUsername);
	}

	public User enableStatus(int userId) {

		User user = getUser(userId);
		user.setStatus(Status.OFFLINE);
		return user;
	}

	public User updateUserInfo(@Valid UserInfoDto infoDto, int userId) {

		User updateUser = getUser(userId);
		updateUser.setFullName(infoDto.getFullName());
		updateUser.setBio(infoDto.getBio());
		logger.info("User info updated successfully !!!");

		return userRepo.save(updateUser);
	}

	public User getUser(int userId) {
		Optional<User> userOpt = userRepo.findById(userId);
		if (!userOpt.isPresent()) {
			throw new NotFoundException("User " + userId + " not found");
		}
		return userOpt.get();
	}

	public User checkUsernameAvailable(String username) {
		Optional<User> userOpt = userRepo.findByUsername(username);
		if (!userOpt.isPresent()) {
			throw new NotFoundException("Username " + username + " not found");
		}
		return userOpt.get();
	}

	public List<UserDto> displayAllUser() {
		List<UserDto> userDtos = new ArrayList<>();

		userRepo.findAll().forEach(u -> {
			UserDto userDto = new UserDto();
			userDto.setBio(u.getBio());
			userDto.setFullName(u.getFullName());
			userDto.setUsername(u.getUsername());
			userDto.setStatus(u.getStatus());

			List<PostDto> postDtos = new ArrayList<>();

			u.getPosts().forEach(p -> {
				PostDto postDto = new PostDto();
				postDto.setCaption(p.getCaption());
				postDto.setLikeCount(p.getLikeCount());

//				List<CommentDto> commentDtos = new ArrayList<>();
//				p.getComments().forEach(c -> {
//
//					CommentDto commentDto = new CommentDto();
//					commentDto.setComment(c.getComment());
//
//					commentDtos.add(commentDto);
//				});

				postDtos.add(postDto);

			});

			userDto.setPosts(u.getPosts());

			userDtos.add(userDto);
		});
		return userDtos;
	}

	public void deleteUserPermanent(int userId) {
		User user = getUser(userId);
		userRepo.delete(user);
		logger.info("User deleted successfully !!!");
	}

}
