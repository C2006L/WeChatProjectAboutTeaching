import { courseList } from './course';
import type { PrepareResult } from '@/types/mock';

const prepareOptionMap = {
  grades: ['1-3年级', '4-6年级'] as const,
  themes: ['身体与安全', '家庭与合作', '人际与尊重', '成长与变化'] as const,
  durations: [20, 30, 40, 60] as const,
};

export { prepareOptionMap };

export function generatePrepareList(
  grade: string,
  theme: string,
  duration: number,
): PrepareResult {
  const matched = courseList
    .filter((c) => c.grade.includes(grade) && c.themeLabel === theme)
    .sort((a, b) => a.duration - b.duration)
    .filter((c) => c.duration <= duration);

  const recommended = matched.length > 0 ? matched.slice(0, 2) : [courseList[0]];

  const teachingFlow =
    duration <= 30
      ? ['热身活动（5分钟）', '主题导入（5分钟）', '主体活动（15分钟）', '总结延伸（5分钟）']
      : ['热身活动（8分钟）', '主题导入（7分钟）', '主体活动（15分钟）', '分享讨论（8分钟）', '总结延伸（' + (duration - 38) + '分钟）'];

  const attentionNotes: string[] = [];
  if (theme === '身体与安全') {
    attentionNotes.push('涉及身体隐私内容时，提醒学生尊重彼此边界，避免让个别孩子感到不适。');
  }
  if (theme === '人际与尊重') {
    attentionNotes.push('讨论中若出现同学间真实矛盾，需课后单独沟通，不公开评价。');
  }
  if (theme === '成长与变化') {
    attentionNotes.push('发育相关问题请用科学、平和语气回应，避免比较或嘲笑。');
  }
  if (theme === '家庭与合作') {
    attentionNotes.push('尊重不同家庭结构，避免让孩子比较或评判彼此家庭。');
  }
  if (attentionNotes.length === 0) {
    attentionNotes.push('根据班级实际情况灵活调整节奏，关注学生情绪反应。');
  }

  return {
    grade,
    theme,
    duration,
    recommendedCourses: recommended.map((c) => c.id),
    teachingFlow,
    materialChecklist: ['教案 1 份', '课件 1 份', '教具清单 1 份', '课程视频 1 段'],
    attentionNotes,
  };
}

