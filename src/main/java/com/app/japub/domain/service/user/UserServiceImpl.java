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
		return userDao.findByUserId(userId);
	}

	@Override
	public UserDto findByUserEmail(String userEmail) {
		return userDao.findByUserEmail(userEmail);
	}

	@Override
	public UserDto findByUserNum(Long userNum) {
		return userDao.findByUserNum(userNum);
	}

	@Override
	public boolean insert(UserDto userDto) {
		userDto.setUserPassword(passwordService.encode(userDto.getUserPassword()));
		return userDao.insert(userDto) == DbConstants.SUCCESS_CODE;
	}

	@Override
	public boolean update(UserDto userDto) {
		userDto.setUserPassword(passwordService.encode(userDto.getUserPassword()));
		return userDao.update(userDto) == DbConstants.SUCCESS_CODE;
	}

	@Override
	public boolean delete(Long userNum) {
		return userDao.delete(userNum) == DbConstants.SUCCESS_CODE;
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
		mailService.sendMail(userDto);
	}

}
