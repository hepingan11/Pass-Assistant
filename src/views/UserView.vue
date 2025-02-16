<template>
  <div class="body">
    <div class="user-container">
      <div class="user-card">
        <!-- 头像和用户名部分 -->
        <div class="user-header">
          <el-upload
            class="avatar-uploader"
            :action="null"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleAvatarChange"
            accept="image/*"
          >
            <div class="avatar-container">
              <el-avatar shape="circle" :size="120" class="user-avatar">
                <el-image :src="imageUrl+userinfo.avatar" fit="cover">
                  <template #error>
                    <div class="avatar-placeholder">
                      <el-icon><UserFilled /></el-icon>
                    </div>
                  </template>
                </el-image>
              </el-avatar>
              <div class="avatar-hover-mask">
                <el-icon><Camera /></el-icon>
                <span>更换头像</span>
              </div>
              <div v-if="uploading" class="upload-loading">
                <el-icon class="rotating"><Loading /></el-icon>
                <span>上传中...</span>
              </div>
            </div>
          </el-upload>
          
          <div class="user-name-section">
            <div v-if="editNameStaus === 1" class="edit-name">
              <el-input 
                v-model="userinfo.userName" 
                class="name-input"
                placeholder="请输入新昵称" 
              />
              <el-button type="primary" @click="savaName(userinfo.userName)" class="save-btn">
                保存
              </el-button>
            </div>
            <div v-else class="display-name">
              <h2>{{userinfo.userName}}</h2>
              <el-button @click="setNameStaus" class="edit-btn" color="var(--themeColor1)">
                <el-icon :size="16"><Edit /></el-icon>
                修改昵称
              </el-button>
            </div>
          </div>
        </div>

        <!-- IT币信息 -->
        <div class="balance-section">
          <div class="balance-card">
            <div class="balance-header">
              <h3>剩余IT币</h3>
              <el-button
                class="refresh-btn"
                :icon="Refresh"
                circle
                @click="getUser"
                :loading="isRefreshing"
              />
            </div>
            <div class="balance-amount">{{userinfo.frequency}}</div>
          </div>
        </div>

        <!-- 功能按钮组 -->
        <div class="action-buttons">
          <el-button @click="router().push({ path: '/purchase' })" type="primary" class="action-btn">
            <el-icon><Plus /></el-icon>充值
          </el-button>
          <el-button @click="router().push({ path: '/Collection' })" type="primary" class="action-btn">
            <el-icon><Star /></el-icon>我的收藏
          </el-button>
          <el-button @click="getImageList" type="primary" class="action-btn">
            <el-icon><Picture /></el-icon>我的绘画
          </el-button>
        </div>

        <!-- 绘画展示区 -->
        <div v-if="showParam === 1" class="gallery-section">
          <h3 style="color: var(--textColor1);">我的绘画作品</h3>
          <el-scrollbar height="400px">
            <div class="image-grid">
              <div v-for="item in imageList" :key="item" class="image-item hover-scale">
                <el-image 
                  :src="imageUrl+item.generateUrl" 
                  fit="cover" 
                  :preview-src-list="[imageUrl+item.generateUrl]"
                  class="gallery-image"
                />
              </div>
            </div>
          </el-scrollbar>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import store from "@/store";
import {h, onMounted, ref} from "vue";
import {getUserDraw, GetUserInfo, updateUserName, UpdateUserAvatar} from "../../api/BSideApi";
import router from "@/router";
import {ElNotification, ElMessage} from "element-plus";
import { Refresh, UserFilled, Camera, Loading } from '@element-plus/icons-vue'
import { set } from "nprogress";

export default {
  name: `UserView`,
  methods: {
    router() {
      return router
    }
  },
  computed: {
    store() {
      return store
    }
  },
  setup() {
    const userinfo = ref({
        'userName': "用户",
        "avatar": "",
        "frequency": ""
    });

    const imageList = ref([])

    const showParam = ref(0);

    const imageUrl = ref('');

    const editNameStaus = ref(0)

    const isRefreshing = ref(false)

    const uploading = ref(false)

    onMounted(() => {
      imageUrl.value = process.env.VUE_APP_IMAGE;
      getUser()
    })

    async function getUser() {
      isRefreshing.value = true
      try {
        const res = await GetUserInfo()
        store.commit("setUserinfo", res)
        if(res.avatar != null) {
          userinfo.value.avatar = res.avatar
        }
        if (res.userName != null) {
          userinfo.value.userName = res.userName
        }
        userinfo.value.frequency = res.frequency
      } finally {
        isRefreshing.value = false
      }
    }

    async function getImageList() {
      imageList.value= await getUserDraw();
      showParam.value = 1;
    }

    function setNameStaus() {
      editNameStaus.value=1;
    }

    async function savaName(userName) {
      await updateUserName(userName)
      editNameStaus.value = 0;
      ElNotification({
        title: '修改成功',
        message: h('i', {style: 'color: teal'}, 'Success'),
      })
    }

    const handleAvatarChange = async (file) => {
      if (!['image/jpeg', 'image/png', 'image/gif'].includes(file.raw.type)) {
        ElMessage.error('请上传图片文件！')
        return
      }
      
      if (file.size / 1024 / 1024 > 2) {
        ElMessage.error('图片大小不能超过2MB！')
        return
      }

      uploading.value = true
      try {
        const formData = new FormData()
        formData.append('avatar', file.raw)
        
        const res = await UpdateUserAvatar(formData)
        if (!res) {
          ElMessage.success('头像更新成功')
          store.commit('setUserinfo', {
            ...store.state.userinfo,
            avatar: res
          })
          setTimeout(() => {
            getUser()
          }, 500)
        } else {
          ElMessage.error('上传失败，请重试')
        }
      } catch (error) {
        ElMessage.error('上传失败，请重试:'+error)
      } finally {
        uploading.value = false
      }
    }

    return {
      userinfo,
      imageUrl,
      showParam,
      imageList,
      getImageList,
      editNameStaus,
      savaName,
      setNameStaus,
      isRefreshing,
      getUser,
      handleAvatarChange,
      uploading
    }
  }
}
</script>

<style lang="scss" scoped>
.body {
  background-color: var(--bgColor2);
  min-height: 100vh;
  padding: 20px;
}

.user-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.user-card {
  background: var(--bgColor1);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.user-header {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 30px;
}

.hover-scale {
  transition: transform 0.3s ease;
  &:hover {
    transform: scale(1.05);
  }
}

.user-name-section {
  flex: 1;
  .edit-name {
    display: flex;
    gap: 10px;
    align-items: center;
    .name-input {
      max-width: 200px;
    }
  }
  .display-name {
    h2 {
      margin: 0 0 10px 0;
      color: var(--textColor1);
    }
  }
}

.balance-section {
  margin-bottom: 30px;
  .balance-card {
    background: linear-gradient(135deg, var(--themeColor2), #4a90e2);
    border-radius: 12px;
    padding: 20px;
    color: white;

    .balance-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;

      h3 {
        margin: 0;
        opacity: 0.9;
      }

      .refresh-btn {
        background: rgba(255, 255, 255, 0.2);
        border: none;
        color: white;
        transition: all 0.3s ease;
        
        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: rotate(180deg);
        }
        
        &:active {
          background: rgba(255, 255, 255, 0.4);
        }
      }
    }

    .balance-amount {
      font-size: 32px;
      font-weight: bold;
    }
  }
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  
  .action-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: var(--themeColor2);
    transition: transform 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
    }
  }
}

.gallery-section {
  h3 {
    margin-bottom: 20px;
    color: var(--el-text-color-primary);
  }
  
  .image-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
    padding: 10px;
  }
  
  .image-item {
    border-radius: 8px;
    overflow: hidden;
    transition: transform 0.3s ease;
    
    &:hover {
      transform: scale(1.03);
    }
  }
  
  .gallery-image {
    width: 100%;
    height: 250px;
    object-fit: cover;
  }
}

@media screen and (max-width: 768px) {
  .user-header {
    flex-direction: column;
    text-align: center;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .image-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
  
  .gallery-image {
    height: 200px;
  }
}

.avatar-container {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.05);
    
    .avatar-hover-mask {
      opacity: 1;
    }
  }
}

.user-avatar {
  border: 4px solid var(--bgColor1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.avatar-hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  
  .el-icon {
    font-size: 24px;
    color: white;
    margin-bottom: 8px;
  }
  
  span {
    color: white;
    font-size: 14px;
  }
}

.upload-loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  
  .el-icon {
    font-size: 24px;
    color: white;
    margin-bottom: 8px;
  }
  
  span {
    color: white;
    font-size: 14px;
  }
}

.rotating {
  animation: rotate 1.5s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: var(--bgColor2);
  display: flex;
  justify-content: center;
  align-items: center;
  
  .el-icon {
    font-size: 48px;
    color: var(--textColor2);
  }
}
</style>
