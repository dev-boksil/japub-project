package com.app.japub.domain.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.japub.common.DbConstants;
import com.app.japub.domain.dao.user.UserDao;
import com.app.japub.domain.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserDao userDao;
	private final PasswordService passwordService;
	private final MailService mailService;

	@Override
	public UserDto findByUserId(String userId) {
		try {
			return userDao.findByUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("userService findByUserId error");
			return null;
		}
	}

	@Override
	public UserDto findByUserEmail(String userEmail) {
		try {
			return userDao.findByUserEmail(userEmail);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("userService findByUserEmail error");
			return null;
		}
	}

	@Override
	public UserDto findByUserNum(Long userNum) {
		try {
			return userDao.findByUserNum(userNum);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("userService findByUserNum error");
			return null;
		}
	}

	@Override
	public boolean insert(UserDto userDto) {
		String encodedPassword = passwordService.encode(userDto.getUserPassword());
		userDto.setUserPassword(encodedPassword);
		try {
			return userDao.insert(userDto) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("userService insert error");
			return false;
		}
	}

	@Override
	public boolean update(UserDto userDto) {
		String encodedPassword = passwordService.encode(userDto.getUserPassword());
		userDto.setUserPassword(encodedPassword);
		try {
			return userDao.update(userDto) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("userService update error");
			return false;
		}
	}

	@Override
	public boolean delete(Long userNum) {
		try {
			return userDao.delete(userNum) == DbConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("userService delete error");
			return false;
		}
	}

	@Override
	public UserDto login(String userId, String userPassword) {
		UserDto userDto = findByUserId(userId);
		return userDto != null && passwordService.matches(userPassword, userDto.getUserPassword()) ? userDto : null;
	}

	@Override
	public UserDto findByUserNumAndUserPassword(Long userNum, String userPassword) {
		UserDto userDto = findByUserNum(userNum);
		return userDto != null && passwordService.matches(userPassword, userDto.getUserPassword()) ? userDto : null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setUserPasswordAndSendMail(UserDto userDto) {
		String tempPassword = passwordService.getTempPassword();
		userDto.setUserPassword(tempPassword);
		if (!update(userDto)) {
			throw new RuntimeException("userService setUserPasswordAndSendMail update error");
		}
		userDto.setUserPassword(tempPassword);
		if (!mailService.sendMail(userDto)) {
			throw new RuntimeException("userService setUserPasswordAndSendMail sendMail error");
		}

	}

}
