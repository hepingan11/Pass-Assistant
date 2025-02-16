<template>
  <div class="body">
    <div class="upload">
      <!-- 上传组件 -->
      <el-upload
          class="upload-demo"
          drag
          multiple
          ref="uploadRef"
          :action="null"
          :on-remove="removeImg"
        :auto-upload="false"
        :on-change="handleChange"
        :file-list="fileList"
        list-type="picture-card"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽到此 或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip" style="margin-top: 57px">
            只能上传图片且小于10MB
          </div>
        </template>
      </el-upload>

      <!-- 上传按钮 -->
      <el-button @click="handleUpload" color="var(--themeColor1)" style="margin-top: 40px">上传所有图片</el-button>
    </div>
    <div class="imgList">
      <div class="container">
        <el-row :gutter="20">
          <el-col 
            :xs="12" 
            :sm="12" 
            :md="8" 
            :lg="8" 
            v-for="item in imgList" 
            :key="item" 
            class="img-card"
          >
            <el-card :body-style="{ padding: '0px' }" shadow="hover">
              <div class="img-wrapper">
                <el-image 
                  :src="imgLink+item.url" 
                  class="img-item" 
                  fit="cover"
                  lazy 
                  :preview-src-list="[imgLink+item.url]"
                  :initial-index="0"
                  preview-teleported
                />
              </div>
              <div class="img-actions">
                <el-tooltip
                    content="复制链接"
                    placement="top"
                    effect="light"
                >
                  <el-button type="primary" circle @click="copyUrl(item.url)">
                    <el-icon><Document /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip
                  :content="item.isPublic ? '设置为私有图片' : '设置为公共图片'"
                  placement="top"
                  effect="light"
                >
                  <el-button 
                    :type="item.isPublic ? 'success' : 'info'" 
                    circle 
                    @click="togglePublic(item.photoId)"
                  >
                    <el-icon>
                      <Lock v-if="!item.isPublic"/>
                      <Unlock v-else/>
                    </el-icon>
                  </el-button>
                </el-tooltip>
                <el-button type="danger" circle @click="deleteImg(item.photoId)">
                  <el-icon><DeleteFilled /></el-icon>
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
    <p style="margin-top: 200px;color: #7c7c7c;font-size: 10px">图床由阿里云oss支持，防刷所以一个人最多只能存储20张~</p>

    <!-- 添加分隔线和标题 -->
    <div class="section-divider">
      <h2>公共图片展示</h2>
    </div>
    
    <!-- 公共图片列表 -->
    <div class="public-imgList">
      <div class="container">
        <el-row :gutter="20">
          <el-col 
            :xs="8" 
            :sm="8" 
            :md="6" 
            :lg="4" 
            v-for="item in allImgList" 
            :key="item" 
            class="public-img-card"
          >
            <el-card :body-style="{ padding: '0px' }" shadow="hover">
              <div class="square-wrapper">
                <el-image 
                  :src="imgLink+item.url" 
                  class="square-img" 
                  fit="cover"
                  lazy 
                  :preview-src-list="[imgLink+item.url]"
                  :initial-index="0"
                  preview-teleported
                />
              </div>
              <div class="img-actions">
                <el-button type="primary" circle @click="copyUrl(item.url)">
                  <el-icon><Document /></el-icon>
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
  <el-dialog v-model="dialog" title="确认删除？" width="400">
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialog = false">Cancel</el-button>
        <el-button type="primary" @click="deleteImgReal" color="var(--themeColor1)">
          Yes
        </el-button>
      </div>
    </template>
  </el-dialog>
  <LoginDialog :show="loginVisible" @close="loginVisible = false"/>
</template>

<script setup>
import {ElLoading, ElMessage, ElNotification} from "element-plus";
import {onMounted, ref} from "vue";
import {DeleteImgById, GetUserImgList, GetAllImgList, UploadFile, TogglePhotoPublic} from "../../api/BSideApi";
import LoginDialog from "@/components/LoginDialog.vue";
import router from "@/router";
import { Lock, Unlock } from '@element-plus/icons-vue'

const fileList = ref([])
const uploadRef = ref(null)
const MAX_FILE_SIZE = 10 * 1024 * 1024;
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

// 处理文件选择事件
const handleChange = (file) => {
  // 检查文件类型
  if (!ALLOWED_TYPES.includes(file.raw.type)) {
    ElMessage.warning(`文件 ${file.name} 不是支持的图片格式！请上传 JPG、PNG、GIF 或 WEBP 格式的图片`);
    return;
  }
  
  if (file.size > MAX_FILE_SIZE) {
    ElMessage.warning(`文件 ${file.name} 大小超过限制，请选择小于10MB的图片！`);
    return;
  }
  
  if (fileList.value.length === 5) {
    ElMessage.warning("一次性最多只能上传5张(虽然显示还可以上传其实不行了)");
    return;
  }
  
  fileList.value.push(file);
}
let loginVisible = ref(false);
// 处理上传逻辑
const handleUpload = async () => {
  const loading = ElLoading.service({
    text: '正在上传...',
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  try {
    // 假设这里有一个上传接口
    // 模拟上传请求
    const formData = new FormData();
    // 添加自定义参数到 FormData
    fileList.value.forEach((file) => {
      formData.append('files', file.raw, file.name)
    })
    await UploadFile(formData)

    ElMessage.success('上传成功')
    fileList.value = []  // 清空已上传的文件列表
    setTimeout(() => {
      router.go(0);
    }, 1000);
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    loading.close()
  }
  
}
const imgList = ref([])
const allImgList = ref([])
const dialog = ref(false)
const imgId = ref(null)
function deleteImg(id){
  imgId.value = id
  dialog.value = true
}

async function deleteImgReal() {
  await DeleteImgById(imgId.value);
  dialog.value = false
  ElMessage.success("删除成功")
  setTimeout(() => {
    router.go(0);
  }, 1000);
}
const removeImg = (file) =>{
  fileList.value.filter((v) => v === file)
}

const imgLink = ref('')

function copyUrl(url) {
  const v = imgLink.value+url;
  navigator.clipboard.writeText(v);
  ElNotification({
    message: "复制链接成功",
    type: "success",
  });
}

async function togglePublic(photoId) {
  await TogglePhotoPublic(photoId);
  ElMessage.success("修改成功");
  setTimeout(() => {
    router.go(0);  // 刷新页面获取最新状态
  }, 1000);
}

onMounted(async () => {
  imgList.value = await GetUserImgList();
  imgLink.value = process.env.VUE_APP_IMAGE;
  allImgList.value = await GetAllImgList();
})
</script>

<style lang="scss" scoped>

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

.body{
  margin: 0;
  background-color: var(--bgColor2);
  height: 100%;
  text-align: center;
  animation: explainAnimation 0.3s;
  box-sizing: border-box;
  overflow-y: auto;
}

.imgList {
  margin-top: 50px;
  padding: 0 20px;
}

.container {
  max-width: 1600px;  // 整体容器最大宽度
  margin: 0 auto;     // 容器居中
}

.img-card {
  margin-bottom: 20px;
  
  .el-card {
    max-width: 500px;  // 设置卡片最大宽度
    margin: 0 auto;    // 卡片居中
  }
}

.img-wrapper {
  cursor: pointer;
}

.img-item {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: all 0.3s;
}

.img-actions {
  padding: 12px;
  display: flex;
  justify-content: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.9);
  
  .el-button {
    &.el-button--success {
      background-color: #67c23a;
    }
    &.el-button--info {
      background-color: #909399;
    }
  }
}

.el-card:hover .img-item {
  transform: scale(1.02);
}

.section-divider {
  margin: 50px 0 30px;
  padding: 0 20px;
  
  h2 {
    color: var(--themeColor1);
    font-size: 24px;
    font-weight: 500;
    text-align: center;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: -10px;
      left: 50%;
      transform: translateX(-50%);
      width: 50px;
      height: 3px;
      background-color: var(--themeColor1);
      border-radius: 3px;
    }
  }
}

// 优化滚动加载性能
.imgList {
  margin-top: 50px;
  padding: 0 20px;
  
  img {
    will-change: transform;
  }
}

.public-imgList {
  margin-top: 50px;
  padding: 0 20px;
  
  .container {
    max-width: 1400px;
    margin: 0 auto;
  }
}

.public-img-card {
  margin-bottom: 20px;
  
  .el-card {
    max-width: 200px;
    margin: 0 auto;
  }
}

.square-wrapper {
  position: relative;
  width: 100%;
  padding-bottom: 100%; // 创建正方形容器
  overflow: hidden;
}

.square-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
  
  &:hover {
    transform: scale(1.05);
  }
}

// 响应式调整
@media screen and (max-width: 1400px) {
  .public-img-card .el-card {
    max-width: 180px;
  }
}

@media screen and (max-width: 1200px) {
  .public-img-card .el-card {
    max-width: 160px;
  }
}

@media screen and (max-width: 768px) {
  .public-img-card .el-card {
    max-width: 140px;
  }
}
</style>