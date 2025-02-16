<template>
  <div class="body">
    <div class="user-container">
      <div class="user-card">
        <!-- 头像和用户名部分 -->
        <div class="user-header">
          <el-avatar shape="circle" :size="120" class="hover-scale">
            <h2>{{ userinfo.userName.charAt(0) }}</h2>
          </el-avatar>
          
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
              <el-button @click="setNameStaus" class="edit-btn">
                <el-icon :size="16"><Edit /></el-icon>
                修改昵称
              </el-button>
            </div>
          </div>
        </div>

        <!-- IT币信息 -->
        <div class="balance-section">
          <div class="balance-card">
            <h3>剩余IT币</h3>
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
          <h3>我的绘画作品</h3>
          <el-scrollbar height="400px">
            <div class="image-grid">
              <div v-for="item in imageList" :key="item" class="image-item hover-scale">
                <el-image 
                  :src="imageUrl+item" 
                  fit="cover" 
                  :preview-src-list="[imageUrl+item]"
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
import {getUserDraw, GetUserInfo, updateUserName} from "../../api/BSideApi";
import router from "@/router";
import {ElNotification} from "element-plus";

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

    onMounted(() => {
      imageUrl.value = process.env.VUE_APP_IMAGE;
      getUser()
    })

    async function getUser() {
      const res = await GetUserInfo();
      store.commit("setUserinfo", res);

      if(res.avatar !=null){
        userinfo.value.avatar = res.avatar;
      }
      if (res.userName !=null){
        userinfo.value.userName= res.userName;
      }
      userinfo.value.frequency= res.frequency;
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

    return {
      userinfo,
      imageUrl,
      showParam,
      imageList,
      getImageList,
      editNameStaus,
      savaName,
      setNameStaus
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
      color: var(--el-text-color-primary);
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
    h3 {
      margin: 0;
      opacity: 0.9;
    }
    .balance-amount {
      font-size: 32px;
      font-weight: bold;
      margin-top: 10px;
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
</style>
