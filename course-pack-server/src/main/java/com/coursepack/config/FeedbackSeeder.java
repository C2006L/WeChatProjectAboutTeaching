package com.coursepack.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.entity.FeedbackTicket;
import com.coursepack.entity.User;
import com.coursepack.mapper.FeedbackTicketMapper;
import com.coursepack.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 问答工单种子（批次J，幂等）：6 条与 Web 端 mock/seed.ts 同构
 * （陈雪梅/周雨桐/刘志强 3 条已回复带管理员话术；其余 PENDING）。
 */
@Slf4j
@Component
@Order(130)
public class FeedbackSeeder implements CommandLineRunner {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FeedbackTicketMapper ticketMapper;
    private final UserMapper userMapper;

    public FeedbackSeeder(FeedbackTicketMapper ticketMapper, UserMapper userMapper) {
        this.ticketMapper = ticketMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) {
        Long any = ticketMapper.selectCount(Wrappers.emptyWrapper());
        if (any != null && any > 0) {
            return;
        }
        seed(1L, "课件下载后解压失败",
                "《我会表达拒绝》的课件包下载后解压提示文件损坏，换了两次网络都一样，麻烦帮忙看下资源包。",
                "其他", "2026-09-05 14:20", null, null);
        seed(2L, "建议补充低年级绘本资源",
                "我们服务的是小学 1-3 年级，现有绘本清单整体偏高年级，希望平台补充低龄段书单和配套导读。",
                "其他", "2026-09-05 09:05", null, null);
        seed(3L, "必修课学习进度未保存",
                "昨晚学完《家校沟通技巧》并完成打卡，今天培训页进度还是 64%，学习记录疑似没有上传成功。",
                "其他", "2026-09-04 18:42", null, null);
        seed(4L, "课程视频无法播放",
                "《生命教育核心理念》第 3 节视频一直转圈，安卓手机和电脑都试过，无法播放。",
                "其他", "2026-09-03 10:15",
                "已处理：该节视频源已完成更换，请退出后重新进入学习页，即可正常播放。给您带来不便深表歉意。",
                "2026-09-03 15:30");
        seed(2L, "如何申请成为种子教师？",
                "听说种子教师可以优先参与课程共创，想了解申请条件和认证流程。",
                "其他", "2026-09-02 16:08",
                "种子教师由管理员在教师管理页标记认证，通常要求：完成全部必修培训 + 至少上传 1 个通过审核的教学案例。您目前的进度已接近，保持活跃即可。",
                "2026-09-02 17:12");
        seed(3L, "希望开放案例交流区",
                "教学案例目前只能看不能评论，希望增加交流区，方便队伍之间互相取经。",
                "其他", "2026-09-01 11:26",
                "建议已收录进需求池，排期在后续版本评估。感谢您的反馈，种子教师共创群也会同步这个消息。",
                "2026-09-01 14:05");
        log.info("[seed] 创建问答工单种子 6 条");
    }

    private void seed(Long userId, String title, String content, String category,
                      String createdAt, String reply, String replyAt) {
        FeedbackTicket t = new FeedbackTicket();
        t.setUserId(userId);
        t.setTitle(title);
        t.setContent(content);
        t.setCategory(category);
        t.setCreatedAt(LocalDateTime.parse(createdAt, TIME));
        if (reply != null) {
            t.setStatus("REPLIED");
            t.setReplyContent(reply);
            t.setReplyAt(LocalDateTime.parse(replyAt, TIME));
        } else {
            t.setStatus("PENDING");
        }
        ticketMapper.insert(t);
    }
}
