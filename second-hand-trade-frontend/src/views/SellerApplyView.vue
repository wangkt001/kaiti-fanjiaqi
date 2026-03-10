<template>
  <div class="seller-apply-view">
    <div class="apply-container">
      <h1 class="page-title">申请成为卖家</h1>
      <p class="page-subtitle">填写以下信息，申请开设您的店铺</p>

      <el-card class="apply-form-card">
        <el-form
          ref="formRef"
          :model="applyForm"
          :rules="rules"
          label-width="120px"
          class="apply-form"
        >
          <el-divider content-position="left">基本信息</el-divider>

          <el-form-item label="真实姓名" prop="realName">
            <el-input
              v-model="applyForm.realName"
              placeholder="请输入您的真实姓名"
              clearable
            />
          </el-form-item>

          <el-form-item label="身份证号" prop="idCard">
            <el-input
              v-model="applyForm.idCard"
              placeholder="请输入身份证号"
              clearable
              maxlength="18"
            />
          </el-form-item>

          <el-form-item label="联系电话" prop="phone">
            <el-input
              v-model="applyForm.phone"
              placeholder="请输入联系电话"
              clearable
              maxlength="11"
            />
          </el-form-item>

          <el-form-item label="微信号" prop="wechat">
            <el-input
              v-model="applyForm.wechat"
              placeholder="请输入微信号（选填）"
              clearable
            />
          </el-form-item>

          <el-divider content-position="left">店铺信息</el-divider>

          <el-form-item label="店铺名称" prop="shopName">
            <el-input
              v-model="applyForm.shopName"
              placeholder="请输入店铺名称"
              clearable
              maxlength="30"
            />
          </el-form-item>

          <el-form-item label="店铺类型" prop="shopType">
            <el-select v-model="applyForm.shopType" placeholder="请选择店铺类型" style="width: 100%">
              <el-option label="个人店铺" value="personal" />
              <el-option label="企业店铺" value="enterprise" />
              <el-option label="品牌店铺" value="brand" />
            </el-select>
          </el-form-item>

          <el-form-item label="主营类目" prop="mainCategory">
            <el-select v-model="applyForm.mainCategory" placeholder="请选择主营类目" style="width: 100%">
              <el-option label="传统工艺品" value="traditional_crafts" />
              <el-option label="非遗文化" value="intangible_heritage" />
              <el-option label="博物馆文创" value="museum_creative" />
              <el-option label="地方特产" value="local_specialties" />
              <el-option label="文创周边" value="creative_products" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>

          <el-form-item label="店铺描述" prop="shopDescription">
            <el-input
              v-model="applyForm.shopDescription"
              type="textarea"
              :rows="4"
              placeholder="请描述您的店铺特色、经营理念等（200 字以内）"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="店铺 Logo" prop="shopLogo">
            <el-upload
              class="logo-uploader"
              action="#"
              :http-request="uploadLogo"
              :show-file-list="false"
              :before-upload="beforeLogoUpload"
              accept="image/*"
            >
              <img v-if="applyForm.shopLogo" :src="applyForm.shopLogo" class="logo-preview" />
              <el-icon v-else class="logo-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
            <div class="form-tip">建议尺寸：200x200 像素，支持 JPG、PNG 格式，大小不超过 2MB</div>
          </el-form-item>

          <el-form-item label="资质证明" prop="certificates">
            <el-upload
              class="certificate-uploader"
              action="#"
              :http-request="uploadCertificate"
              :file-list="certificateList"
              :on-remove="removeCertificate"
              :before-upload="beforeCertificateUpload"
              accept="image/*"
              multiple
              :limit="5"
              list-type="picture-card"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="form-tip">上传营业执照、身份证照片等资质证明，最多 5 张</div>
          </el-form-item>

          <el-divider content-position="left">补充说明</el-divider>

          <el-form-item label="其他说明" prop="remark">
            <el-input
              v-model="applyForm.remark"
              type="textarea"
              :rows="3"
              placeholder="其他需要说明的信息（选填）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-checkbox v-model="applyForm.agreeTerms">
              我已阅读并同意
              <el-link type="primary" :underline="false" @click="showTerms = true">《卖家入驻协议》</el-link>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="submitApply" size="large">
              提交申请
            </el-button>
            <el-button @click="goBack" size="large">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 入驻协议弹窗 -->
      <el-dialog v-model="showTerms" title="卖家入驻协议" width="60%">
        <div class="terms-content">
          <h3>一、总则</h3>
          <p>1.1 本协议是您与豫见好物平台之间关于您使用本平台服务所订立的协议。</p>
          <p>1.2 本平台保留随时修改本协议的权利，修改后的协议一旦公布即有效代替原来的协议。</p>

          <h3>二、卖家义务</h3>
          <p>2.1 卖家应保证所售商品的合法性和真实性。</p>
          <p>2.2 卖家应遵守国家相关法律法规和平台规则。</p>
          <p>2.3 卖家应保证提供的资质证明真实有效。</p>

          <h3>三、服务费用</h3>
          <p>3.1 平台目前免收店铺入驻费用。</p>
          <p>3.2 交易成功后，平台将按成交金额收取一定比例的技术服务费。</p>

          <h3>四、违约责任</h3>
          <p>4.1 如卖家违反本协议或平台规则，平台有权采取警告、下架商品、关闭店铺等措施。</p>
          <p>4.2 如因卖家原因给平台或第三方造成损失，卖家应承担赔偿责任。</p>
        </div>
        <template #footer>
          <el-button @click="showTerms = false">关闭</el-button>
          <el-button type="primary" @click="agreeTerms">同意</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { applySeller } from '@/api/modules/user';

const router = useRouter();

// 表单引用
const formRef = ref();

// 提交状态
const submitting = ref(false);

// 协议弹窗
const showTerms = ref(false);

// 证书列表
const certificateList = ref<any[]>([]);

// 申请表单
const applyForm = reactive({
  realName: '',
  idCard: '',
  phone: '',
  wechat: '',
  shopName: '',
  shopType: 'personal',
  mainCategory: 'traditional_crafts',
  shopDescription: '',
  shopLogo: '',
  certificates: [] as string[],
  remark: '',
  agreeTerms: false,
});

// 表单验证规则
const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    {
      pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/,
      message: '请输入正确的身份证号',
      trigger: 'blur',
    },
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur',
    },
  ],
  shopName: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' },
    { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' },
  ],
  shopType: [{ required: true, message: '请选择店铺类型', trigger: 'change' }],
  mainCategory: [{ required: true, message: '请选择主营类目', trigger: 'change' }],
  shopDescription: [
    { required: true, message: '请输入店铺描述', trigger: 'blur' },
    { max: 200, message: '不超过 200 字', trigger: 'blur' },
  ],
  agreeTerms: [{ required: true, message: '请先同意入驻协议', trigger: 'change' }],
};

// 上传 Logo
const uploadLogo = async (file: any) => {
  // TODO: 实现文件上传到服务器
  // 这里使用本地预览
  const reader = new FileReader();
  reader.onload = (e) => {
    applyForm.shopLogo = e.target?.result as string;
  };
  reader.readAsDataURL(file.file);
};

// 上传资质证明
const uploadCertificate = async (file: any) => {
  // TODO: 实现文件上传到服务器
  const reader = new FileReader();
  reader.onload = (e) => {
    certificateList.value.push({
      name: file.file.name,
      url: e.target?.result as string,
    });
    applyForm.certificates.push(e.target?.result as string);
  };
  reader.readAsDataURL(file.file);
};

// 移除证书
const removeCertificate = (file: any) => {
  const index = certificateList.value.findIndex((item) => item.uid === file.uid);
  if (index > -1) {
    certificateList.value.splice(index, 1);
    applyForm.certificates.splice(index, 1);
  }
};

// Logo 上传前校验
const beforeLogoUpload = (file: File) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('只能上传图片文件！');
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！');
  }
  return isImage && isLt2M;
};

// 证书上传前校验
const beforeCertificateUpload = (file: File) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('只能上传图片文件！');
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！');
  }
  return isImage && isLt2M;
};

// 同意协议
const agreeTerms = () => {
  applyForm.agreeTerms = true;
  showTerms.value = false;
};

// 提交申请
const submitApply = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return;

    if (!applyForm.agreeTerms) {
      ElMessage.warning('请先同意入驻协议');
      return;
    }

    submitting.value = true;

    try {
      // 构造提交数据
      const sellerInfo = {
        realName: applyForm.realName,
        idCard: applyForm.idCard,
        phone: applyForm.phone,
        wechat: applyForm.wechat,
        shopName: applyForm.shopName,
        shopType: applyForm.shopType,
        mainCategory: applyForm.mainCategory,
        shopDescription: applyForm.shopDescription,
        shopLogo: applyForm.shopLogo,
        certificates: applyForm.certificates,
        remark: applyForm.remark,
      };

      await applySeller({ sellerInfo });

      ElMessage.success('申请提交成功！请等待平台审核，审核结果将短信通知您。');
      router.push('/user-center');
    } catch (error) {
      console.error('提交申请失败:', error);
      ElMessage.error('提交申请失败，请稍后重试');
    } finally {
      submitting.value = false;
    }
  });
};

// 返回
const goBack = () => {
  router.back();
};
</script>

<style lang="scss" scoped>
.seller-apply-view {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40px 20px;
}

.apply-container {
  max-width: 800px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 10px;
}

.page-subtitle {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-bottom: 30px;
}

.apply-form-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 30px;
  }
}

.apply-form {
  .el-divider {
    margin: 20px 0;
    background: #f5f7fa;

    .el-divider__text {
      font-weight: bold;
      color: #333;
    }
  }
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  line-height: 1.5;
}

.logo-uploader {
  .logo-preview {
    width: 100px;
    height: 100px;
    border-radius: 8px;
    object-fit: cover;
    border: 1px solid #e4e7ed;
  }

  .logo-uploader-icon {
    width: 100px;
    height: 100px;
    border: 1px dashed #d9d9d9;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
    color: #8c939d;
    cursor: pointer;

    &:hover {
      border-color: #409eff;
      color: #409eff;
    }
  }
}

.certificate-uploader {
  :deep(.el-upload-list__item) {
    transition: all 0.3s;
  }

  :deep(.el-upload--picture-card) {
    width: 100px;
    height: 100px;
  }

  :deep(.el-upload-list__item) {
    width: 100px;
    height: 100px;
  }
}

.terms-content {
  max-height: 400px;
  overflow-y: auto;
  line-height: 1.8;

  h3 {
    margin: 20px 0 10px;
    color: #333;
    font-size: 16px;

    &:first-child {
      margin-top: 0;
    }
  }

  p {
    margin: 8px 0;
    color: #666;
    font-size: 14px;
    text-indent: 2em;
  }
}
</style>
