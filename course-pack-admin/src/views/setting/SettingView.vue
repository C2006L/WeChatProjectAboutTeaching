<template>
  <div class="page">
    <!-- ① 演示账号表 -->
    <section class="card card-leaf">
      <div class="card-head">
        <h2 class="card-title">演示账号</h2>
        <p class="card-desc">
          演示密码统一 <span class="pwd">{{ MOCK_PASSWORD }}</span>，与 README 账号表、后端 DataSeeder 三方同构
        </p>
      </div>
      <el-table :data="demoAccounts" style="width: 100%">
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <span class="mono">{{ row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <span class="role-badge" :class="`role-${row.role.toLowerCase()}`">{{ roleLabel(row.role) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" effect="light" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核领域" width="130">
          <template #default="{ row }">
            <span v-if="row.auditField">{{ AUDIT_FIELD_LABEL[row.auditField] || row.auditField }}</span>
            <span v-else class="dim">—</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="180">
          <template #default="{ row }">
            <span v-if="row.isSeed === 1" class="seed-tag">
              <svg viewBox="0 0 24 24" fill="none" class="seed-ico" aria-hidden="true">
                <path d="M12 20v-6.6" style="stroke: var(--line-brand)" stroke-width="2" stroke-linecap="round" />
                <path d="M12 13.6c-2.8 0-4.5-1.5-4.5-3.9 2.4 0 4.5 1.5 4.5 3.9Z" style="stroke: var(--line-brand)" stroke-width="2" stroke-linejoin="round" />
                <path d="M12 13.6c2.8-.3 4.5-1.8 4.5-4.1-2.4-.3-4.5 1.6-4.5 4.1Z" style="stroke: var(--line-brand)" stroke-width="2" stroke-linejoin="round" />
              </svg>
              种子教师
            </span>
            <span v-if="row.status === 'REJECTED' && row.rejectReason" class="reason">原因：{{ row.rejectReason }}</span>
            <span v-if="row.isSeed !== 1 && !(row.status === 'REJECTED' && row.rejectReason)" class="dim">—</span>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <!-- ②③ 平台基本信息 + 修改密码 -->
    <div class="form-grid">
      <section class="card card-leaf">
        <div class="card-head">
          <h2 class="card-title">平台基本信息</h2>
          <p class="card-desc">保存后立即生效（本地演示持久化）</p>
        </div>
        <el-form label-width="88px" @submit.prevent>
          <el-form-item label="平台名称">
            <el-input v-model="platform.name" maxlength="24" show-word-limit />
          </el-form-item>
          <el-form-item label="副标题">
            <el-input v-model="platform.subtitle" maxlength="48" show-word-limit />
          </el-form-item>
          <el-form-item label="联系邮箱">
            <el-input v-model="platform.contactEmail" maxlength="64" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSavePlatform">保存修改</el-button>
            <el-button @click="onResetPlatform">还原默认</el-button>
          </el-form-item>
        </el-form>
      </section>

      <section class="card card-leaf">
        <div class="card-head">
          <h2 class="card-title">修改密码</h2>
          <p class="card-desc">演示环境仅前端校验，不会实际修改</p>
        </div>
        <el-form label-width="88px" @submit.prevent>
          <el-form-item label="当前密码">
            <el-input v-model="pwdForm.current" type="password" show-password placeholder="请输入当前密码" />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwdForm.next" type="password" show-password placeholder="不少于 6 位" />
          </el-form-item>
          <el-form-item label="确认新密码">
            <el-input v-model="pwdForm.confirm" type="password" show-password placeholder="再次输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onChangePassword">确认修改</el-button>
          </el-form-item>
        </el-form>
      </section>
    </div>

    <!-- ④ 审核领域说明 -->
    <section class="card card-leaf">
      <div class="card-head">
        <h2 class="card-title">审核领域说明</h2>
        <p class="card-desc">审核员按 audit_field 字段分流审核队列，仅能看到所属领域的待审内容</p>
      </div>
      <div class="field-list">
        <div v-for="(label, key) in AUDIT_FIELD_LABEL" :key="key" class="field-row">
          <span class="field-code">{{ key }}</span>
          <span class="field-name">{{ label }}</span>
          <span class="field-desc">
            {{ key === 'PSY' ? '儿童心理、情绪疏导、安全教育类课程与案例' : '教学方法、课程设计、课堂管理类课程与案例' }}
          </span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import {
  demoAccounts,
  MOCK_PASSWORD,
  AUDIT_FIELD_LABEL,
  loadPlatformInfo,
  savePlatformInfo,
  DEFAULT_PLATFORM_INFO,
} from '@/mock/setting';
import type { PlatformInfo, DemoAccount } from '@/mock/setting';

/* ── ① 演示账号 ─────────────────────────────── */
const accounts = ref<DemoAccount[]>(demoAccounts);

const ROLE_LABEL: Record<string, string> = {
  ADMIN: '系统管理员',
  AUDITOR: '审核员',
  TEACHER: '教师',
};
const roleLabel = (r: string) => ROLE_LABEL[r] || r;

const STATUS_LABEL: Record<string, string> = {
  PENDING: '待审核',
  ACTIVE: '正常',
  REJECTED: '已驳回',
  DISABLED: '已停用',
};
const statusLabel = (s: string) => STATUS_LABEL[s] || s;

const STATUS_TAG: Record<string, 'success' | 'warning' | 'danger' | 'info'> = {
  PENDING: 'warning',
  ACTIVE: 'success',
  REJECTED: 'danger',
  DISABLED: 'info',
};
const statusTag = (s: string) => STATUS_TAG[s] || 'info';

/* ── ② 平台基本信息 ─────────────────────────── */
const platform = ref<PlatformInfo>({ ...DEFAULT_PLATFORM_INFO });

async function onSavePlatform() {
  if (!platform.value.name.trim()) {
    ElMessage.warning('平台名称不能为空');
    return;
  }
  savePlatformInfo({ ...platform.value });
  ElMessage.success('平台信息已保存');
}

function onResetPlatform() {
  platform.value = { ...DEFAULT_PLATFORM_INFO };
  savePlatformInfo({ ...platform.value });
  ElMessage.info('已还原为默认信息');
}

/* ── ③ 修改密码（仅前端校验演示） ───────────── */
const pwdForm = reactive({ current: '', next: '', confirm: '' });

function onChangePassword() {
  if (!pwdForm.current || !pwdForm.next || !pwdForm.confirm) {
    ElMessage.warning('请填写完整三项密码');
    return;
  }
  if (pwdForm.next.length < 6) {
    ElMessage.warning('新密码不少于 6 位');
    return;
  }
  if (pwdForm.next !== pwdForm.confirm) {
    ElMessage.warning('两次输入的新密码不一致');
    return;
  }
  ElMessage.info(`演示环境密码固定为 ${MOCK_PASSWORD}，未实际修改`);
  pwdForm.current = '';
  pwdForm.next = '';
  pwdForm.confirm = '';
}

onMounted(() => {
  platform.value = loadPlatformInfo();
});
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: var(--space-l);
  padding-bottom: var(--space-l);
}
.card {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: var(--space-l);
  box-shadow: var(--shadow-card);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.card-head {
  margin-bottom: var(--space-m);
}
.card-title {
  margin: 0;
  font-family: var(--font-serif);
  font-size: var(--font-size-m);
  font-weight: 650;
  color: var(--text-primary);
}
.card-desc {
  margin: 4px 0 0;
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}
.pwd {
  color: var(--brand-primary-dark);
  font-weight: 600;
  font-family: monospace;
}
/* 表格行分隔线/表头线统一墨绿低透明度（禁中性灰） */
.card :deep(.el-table) {
  --el-table-border-color: var(--divider-soft);
}
.mono {
  font-family: monospace;
  color: var(--text-regular);
}
.dim {
  color: var(--text-disabled);
}
.role-badge {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 10px;
  border-radius: 11px;
  font-size: 12px;
  font-weight: 600;
}
.role-admin {
  color: var(--on-brand);
  background: var(--brand-primary);
}
.role-auditor {
  color: var(--brand-primary-dark);
  background: var(--brand-primary-light);
}
.role-teacher {
  color: var(--text-regular);
  background: var(--bg-oat);
}
.seed-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  margin-right: 8px;
  padding: 0 8px;
  height: 20px;
  border-radius: 10px;
  font-size: 12px;
  color: var(--brand-primary-dark);
  background: var(--brand-primary-light);
}
.seed-ico {
  width: 12px;
  height: 12px;
}
.reason {
  font-size: 12px;
  color: var(--danger);
}
/* ②③ 表单双列 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--space-l);
}
@media (max-width: 960px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
/* ④ 审核领域说明 */
.field-list {
  display: flex;
  flex-direction: column;
}
.field-row {
  display: flex;
  align-items: baseline;
  gap: var(--space-m);
  padding: 12px 0;
}
.field-row + .field-row {
  border-top: 1px solid var(--divider-soft);
}
.field-code {
  flex-shrink: 0;
  width: 52px;
  padding: 2px 0;
  text-align: center;
  border-radius: var(--radius-s);
  font-family: monospace;
  font-size: var(--font-size-xs);
  font-weight: 600;
  color: var(--brand-primary-dark);
  background: var(--brand-primary-light);
}
.field-name {
  flex-shrink: 0;
  font-weight: 600;
  color: var(--text-primary);
}
.field-desc {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}
</style>
