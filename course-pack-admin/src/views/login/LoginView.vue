<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2 class="title">伴芽管理后台</h2>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="form"
      >
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="账号" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="btn" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
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

const form = ref({
  username: '',
  password: '',
});

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
      // 支持守卫带回的 redirect（无权限路径会被守卫再弹回，安全）
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
.login-page {
  height: 100vh;
  background: var(--bg-page);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  width: 400px;
  /* Element 卡片自带圆角，需就地覆盖为叶片裁角（V3） */
  border-radius: var(--radius-leaf-lg);
}
.title {
  text-align: center;
  margin-top: 0;
  color: var(--text-primary);
}
.form {
  margin-top: 24px;
}
.btn {
  width: 100%;
}
</style>