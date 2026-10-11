<template>
  <div class="login-page">
    <section class="brand-panel">
      <div class="brand-lockup">
        <img class="brand-mark light" src="/brand-logo.png" alt="" aria-hidden="true" />
        <div><strong>伴芽小站</strong><span>乡村生命教育支持计划</span></div>
      </div>
      <div class="brand-message">
        <span class="kicker">FOR EVERY CLASSROOM</span>
        <h1>把可靠的课程，<br />送到每一间需要的教室。</h1>
        <p>为支教教师提供课程、培训与现场工具，也让项目团队看见每一份努力如何发生。</p>
      </div>
      <div class="field-scene" aria-hidden="true">
        <div class="sun"></div>
        <div class="hill hill-back"></div>
        <div class="hill hill-front"></div>
        <div class="scene-sprout"><i></i><b></b><em></em></div>
      </div>
      <span class="edition">伴芽小站 · 项目运营端</span>
    </section>

    <section class="auth-panel">
      <div class="login-card">
        <div class="mobile-brand">
          <img class="brand-mark" src="/brand-logo.png" alt="" aria-hidden="true" />
          <span>伴芽小站</span>
        </div>
        <span class="auth-kicker">WELCOME BACK</span>
        <h2 class="title">登录工作台</h2>
        <p class="subtitle">处理课程、审核任务与教师支持工作</p>
        <el-form ref="formRef" :model="form" :rules="rules" class="form" label-position="top">
          <el-form-item prop="username" label="账号">
            <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" clearable size="large" />
          </el-form-item>
          <el-form-item prop="password" label="密码">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large" @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="btn" :loading="loading" @click="handleLogin">进入工作台</el-button>
          </el-form-item>
        </el-form>
        <p class="demo-tip">演示管理员：admin01　密码：123456</p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/store/auth';
import { ElMessage } from 'element-plus/es/components/message/index';

const formRef = ref<FormInstance>();
const router = useRouter();
const route = useRoute();
const auth = useAuthStore();
const loading = ref(false);
const form = ref({ username: '', password: '' });
const rules = ref<FormRules>({
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
});

async function handleLogin() {
  if (!formRef.value) return;
  formRef.value.validate(async (valid) => {
    if (!valid) return;
    loading.value = true;
    try {
      await auth.login(form.value.username, form.value.password);
      ElMessage.success('登录成功');
      const home = auth.role === 'AUDITOR'
        ? '/audit'
        : auth.role === 'TEACHER'
          ? (auth.status === 'ACTIVE' ? '/teacher/workbench' : '/teacher/status')
          : '/dashboard';
      const redirect = (route.query.redirect as string) || home;
      router.push(redirect);
    } catch (e) {
      // message 已在 store/request 处理
    } finally {
      loading.value = false;
    }
  });
}
</script>

<style scoped>
.login-page { min-height: 100vh; display: grid; grid-template-columns: minmax(520px, 1.12fr) minmax(460px, .88fr); background: #fff; }
.brand-panel { position: relative; min-height: 100vh; overflow: hidden; padding: 48px 56px; background: var(--brand-ink); color: #fff; }
.brand-lockup { position: relative; z-index: 2; display: flex; align-items: center; gap: 13px; }
.brand-lockup > div:last-child { display: flex; flex-direction: column; }
.brand-lockup strong { font-size: 18px; }
.brand-lockup span { margin-top: 2px; font-size: 11px; color: rgba(255,255,255,.5); letter-spacing: .08em; }
.brand-message { position: relative; z-index: 2; max-width: 590px; margin-top: 15vh; }
.brand-message .kicker { font-size: 11px; font-weight: 600; color: #68C7BD; letter-spacing: .16em; }
.brand-message h1 { margin: 16px 0 0; font-size: clamp(36px, 4vw, 58px); line-height: 1.25; letter-spacing: -.03em; }
.brand-message p { max-width: 520px; margin: 22px 0 0; font-size: 15px; line-height: 1.8; color: rgba(255,255,255,.64); }
.edition { position: absolute; left: 56px; bottom: 34px; z-index: 2; font-size: 11px; color: rgba(255,255,255,.38); }
.auth-panel { display: flex; align-items: center; justify-content: center; padding: 48px; }
.login-card { width: min(400px, 100%); }
.mobile-brand { display: none; }
.auth-kicker { font-size: 11px; font-weight: 700; color: var(--brand-primary); letter-spacing: .15em; }
.title { margin: 10px 0 0; font-size: 28px; color: var(--text-primary); }
.subtitle { margin: 8px 0 0; font-size: 14px; color: var(--text-secondary); }
.brand-mark { flex-shrink: 0; width: 42px; height: 42px; border-radius: 13px; object-fit: cover; box-shadow: 0 6px 18px rgba(23, 111, 102, .16); }
.form { margin-top: 36px; }
.btn { width: 100%; }
.demo-tip { margin: 10px 0 0; font-size: 12px; color: var(--text-secondary); text-align: center; }
.field-scene { position: absolute; inset: auto 0 0; height: 42%; opacity: .9; }
.sun { position: absolute; right: 16%; top: 2%; width: 76px; height: 76px; border-radius: 50%; background: #F4B363; opacity: .9; }
.hill { position: absolute; left: -8%; right: -8%; bottom: -35%; height: 96%; border-radius: 50% 50% 0 0; }
.hill-back { bottom: -46%; background: #2A6D65; transform: rotate(-4deg); }
.hill-front { left: 18%; right: -25%; background: #1D5751; transform: rotate(6deg); }
.scene-sprout { position: absolute; left: 59%; bottom: 12%; width: 130px; height: 210px; }
.scene-sprout i { position: absolute; left: 63px; bottom: 0; width: 5px; height: 148px; border-radius: 5px; background: #68C77B; }
.scene-sprout b, .scene-sprout em { position: absolute; top: 28px; width: 70px; height: 48px; border-radius: 70px 8px 60px 8px; background: #76D183; }
.scene-sprout b { left: 4px; transform: rotate(28deg); }
.scene-sprout em { right: 0; transform: rotate(-28deg) scaleX(-1); }
@media (max-width: 980px) {
  .login-page { grid-template-columns: 1fr; background: var(--bg-page); }
  .brand-panel { display: none; }
  .auth-panel { min-height: 100vh; }
  .mobile-brand { display: flex; align-items: center; gap: 12px; margin-bottom: 40px; font-weight: 700; color: var(--text-primary); }
}
</style>
