package com.example.gamemate.global.eventListener;

import com.example.gamemate.domain.comment.entity.Comment;
import com.example.gamemate.domain.follow.entity.Follow;
import com.example.gamemate.domain.like.entity.BoardLike;
import com.example.gamemate.domain.like.entity.ReviewLike;
import com.example.gamemate.domain.match.entity.Match;
import com.example.gamemate.domain.notification.entity.Notification;
import com.example.gamemate.domain.notification.enums.NotificationType;
import com.example.gamemate.domain.notification.service.NotificationService;
import com.example.gamemate.domain.reply.entity.Reply;
import com.example.gamemate.global.eventListener.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleCreateFollow(FollowCreatedEvent event) {
        log.info("새로운 팔로우 알림 전송 시작");
        Follow follow = event.getFollow();

        Notification notification = notificationService.createNotification(follow.getFollowee(), NotificationType.NEW_FOLLOWER, "/users/" + follow.getFollower().getId());
        notificationService.sendNotification(notification);
    }

    @Async
    @EventListener
    public void handleCreateMatch(MatchCreatedEvent event) {
        log.info("새로운 매칭 알림 전송 시작");
        Match match = event.getMatch();

        Notification notification = notificationService.createNotification(match.getReceiver(), NotificationType.NEW_MATCH, "/matches/" + match.getId());
        notificationService.sendNotification(notification);
    }

    @Async
    @EventListener
    public void handleAcceptMatch(MatchAcceptedEvent event) {
        log.info("매칭 수락 알림 전송 시작");
        Match match = event.getMatch();

        Notification notification = notificationService.createNotification(match.getSender(), NotificationType.MATCH_ACCEPTED, "/matches/" + match.getId());
        notificationService.sendNotification(notification);
    }

    @Async
    @EventListener
    public void handleRejectMatch(MatchRejectedEvent event) {
        log.info("매칭 거절 알림 전송 시작");
        Match match = event.getMatch();

        Notification notification = notificationService.createNotification(match.getSender(), NotificationType.MATCH_REJECTED, "/matches/" + match.getId());
        notificationService.sendNotification(notification);
    }

    @Async
    @EventListener
    public void handleCreateBoardLike(BoardLikeCreatedEvent event) {
        log.info("게시글 새로운 좋아요 알림 전송 시작");
        BoardLike boardLike = event.getBoardLike();

        Notification notification = notificationService.createNotification(boardLike.getBoard().getUser(), NotificationType.NEW_LIKE, "/boards/" + boardLike.getBoard().getId());
        notificationService.sendNotification(notification);
    }

    @Async
    @EventListener
    public void handleCreateReviewLike(ReviewLikeCreatedEvent event) {
        log.info("리뷰 새로운 좋아요 알림 전송 시작");
        ReviewLike reviewLike = event.getReviewLike();

        Notification notification = notificationService.createNotification(reviewLike.getReview().getUser(), NotificationType.NEW_LIKE, "/reviews/" + reviewLike.getReview().getId());
        notificationService.sendNotification(notification);
    }

    @Async
    @EventListener
    public void handleCreateComment(CommentCreatedEvent event) {
        log.info("새로운 댓글 알림 전송 시작");
        Comment comment = event.getComment();

        if (!Objects.equals(comment.getUser().getId(), comment.getBoard().getUser().getId())) {
            Notification notification = notificationService.createNotification(comment.getBoard().getUser(), NotificationType.NEW_COMMENT, "/comments/" + comment.getId());
            notificationService.sendNotification(notification);
        }
    }

    @Async
    @EventListener
    public void handleCreateReply(ReplyCreatedEvent event) {
        log.info("새로운 대댓글 알림 전송 시작");
        Reply reply = event.getReply();

        if (!Objects.equals(reply.getUser().getId(), reply.getComment().getBoard().getUser().getId())) {
            Notification boardNotification = notificationService.createNotification(reply.getComment().getBoard().getUser(), NotificationType.NEW_COMMENT, "/replies/" + reply.getId());
            notificationService.sendNotification(boardNotification);
        }

        if (!Objects.equals(reply.getUser().getId(), reply.getComment().getUser().getId())) {
            Notification commentNotification = notificationService.createNotification(reply.getComment().getUser(), NotificationType.NEW_COMMENT, "/replies/" + reply.getId());
            notificationService.sendNotification(commentNotification);
        }

        if (reply.getParentReply() != null && !Objects.equals(reply.getParentReply().getUser().getId(), reply.getUser().getId())) {
            Notification parentReplyNotification = notificationService.createNotification(reply.getParentReply().getUser(), NotificationType.NEW_COMMENT, "/replies/" + reply.getId());
            notificationService.sendNotification(parentReplyNotification);
        }
    }
}
