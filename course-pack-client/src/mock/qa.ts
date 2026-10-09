import type { QA } from '@/types/mock';

export const qaList: QA[] = [
  {
    id: 1,
    question: '老师，男生和女生有什么不一样？',
    category: '身体与安全',
    scene: '课间休息时，有学生突然向老师提问。',
    response: '男生和女生在身体上有些不同，这些都是正常的成长现象。重要的是我们都要尊重彼此的身体和隐私。',
    tips: [
      { type: 'do', content: '用科学、平和的语气回答' },
      { type: 'do', content: '强调“尊重隐私”' },
      { type: 'caution', content: '避免让孩子感到羞耻或好奇被批评' },
    ],
  },
  {
    id: 2,
    question: '老师，为什么有人有胸，有人没有？',
    category: '成长变化',
    scene: '体育课后换衣服时，学生观察到身体差异。',
    response: '这是身体发育的一部分，每个人发育的时间不同，都是正常的。',
    tips: [
      { type: 'do', content: '解释发育的个体差异' },
      { type: 'caution', content: '不要嘲笑或比较任何孩子' },
    ],
  },
  {
    id: 3,
    question: '老师，别人摸我这里，我不舒服，怎么办？',
    category: '身体与安全',
    scene: '学生感到身体被触碰后不舒服，主动向老师求助。',
    response: '如果有人让你不舒服，你可以大声说“不要这样”，然后马上离开，告诉老师或家长。',
    tips: [
      { type: 'do', content: '明确告诉孩子可以说“不”' },
      { type: 'do', content: '强调告诉信任的成人' },
      { type: 'do', content: '肯定孩子的感受' },
    ],
  },
  {
    id: 4,
    question: '老师，我可以不告诉父母吗？',
    category: '人际关系',
    scene: '孩子遇到困扰，希望老师保密。',
    response: '有些事情如果你感到害怕或担心，告诉父母或老师会帮助你。我们会一起保护你。',
    tips: [
      { type: 'do', content: '表达支持' },
      { type: 'caution', content: '不要承诺替孩子保密涉及安全的事情' },
    ],
  },
  {
    id: 5,
    question: '老师，谈恋爱是什么？',
    category: '成长变化',
    scene: '学生听到同学谈论“谈恋爱”，产生好奇。',
    response: '谈恋爱是成年人之间一种亲密的交往方式，你们现在的主要任务是学习和交朋友。',
    tips: [
      { type: 'do', content: '回答简洁' },
      { type: 'caution', content: '不要贬低或嘲笑孩子的好奇' },
    ],
  },
  {
    id: 6,
    question: '老师，为什么有的家庭只有爸爸（妈妈）？',
    category: '人际关系',
    scene: '课堂上讨论家庭话题，学生提出疑问。',
    response: '每个家庭都不一样，有的家庭有爸爸妈妈，有的只有爸爸或妈妈，有的和爷爷奶奶一起生活。爱我们的家人就是最重要的。',
    tips: [
      { type: 'do', content: '强调家庭多样性' },
      { type: 'do', content: '避免负面评价任何家庭结构' },
    ],
  },
  {
    id: 7,
    question: '老师，有人给我起外号，我不喜欢',
    category: '人际关系',
    scene: '学生因被起外号感到困扰。',
    response: '你可以告诉对方“请不要这样叫我，我不喜欢”。如果对方继续，你可以来找老师。',
    tips: [
      { type: 'do', content: '教孩子表达感受' },
      { type: 'do', content: '提供求助渠道' },
    ],
  },
  {
    id: 8,
    question: '老师，我可以一个人去厕所吗？',
    category: '身体与安全',
    scene: '课间学生询问独自去厕所是否安全。',
    response: '当然可以。但如果在不熟悉的地方，最好和小伙伴一起去，互相照应。',
    tips: [
      { type: 'do', content: '鼓励孩子独立' },
      { type: 'do', content: '提醒安全场景' },
    ],
  },
  {
    id: 9,
    question: '老师，我为什么长得比别人矮？',
    category: '成长变化',
    scene: '学生对身体身高差异产生焦虑。',
    response: '每个人的成长速度不一样，有的先长高，有的后长高。只要健康饮食、多运动、睡好觉，身体就会健康成长。',
    tips: [
      { type: 'do', content: '消除焦虑' },
      { type: 'caution', content: '不要承诺具体长高时间' },
    ],
  },
  {
    id: 10,
    question: '老师，如果有人让我看不好的图片怎么办？',
    category: '身体与安全',
    scene: '学生接触到不适宜内容后向老师求助。',
    response: '不好的图片不是我们这个年龄该看的，你可以拒绝，并且告诉老师或家长。',
    tips: [
      { type: 'do', content: '明确拒绝' },
      { type: 'do', content: '强调报告' },
      { type: 'caution', content: '不要追问孩子看到了什么细节' },
    ],
  },
];

export function getQaById(id: number): QA | undefined {
  return qaList.find((q) => q.id === id);
}
