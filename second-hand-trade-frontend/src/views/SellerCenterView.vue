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

            <div class="pagination-container">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :total="total"
                layout="total, prev, pager, next, jumper"
                @current-change="handlePageChange"
              />
            </div>
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
                    :file-list="imageFileList"
                    :on-change="handleImageChange"
                    :on-remove="handleImageRemove"
                    accept="image/png, image/jpeg, image/jpg"
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
            <div class="toolbar">
              <el-select
                v-model="orderStatusFilter"
                placeholder="订单状态"
                style="width: 150px"
              >
                <el-option label="全部" :value="undefined" />
                <el-option label="待付款" :value="0" />
                <el-option label="待发货" :value="1" />
                <el-option label="待收货" :value="2" />
                <el-option label="已完成" :value="3" />
                <el-option label="已取消" :value="4" />
              </el-select>
            </div>
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
              <el-table-column label="操作" width="200" fixed="right">
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
            <div class="pagination-container">
              <el-pagination
                v-model:current-page="orderCurrentPage"
                v-model:page-size="orderPageSize"
                :total="orderTotal"
                layout="total, prev, pager, next, jumper"
                @current-change="handleOrderPageChange"
              />
            </div>
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

    <!-- 发布/编辑商品对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEditMode ? '编辑商品' : '发布商品'"
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
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number
            v-model="productForm.stock"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select
            v-model="productForm.categoryId"
            placeholder="请选择分类"
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
            :file-list="dialogImageFileList"
            :on-change="handleDialogImageChange"
            :on-remove="handleDialogImageRemove"
            accept="image/png, image/jpeg, image/jpg"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">
            请上传图片，支持 jpg/png 格式，大小不超过 2MB
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEditMode ? "保存" : "发布" }}
        </el-button>
      </template>
    </el-dialog>
    <!-- 发货对话框 -->
    <el-dialog v-model="showDeliverDialog" title="发货" width="500px">
      <el-form :model="deliverForm" label-width="100px">
        <el-form-item label="配送方式" required>
          <el-select
            v-model="deliverForm.deliveryType"
            placeholder="请选择配送方式"
          >
            <el-option label="快递配送" value="express" />
            <el-option label="门店自提" value="pickup" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" required>
          <el-input
            v-model="deliverForm.deliveryNo"
            placeholder="请输入物流单号"
            maxlength="50"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDeliverDialog = false">取消</el-button>
        <el-button
          type="primary"
          :loading="delivering"
          @click="handleSubmitDeliver"
        >
          确认发货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import NavBar from "@/components/NavBar.vue";
import {
  createGoods,
  getSellerGoodsList,
  deleteGoods,
  updateGoods,
} from "@/api/modules/goods";
import { getSellerOrderList, shipOrder } from "@/api/modules/order";
import { getCurrentUser } from "@/api/modules/auth";
import { useUserStore } from "@/store/user";
import type { Goods } from "@/types";
import type { Order } from "@/api/modules/order";

const userStore = useUserStore();

const activeTab = ref("products");
const loading = ref(false);
const submitting = ref(false);
const showAddDialog = ref(false);
const isEditMode = ref(false);
const editingProductId = ref<number | null>(null);

// 商品列表
const productList = ref<Goods[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取卖家商品列表
const fetchSellerProducts = async () => {
  loading.value = true;
  try {
    const res = await getSellerGoodsList({
      current: currentPage.value,
      size: pageSize.value,
    });
    if (res) {
      productList.value = res.records;
      total.value = res.total;
    }
  } catch (error) {
    console.error("获取商品列表失败:", error);
    ElMessage.error("获取商品列表失败");
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchSellerProducts();
};

// 订单列表
const orderList = ref<Order[]>([]);
const orderCurrentPage = ref(1);
const orderPageSize = ref(10);
const orderTotal = ref(0);
const orderStatusFilter = ref<number | undefined>(undefined);

// 发货相关
const showDeliverDialog = ref(false);
const delivering = ref(false);
const deliveringOrderId = ref<number | null>(null);
const deliverForm = reactive({
  deliveryType: "express",
  deliveryNo: "",
});

// 获取卖家订单列表
const fetchSellerOrders = async () => {
  loading.value = true;
  try {
    const res = await getSellerOrderList({
      status: orderStatusFilter.value,
      current: orderCurrentPage.value,
      size: orderPageSize.value,
    });
    if (res) {
      orderList.value = res.records;
      orderTotal.value = res.total;
    }
  } catch (error) {
    console.error("获取订单列表失败:", error);
    ElMessage.error("获取订单列表失败");
  } finally {
    loading.value = false;
  }
};

const handleOrderPageChange = (page: number) => {
  orderCurrentPage.value = page;
  fetchSellerOrders();
};

// 监听订单状态筛选变化
watch(orderStatusFilter, () => {
  orderCurrentPage.value = 1;
  fetchSellerOrders();
});

// 监听标签切换
watch(activeTab, (newTab) => {
  if (newTab === "orders") {
    fetchSellerOrders();
  } else if (newTab === "products") {
    fetchSellerProducts();
  }
});

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
  imageUrl: "",
});

// 对话框图片文件列表
const dialogImageFileList = ref<any[]>([]);

// 发布商品表单
const publishForm = reactive({
  name: "",
  description: "",
  price: 0,
  stock: 0,
  categoryId: 1,
  imageUrl: "",
});

// 图片文件列表
const imageFileList = ref<any[]>([]);

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
  isEditMode.value = true;
  editingProductId.value = row.id;
  productForm.name = row.name;
  productForm.description = row.description;
  productForm.price = row.price;
  productForm.stock = row.stock;
  productForm.categoryId = row.categoryId;
  productForm.imageUrl = row.imageUrl || "";
  if (row.imageUrl) {
    dialogImageFileList.value = [
      {
        name: "product-image",
        url: row.imageUrl,
      },
    ];
  } else {
    dialogImageFileList.value = [];
  }
  showAddDialog.value = true;
};

// 处理对话框图片变化
const handleDialogImageChange = (file: any) => {
  const maxSize = 2 * 1024 * 1024;
  if (file.size > maxSize) {
    ElMessage.error("图片大小不能超过 2MB");
    return;
  }

  const reader = new FileReader();
  reader.onload = (e: any) => {
    productForm.imageUrl = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

// 处理对话框图片移除
const handleDialogImageRemove = () => {
  productForm.imageUrl = "";
};

const handleDelete = (row: any) => {
  ElMessageBox.confirm("确定要删除该商品吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await deleteGoods(row.id);
      ElMessage.success("删除成功");
      fetchSellerProducts();
    } catch (error) {
      console.error("删除商品失败:", error);
      ElMessage.error("删除失败");
    }
  });
};

const handleViewOrder = (row: any) => {
  ElMessage.info("查看订单功能开发中");
};

const handleDeliver = (row: any) => {
  deliveringOrderId.value = row.id;
  deliverForm.deliveryType = "express";
  deliverForm.deliveryNo = "";
  showDeliverDialog.value = true;
};

const handleSubmitDeliver = async () => {
  if (!deliverForm.deliveryType || !deliverForm.deliveryNo) {
    ElMessage.warning("请填写完整的发货信息");
    return;
  }

  delivering.value = true;
  try {
    if (deliveringOrderId.value) {
      await shipOrder(
        deliveringOrderId.value,
        deliverForm.deliveryNo,
        deliverForm.deliveryType,
      );
      ElMessage.success("发货成功");
      showDeliverDialog.value = false;
      fetchSellerOrders();
    }
  } catch (error) {
    console.error("发货失败:", error);
    ElMessage.error("发货失败，请重试");
  } finally {
    delivering.value = false;
  }
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
      imageUrl: productForm.imageUrl,
    };

    if (isEditMode.value && editingProductId.value) {
      await updateGoods(editingProductId.value, submitData);
      ElMessage.success("更新成功");
    } else {
      await createGoods(submitData);
      ElMessage.success("发布成功");
    }

    showAddDialog.value = false;
    // 刷新商品列表
    fetchSellerProducts();
  } catch (error) {
    console.error(isEditMode.value ? "更新商品失败:" : "发布商品失败:", error);
    ElMessage.error(
      isEditMode.value ? "更新失败，请稍后重试" : "发布失败，请稍后重试",
    );
  } finally {
    submitting.value = false;
  }
};

const resetForm = () => {
  isEditMode.value = false;
  editingProductId.value = null;
  productForm.name = "";
  productForm.description = "";
  productForm.price = 0;
  productForm.stock = 0;
  productForm.categoryId = 1;
  productForm.imageUrl = "";
  dialogImageFileList.value = [];
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
        imageUrl: publishForm.imageUrl,
      };

      await createGoods(submitData);

      ElMessage.success("发布成功");
      resetPublishForm();
      // 切换到商品管理标签页
      activeTab.value = "products";
      fetchSellerProducts();
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
  publishForm.imageUrl = "";
  imageFileList.value = [];
  if (publishFormRef.value) {
    publishFormRef.value.clearValidate();
  }
};

// 处理图片变化
const handleImageChange = (file: any) => {
  // 验证文件大小（2MB）
  const maxSize = 2 * 1024 * 1024;
  if (file.size > maxSize) {
    ElMessage.error("图片大小不能超过 2MB");
    return;
  }

  // 转换为 Base64
  const reader = new FileReader();
  reader.onload = (e: any) => {
    publishForm.imageUrl = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

// 处理图片移除
const handleImageRemove = () => {
  publishForm.imageUrl = "";
};

onMounted(() => {
  console.log("卖家中心已加载");
  // 刷新用户信息，确保角色是最新的
  refreshUserInfo();
  // 获取商品列表
  fetchSellerProducts();
  // 获取订单列表
  fetchSellerOrders();
});

// 刷新用户信息
const refreshUserInfo = async () => {
  try {
    const userData = await getCurrentUser();

    if (userData) {
      // 更新 store 中的用户信息
      userStore.setUserInfo(userData);
      console.log("用户信息已刷新:", userData);

      // 回显店铺设置表单
      shopForm.shopName = userData.shopName || "";
      shopForm.shopDescription = userData.shopDescription || "";

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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.publish-form-container {
  padding: 20px 0;

  .form-tip {
    font-size: 12px;
    color: #999;
    margin-top: 8px;
  }
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
</style>
