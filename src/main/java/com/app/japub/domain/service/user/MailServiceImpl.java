package com.app.japub.domain.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.app.japub.domain.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class MailServiceImpl implements MailService {
	private final JavaMailSender mailSender;
	private static final String TITLE = "안녕하세요 중앙경제평론사 입니다.";
	@Value("${spring.mail.username")
	private String from;

	@Override
	public void sendMail(UserDto userDto) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from); // 보내는 사람 이메일
		message.setTo(userDto.getUserEmail()); // 받는 사람 이메일
		message.setSubject(TITLE); // 이메일 제목
		message.setText(getMailContent(userDto)); // 이메일 본문
		mailSender.send(message);
	}

	@Override
	public String getMailContent(UserDto userDto) {
		return "귀하의 아이디는" + userDto.getUserId() + "이고 임시 비밀번호는 " + userDto.getUserPassword() + "입니다";
	}

}
