<template>
  <div class="register-view">
    <div class="register-container">
      <h2 class="register-title">用户注册</h2>

      <el-form
        ref="formRef"
        :model="registerForm"
        :rules="rules"
        label-width="80px"
        class="register-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleRegister"
            class="register-btn"
          >
            注册
          </el-button>
          <el-button @click="handleLogin"> 去登录 </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import { register } from "@/api/modules/auth";

const router = useRouter();
const formRef = ref<FormInstance>();
const loading = ref(false);

const registerForm = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  nickname: "",
  phone: "",
});

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const rules = reactive<FormRules>({
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度在 3-20 个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于 6 位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" },
  ],
  nickname: [{ required: true, message: "请输入昵称", trigger: "blur" }],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
});

const handleRegister = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await register({
          username: registerForm.username,
          password: registerForm.password,
          nickname: registerForm.nickname,
          phone: registerForm.phone || undefined,
          role: "buyer", // 默认注册为买家
        });
        ElMessage.success("注册成功");
        router.push("/login");
      } catch (error: any) {
        ElMessage.error(error.message || "注册失败");
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleLogin = () => {
  router.push("/login");
};
</script>

<style scoped lang="scss">
.register-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-container {
  width: 100%;
  max-width: 450px;
  padding: 40px;
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
}

.register-form {
  .register-btn {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>
