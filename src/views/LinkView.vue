<template>
  <div class="page-container">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': isCollapsed }">
      <div class="logo">
        <img src="@/assets/logo02.svg" alt="TD导航" class="logo-img">
      </div>
      <el-menu class="sidebar-menu" background-color="transparent" text-color="#fff" active-text-color="#409EFF"
        :collapse="isCollapsed" @select="handleSelect">
        <el-menu-item index="featured">
          <el-icon>
            <House />
          </el-icon>
          <span>精选网站</span>
        </el-menu-item>
        <el-menu-item index="tools">
          <el-icon>
            <Tools />
          </el-icon>
          <span>实用工具</span>
        </el-menu-item>
        <el-menu-item index="ai">
          <el-icon>
            <Picture />
          </el-icon>
          <span>AI内容</span>
        </el-menu-item>
        <el-menu-item index="hax">
          <el-icon>
            <Monitor />
          </el-icon>
          <span>黑科技</span>
        </el-menu-item>
        <el-menu-item index="hot">
          <el-icon>
            <Document />
          </el-icon>
          <span>知名网站</span>
        </el-menu-item>
        <el-menu-item index="other">
          <el-icon>
            <Document />
          </el-icon>
          <span>其它</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 折叠按钮 -->
    <div class="collapse-btn" @click="toggleSidebar">
      <el-icon :size="20">
        <Fold v-if="!isCollapsed" />
        <Expand v-else />
      </el-icon>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content" :class="{ 'content-expanded': isCollapsed }">
      <div class="content-wrapper">
        <!-- 顶部导航栏 -->
        <div class="img-container">
          <el-image :src="topImg" class="top-img" fit="cover" @click="handleImageClick"></el-image>
        </div>

        <!-- 内容区域 -->
        <div class="content">
          <h3 class="section-title" id="featured">精选网站</h3>
          <el-row :gutter="20">
            <template v-for="(item, index) in websites" :key="index">
              <el-col v-if="item.isHot === 1" :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                <el-popover placement="top-start" :width="200" trigger="hover" :content="item.linkIntro">
                  <template #reference>
                    <el-card class="website-card" :body-style="{ padding: '0px' }" @click="goLink(item.linkUrl)">
                      <div class="card-content">
                        <el-image :src="imageUrl + item.linkImg" :alt="item.linkName" class="item-icon" />
                        <div class="item-info">
                          <h4>{{ item.linkName }}</h4>
                          <p>{{ item.linkIntro }}</p>
                        </div>
                        <div class="favorite-icon" @click.stop="addLinkStat(item.linkId)">
                          <el-icon>
                            <Star />
                          </el-icon>
                        </div>
                      </div>
                    </el-card>
                  </template>
                </el-popover>
              </el-col>
            </template>
          </el-row>

          <h3 class="section-title" id="featured">我的收藏</h3>
          <el-row :gutter="20">
            <template v-for="(item, index) in userStatLink" :key="index">
              <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                <el-popover placement="top-start" :width="200" trigger="hover" :content="item.linkIntro">
                  <template #reference>
                    <el-card class="website-card" :body-style="{ padding: '0px' }" @click="goLink(item.linkUrl)">
                      <div class="card-content">
                        <el-image :src="imageUrl + item.linkImg" :alt="item.linkName" class="item-icon" />
                        <div class="item-info">
                          <h4>{{ item.linkName }}</h4>
                          <p>{{ item.linkIntro }}</p>
                        </div>
                        <div class="favorite-icon" @click.stop="cancelLinkStat(item.linkId)">
                          <el-icon>
                            <Star />
                          </el-icon>
                        </div>
                      </div>
                    </el-card>
                  </template>
                </el-popover>
              </el-col>
            </template>
          </el-row>

          <h3 class="section-title" id="tools">实用工具</h3>
          <el-row :gutter="20">
            <template v-for="(item, index) in websites" :key="index">
              <el-col v-if="item.linkSort === 'tools'" :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                <el-popover placement="top-start" :width="200" trigger="hover" :content="item.linkIntro">
                  <template #reference>
                    <el-card class="website-card" :body-style="{ padding: '0px' }" @click="goLink(item.linkUrl)">
                      <div class="card-content">
                        <el-image :src="imageUrl + item.linkImg" :alt="item.linkName" class="item-icon" />
                        <div class="item-info">
                          <h4>{{ item.linkName }}</h4>
                          <p>{{ item.linkIntro }}</p>
                        </div>
                        <div class="favorite-icon" @click.stop="addLinkStat(item.linkId)">
                          <el-icon>
                            <Star />
                          </el-icon>
                        </div>
                      </div>
                    </el-card>
                  </template>
                </el-popover>
              </el-col>
            </template>
          </el-row>

          <h3 class="section-title" id="ai">AI内容</h3>
          <el-row :gutter="20">
            <template v-for="(item, index) in websites" :key="index">
              <el-col v-if="item.linkSort === 'ai'" :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                <el-popover placement="top-start" :width="200" trigger="hover" :content="item.linkIntro">
                  <template #reference>
                    <el-card class="website-card" :body-style="{ padding: '0px' }" @click="goLink(item.linkUrl)">
                      <div class="card-content">
                        <el-image :src="imageUrl + item.linkImg" :alt="item.linkName" class="item-icon" />
                        <div class="item-info">
                          <h4>{{ item.linkName }}</h4>
                          <p>{{ item.linkIntro }}</p>
                        </div>
                        <div class="favorite-icon" @click.stop="addLinkStat(item.linkId)">
                          <el-icon>
                            <Star />
                          </el-icon>
                        </div>
                      </div>
                    </el-card>
                  </template>
                </el-popover>
              </el-col>
            </template>
          </el-row>

          <h3 class="section-title" id="hax">黑科技</h3>
          <el-row :gutter="20">
            <template v-for="(item, index) in websites" :key="index">
              <el-col v-if="item.linkSort === 'hax'" :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                <el-popover placement="top-start" :width="200" trigger="hover" :content="item.linkIntro">
                  <template #reference>
                    <el-card class="website-card" :body-style="{ padding: '0px' }" @click="goLink(item.linkUrl)">
                      <div class="card-content">
                        <el-image :src="imageUrl + item.linkImg" :alt="item.linkName" class="item-icon" />
                        <div class="item-info">
                          <h4>{{ item.linkName }}</h4>
                          <p>{{ item.linkIntro }}</p>
                        </div>
                        <div class="favorite-icon" @click.stop="addLinkStat(item.linkId)">
                          <el-icon>
                            <Star />
                          </el-icon>
                        </div>
                      </div>
                    </el-card>
                  </template>
                </el-popover>
              </el-col>
            </template>
          </el-row>

          <h3 class="section-title" id="hot">知名网站</h3>
          <el-row :gutter="20">
            <template v-for="(item, index) in websites" :key="index">
              <el-col v-if="item.linkSort === 'hot'" :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                <el-popover placement="top-start" :width="200" trigger="hover" :content="item.linkIntro">
                  <template #reference>
                    <el-card class="website-card" :body-style="{ padding: '0px' }" @click="goLink(item.linkUrl)">
                      <div class="card-content">
                        <el-image :src="imageUrl + item.linkImg" :alt="item.linkName" class="item-icon" />
                        <div class="item-info">
                          <h4>{{ item.linkName }}</h4>
                          <p>{{ item.linkIntro }}</p>
                        </div>
                        <div class="favorite-icon" @click.stop="addLinkStat(item.linkId)">
                          <el-icon>
                            <Star />
                          </el-icon>
                        </div>
                      </div>
                    </el-card>
                  </template>
                </el-popover>
              </el-col>
            </template>
          </el-row>

          <h3 class="section-title" id="other">其它</h3>
          <el-row :gutter="20">
            <template v-for="(item, index) in websites" :key="index">
              <el-col v-if="item.linkSort === 'other'" :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                <el-popover placement="top-start" :width="200" trigger="hover" :content="item.linkIntro">
                  <template #reference>
                    <el-card class="website-card" :body-style="{ padding: '0px' }" @click="goLink(item.linkUrl)">
                      <div class="card-content">
                        <el-image :src="imageUrl + item.linkImg" :alt="item.linkName" class="item-icon" />
                        <div class="item-info">
                          <h4>{{ item.linkName }}</h4>
                          <p>{{ item.linkIntro }}</p>
                        </div>
                        <div class="favorite-icon" @click.stop="addLinkStat(item.linkId)">
                          <el-icon>
                            <Star />
                          </el-icon>
                        </div>
                      </div>
                    </el-card>
                  </template>
                </el-popover>
              </el-col>
            </template>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { addLinkStatById, cancelLinkStatById, getLinkList, getTopImgUrl, selectStatLink } from "../../api/BSideApi";
import { Search, House, Tools, Picture, Monitor, Document, Fold, Expand, Star } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import {ElNotification} from "element-plus";


const isCollapsed = ref(false)
const isMobile = ref(false)
const imageUrl = ref('')

const router = useRouter()

// 切换侧边栏
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const topImg = ref('')
async function getTopImg() {
  topImg.value = await getTopImgUrl();
}

// 检查是否为移动设备
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
  isCollapsed.value = isMobile.value
}

async function addLinkStat(id) {
  const v = await addLinkStatById(id)
  if (!v) {
    ElNotification({
      title: "收藏成功",
      message: '可以刷新页面查看最新',
      type: "success",
    });
  }
}

// 监听窗口大小变化
onMounted(async () => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  imageUrl.value = process.env.VUE_APP_IMAGE;
  await LinkList()
  await getTopImg();
  await getUserStatLink();
})

async function LinkList() {
  websites.value = await getLinkList();
  websites.value = websites.value.filter(item => item.isPublic !== 0);
  console.log(websites.value)
}
function goLink(url) {
  window.open(url, '_blank')
}

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})

const websites = ref([]);

// 添加处理菜单选择的方法
const handleSelect = (index) => {
  const element = document.getElementById(index);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  }
}

async function cancelLinkStat(id) {
  const v = await cancelLinkStatById(id);
  if (!v) {
    ElNotification({
      title: "取消收藏成功",
      message: '可以刷新页面查看最新',
      type: "success",
    });
  }
}
const userStatLink = ref([]);

async function getUserStatLink() {
  userStatLink.value = await selectStatLink();
  console.log(userStatLink)
}

// 添加图片点击处理函数
const handleImageClick = () => {
  router.push('/apply_view')
}
</script>

<style scoped>
.page-container {
  display: flex;
  min-height: 100vh;
  position: relative;
}

.sidebar {
  width: 200px;
  background-color: rgba(0, 0, 0, 0.2);
  padding: 20px 0;
  transition: all 0.3s ease;
  position: fixed;
  height: 100vh;
  z-index: 1000;
}

.sidebar-collapsed {
  width: 64px;
  padding: 20px 0;
}

.sidebar-collapsed .logo {
  padding: 0 12px;
}

.main-content {
  flex: 1;
  padding: 30px;
  margin-left: 200px;
  transition: all 0.3s ease;
  height: 100vh;
  overflow-y: auto;
}

.content-expanded {
  margin-left: 64px;
}

.collapse-btn {
  position: fixed;
  left: 200px;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.2);
  color: white;
  width: 24px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 0 4px 4px 0;
  transition: all 0.3s ease;
  z-index: 1000;
}

.sidebar-collapsed+.collapse-btn {
  left: 64px;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }

  .sidebar-collapsed {
    transform: translateX(0);
  }

  .main-content {
    margin-left: 0;
  }

  .content-expanded {
    margin-left: 0;
  }

  .collapse-btn {
    left: 0;
  }

  .sidebar-collapsed+.collapse-btn {
    left: 64px;
  }
}

.logo {
  padding: 0 20px;
  margin-bottom: 20px;
}

.logo-img {
  max-width: 100%;
  height: auto;
}

.sidebar-menu {
  border-right: none;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.nav-bar {
  margin-bottom: 30px;
}

.search-input {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.1);
}

:deep(.el-input__inner) {
  color: white;
}

.menu-bar {
  border: none !important;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}

:deep(.el-menu-item) {
  border-radius: 20px;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.section-title {
  color: #fff;
  margin-bottom: 20px;
  font-size: 24px;
}

.website-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
  background-color: var(--bgColor2);
}

.website-card:hover {
  transform: translateY(-5px);
}

.card-content {
  padding: 15px;
  display: flex;
  align-items: center;
  position: relative;
}

.item-icon {
  width: 40px;
  height: 40px;
  margin-right: 15px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-info h4 {
  margin: 0;
  font-size: 16px;
  color: var(--textColor2);
}

.item-info p {
  margin: 5px 0 0;
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.favorite-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
  color: #ffd700;
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  padding: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.favorite-icon:hover {
  background-color: rgba(0, 0, 0, 0.2);
}

.website-card:hover .favorite-icon {
  opacity: 1;
}

.content {
  padding-bottom: 150px;
}

.img-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.top-img {
  width: 100%;
  height: 250px;
  border-radius: 10px;
  max-width: 700px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.top-img:hover {
  transform: scale(1.02);
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px 50px;
}

/* 响应式布局调整 */
@media screen and (max-width: 1600px) {
  .content-wrapper {
    max-width: 1200px;
  }
}

@media screen and (max-width: 1200px) {
  .content-wrapper {
    max-width: 1000px;
  }
}

@media screen and (max-width: 768px) {
  .content-wrapper {
    max-width: 100%;
    padding: 0 10px;
  }
}
</style>