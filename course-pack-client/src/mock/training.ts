import type { Training } from '@/types/mock';

/**
 * 培训课程（8 节：必修 5 + 选修 3）
 * 完成状态与 mock/user.ts 的 trainingProgress { completed: 6, total: 8 } 对齐（75%）。
 */
export const trainingList: Training[] = [
  {
    id: 1,
    title: '破冰与价值共识',
    type: 'required',
    duration: 20,
    progress: 100,
    status: 'done',
    videoUrl: '',
    introduction: '建立培训共同价值，理解生命教育在乡村支教中的意义。',
    outline: ['培训开场与相互认识', '价值共识活动', '培训目标与安排说明'],
    materials: [
      { name: '破冰活动手册.pdf', type: 'pdf' },
      { name: '价值共识卡片.pptx', type: 'ppt' },
    ],
  },
  {
    id: 2,
    title: '核心理念与科学框架',
    type: 'required',
    duration: 30,
    progress: 100,
    status: 'done',
    videoUrl: '',
    introduction: '介绍联合国教科文组织《国际性教育技术指导纲要》的八大核心概念与适龄性原则。',
    outline: ['全面性教育八大核心概念', '适龄性原则', '课程边界与伦理要求'],
    materials: [
      { name: '核心理念讲义.pdf', type: 'pdf' },
      { name: '科学框架图解.png', type: 'img' },
    ],
  },
  {
    id: 3,
    title: '课程包使用方法',
    type: 'required',
    duration: 25,
    progress: 100,
    status: 'done',
    videoUrl: '',
    introduction: '掌握课程包结构、材料获取方式与备课流程。',
    outline: ['课程包结构解析', '材料下载与使用', '备课清单生成'],
    materials: [
      { name: '课程包使用指南.pdf', type: 'pdf' },
      { name: '备课流程示范.pptx', type: 'ppt' },
    ],
  },
  {
    id: 4,
    title: '授课技巧与课堂应对',
    type: 'required',
    duration: 35,
    progress: 100,
    status: 'done',
    videoUrl: '',
    introduction: '学习课堂组织、学生互动与突发问题应对技巧。',
    outline: ['课堂导入方法', '小组活动组织', '敏感问题应对原则'],
    materials: [
      { name: '授课技巧要点.pdf', type: 'pdf' },
    ],
  },
  {
    id: 5,
    title: '价值引导与伦理风险控制',
    type: 'required',
    duration: 30,
    progress: 60,
    status: 'current',
    videoUrl: '',
    introduction: '理解价值引导边界，掌握伦理风险识别与上报流程。',
    outline: ['价值中立与引导', '伦理风险识别', '上报与求助机制'],
    materials: [
      { name: '伦理风险清单.pdf', type: 'pdf' },
    ],
  },
  {
    id: 6,
    title: '积极的社会学视角赋能',
    type: 'elective',
    duration: 30,
    progress: 100,
    status: 'done',
    videoUrl: '',
    introduction: '从社会学视角理解乡村儿童处境与教师赋能路径。',
    outline: ['儿童权利视角', '乡村社会生态', '教师角色与边界'],
    materials: [
      { name: '社会学视角导读.pdf', type: 'pdf' },
    ],
  },
  {
    id: 7,
    title: '乡村儿童心理特点与沟通',
    type: 'elective',
    duration: 35,
    progress: 100,
    status: 'done',
    videoUrl: '',
    introduction: '了解乡村儿童心理发展特点，学习有效沟通策略。',
    outline: ['儿童心理发展特点', '创伤知情沟通', '建立信任关系'],
    materials: [
      { name: '沟通策略卡片.pdf', type: 'pdf' },
    ],
  },
  {
    id: 8,
    title: '支教场景课程实践复盘',
    type: 'elective',
    duration: 25,
    progress: 0,
    status: 'lock',
    videoUrl: '',
    introduction: '结合真实支教场景复盘课程实施，沉淀可复用的课堂经验。',
    outline: ['课堂实施案例', '问题与改进', '经验沉淀分享'],
    materials: [
      { name: '复盘模板.pdf', type: 'pdf' },
    ],
  },
];

export function getTrainingById(id: number): Training | undefined {
  return trainingList.find((t) => t.id === id);
}
