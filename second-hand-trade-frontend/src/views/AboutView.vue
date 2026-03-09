<template>
  <div class="about-view">
    <NavBar />

    <div class="container">
      <h2 class="page-title">关于我们</h2>

      <div class="about-content">
        <section class="intro-section">
          <h3>平台介绍</h3>
          <p>
            豫见好物是一个专注于河南文创产品销售的平台，致力于推广和传承河南丰富的文化遗产。
            我们提供传统工艺品、文化创意产品、非遗传承制品等多种文创商品，
            让传统文化与现代生活完美融合。
          </p>
        </section>

        <section class="mission-section">
          <h3>我们的使命</h3>
          <ul>
            <li>传承和弘扬河南优秀传统文化</li>
            <li>为文创产品提供展示和销售平台</li>
            <li>连接文创匠人与消费者</li>
            <li>推动文化创意产业发展</li>
          </ul>
        </section>

        <section class="contact-section">
          <h3>联系我们</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="平台名称"
              >豫见好物</el-descriptions-item
            >
            <el-descriptions-item label="定位"
              >河南文创产品销售平台</el-descriptions-item
            >
            <el-descriptions-item label="服务范围">全国</el-descriptions-item>
          </el-descriptions>
        </section>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import NavBar from "@/components/NavBar.vue";
import Footer from "@/components/Footer.vue";
</script>

<style scoped lang="scss">
.about-view {
  min-height: 100vh;
  background: #f5f5f5;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 40px;
  color: #333;
}

.about-content {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

section {
  margin-bottom: 30px;

  h3 {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 15px;
    border-left: 4px solid #409eff;
    padding-left: 10px;
  }

  p {
    font-size: 16px;
    line-height: 1.8;
    color: #666;
    text-indent: 2em;
  }

  ul {
    list-style: none;
    padding: 0;

    li {
      font-size: 16px;
      line-height: 2;
      color: #666;
      padding-left: 20px;
      position: relative;

      &::before {
        content: "•";
        position: absolute;
        left: 0;
        color: #409eff;
        font-size: 20px;
      }
    }
  }
}
</style>
