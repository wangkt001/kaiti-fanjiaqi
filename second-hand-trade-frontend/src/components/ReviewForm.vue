<template>
  <el-dialog
    v-model="dialogVisible"
    title="发表评价"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="评分" prop="rating">
        <el-rate v-model="form.rating" show-text text-color="#ff9900" />
      </el-form-item>

      <el-form-item label="评价内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="4"
          placeholder="分享您的使用感受..."
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="上传图片">
        <el-upload
          v-model:file-list="fileList"
          action="#"
          list-type="picture-card"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleRemove"
          :limit="9"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <el-form-item label="匿名评价">
        <el-switch v-model="form.isAnonymous" />
        <span style="margin-left: 10px; font-size: 12px; color: #999">
          匿名后您的真实姓名将显示为"匿名用户"
        </span>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          提交评价
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import { Plus } from "@element-plus/icons-vue";
import {
  ElMessage,
  type FormInstance,
  type UploadUserFile,
} from "element-plus";
import { createReview, type ReviewCreateParams } from "@/api/modules/review";
import { uploadImageFile } from "@/api/modules/user";
import { useUserStore } from "@/store/user";

const props = defineProps<{
  productId: number;
  orderId: number;
  modelValue: boolean;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
  (e: "success"): void;
}>();

const userStore = useUserStore();

const formRef = ref<FormInstance>();
const dialogVisible = ref(props.modelValue);
const submitting = ref(false);
const fileList = ref<UploadUserFile[]>([]);
const imageUrls = ref<string[]>([]);

const form = reactive<ReviewCreateParams>({
  productId: props.productId,
  orderId: props.orderId,
  rating: 5,
  content: "",
  images: [],
  isAnonymous: false,
});

const rules = {
  rating: [{ required: true, message: "请选择评分", trigger: "change" }],
  content: [
    { required: true, message: "请输入评价内容", trigger: "blur" },
    {
      min: 10,
      max: 500,
      message: "评价内容长度在 10-500 个字符之间",
      trigger: "blur",
    },
  ],
};

watch(
  () => props.modelValue,
  (val) => {
    dialogVisible.value = val;
  },
  { immediate: true },
);

watch(dialogVisible, (val) => {
  emit("update:modelValue", val);
  if (!val) {
    resetForm();
  }
});

watch(
  [() => props.productId, () => props.orderId],
  ([productId, orderId]) => {
    form.productId = productId;
    form.orderId = orderId;
  },
  { immediate: true },
);

const handleFileChange = (file: UploadUserFile) => {
  if (!file.raw) return;
  uploadImage(file.raw);
};

const handleRemove = (file: UploadUserFile) => {
  const index = fileList.value.findIndex((item) => item.uid === file.uid);
  if (index > -1) {
    imageUrls.value.splice(index, 1);
  }
};

const uploadImage = async (rawFile: File) => {
  try {
    const res = await uploadImageFile(rawFile);
    imageUrls.value.push(res.url);
  } catch (error) {
    console.error("上传评价图片失败:", error);
    ElMessage.error("上传评价图片失败");
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    if (!userStore.isLoggedIn) {
      ElMessage.warning("请先登录");
      return;
    }

    submitting.value = true;
    try {
      form.productId = props.productId;
      form.orderId = props.orderId;
      form.images = imageUrls.value;

      await createReview(form);
      ElMessage.success("评价成功");
      dialogVisible.value = false;
      emit("success");
    } catch (error) {
      console.error("评价失败:", error);
    } finally {
      submitting.value = false;
    }
  });
};

const handleClose = () => {
  dialogVisible.value = false;
  resetForm();
};

const resetForm = () => {
  form.rating = 5;
  form.content = "";
  form.isAnonymous = false;
  fileList.value = [];
  imageUrls.value = [];
  formRef.value?.resetFields();
};
</script>

<style lang="scss" scoped>
:deep(.el-upload-list__item) {
  transition: all 0.3s;
}

:deep(.el-upload--picture-card) {
  background-color: #f5f7fa;
  border: 2px dashed #dcdfe6;
}
</style>
