<template>
  <!-- 可复用箭头：线稿 SVG（严禁裸写 → 或 > 字符，与小程序端同一约定） -->
  <svg class="arrow-icon" viewBox="0 0 16 16" aria-hidden="true" focusable="false">
    <path
      d="M3 8h9.2M8.6 4.4 12.2 8l-3.6 3.6"
      fill="none"
      stroke="currentColor"
      stroke-width="1.5"
      stroke-linecap="round"
      stroke-linejoin="round"
    />
  </svg>
</template>

<style scoped>
/* 尺寸随字号（1em），颜色随文字（currentColor），行内/弹性容器均可用 */
.arrow-icon {
  width: 1em;
  height: 1em;
  margin-left: 2px;
  vertical-align: -0.12em;
  flex-shrink: 0;
}
</style>