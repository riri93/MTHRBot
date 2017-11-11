package com.example.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.entity.ChatMessageLine;
import com.example.entity.JobCandidateRelation;
import com.example.entity.Shop;
import com.example.entity.ShopCandidateRelation;
import com.example.repository.ChatMessageLineRepository;
import com.example.repository.JobCandidateRelationRepository;
import com.example.repository.ShopCandidateRelationRepository;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;

import retrofit2.Response;

@Configuration
@EnableScheduling
public class BotScheduler {

	private Shop shop = new Shop();
	private String channelToken = "wvydTwaiKtsG4Z90XPfG6hWB31/TX2tceTz+v1NqSXgOMgUZ55c4GnZZ6rd+i9lJn8d0k17/7A5E0Mq1kKpmAdMKWkmqGaiezxDAZykxJIA8MoDYx+a19t4cQbRd5zLWl3k30y2pSM1zzZQz/JVSjwdB04t89/1O/w1cDnyilFU=";

	@Autowired
	JobCandidateRelationRepository jobCandidateRelationRepository;

	@Autowired
	ShopCandidateRelationRepository shopCandidateRelationRepository;

	@Autowired
	ChatMessageLineRepository chatMessageLineRepository;

	/**
	 * 
	 * @author Rihab Kallel
	 * 
	 *         send this message after three hours from apply
	 * 
	 *         scheduler cron checks the database every day
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "0 * * * * *")
	public void sendCallShopMessage() throws Exception {

		System.out.println("************CALL*******************");

		List<JobCandidateRelation> jobCandidateRelations = new ArrayList<>();
		jobCandidateRelations = jobCandidateRelationRepository.getAllAppliedCandidates();

		if (jobCandidateRelations != null) {

			for (JobCandidateRelation jobCandidateRelation : jobCandidateRelations) {
				if (jobCandidateRelation.getAppliedDate() != null) {

					if (jobCandidateRelation.getCallShopMessageDate() == null) {

						int callShopMessageCounter = jobCandidateRelation.getCallShopMessageCounter();

						if (callShopMessageCounter < 3) {
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
								Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken)
										.build().pushMessage(pushMessage).execute();

								shop = jobCandidateRelation.getJob().getShop();

								callShopMessageCounter++;
								jobCandidateRelation.setCallShopMessageCounter(callShopMessageCounter);
								jobCandidateRelation.setCallShopMessageDate((new Date()));
								jobCandidateRelationRepository.saveAndFlush(jobCandidateRelation);

								ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
								chatMessageLineToAdd
										.setChatLineAdmin(jobCandidateRelation.getCandidate().getChatLineAdmin());
								chatMessageLineToAdd
										.setMessageDirection(jobCandidateRelation.getCandidate().getIdUser());
								chatMessageLineToAdd.setMessageText("Have you got in contact with the shop?");
								chatMessageLineToAdd.setReadState(false);
								chatMessageLineToAdd.setMessageDate((new Date()));
								chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
							}
						} else {
							
							Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
							cal.setTime(jobCandidateRelation.getCallShopMessageDate());
							cal.add(Calendar.DAY_OF_WEEK, 2);
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
								Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken)
										.build().pushMessage(pushMessage).execute();

								shop = jobCandidateRelation.getJob().getShop();

								callShopMessageCounter++;
								jobCandidateRelation.setCallShopMessageCounter(callShopMessageCounter);
								jobCandidateRelation.setCallShopMessageDate((new Date()));
								jobCandidateRelationRepository.saveAndFlush(jobCandidateRelation);

								ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
								chatMessageLineToAdd
										.setChatLineAdmin(jobCandidateRelation.getCandidate().getChatLineAdmin());
								chatMessageLineToAdd
										.setMessageDirection(jobCandidateRelation.getCandidate().getIdUser());
								chatMessageLineToAdd.setMessageText("Have you got in contact with the shop?");
								chatMessageLineToAdd.setReadState(false);
								chatMessageLineToAdd.setMessageDate((new Date()));
								chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);

							}
						}
					}
				}
			}
		}
	}

	/**
	 * @author Rihab Kallel
	 * 
	 *         send this message after 3 hours if user answered no to "have got in
	 *         contact with the shop?"
	 * 
	 *         scheduler cron checks the database every hour
	 * @throws Exception
	 * 
	 */
	@Scheduled(cron = "0 * * * * *")
	public void sendCallShopMessage3Hours() throws Exception {

		List<JobCandidateRelation> jobCandidateRelations = new ArrayList<>();
		jobCandidateRelations = jobCandidateRelationRepository.getAllAppliedCandidates();

		if (jobCandidateRelations != null) {

			for (JobCandidateRelation jobCandidateRelation : jobCandidateRelations) {
				if (jobCandidateRelation.getAppliedDate() != null) {

					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					cal.setTime(jobCandidateRelation.getCallShopMessageDate());
					cal.add(Calendar.HOUR_OF_DAY, 3);
					cal.getTime();

					int callShopMessageCounter = jobCandidateRelation.getCallShopMessageCounter();

					if (callShopMessageCounter < 3) {
						if (jobCandidateRelation.getCallShopMessageDate() != null) {
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
								Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken)
										.build().pushMessage(pushMessage).execute();

								shop = jobCandidateRelation.getJob().getShop();

								callShopMessageCounter++;
								jobCandidateRelation.setCallShopMessageCounter(callShopMessageCounter);
								jobCandidateRelation.setCallShopMessageDate((new Date()));
								jobCandidateRelationRepository.saveAndFlush(jobCandidateRelation);

								ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
								chatMessageLineToAdd
										.setChatLineAdmin(jobCandidateRelation.getCandidate().getChatLineAdmin());
								chatMessageLineToAdd
										.setMessageDirection(jobCandidateRelation.getCandidate().getIdUser());
								chatMessageLineToAdd.setMessageText("Have you got in contact with the shop?");
								chatMessageLineToAdd.setReadState(false);
								chatMessageLineToAdd.setMessageDate((new Date()));
								chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @author Rihab Kallel
	 * 
	 *         send this message after two days from interview
	 * 
	 *         scheduler cron checks the database every day
	 * 
	 * @throws Exception
	 * 
	 */
	@Scheduled(cron = "0 * * * * *")
	public void sendHaveYouPassedMessage() throws Exception {

		System.out.println("************PASSED*******************");

		List<ShopCandidateRelation> shopCandidateRelations = new ArrayList<>();
		shopCandidateRelations = shopCandidateRelationRepository.findAll();

		if (shopCandidateRelations != null) {

			for (ShopCandidateRelation shopCandidateRelation : shopCandidateRelations) {
				if (shopCandidateRelation.getInterviewDate() != null) {

					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					cal.setTime(shopCandidateRelation.getInterviewDate());
					cal.add(Calendar.DAY_OF_WEEK, 2);
					cal.getTime();

					if (shopCandidateRelation.getPassedInterviewMessageDate() == null) {

						Date currentTime = retrieveCurrentTimeStamp();

						if (currentTime.equals(cal.getTime())) {

							ConfirmTemplate confirmTemplate = new ConfirmTemplate("Have you passed the interview?",
									new MessageAction("Yes", "Yes I passed"), new MessageAction("No", "No I failed"));
							TemplateMessage templateMessage = new TemplateMessage("Have you passed the interview?",
									confirmTemplate);
							PushMessage pushMessage = new PushMessage(
									shopCandidateRelation.getCandidate().getUserLineId().toString(), templateMessage);
							Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
									.pushMessage(pushMessage).execute();

							shopCandidateRelation.setPassedInterviewMessageDate((new Date()));
							shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

							ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
							chatMessageLineToAdd
									.setChatLineAdmin(shopCandidateRelation.getCandidate().getChatLineAdmin());
							chatMessageLineToAdd.setMessageDirection(shopCandidateRelation.getCandidate().getIdUser());
							chatMessageLineToAdd.setMessageText("Have you passed the interview?");
							chatMessageLineToAdd.setReadState(false);
							chatMessageLineToAdd.setMessageDate((new Date()));
							chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
						}
					}
				}
			}
		}
	}

	/**
	 * @author Rihab Kallel
	 * 
	 *         send this message if interview time is not yet confirmed after two
	 *         days
	 * 
	 *         scheduler cron checks the database every day
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "0 * * * * *")
	public void sendInterviewTimeMessage() throws Exception {

		System.out.println("************INTERVIEW*******************");

		List<ShopCandidateRelation> shopCandidateRelations = new ArrayList<>();
		shopCandidateRelations = shopCandidateRelationRepository.findAll();

		if (shopCandidateRelations != null) {

			for (ShopCandidateRelation shopCandidateRelation : shopCandidateRelations) {

				if ((shopCandidateRelation.getAskInterviewDate() != null
						&& !shopCandidateRelation.isConfirmedInterview())
						|| (shopCandidateRelation.getAskInterviewDate() != null
								&& shopCandidateRelation.isConfirmedInterview()
								&& shopCandidateRelation.getInterviewDate() == null)) {

					int askInterviewCounter = shopCandidateRelation.getAskInterviewCounter();

					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					cal.setTime(shopCandidateRelation.getAskInterviewDate());
					cal.add(Calendar.DAY_OF_WEEK, 2);
					cal.getTime();

					Date currentTime = retrieveCurrentTimeStamp();

					if (askInterviewCounter < 2) {
						if (currentTime.equals(cal.getTime())) {

							ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
									"https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png",
									"Reason", "Did you confirm the interview time?",
									Arrays.asList(new MessageAction("Confirmed", "Interview confirmed"),
											new MessageAction("Not confirmed", "Interview not confirmed"),
											new MessageAction("No interview", "No interview")));
							TemplateMessage templateMessage = new TemplateMessage("Reason", buttonsTemplate);

							PushMessage pushMessage = new PushMessage(
									shopCandidateRelation.getCandidate().getUserLineId().toString(), templateMessage);
							Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
									.pushMessage(pushMessage).execute();

							askInterviewCounter++;
							shopCandidateRelation.setAskInterviewCounter(askInterviewCounter);
							shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

							ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
							chatMessageLineToAdd
									.setChatLineAdmin(shopCandidateRelation.getCandidate().getChatLineAdmin());
							chatMessageLineToAdd.setMessageDirection(shopCandidateRelation.getCandidate().getIdUser());
							chatMessageLineToAdd.setMessageText("Did you confirm the interview time?");
							chatMessageLineToAdd.setReadState(false);
							chatMessageLineToAdd.setMessageDate((new Date()));
							chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
						}
					}
				}
			}
		}
	}

	/**
	 * @author Rihab Kallel
	 * 
	 *         send interview reminder one day before the interview
	 * 
	 *         scheduler cron checks the database every day
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void sendInterviewRemider() throws Exception {
		System.out.println("************REMINDER*******************");

		List<ShopCandidateRelation> shopCandidateRelations = new ArrayList<>();
		shopCandidateRelations = shopCandidateRelationRepository.findAll();

		if (shopCandidateRelations != null) {

			for (ShopCandidateRelation shopCandidateRelation : shopCandidateRelations) {
				if (shopCandidateRelation.getInterviewDate() != null) {

					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					cal.setTime(shopCandidateRelation.getInterviewDate());
					cal.add(Calendar.DAY_OF_WEEK, -1);
					cal.getTime();

					if (shopCandidateRelation.getRemindInterviewDate() == null) {

						Date currentTime = retrieveCurrentTimeStamp();

						if (currentTime.equals(cal.getTime())) {

							TextMessage textMessage = new TextMessage("Tomorrow is the interview!");
							PushMessage pushMessage = new PushMessage(
									shopCandidateRelation.getCandidate().getUserLineId().toString(), textMessage);
							Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
									.pushMessage(pushMessage).execute();
							shopCandidateRelation.setRemindInterviewDate((new Date()));
							shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

							ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
							chatMessageLineToAdd
									.setChatLineAdmin(shopCandidateRelation.getCandidate().getChatLineAdmin());
							chatMessageLineToAdd.setMessageDirection(shopCandidateRelation.getCandidate().getIdUser());
							chatMessageLineToAdd.setMessageText("Tomorrow is the interview!");
							chatMessageLineToAdd.setReadState(false);
							chatMessageLineToAdd.setMessageDate((new Date()));
							chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);

						}
					}
				}
			}
		}
	}

	/**
	 * @author Rihab Kallel
	 * 
	 *         method to get current date
	 * 
	 */
	public Date retrieveCurrentTimeStamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date currentDateAux = new Date();
		String formattedDate = formatter.format(currentDateAux);
		try {
			Date currentDate = formatter.parse(formattedDate);
			return currentDate;

		} catch (Exception e) {
			return null;
		}
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

}
