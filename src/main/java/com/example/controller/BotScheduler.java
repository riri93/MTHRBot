package com.example.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import com.example.entity.BotInformation;
import com.example.entity.Candidate;
import com.example.entity.ChatMessageLine;
import com.example.entity.JobCandidateRelation;
import com.example.entity.Shop;
import com.example.entity.ShopCandidateRelation;
import com.example.entity.ShopCandidateRelationPK;
import com.example.repository.BotInformationRepository;
import com.example.repository.ChatMessageLineRepository;
import com.example.repository.JobCandidateRelationRepository;
import com.example.repository.ShopCandidateRelationRepository;
import com.example.repository.ShopRepository;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;

@Configuration
@EnableScheduling
public class BotScheduler {

	private Shop shop = new Shop();
	private String channelToken = "wvydTwaiKtsG4Z90XPfG6hWB31/TX2tceTz+v1NqSXgOMgUZ55c4GnZZ6rd+i9lJn8d0k17/7A5E0Mq1kKpmAdMKWkmqGaiezxDAZykxJIA8MoDYx+a19t4cQbRd5zLWl3k30y2pSM1zzZQz/JVSjwdB04t89/1O/w1cDnyilFU=";

	final String uri = "https://hooks.slack.com/services/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";
	RestTemplate restTemplate = new RestTemplate();

	@Autowired
	JobCandidateRelationRepository jobCandidateRelationRepository;

	@Autowired
	ShopCandidateRelationRepository shopCandidateRelationRepository;

	@Autowired
	ChatMessageLineRepository chatMessageLineRepository;

	@Autowired
	ShopRepository shopRepository;

	@Autowired
	BotInformationRepository botInformationRepository;

	/**
	 * 
	 * @author Rihab Kallel
	 * 
	 *         send this message after one day from apply
	 * 
	 *         send this message after two days from the call shop message date
	 *         (second time)
	 * 
	 *         send this message after 3 hours if user answered no to "have got in
	 *         contact with the shop?" (3 hours after the call shop message date)
	 * 
	 *         send this message after two days from interview send this message if
	 *         interview time is not yet confirmed after two days
	 * 
	 *         send interview reminder one day before the interview
	 * 
	 *         sets the candidate to a potential candidate in admin shop-candidate
	 *         relation if candidate did not reply to the reason message for 2 days
	 * 
	 *         scheduler cron checks the database every hour
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "0 0 * * * *")
	public void sendPushMessages() throws Exception {

		List<JobCandidateRelation> jobCandidateRelations = new ArrayList<>();
		jobCandidateRelations = jobCandidateRelationRepository.getAllAppliedCandidates();

		List<ShopCandidateRelation> shopCandidateRelations = new ArrayList<>();
		shopCandidateRelations = shopCandidateRelationRepository.findAll();

		if (jobCandidateRelations != null) {

			for (JobCandidateRelation jobCandidateRelation : jobCandidateRelations) {

				// have you got in contact with the shop
				if (jobCandidateRelation.getAppliedDate() != null) {
					ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
					ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
					shopCandidateRelationPK.setIdCandidate(jobCandidateRelation.getCandidate().getIdUser());
					shopCandidateRelationPK.setIdShop(jobCandidateRelation.getJob().getShop().getIdShop());

					shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);

					BotInformation botInformation = new BotInformation();
					botInformation = shopCandidateRelation.getCandidate().getBotInformation();
					botInformation.setSearchCriteria("call shop");
					botInformationRepository.saveAndFlush(botInformation);

					Date askInterviewDate = null;
					if (shopCandidateRelation != null) {
						askInterviewDate = shopCandidateRelation.getAskInterviewDate();
					}
					int callShopMessageCounter = jobCandidateRelation.getCallShopMessageCounter();

					// if user answered with NO or did not get asked for interview time
					if (askInterviewDate == null) {
						if (callShopMessageCounter < 3) {

							// send after one day from apply
							if (jobCandidateRelation.getCallShopMessageDate() == null) {

								Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
								cal.setTime(jobCandidateRelation.getAppliedDate());
								cal.add(Calendar.DAY_OF_WEEK, 1);
								cal.getTime();
								Date date = new Date();
								SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
								sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
								String time = sdf.format(date);
								Date currentTime = sdf.parse(time);

								if (currentTime.after(cal.getTime())) {

									ConfirmTemplate confirmTemplate = new ConfirmTemplate(
											"Have you got in contact with the shop?",
											new MessageAction("Yes", "Yes I called"),
											new MessageAction("No", "No I did not"));
									TemplateMessage templateMessage = new TemplateMessage(
											"Have you got in contact with the shop?", confirmTemplate);
									PushMessage pushMessage = new PushMessage(
											jobCandidateRelation.getCandidate().getUserLineId().toString(),
											templateMessage);
									LineMessagingServiceBuilder.create(channelToken).build().pushMessage(pushMessage)
											.execute();

									shop = jobCandidateRelation.getJob().getShop();

									callShopMessageCounter++;
									jobCandidateRelation.setCallShopMessageCounter(callShopMessageCounter);
									jobCandidateRelation.setCallShopMessageDate((new Date()));
									jobCandidateRelationRepository.saveAndFlush(jobCandidateRelation);

									saveChatLineMessage(jobCandidateRelation.getCandidate(),
											"Have you got in contact with the shop?");
								}

							} else {

								Calendar calDay = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
								calDay.setTime(jobCandidateRelation.getCallShopMessageDate());
								calDay.add(Calendar.DAY_OF_WEEK, 2);
								calDay.getTime();

								Calendar calHour = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
								calHour.setTime(jobCandidateRelation.getCallShopMessageDate());
								calHour.add(Calendar.HOUR_OF_DAY, 3);
								calHour.getTime();

								Date date = new Date();
								SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
								sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
								String time = sdf.format(date);
								Date currentTime = sdf.parse(time);

								// send after 2 days from ask first message date
								if (currentTime.after(calDay.getTime())) {

									ConfirmTemplate confirmTemplate = new ConfirmTemplate(
											"Have you got in contact with the shop?",
											new MessageAction("Yes", "Yes I called"),
											new MessageAction("No", "No I did not"));
									TemplateMessage templateMessage = new TemplateMessage(
											"Have you got in contact with the shop?", confirmTemplate);
									PushMessage pushMessage = new PushMessage(
											jobCandidateRelation.getCandidate().getUserLineId().toString(),
											templateMessage);
									LineMessagingServiceBuilder.create(channelToken).build().pushMessage(pushMessage)
											.execute();

									shop = jobCandidateRelation.getJob().getShop();

									callShopMessageCounter++;
									jobCandidateRelation.setCallShopMessageCounter(callShopMessageCounter);
									jobCandidateRelation.setCallShopMessageDate((new Date()));
									jobCandidateRelationRepository.saveAndFlush(jobCandidateRelation);

									saveChatLineMessage(jobCandidateRelation.getCandidate(),
											"Have you got in contact with the shop?");

								}
								// send after 3 hours from ask second message date
								if (currentTime.after(calHour.getTime())) {

									ConfirmTemplate confirmTemplate = new ConfirmTemplate(
											"Have you got in contact with the shop?",
											new MessageAction("Yes", "Yes I called"),
											new MessageAction("No", "No I did not"));
									TemplateMessage templateMessage = new TemplateMessage(
											"Have you got in contact with the shop?", confirmTemplate);
									PushMessage pushMessage = new PushMessage(
											jobCandidateRelation.getCandidate().getUserLineId().toString(),
											templateMessage);
									LineMessagingServiceBuilder.create(channelToken).build().pushMessage(pushMessage)
											.execute();

									shop = jobCandidateRelation.getJob().getShop();

									callShopMessageCounter++;
									jobCandidateRelation.setCallShopMessageCounter(callShopMessageCounter);
									jobCandidateRelation.setCallShopMessageDate((new Date()));
									jobCandidateRelationRepository.saveAndFlush(jobCandidateRelation);

									saveChatLineMessage(jobCandidateRelation.getCandidate(),
											"Have you got in contact with the shop?");
								}
							}
						}
					}
				}
			}
		}

		if (shopCandidateRelations != null) {

			for (ShopCandidateRelation shopCandidateRelation : shopCandidateRelations) {

				// have you passed the interview message after 2 days from the interview
				if (shopCandidateRelation.getInterviewDate() != null) {

					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					cal.setTime(shopCandidateRelation.getInterviewDate());
					cal.add(Calendar.DAY_OF_WEEK, 2);
					cal.getTime();

					BotInformation botInformation = new BotInformation();
					botInformation = shopCandidateRelation.getCandidate().getBotInformation();
					botInformation.setSearchCriteria("pass interview");
					botInformationRepository.saveAndFlush(botInformation);

					int passedInterviewMessageCounter = shopCandidateRelation.getPassedInterviewMessageCounter();

					if (passedInterviewMessageCounter < 1) {
						if (shopCandidateRelation.getPassedInterviewMessageDate() == null) {

							Date date = new Date();
							SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
							sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
							String time = sdf.format(date);
							Date currentTime = sdf.parse(time);

							if (currentTime.after(cal.getTime())) {

								ConfirmTemplate confirmTemplate = new ConfirmTemplate("Have you passed the interview?",
										new MessageAction("Yes", "Yes I passed"),
										new MessageAction("No", "No I failed"));
								TemplateMessage templateMessage = new TemplateMessage("Have you passed the interview?",
										confirmTemplate);
								PushMessage pushMessage = new PushMessage(
										shopCandidateRelation.getCandidate().getUserLineId().toString(),
										templateMessage);
								LineMessagingServiceBuilder.create(channelToken).build().pushMessage(pushMessage)
										.execute();

								passedInterviewMessageCounter++;
								shopCandidateRelation.setPassedInterviewMessageCounter(passedInterviewMessageCounter);
								shopCandidateRelation.setPassedInterviewMessageDate((new Date()));
								shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

								shop = shopCandidateRelation.getShop();

								saveChatLineMessage(shopCandidateRelation.getCandidate(),
										"Have you passed the interview?");
							}
						}
					}
				}

				// ask did you confirm the interview time second time if user answered did not
				// confirm in the first time
				if ((shopCandidateRelation.getAskInterviewDate() != null
						&& !shopCandidateRelation.isConfirmedInterview())
						|| (shopCandidateRelation.getAskInterviewDate() != null
								&& shopCandidateRelation.isConfirmedInterview()
								&& shopCandidateRelation.getInterviewDate() == null)) {

					BotInformation botInformation = new BotInformation();
					botInformation = shopCandidateRelation.getCandidate().getBotInformation();
					botInformation.setSearchCriteria("confirm interview");
					botInformationRepository.saveAndFlush(botInformation);

					int askInterviewCounter = shopCandidateRelation.getAskInterviewCounter();

					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					cal.setTime(shopCandidateRelation.getAskInterviewDate());
					cal.add(Calendar.DAY_OF_WEEK, 2);
					cal.getTime();

					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
					String time = sdf.format(date);
					Date currentTime = sdf.parse(time);

					if (askInterviewCounter < 2) {
						if (currentTime.after(cal.getTime())) {

							ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
									"https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png",
									"Did you confirm the interview time?", "Did you confirm the interview time?",
									Arrays.asList(new MessageAction("Confirmed", "Interview confirmed"),
											new MessageAction("Not confirmed", "Interview not confirmed"),
											new MessageAction("No interview", "No interview")));
							TemplateMessage templateMessage = new TemplateMessage("Did you confirm the interview time?",
									buttonsTemplate);

							PushMessage pushMessage = new PushMessage(
									shopCandidateRelation.getCandidate().getUserLineId().toString(), templateMessage);
							LineMessagingServiceBuilder.create(channelToken).build().pushMessage(pushMessage).execute();

							askInterviewCounter++;
							shopCandidateRelation.setAskInterviewCounter(askInterviewCounter);
							shopCandidateRelation.setAskInterviewDate(new Date());
							shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

							shop = shopCandidateRelation.getShop();

							saveChatLineMessage(shopCandidateRelation.getCandidate(),
									"Did you confirm the interview time?");
						}
					}
				}

				// interview reminder
				if (shopCandidateRelation.getInterviewDate() != null) {

					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					cal.setTime(shopCandidateRelation.getInterviewDate());
					cal.add(Calendar.DAY_OF_WEEK, -1);
					cal.getTime();

					int remindInterviewCounter = shopCandidateRelation.getRemindInterviewCounter();

					BotInformation botInformation = new BotInformation();
					botInformation = shopCandidateRelation.getCandidate().getBotInformation();
					botInformation.setSearchCriteria("remind interview");
					botInformationRepository.saveAndFlush(botInformation);

					if (remindInterviewCounter < 1)
						if (shopCandidateRelation.getRemindInterviewDate() == null) {

							Date date = new Date();
							SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
							sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
							String time = sdf.format(date);
							Date currentTime = sdf.parse(time);

							if (currentTime.after(cal.getTime())) {

								TextMessage textMessage = new TextMessage("Tomorrow is the interview!");
								PushMessage pushMessage = new PushMessage(
										shopCandidateRelation.getCandidate().getUserLineId().toString(), textMessage);
								LineMessagingServiceBuilder.create(channelToken).build().pushMessage(pushMessage)
										.execute();

								remindInterviewCounter++;
								shopCandidateRelation.setRemindInterviewCounter(remindInterviewCounter);
								shopCandidateRelation.setRemindInterviewDate((new Date()));
								shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

								shop = shopCandidateRelation.getShop();

								saveChatLineMessage(shopCandidateRelation.getCandidate(), "Tomorrow is the interview!");
							}
						}
				}

				// set user to potential candidate if he did not reply to the reason message
				// after 2 days from asking
				int askForReasonCounter = shopCandidateRelation.getAskForReasonCounter();
				if (askForReasonCounter < 1) {
					if (shopCandidateRelation.getCandidate().getBotInformation().getAskForReasonDate() != null) {

						Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
						cal.setTime(shopCandidateRelation.getCandidate().getBotInformation().getAskForReasonDate());
						cal.add(Calendar.DAY_OF_WEEK, 2);
						cal.getTime();

						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
						sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
						String time = sdf.format(date);
						Date currentTime = sdf.parse(time);

						if (currentTime.after(cal.getTime())) {

							if (shopCandidateRelation.getShop().getNameShop().equals("admin shop")) {
								shopCandidateRelation.setProgress("Potential Candidate");
								shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

								String input = "{'text':'" + "userID: "
										+ shopCandidateRelation.getCandidate().getUserLineId() + " \n time: "
										+ (new Date()) + " \n Potential candidate: True'}";

								HttpHeaders headers = new HttpHeaders();
								headers.setContentType(MediaType.APPLICATION_JSON);

								HttpEntity<String> entity = new HttpEntity<String>(input, headers);

								ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
										String.class);
							}

							askForReasonCounter++;
							shopCandidateRelation.setAskForReasonCounter(askForReasonCounter);
							shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

						}
					}
				}
			}

		}

	}

	/**
	 * @author Rihab Kallel
	 * 
	 *         method to save every user and bot message to database
	 * @param candidate
	 * @param text
	 */
	private void saveChatLineMessage(Candidate candidate, String text) {

		ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
		chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
		chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
		chatMessageLineToAdd.setMessageText(text);
		chatMessageLineToAdd.setReadState(false);
		chatMessageLineToAdd.setMessageDate((new Date()));
		chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);

	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

}
