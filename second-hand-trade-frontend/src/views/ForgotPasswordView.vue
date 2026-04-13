<template>
  <div class="forgot-password-view">
    <div class="forgot-password-container">
      <router-link to="/login" class="back-login">
        <el-icon><ArrowLeft /></el-icon>
        返回登录
      </router-link>

      <el-card class="forgot-password-card">
        <div class="header">
          <h1 class="title">忘记密码</h1>
          <p class="subtitle">请输入手机号和真实姓名验证身份后设置新密码</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="forgot-password-form"
          @keyup.enter="handleSubmit"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="form.phone"
              placeholder="请输入注册时的手机号"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="nickname">
            <el-input
              v-model="form.nickname"
              placeholder="请输入真实姓名"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="newPassword">
            <el-input
              v-model="form.newPassword"
              type="password"
              placeholder="请输入新密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="submit-button"
              :loading="loading"
              @click="handleSubmit"
            >
              {{ loading ? "提交中..." : "重置密码" }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage } from "element-plus";
import { ArrowLeft, Lock, User, Phone } from "@element-plus/icons-vue";
import { forgotPassword } from "@/api/modules/user";

const router = useRouter();
const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({
  phone: "",
  nickname: "",
  newPassword: "",
  confirmPassword: "",
});

const validateConfirmPassword = (
  _rule: unknown,
  value: string,
  callback: (error?: Error) => void,
) => {
  if (value !== form.newPassword) {
    callback(new Error("两次输入的密码不一致"));
    return;
  }
  callback();
};

const rules: FormRules = {
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  nickname: [
    { required: true, message: "请输入真实姓名", trigger: "blur" },
    { min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur" },
  ],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度在 6 到 20 个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" },
  ],
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    loading.value = true;
    try {
      await forgotPassword({
        phone: form.phone,
        nickname: form.nickname,
        newPassword: form.newPassword,
      });
      ElMessage.success("密码重置成功，请重新登录");
      router.push("/login");
    } catch (error: any) {
      console.error("重置密码失败", error);
      ElMessage.error(error.message || "重置密码失败");
    } finally {
      loading.value = false;
    }
  });
};
</script>

<style lang="scss" scoped>
.forgot-password-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-large;
}

.forgot-password-container {
  width: 100%;
  max-width: 480px;
  position: relative;
}

.back-login {
  position: absolute;
  top: -48px;
  left: 0;
  display: flex;
  align-items: center;
  gap: $spacing-small;
  color: white;
  text-decoration: none;
  font-size: $font-size-base;

  &:hover {
    opacity: 0.9;
  }
}

.forgot-password-card {
  border-radius: $border-radius-large;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);

  :deep(.el-card__body) {
    padding: $spacing-extra-large;
  }
}

.header {
  text-align: center;
  margin-bottom: $spacing-extra-large;

  .title {
    font-size: 32px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: $spacing-small;
  }

  .subtitle {
    color: $text-secondary;
    font-size: $font-size-base;
  }
}

.submit-button {
  width: 100%;
}
</style>
