package com.example.controller;

import java.io.IOException;
import java.util.Arrays;

import org.riversun.linebot.LineBotServlet;

import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;

@SuppressWarnings("serial")
public class BotServlet extends LineBotServlet {

	private static final String CHANNEL_SECRET = "ad750d2dcf8c2679abf87d3d61ca85d8";
	private static final String CHANNEL_ACCESS_TOKEN = "[wvydTwaiKtsG4Z90XPfG6hWB31/TX2tceTz+v1NqSXgOMgUZ55c4GnZZ6rd+i9lJn8d0k17/7A5E0Mq1kKpmAdMKWkmqGaiezxDAZykxJIA8MoDYx+a19t4cQbRd5zLWl3k30y2pSM1zzZQz/JVSjwdB04t89/1O/w1cDnyilFU=";

	@Override
	protected ReplyMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws IOException {

		TextMessageContent userMessage = event.getMessage();

		// Get user profile
		UserProfileResponse userProfile = getUserProfile(event.getSource().getUserId());

		String botResponseText = "Hi," + userProfile.getDisplayName() + "," + "You say '" + userMessage.getText()
				+ "' !";

		TextMessage textMessage = new TextMessage(botResponseText);

		return new ReplyMessage(event.getReplyToken(), Arrays.asList(textMessage));
	}

	@Override
	protected ReplyMessage handleDefaultMessageEvent(Event event) {
		// When other messages not overridden as handle* is received, do nothing
		// (returns null)
		return null;
	}

	@Override
	public String getChannelSecret() {
		return CHANNEL_SECRET;
	}

	@Override
	public String getChannelAccessToken() {
		return CHANNEL_ACCESS_TOKEN;
	}

}
