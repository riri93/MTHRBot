package com.example.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.entity.JobCandidateRelation;
import com.example.entity.ShopCandidateRelation;
import com.example.repository.JobCandidateRelationRepository;
import com.example.repository.ShopCandidateRelationRepository;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;

import retrofit2.Response;

@Configuration
@EnableScheduling
public class BotScheduler {

	String channelToken = "wvydTwaiKtsG4Z90XPfG6hWB31/TX2tceTz+v1NqSXgOMgUZ55c4GnZZ6rd+i9lJn8d0k17/7A5E0Mq1kKpmAdMKWkmqGaiezxDAZykxJIA8MoDYx+a19t4cQbRd5zLWl3k30y2pSM1zzZQz/JVSjwdB04t89/1O/w1cDnyilFU=";

	@Autowired
	JobCandidateRelationRepository jobCandidateRelationRepository;

	@Autowired
	ShopCandidateRelationRepository shopCandidateRelationRepository;

	/**
	 * send this message after three hours from apply
	 * 
	 * scheduler cron checks the database every hour
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "0 0 * * * *")
	public void sendCallShopMessage() throws Exception {

		System.out.println("************CALL*******************");

		List<JobCandidateRelation> jobCandidateRelations = new ArrayList<>();
		jobCandidateRelations = jobCandidateRelationRepository.getAllAppliedCandidates();

		if (jobCandidateRelations != null) {

			for (JobCandidateRelation jobCandidateRelation : jobCandidateRelations) {
				if (jobCandidateRelation.getAppliedDate() != null) {

					Calendar cal = Calendar.getInstance();
					cal.setTime(jobCandidateRelation.getAppliedDate());
					cal.add(Calendar.HOUR_OF_DAY, 3);
					cal.getTime();

					System.out.println("cal.getTime() : " + cal.getTime());

					System.out.println("new Date() : " + new Date());

					if (jobCandidateRelation.getCallShopMessageDate() == null) {
						if ((new Date()).after(cal.getTime())) {

							ConfirmTemplate confirmTemplate = new ConfirmTemplate("Have you called the shop?",
									new MessageAction("Yes", "Yes I called"), new MessageAction("No", "No I did not"));
							TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
							PushMessage pushMessage = new PushMessage(
									jobCandidateRelation.getCandidate().getUserLineId().toString(), templateMessage);
							Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
									.pushMessage(pushMessage).execute();

							jobCandidateRelation.setCallShopMessageDate((new Date()));
							jobCandidateRelationRepository.saveAndFlush(jobCandidateRelation);
						}
					}
				}
			}
		}
	}

	/**
	 * send this message after two days from interview
	 * 
	 * scheduler cron checks the database every day
	 * 
	 * @throws Exception
	 * 
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void sendHaveYouPassedMessage() throws Exception {

		System.out.println("************PASSED*******************");

		List<ShopCandidateRelation> shopCandidateRelations = new ArrayList<>();
		shopCandidateRelations = shopCandidateRelationRepository.findAll();

		if (shopCandidateRelations != null) {

			for (ShopCandidateRelation shopCandidateRelation : shopCandidateRelations) {
				if (shopCandidateRelation.getInterviewDate() != null) {

					Calendar cal = Calendar.getInstance();
					cal.setTime(shopCandidateRelation.getInterviewDate());
					cal.add(Calendar.DAY_OF_WEEK, 2);
					cal.getTime();

					if (shopCandidateRelation.getPassedInterviewMessageDate() == null) {
						if ((new Date()).after(cal.getTime())) {

							ConfirmTemplate confirmTemplate = new ConfirmTemplate("Have you passed the interview?",
									new MessageAction("Yes", "Yes I passed"), new MessageAction("No", "" + ""));
							TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
							PushMessage pushMessage = new PushMessage(
									shopCandidateRelation.getCandidate().getUserLineId().toString(), templateMessage);
							Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
									.pushMessage(pushMessage).execute();

							shopCandidateRelation.setPassedInterviewMessageDate((new Date()));
							shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
						}
					}
				}
			}
		}
	}

	/**
	 * send this message if interview time is not yet confirmed after two days
	 * 
	 * scheduler cron checks the database every day
	 * 
	 * @throws Exception
	 */
	@Scheduled(cron = "0 0 0 * * *")
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

					Calendar cal = Calendar.getInstance();
					cal.setTime(shopCandidateRelation.getAskInterviewDate());
					cal.add(Calendar.DAY_OF_WEEK, 2);
					cal.getTime();

					if ((new Date()).equals(cal.getTime())) {

						ConfirmTemplate confirmTemplate = new ConfirmTemplate("Did you confirm the interview time?",
								new MessageAction("Confirmed", "Interview confirmed"),
								new MessageAction("Not confirmed", "Interview not confirmed"));
						TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
						PushMessage pushMessage = new PushMessage(
								shopCandidateRelation.getCandidate().getUserLineId().toString(), templateMessage);
						Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
								.pushMessage(pushMessage).execute();

					}
				}
			}
		}
	}

	/**
	 * send interview reminder one day before the interview
	 * 
	 * scheduler cron checks the database every day
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

					Calendar cal = Calendar.getInstance();
					cal.setTime(shopCandidateRelation.getInterviewDate());
					cal.add(Calendar.DAY_OF_WEEK, -1);
					cal.getTime();

					if ((new Date()).equals(cal.getTime())) {

						TextMessage textMessage = new TextMessage("Tomorrow is the interview!");
						PushMessage pushMessage = new PushMessage(
								shopCandidateRelation.getCandidate().getUserLineId().toString(), textMessage);
						Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
								.pushMessage(pushMessage).execute();

					}
				}
			}
		}
	}
}
