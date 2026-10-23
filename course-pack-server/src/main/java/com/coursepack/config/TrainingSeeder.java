package com.coursepack.config;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coursepack.entity.TeacherTraining;
import com.coursepack.entity.Training;
import com.coursepack.mapper.TeacherTrainingMapper;
import com.coursepack.mapper.TrainingMapper;
import com.coursepack.mapper.UserMapper;
import com.coursepack.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 培训种子（批次J，幂等）：8 门培训课程与小程序端 mock/training.ts 同构
 * （必修 5 + 选修 3）；teacher01 完成 1-7、teacher02/04 完成 1-2、
 * teacher03 完成 1+4，保证 admin 聚合与客户端进度演示两态齐备。
 */
@Slf4j
@Component
@Order(120)
public class TrainingSeeder implements CommandLineRunner {

    private final TrainingMapper trainingMapper;
    private final TeacherTrainingMapper recordMapper;
    private final UserMapper userMapper;

    public TrainingSeeder(TrainingMapper trainingMapper, TeacherTrainingMapper recordMapper,
                          UserMapper userMapper) {
        this.trainingMapper = trainingMapper;
        this.recordMapper = recordMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) {
        Long any = trainingMapper.selectCount(Wrappers.emptyWrapper());
        if (any != null && any > 0) {
            return;
        }

        seedTraining("破冰与价值共识", "required", 20,
                "建立培训共同价值，理解生命教育在乡村支教中的意义。",
                "['培训开场与相互认识','价值共识活动','培训目标与安排说明']",
                "[{'name':'破冰活动手册.pdf','type':'pdf'},{'name':'价值共识卡片.pptx','type':'ppt'}]");
        seedTraining("核心理念与科学框架", "required", 30,
                "介绍联合国教科文组织《国际性教育技术指导纲要》的八大核心概念与适龄性原则。",
                "['全面性教育八大核心概念','适龄性原则','课程边界与伦理要求']",
                "[{'name':'核心理念讲义.pdf','type':'pdf'},{'name':'科学框架图解.png','type':'img'}]");
        seedTraining("课程包使用方法", "required", 25,
                "掌握课程包结构、材料获取方式与备课流程。",
                "['课程包结构解析','材料下载与使用','备课清单生成']",
                "[{'name':'课程包使用指南.pdf','type':'pdf'},{'name':'备课流程示范.pptx','type':'ppt'}]");
        seedTraining("授课技巧与课堂应对", "required", 35,
                "学习课堂组织、学生互动与突发问题应对技巧。",
                "['课堂导入方法','小组活动组织','敏感问题应对原则']",
                "[{'name':'授课技巧要点.pdf','type':'pdf'}]");
        seedTraining("价值引导与伦理风险控制", "required", 30,
                "理解价值引导边界，掌握伦理风险识别与上报流程。",
                "['价值中立与引导','伦理风险识别','上报与求助机制']",
                "[{'name':'伦理风险清单.pdf','type':'pdf'}]");
        seedTraining("积极的社会学视角赋能", "elective", 30,
                "从社会学视角理解乡村儿童处境与教师赋能路径。",
                "['儿童权利视角','乡村社会生态','教师角色与边界']",
                "[{'name':'社会学视角导读.pdf','type':'pdf'}]");
        seedTraining("乡村儿童心理特点与沟通", "elective", 35,
                "了解乡村儿童心理发展特点，学习有效沟通策略。",
                "['儿童心理发展特点','创伤知情沟通','建立信任关系']",
                "[{'name':'沟通策略卡片.pdf','type':'pdf'}]");
        seedTraining("支教场景课程实践复盘", "elective", 25,
                "结合真实支教场景复盘课程实施，沉淀可复用的课堂经验。",
                "['课堂实施案例','问题与改进','经验沉淀分享']",
                "[{'name':'复盘模板.pdf','type':'pdf'}]");

        seedProgress("teacher01", 1L, 2L, 3L, 4L, 5L, 6L, 7L);
        seedProgress("teacher02", 1L, 2L);
        seedProgress("teacher03", 1L, 4L);
        seedProgress("teacher04", 1L, 2L);
        log.info("[seed] 创建培训种子 8 门 + 教师进度记录");
    }

    private void seedTraining(String title, String kind, int duration, String intro,
                              String outline, String materials) {
        Training t = new Training();
        t.setTitle(title);
        t.setKind(kind);
        t.setDuration(duration);
        t.setIntroduction(intro);
        t.setVideoUrl("");
        t.setOutlineJson(JSONUtil.toJsonStr(cn.hutool.json.JSONUtil.parseArray(outline)));
        t.setMaterialsJson(JSONUtil.toJsonStr(cn.hutool.json.JSONUtil.parseArray(materials)));
        trainingMapper.insert(t);
    }

    private void seedProgress(String username, Long... trainingIds) {
        User u = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (u == null) {
            return;
        }
        for (Long tid : trainingIds) {
            TeacherTraining r = new TeacherTraining();
            r.setUserId(u.getId());
            r.setTrainingId(tid);
            r.setProgress(100);
            recordMapper.insert(r);
        }
    }
}
