<template>
  <div class="login-view">
    <div class="login-container">
      <!-- 返回主页 -->
      <router-link to="/" class="back-home">
        <el-icon><ArrowLeft /></el-icon>
        返回首页
      </router-link>

      <!-- 登录表单 -->
      <el-card class="login-card">
        <div class="login-header">
          <h1 class="login-title">用户登录</h1>
          <p class="login-subtitle">欢迎回到豫见好物</p>
        </div>

        <el-form
          ref="formRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名/手机号/邮箱"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
              <router-link to="/forgot-password" class="forgot-link">
                忘记密码？
              </router-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
            >
              {{ loading ? "登录中..." : "登录" }}
            </el-button>
          </el-form-item>

          <div class="login-divider">
            <span>其他登录方式</span>
          </div>

          <div class="social-login">
            <el-button circle class="social-btn">
              <el-icon><ChatDotRound /></el-icon>
            </el-button>
          </div>
        </el-form>

        <div class="login-footer">
          还没有账号？
          <router-link to="/register" class="register-link"
            >立即注册</router-link
          >
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter, useRoute } from "vue-router";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage } from "element-plus";
import { ArrowLeft, User, Lock, ChatDotRound } from "@element-plus/icons-vue";
import { useUserStore } from "@/store/user";
import { login as loginApi } from "@/api/modules/user";
import type { LoginParams } from "@/types";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const formRef = ref<FormInstance>();
const loading = ref(false);

const loginForm = reactive<LoginParams & { remember?: boolean }>({
  username: "",
  password: "",
  remember: true,
});

const loginRules: FormRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 3,
      max: 50,
      message: "用户名长度在 3 到 50 个字符",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度在 6 到 20 个字符", trigger: "blur" },
  ],
};

// 处理登录
const handleLogin = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    loading.value = true;

    try {
      const res = await loginApi({
        username: loginForm.username,
        password: loginForm.password,
      });

      // res 已经是解包后的数据，直接使用
      const { token, userInfo } = res;
      
      // 保存 token 到 localStorage
      localStorage.setItem('token', token);
      
      // 保存用户信息到 store
      userStore.login(userInfo);

      ElMessage.success("登录成功");

      // 跳转到重定向页面或首页
      const redirect = route.query.redirect as string;
      router.push(redirect || "/");
    } catch (error) {
      console.error("登录失败", error);
    } finally {
      loading.value = false;
    }
  });
};
</script>

<style lang="scss" scoped>
.login-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-large;
}

.login-container {
  width: 100%;
  max-width: 480px;
  position: relative;
}

.back-home {
  position: absolute;
  top: -50px;
  left: 0;
  color: #ffffff;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: $spacing-small;
  font-size: $font-size-base;
  transition: $transition-fast;

  &:hover {
    gap: $spacing-small + 4px;
  }
}

.login-card {
  border-radius: $border-radius-large;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  padding: $spacing-extra-large;
}

.login-header {
  text-align: center;
  margin-bottom: $spacing-extra-large;

  .login-title {
    font-size: $font-size-extra-large;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: $spacing-small;
  }

  .login-subtitle {
    font-size: $font-size-base;
    color: $text-secondary;
  }
}

.login-form {
  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    .forgot-link {
      font-size: $font-size-small;
      color: $primary-color;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .login-button {
    width: 100%;
  }
}

.login-divider {
  position: relative;
  text-align: center;
  margin: $spacing-large 0;

  &::before {
    content: "";
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 1px;
    background-color: $border-lighter;
  }

  span {
    position: relative;
    background-color: $bg-white;
    padding: 0 $spacing-base;
    font-size: $font-size-small;
    color: $text-secondary;
  }
}

.social-login {
  display: flex;
  justify-content: center;
  gap: $spacing-large;

  .social-btn {
    width: 50px;
    height: 50px;
    font-size: 24px;
    border: 1px solid $border-color;

    &:hover {
      background-color: $bg-page;
    }
  }
}

.login-footer {
  text-align: center;
  margin-top: $spacing-large;
  font-size: $font-size-base;
  color: $text-regular;

  .register-link {
    color: $primary-color;
    text-decoration: none;
    font-weight: 500;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
