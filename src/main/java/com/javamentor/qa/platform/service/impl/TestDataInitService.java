package com.javamentor.qa.platform.service.impl;

import com.javamentor.qa.platform.models.entity.Badge;
import com.javamentor.qa.platform.models.entity.BookMarks;
import com.javamentor.qa.platform.models.entity.Comment;
import com.javamentor.qa.platform.models.entity.CommentType;
import com.javamentor.qa.platform.models.entity.chat.*;
import com.javamentor.qa.platform.models.entity.question.*;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.models.entity.question.answer.CommentAnswer;
import com.javamentor.qa.platform.models.entity.user.*;
import com.javamentor.qa.platform.models.entity.user.reputation.Reputation;
import com.javamentor.qa.platform.models.entity.user.reputation.ReputationType;
import com.javamentor.qa.platform.service.abstracts.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestDataInitService {

    final UserService userService;
    final QuestionService questionService;
    final QuestionViewedService questionViewedService;
    final CommentService commentService;
    final ReputationService reputationService;
    final UserBadgesService userBadgesService;
    final TagService tagService;
    final UserFavoriteQuestionService userFavoriteQuestionService;
    final BadgeService badgeService;
    final RelatedTagService relatedTagService;
    final CommentQuestionService commentQuestionService;
    final CommentAnswerService commentAnswerService;
    final AnswerService answerService;
    final VoteAnswerService voteAnswerService;
    final VoteQuestionService voteQuestionService;
    final RoleService roleService;
    final IgnoredTagService ignoredTagService;
    final TrackedTagService trackedTagService;
    final ChatService chatService;
    final SingleChatService singleChatService;
    final GroupChatService groupChatService;
    final MessageService messageService;
    final BookMarksService bookMarksService;
    final Random random;

    int numberOfUsers = 50;
    List<Tag> tagList = new ArrayList<>();
    Role USER_ROLE = Role.builder().name("USER").build();
    Role ADMIN_ROLE = Role.builder().name("ADMIN").build();
    Role MODER_ROLE = Role.builder().name("MODER").build();
    List<User> users = new ArrayList<>();
    List<Chat> chats = new ArrayList<>();


    @Autowired
    public TestDataInitService(UserService userService, BadgeService badgeService, QuestionService questionService,
                               QuestionViewedService questionViewedService, CommentService commentService, ReputationService reputationService, UserBadgesService userBadgesService,
                               TagService tagService, UserFavoriteQuestionService userFavoriteQuestionService,
                               RelatedTagService relatedTagService, CommentQuestionService commentQuestionService,
                               CommentAnswerService commentAnswerService, AnswerService answerService,
                               VoteAnswerService voteAnswerService, VoteQuestionService voteQuestionService, RoleService roleService,
                               IgnoredTagService ignoredTagService, TrackedTagService trackedTagService, ChatService chatService, SingleChatService singleChatService, GroupChatService groupChatService, MessageService messageService, BookMarksService bookMarksService) {
        this.userService = userService;
        this.badgeService = badgeService;
        this.questionService = questionService;
        this.questionViewedService = questionViewedService;
        this.commentService = commentService;
        this.reputationService = reputationService;
        this.userBadgesService = userBadgesService;
        this.tagService = tagService;
        this.userFavoriteQuestionService = userFavoriteQuestionService;
        this.relatedTagService = relatedTagService;
        this.commentQuestionService = commentQuestionService;
        this.commentAnswerService = commentAnswerService;
        this.answerService = answerService;
        this.voteAnswerService = voteAnswerService;
        this.voteQuestionService = voteQuestionService;
        this.roleService = roleService;
        this.ignoredTagService = ignoredTagService;
        this.trackedTagService = trackedTagService;
        this.chatService = chatService;
        this.singleChatService = singleChatService;
        this.groupChatService = groupChatService;
        this.messageService = messageService;
        this.bookMarksService = bookMarksService;
        random = new Random();
    }


    public void createTagEntity() {
        for (int i = 0; i < numberOfUsers; i++) {
            Tag childTag = Tag.builder().name("Child" + i).description("DescriptionChildTag").build();
            Tag tag = new Tag();
            tag.setName("Tag Name" + i);
            tag.setDescription("Tag Description " + i);
            tagService.persist(tag);
            tagService.persist(childTag);

            RelatedTag relatedTag = new RelatedTag();
            relatedTag.setChildTag(childTag);
            relatedTag.setMainTag(tag);
            relatedTagService.persist(relatedTag);

            tagList.add(tag);
        }
    }

    @Transactional
    public void createEntity() {
        createTagEntity();

        roleService.persist(USER_ROLE);
        roleService.persist(ADMIN_ROLE);
        roleService.persist(MODER_ROLE);

        for (int i = 0; i < numberOfUsers; i++) {
            User user = new User();
            user.setEmail("ivanov@mail.com" + i);
            user.setPassword("password" + i);
            user.setFullName("Ivanov Ivan" + i);
            user.setIsEnabled(true);
            //user.setReputationCount(0);
            user.setCity("Moscow" + i);
            user.setLinkSite("http://google.com" + i);
            user.setLinkGitHub("http://github.com");
            user.setLinkVk("http://vk.com");
            user.setAbout("very good man");
            user.setImageLink("https://pbs.twimg.com/profile_images/1182694005408186375/i5xT6juJ_400x400.jpg");
            //user.setReputationCount(1);
            if (i == 0) user.setRole(ADMIN_ROLE);
            else if (i == 1) user.setRole(MODER_ROLE);
            else user.setRole(USER_ROLE);
            userService.persist(user);

            Question question = new Question();
            List<Tag> randomQuestionTagList = new ArrayList<>();

            int questionTagsNumber = random.nextInt(5) + 1;
            int j = 0;
            while (j != questionTagsNumber) {
                j++;
                int tagIndex = random.nextInt(49) + 1;
                randomQuestionTagList.add(tagList.get(tagIndex));
            }

            Integer viewCountQuestion = random.nextInt(1000) + 1;
            question.setTitle("Question Title" + i);
            question.setDescription("Question Description" + i);
            question.setUser(user);
            question.setTags(randomQuestionTagList.stream().limit(5).collect(Collectors.toList()));
            question.setIsDeleted(false);
            questionService.persist(question);

            Question questionNoAnswer = new Question();
            List<Tag> randomQuestionNoAnsTagList = new ArrayList<>();

            int questionNoAnsTagsNumber = random.nextInt(5) + 1;
            int k = 0;
            while (k != questionNoAnsTagsNumber) {
                k++;
                int tagIndex = random.nextInt(49) + 1;
                randomQuestionNoAnsTagList.add(tagList.get(tagIndex));
            }

            Integer viewCountQuestionNoAnswer = random.nextInt(1000) + 1;
            questionNoAnswer.setTitle("Question NoAnswer " + i);
            questionNoAnswer.setDescription("Question NoAnswer Description" + i);
            questionNoAnswer.setUser(user);
            questionNoAnswer.setTags(randomQuestionNoAnsTagList.stream().limit(5).collect(Collectors.toList()));
            questionNoAnswer.setIsDeleted(false);
            questionService.persist(questionNoAnswer);

            UserFavoriteQuestion userFavoriteQuestion = new UserFavoriteQuestion();
            userFavoriteQuestion.setUser(user);
            userFavoriteQuestion.setQuestion(question);
            userFavoriteQuestionService.persist(userFavoriteQuestion);

            VoteQuestion voteQuestion = new VoteQuestion();
            voteQuestion.setUser(user);
            voteQuestion.setQuestion(question);
            voteQuestion.setVote(1);
            voteQuestionService.persist(voteQuestion);

            QuestionViewed questionViewed = new QuestionViewed();
            questionViewed.setQuestion(question);
            questionViewed.setUser(user);
            questionViewedService.persist(questionViewed);

            Answer answer = new Answer();
            answer.setUser(user);
            answer.setQuestion(question);
            answer.setHtmlBody("Answer" + i + ":  Hello! There you can find an answer on your question: www.google.com.");
            answer.setIsHelpful(false);
            answer.setIsDeleted(false);
            if (i < 25) {
                answer.setIsDeletedByModerator(random.nextBoolean());
            } else {
                answer.setIsDeletedByModerator(false);
            }
            answerService.persist(answer);

            Reputation reputation = new Reputation();
            reputation.setAuthor(user);
            reputation.setCount(random.nextInt(100) + 1);
            if (i % 4 == 0) {
                reputation.setType(ReputationType.Question);
                reputation.setQuestion(question);
            } else if (i % 4 == 1) {
                reputation.setType(ReputationType.Answer);
                reputation.setAnswer(answer);
            } else if (i % 4 == 2) {
                reputation.setType(ReputationType.VoteAnswer);
                reputation.setAnswer(answer);
            } else {
                reputation.setType(ReputationType.VoteQuestion);
                reputation.setQuestion(question);
            }
            reputationService.persist(reputation);

            CommentQuestion commentQuestion = new CommentQuestion();
            commentQuestion.setQuestion(question);
            commentQuestion.setComment(Comment.builder().text("Comment Text" + i)
                    .user(user).commentType(CommentType.QUESTION).build());
            commentQuestionService.persist(commentQuestion);

            CommentAnswer commentAnswer = new CommentAnswer();
            commentAnswer.setAnswer(answer);
            commentAnswer.setComment(Comment.builder().text("Comment Text" + i)
                    .user(user).commentType(CommentType.ANSWER).build());
            commentAnswerService.persist(commentAnswer);

            Badge badge = new Badge();
            badge.setBadgeName("Super Badge" + i);
            badge.setReputationForMerit(1);
            badge.setDescription("Badge Description" + i);
            badgeService.persist(badge);

            UserBadges userBadges = new UserBadges();
            userBadges.setReady(true);
            userBadges.setUser(user);
            userBadges.setBadge(badge);
            userBadgesService.persist(userBadges);


            IgnoredTag ignoredTag = new IgnoredTag();
            int randomIgnoredTagNum = random.nextInt(7);
            ignoredTag.setUser(user);
            if (randomIgnoredTagNum > 0) {
                ignoredTag.setIgnoredTag(tagList.get(randomIgnoredTagNum));
                ignoredTagService.persist(ignoredTag);
            }

            TrackedTag trackedTag = new TrackedTag();
            int randomTrackedTagNum = (int) (Math.random() * 7); //TODO: double check randoms
            trackedTag.setUser(user);
            if (randomTrackedTagNum > 0) {
                trackedTag.setTrackedTag(tagList.get(randomTrackedTagNum));
                trackedTagService.persist(trackedTag);
            }

            BookMarks bookMarks = new BookMarks();
            bookMarks.setUser(user);
            bookMarks.setQuestion(question);
            bookMarksService.persist(bookMarks);

            users.add(user);
        }


        int randomChatSingle = random.nextInt(19) + 1;

        for (int i = 0; i < randomChatSingle; i++) {
            int randomUserOne = random.nextInt(49) + 1;
            int randomUserTwo = random.nextInt(49) + 1;

            SingleChat singleChat = new SingleChat();
            singleChat.setChat(Chat.builder()
                    .title("Single chat title" + i)
                    .chatType(ChatType.SINGLE)
                    .build());

            Message message1 = new Message("Text single message" + i, users.get(randomUserOne), singleChat.getChat());
            Message message2 = new Message("Text single message" + i, users.get(randomUserTwo), singleChat.getChat());

            singleChat.setUserOne(users.get(randomUserOne));
            singleChat.setUseTwo(users.get(randomUserTwo));
            singleChatService.persist(singleChat);
            messageService.persist(message1);
            messageService.persist(message2);

        }

        int randomChatGroup = random.nextInt(19) + 1;

        for (int i = 0; i < randomChatGroup; i++) {
            int randomUsers = random.nextInt(49) + 1;


            GroupChat groupChat = new GroupChat();
            groupChat.setChat(Chat.builder()
                    .title("Group Chat title" + i)
                    .chatType(ChatType.GROUP)
                    .build());

            Message message1 = new Message("Text group message" + i, users.get(randomUsers), groupChat.getChat());

            groupChat.setUsers(users.stream().limit(randomUsers).collect(Collectors.toSet()));
            groupChatService.persist(groupChat);
            messageService.persist(message1);
        }

    }





}
