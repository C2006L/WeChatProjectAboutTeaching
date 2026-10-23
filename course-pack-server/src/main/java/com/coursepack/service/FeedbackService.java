package com.coursepack.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.common.BusinessException;
import com.coursepack.common.ErrorCode;
import com.coursepack.dto.feedback.TicketCreateRequest;
import com.coursepack.dto.feedback.TicketVO;
import com.coursepack.entity.FeedbackTicket;
import com.coursepack.entity.User;
import com.coursepack.mapper.FeedbackTicketMapper;
import com.coursepack.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 问答工单服务（批次J）：教师提问 → 管理员回复（时间线联动）。
 */
@Service
public class FeedbackService {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FeedbackTicketMapper ticketMapper;
    private final UserMapper userMapper;
    private final TimelineService timelineService;

    public FeedbackService(FeedbackTicketMapper ticketMapper, UserMapper userMapper,
                           TimelineService timelineService) {
        this.ticketMapper = ticketMapper;
        this.userMapper = userMapper;
        this.timelineService = timelineService;
    }

    /** 我的提问列表（最新在前） */
    public List<TicketVO> mine(Long userId) {
        List<FeedbackTicket> list = ticketMapper.selectList(Wrappers.<FeedbackTicket>lambdaQuery()
                .eq(FeedbackTicket::getUserId, userId)
                .orderByDesc(FeedbackTicket::getId)
                .last("LIMIT 100"));
        Map<Long, String> names = namesOf(list);
        return list.stream().map(t -> toVO(t, names)).collect(Collectors.toList());
    }

    /** 教师提交提问 */
    @Transactional
    public TicketVO create(Long userId, TicketCreateRequest req) {
        FeedbackTicket t = new FeedbackTicket();
        t.setUserId(userId);
        t.setTitle(req.getTitle());
        t.setContent(req.getContent());
        t.setCategory(req.getCategory() == null || req.getCategory().isBlank() ? "其他" : req.getCategory());
        t.setStatus("PENDING");
        t.setCreatedAt(LocalDateTime.now());
        ticketMapper.insert(t);
        User u = userMapper.selectById(userId);
        Map<Long, String> names = Map.of(userId, u == null || u.getName() == null ? "教师" : u.getName());
        return toVO(t, names);
    }

    /** 管理端全量列表（最新在前） */
    public List<TicketVO> adminList() {
        List<FeedbackTicket> list = ticketMapper.selectList(Wrappers.<FeedbackTicket>lambdaQuery()
                .orderByDesc(FeedbackTicket::getId)
                .last("LIMIT 200"));
        Map<Long, String> names = namesOf(list);
        return list.stream().map(t -> toVO(t, names)).collect(Collectors.toList());
    }

    /** 管理员回复：写入回复并写时间线 */
    @Transactional
    public TicketVO reply(Long ticketId, String content, Long operatorId) {
        FeedbackTicket t = ticketMapper.selectById(ticketId);
        if (t == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        t.setStatus("REPLIED");
        t.setReplyContent(content);
        t.setReplyAt(LocalDateTime.now());
        ticketMapper.updateById(t);

        String operatorName = "项目管理员";
        User op = operatorId == null ? null : userMapper.selectById(operatorId);
        if (op != null && op.getName() != null) {
            operatorName = op.getName();
        }
        timelineService.log("FEEDBACK_REPLIED",
                "已回复教师「" + displayName(t.getUserId()) + "」的问答工单：" + t.getTitle(), operatorId);
        return toVO(t, namesOf(List.of(t)), operatorName);
    }

    private Map<Long, String> namesOf(List<FeedbackTicket> tickets) {
        List<Long> ids = tickets.stream().map(FeedbackTicket::getUserId).distinct()
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectList(Wrappers.<User>lambdaQuery().in(User::getId, ids)).stream()
                .collect(Collectors.toMap(User::getId, u -> u.getName() == null ? "教师" : u.getName()));
    }

    private String displayName(Long userId) {
        User u = userMapper.selectById(userId);
        return u != null && u.getName() != null ? u.getName() : "教师";
    }

    private TicketVO toVO(FeedbackTicket t, Map<Long, String> names) {
        return toVO(t, names, null);
    }

    private TicketVO toVO(FeedbackTicket t, Map<Long, String> names, String replyAdmin) {
        TicketVO vo = new TicketVO();
        vo.setId(t.getId());
        vo.setTeacher(names.getOrDefault(t.getUserId(), "教师"));
        vo.setTeam("未分配"); // 批次J4：队伍域建立后按 users 关联补齐
        vo.setTime(t.getCreatedAt() == null ? "" : TIME.format(t.getCreatedAt()));
        vo.setStatus(t.getStatus());
        vo.setTitle(t.getTitle());
        vo.setContent(t.getContent());
        vo.setCategory(t.getCategory());
        if (t.getReplyAt() != null) {
            TicketVO.Reply reply = new TicketVO.Reply();
            reply.setContent(t.getReplyContent());
            reply.setAdmin(replyAdmin == null ? "项目管理员" : replyAdmin);
            reply.setTime(TIME.format(t.getReplyAt()));
            vo.setReply(reply);
        }
        return vo;
    }
}
