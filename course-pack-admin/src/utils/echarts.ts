/**
 * ECharts 按需引入（仅 LineChart / PieChart 及必需组件）。
 * 消除全量引入导致的 500kB chunk 警告；图表组件通过动态 import 使用本模块。
 */
import * as echarts from 'echarts/core';
import { LineChart, PieChart } from 'echarts/charts';
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer]);

export default echarts;
export { echarts };
