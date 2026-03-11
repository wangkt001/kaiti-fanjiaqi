<template>
  <div class="seller-center-view">
    <NavBar />

    <div class="container">
      <h2 class="page-title">卖家中心</h2>

      <el-tabs v-model="activeTab" class="seller-tabs">
        <!-- 商品管理 -->
        <el-tab-pane label="商品管理" name="products">
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" @click="showAddDialog = true">
                发布商品
              </el-button>
            </div>

            <el-table
              :data="productList"
              v-loading="loading"
              style="width: 100%"
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="商品名称" />
              <el-table-column prop="price" label="价格" width="100">
                <template #default="{ row }"> ¥{{ row.price }} </template>
              </el-table-column>
              <el-table-column prop="stock" label="库存" width="80" />
              <el-table-column prop="salesCount" label="销量" width="80" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" @click="handleEdit(row)">
                    编辑
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="handleDelete(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 发布商品 -->
        <el-tab-pane label="发布商品" name="publish">
          <div class="tab-content">
            <div class="publish-form-container">
              <el-form
                ref="publishFormRef"
                :model="publishForm"
                label-width="120px"
                style="max-width: 600px"
              >
                <el-form-item
                  label="商品名称"
                  prop="name"
                  :rules="{
                    required: true,
                    message: '请输入商品名称',
                    trigger: 'blur',
                  }"
                >
                  <el-input
                    v-model="publishForm.name"
                    placeholder="请输入商品名称"
                    maxlength="100"
                    show-word-limit
                  />
                </el-form-item>

                <el-form-item
                  label="商品描述"
                  prop="description"
                  :rules="{
                    required: true,
                    message: '请输入商品描述',
                    trigger: 'blur',
                  }"
                >
                  <el-input
                    v-model="publishForm.description"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入商品描述"
                    maxlength="1000"
                    show-word-limit
                  />
                </el-form-item>

                <el-form-item
                  label="商品价格"
                  prop="price"
                  :rules="{
                    required: true,
                    type: 'number',
                    min: 0.01,
                    message: '请输入正确的价格',
                    trigger: 'blur',
                  }"
                >
                  <el-input-number
                    v-model="publishForm.price"
                    :min="0.01"
                    :max="999999"
                    :precision="2"
                    :step="0.1"
                    placeholder="请输入价格"
                    style="width: 100%"
                  />
                </el-form-item>

                <el-form-item
                  label="商品库存"
                  prop="stock"
                  :rules="{
                    required: true,
                    type: 'number',
                    min: 0,
                    message: '请输入正确的库存',
                    trigger: 'blur',
                  }"
                >
                  <el-input-number
                    v-model="publishForm.stock"
                    :min="0"
                    :max="9999"
                    placeholder="请输入库存"
                    style="width: 100%"
                  />
                </el-form-item>

                <el-form-item
                  label="商品分类"
                  prop="categoryId"
                  :rules="{
                    required: true,
                    message: '请选择商品分类',
                    trigger: 'change',
                  }"
                >
                  <el-select
                    v-model="publishForm.categoryId"
                    placeholder="请选择商品分类"
                    style="width: 100%"
                  >
                    <el-option label="传统工艺品" :value="1" />
                    <el-option label="文化创意产品" :value="2" />
                    <el-option label="非遗传承" :value="3" />
                    <el-option label="博物馆文创" :value="4" />
                    <el-option label="地方特色" :value="5" />
                  </el-select>
                </el-form-item>

                <el-form-item label="商品图片">
                  <el-upload
                    action="#"
                    list-type="picture-card"
                    :auto-upload="false"
                    :limit="1"
                  >
                    <el-icon><Plus /></el-icon>
                  </el-upload>
                  <div class="form-tip">
                    请上传图片，支持 jpg/png 格式，大小不超过 2MB
                  </div>
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="primary"
                    :loading="publishing"
                    @click="handlePublish"
                  >
                    立即发布
                  </el-button>
                  <el-button @click="resetPublishForm">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-tab-pane>

        <!-- 订单管理 -->
        <el-tab-pane label="订单管理" name="orders">
          <div class="tab-content">
            <el-table :data="orderList" v-loading="loading" style="width: 100%">
              <el-table-column prop="orderNo" label="订单编号" width="180" />
              <el-table-column prop="receiverName" label="收货人" width="100" />
              <el-table-column prop="paymentAmount" label="金额" width="100">
                <template #default="{ row }">
                  ¥{{ row.paymentAmount }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getOrderStatusType(row.status)">
                    {{ getOrderStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="下单时间" width="180" />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" @click="handleViewOrder(row)">
                    查看
                  </el-button>
                  <el-button
                    v-if="row.status === 1"
                    size="small"
                    type="success"
                    @click="handleDeliver(row)"
                  >
                    发货
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 店铺设置 -->
        <el-tab-pane label="店铺设置" name="shop">
          <div class="tab-content">
            <el-form
              :model="shopForm"
              label-width="100px"
              style="max-width: 600px"
            >
              <el-form-item label="店铺名称">
                <el-input
                  v-model="shopForm.shopName"
                  placeholder="请输入店铺名称"
                />
              </el-form-item>
              <el-form-item label="店铺描述">
                <el-input
                  v-model="shopForm.shopDescription"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入店铺描述"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSaveShop">
                  保存设置
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 发布商品对话框（简化版） -->
    <el-dialog
      v-model="showAddDialog"
      title="发布商品"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="productForm" label-width="100px">
        <el-form-item label="商品名称" required>
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品描述" required>
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number
            v-model="productForm.price"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number v-model="productForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="productForm.categoryId" placeholder="请选择分类">
            <el-option label="传统工艺品" :value="1" />
            <el-option label="文化创意产品" :value="2" />
            <el-option label="非遗传承" :value="3" />
            <el-option label="博物馆文创" :value="4" />
            <el-option label="地方特色" :value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          发布
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import NavBar from "@/components/NavBar.vue";
import { createGoods } from "@/api/modules/goods";
import { getCurrentUser } from "@/api/modules/auth";
import { useUserStore } from "@/store/user";

const userStore = useUserStore();

const activeTab = ref("products");
const loading = ref(false);
const submitting = ref(false);
const showAddDialog = ref(false);

// 商品列表
const productList = ref([
  {
    id: 1,
    name: "示例商品",
    price: 99.0,
    stock: 100,
    salesCount: 10,
    status: "on_sale",
  },
]);

// 订单列表
const orderList = ref([
  {
    id: 1,
    orderNo: "ORD202603090001",
    receiverName: "张三",
    paymentAmount: 99.0,
    status: 1,
    createdAt: "2026-03-09 10:00:00",
  },
]);

// 店铺信息
const shopForm = reactive({
  shopName: "",
  shopDescription: "",
});

// 商品表单
const productForm = reactive({
  name: "",
  description: "",
  price: 0,
  stock: 0,
  categoryId: 1,
});

// 发布商品表单
const publishForm = reactive({
  name: "",
  description: "",
  price: 0,
  stock: 0,
  categoryId: 1,
});

// 发布商品相关状态
const publishing = ref(false);
const publishFormRef = ref();

const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    pending: "warning",
    on_sale: "success",
    sold_out: "info",
    offline: "danger",
  };
  return typeMap[status] || "";
};

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: "待审核",
    on_sale: "销售中",
    sold_out: "售罄",
    offline: "已下架",
  };
  return textMap[status] || status;
};

const getOrderStatusType = (status: number) => {
  const typeMap: Record<number, any> = {
    0: "warning",
    1: "success",
    2: "",
    3: "info",
    4: "danger",
  };
  return typeMap[status] || "";
};

const getOrderStatusText = (status: number) => {
  const textMap: Record<number, string> = {
    0: "待付款",
    1: "待发货",
    2: "待收货",
    3: "已完成",
    4: "已取消",
  };
  return textMap[status] || "";
};

const handleEdit = (row: any) => {
  ElMessage.info("编辑功能开发中");
};

const handleDelete = (row: any) => {
  ElMessageBox.confirm("确定要删除该商品吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    ElMessage.success("删除成功");
  });
};

const handleViewOrder = (row: any) => {
  ElMessage.info("查看订单功能开发中");
};

const handleDeliver = (row: any) => {
  ElMessageBox.confirm("确定要发货吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    ElMessage.success("发货成功");
  });
};

const handleSaveShop = () => {
  ElMessage.success("保存成功");
};

const handleSubmit = async () => {
  if (!productForm.name || !productForm.description) {
    ElMessage.warning("请填写完整信息");
    return;
  }

  if (productForm.price <= 0 || productForm.stock < 0) {
    ElMessage.warning("价格和库存必须大于 0");
    return;
  }

  // 先验证当前用户是否是卖家
  try {
    const userData = await getCurrentUser();
    if (!userData || userData.role !== "seller") {
      ElMessage.warning("您的账号还不是卖家，请先申请入驻");
      return;
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    ElMessage.error("获取用户信息失败，请重新登录");
    return;
  }

  submitting.value = true;

  try {
    // 构造提交数据
    const submitData = {
      name: productForm.name,
      description: productForm.description,
      price: productForm.price,
      stock: productForm.stock,
      categoryId: productForm.categoryId,
    };

    await createGoods(submitData);

    ElMessage.success("发布成功");
    showAddDialog.value = false;
    // 这里可以刷新商品列表
  } catch (error) {
    console.error("发布商品失败:", error);
    ElMessage.error("发布失败，请稍后重试");
  } finally {
    submitting.value = false;
  }
};

const resetForm = () => {
  productForm.name = "";
  productForm.description = "";
  productForm.price = 0;
  productForm.stock = 0;
  productForm.categoryId = 1;
};

// 处理发布商品
const handlePublish = async () => {
  if (!publishFormRef.value) return;

  await publishFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return;

    // 先验证当前用户是否是卖家
    try {
      const userData = await getCurrentUser();
      console.log("获取用户信息:", userData);
      if (!userData || userData.role !== "seller") {
        ElMessage.warning("您的账号还不是卖家，请先申请入驻");
        return;
      }
    } catch (error) {
      console.error("获取用户信息失败:", error);
      ElMessage.error("获取用户信息失败，请重新登录");
      return;
    }

    publishing.value = true;

    try {
      const submitData = {
        name: publishForm.name,
        description: publishForm.description,
        price: publishForm.price,
        stock: publishForm.stock,
        categoryId: publishForm.categoryId,
      };

      await createGoods(submitData);

      ElMessage.success("发布成功");
      resetPublishForm();
      // 切换到商品管理标签页
      activeTab.value = "products";
    } catch (error) {
      console.error("发布商品失败:", error);
      ElMessage.error("发布失败，请稍后重试");
    } finally {
      publishing.value = false;
    }
  });
};

// 重置发布表单
const resetPublishForm = () => {
  publishForm.name = "";
  publishForm.description = "";
  publishForm.price = 0;
  publishForm.stock = 0;
  publishForm.categoryId = 1;
  if (publishFormRef.value) {
    publishFormRef.value.clearValidate();
  }
};

onMounted(() => {
  // 简化版：使用模拟数据，不请求后端
  console.log("卖家中心已加载");

  // 刷新用户信息，确保角色是最新的
  refreshUserInfo();
});

// 刷新用户信息
const refreshUserInfo = async () => {
  try {
    const userData = await getCurrentUser();

    if (userData) {
      // 更新 store 中的用户信息
      userStore.setUserInfo(userData);
      console.log("用户信息已刷新:", userData);

      // 检查角色是否为卖家
      if (userData.role !== "seller") {
        ElMessage.warning("您的账号还不是卖家，请先申请入驻");
        // 可以重定向到申请页面
        // router.push('/seller/apply');
      }
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
  }
};
</script>

<style scoped lang="scss">
.seller-center-view {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.seller-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.tab-content {
  min-height: 400px;
}

.toolbar {
  margin-bottom: 20px;
}

.publish-form-container {
  padding: 20px 0;

  .form-tip {
    font-size: 12px;
    color: #999;
    margin-top: 8px;
  }
}
</style>
