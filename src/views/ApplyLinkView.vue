<template>
  <div class="body">
    <div class="container">
      <div class="content-wrapper">
        <!-- 左侧申请表单 -->
        <div class="form-section">
          <el-button @click="this.$router.go(-1)" class="back-btn" color="var(--themeColor1)">
            <el-icon><ArrowLeft /></el-icon>返回
          </el-button>
          
          <div class="form-item">
            <span class="form-label">链接名称</span>
            <el-input 
              v-model="form.linkName" 
              placeholder="例如: Pass Assistant"
              class="form-input"
            />
          </div>

          <div class="form-item">
            <span class="form-label">链接地址</span>
            <el-input 
              v-model="form.linkUrl" 
              placeholder="https://...."
              class="form-input"
            />
          </div>

          <div class="form-item">
            <span class="form-label">链接简介</span>
            <el-input 
              v-model="form.linkIntro" 
              type="textarea"
              placeholder="简单介绍一下这个网站..."
              class="form-input"
            />
          </div>

          <div class="form-item">
            <span class="form-label">链接分区</span>
            <el-select
              v-model="form.linkSort"
              placeholder="选择分区"
              class="form-input"
            >
              <el-option
                v-for="item in sorts"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>

          <div class="form-item">
            <span class="form-label">链接图标</span>
            <el-upload
              class="upload-box"
              :auto-upload="false"
              :on-change="onChange"
              :show-file-list="false"
            >
              <div class="upload-content">
                <img v-if="tempFile" :src="tempFile" class="preview-img"/>
                <div v-else class="upload-placeholder">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <span>点击上传图标</span>
                </div>
              </div>
            </el-upload>
          </div>

          <el-button 
            @click="applyLink" 
            :loading="load === 1"
            class="submit-btn"
            color="var(--themeColor1)"
          >
            {{ load === 0 ? '提交申请' : '提交中...' }}
          </el-button>
        </div>

        <!-- 右侧申请列表 -->
        <div class="list-section">
          <h2>我的申请记录</h2>
          <div v-if="!userLinkList || userLinkList.length === 0" class="empty-state">
            <p>暂无申请记录</p>
          </div>
          
          <div class="link-list">
            <div v-for="item in userLinkList" :key="item.linkId" class="link-card">
              <div class="link-image">
                <el-image :src="imageUrl+item.linkImg" fit="cover"></el-image>
              </div>
              <div class="link-info">
                <h3>{{ item.linkName }}</h3>
                <p class="link-intro">{{ item.linkIntro }}</p>
                <div class="link-meta">
                  <el-tag size="small">{{ item.linkSort }}</el-tag>
                  <div class="link-actions">
                    <el-button type="primary" size="small" @click="handle(item.linkUrl)">
                      访问
                    </el-button>
                    <el-button type="danger" size="small" @click="deleteLink(item.linkId)">
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import store from "@/store";
import {onMounted, ref} from "vue";
import {ElNotification} from "element-plus";
import {ApplyLink, deleteLinkById, GetUserLinkList} from "../../api/BSideApi";

export default {
  name: "ApplyLinkView",
  computed: {
    store() {
      return store;
    },
  },
  methods: {

  },
  setup() {
    const form = ref({
      linkName: '',
      linkUrl: '',
      linkIntro: '',
      linkSort: '',
      images: ''
    });

    const tempFile = ref('')

    function handle(url){
      window.open(url,'_blank');
    }

    const userLinkList = ref([])

    const sorts = [
      {
        value: 'tools',
        label: '实用工具',
      },
      {
        value: 'ai',
        label: 'Ai类',
      },
      {
        value: 'hax',
        label: '黑科技',
      },
      {
        value: 'hot',
        label: '知名网站',
      },
      {
        value: 'other',
        label: '其它',
      },
    ]

    const imageUrl = ref('')

    function setCss() {
      let flag = navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)
      if (flag){
        let byClass = document.getElementsByClassName('news-item')[0].style
        byClass.flexDirection = 'column';
        byClass.height = '29vh';
      }
    }

    onMounted(() => {
      imageUrl.value = process.env.VUE_APP_IMAGE;
      if (store.getters.userinfo) {
        getUserLinkList();
        setCss();
        getUserLinkList();
      }
    })


    async function getUserLinkList() {
      userLinkList.value = await GetUserLinkList();
    }


    function onChange(e) {
      console.log(e.raw.type)
      if (e.raw.type === 'image/jpg' || e.raw.type === 'image/png' || e.raw.type === 'image/jpeg') {
        if (e.raw.size / 1024 / 1024 > 4) {
          ElNotification({
            title: "错误",
            message: '图片大小不得超过4MB',
            type: "error",
          });
          return false
        }
        new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.onload = (event) => {
            tempFile.value = event.target.result
            form.value.images = e.raw
            console.log(e)
            resolve(e);
          };
          reader.onerror = (error) => {
            reject(error);
          };
          reader.readAsDataURL(e.raw);
        });
      } else {
        ElNotification({
          title: "错误",
          message: '请上传正确的图片',
          type: "error",
        });
        return false
      }
    }
    const load = ref(0)
    async function applyLink() {
      let value = form.value;
      if (!value.linkUrl) {
        ElNotification({
          title: "错误",
          message: '请设置链接地址',
          type: "error",
        });
        return
      }
      if (!value.linkName) {
        ElNotification({
          title: "错误",
          message: '请设置链接名称',
          type: "error",
        });
        return
      }
      if (!value.linkIntro) {
        ElNotification({
          title: "错误",
          message: '请设置链接简介',
          type: "error",
        });
        return
      }
      if (!tempFile.value) {
        ElNotification({
          title: "错误",
          message: '请设置链接封面图',
          type: "error",
        });
        return
      }
      if (!value.linkSort) {
        ElNotification({
          title: "错误",
          message: '请设置链接分类分区',
          type: "error",
        });
        return
      }
      const formData = new FormData();
      for (const key in form.value) {
        if (form.value[key]) {
          formData.append(key, form.value[key]);
          console.log(key)
        }
      }
      try {
        load.value = 1
        const promise = await ApplyLink(formData);
        if (!promise){
          ElNotification({
            title: "申请成功",
            message: '请刷新页面查看,可以联系1973016127@qq.com加速申请',
            type: "success",
          });
          location.reload()
        }
      } catch (e) {
        ElNotification({
          title: "错误",
          message: e,
          type: "error",
        });
      }
    }

    async function deleteLink(id) {
      const value = await deleteLinkById(id);
      if (!value){
        ElNotification({
          title: "删除成功",
          message: '请刷新页面查看',
          type: "success",
        });
      }
    }

    return{
      form,
      tempFile,
      onChange,
      applyLink,
      userLinkList,
      sorts,
      handle,
      imageUrl,
      deleteLink,
      load
    }
  }
}

</script>

<style lang="scss" scoped>
.body {
  min-height: 100vh;
  padding: 20px;
  overflow-y: auto;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease;
}

.content-wrapper {
  display: flex;
  gap: 40px;
  padding: 20px;
}

.form-section {
  flex: 1;
  padding: 30px;
  border-radius: 12px;
  animation: slideIn 0.5s ease;
}

.back-btn {
  margin-bottom: 20px;
}

.form-item {
  margin-bottom: 25px;
  animation: fadeIn 0.5s ease;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  color: var(--textColor2);
  font-size: 14px;
}

.form-input {
  width: 80%;
}

.upload-box {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
}

.upload-content {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  text-align: center;
  color: #909399;
}

.upload-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.submit-btn {
  width: 80%;
  margin-top: 20px;
  height: 40px;
  border: none;
}

.list-section {
  flex: 1;
  animation: slideIn 0.5s ease 0.2s;
  max-height: 80vh;
  overflow-y: auto;
  padding-right: 10px;
}

.list-section h2 {
  margin: 0 0 20px 0;
  color: var(--textColor2);
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.link-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.link-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  border-radius: 12px;
  transition: all 0.3s ease;
  background-color: var(--dColor2);
}

.link-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.link-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.link-info {
  flex: 1;
  min-width: 0;
}

.link-info h3 {
  margin: 0 0 10px 0;
  color: var(--textColor2);
}

.link-intro {
  margin: 0 0 15px 0;
  color: #909399;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.link-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.link-actions {
  display: flex;
  gap: 10px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media screen and (max-width: 1024px) {
  .content-wrapper {
    flex-direction: column;
  }
  
  .form-section, .list-section {
    width: 100%;
  }
}

@media screen and (max-width: 768px) {
  .list-section {
    max-height: none;
    overflow-y: visible;
    padding-right: 0;
  }

  .link-card {
    flex-direction: column;
  }
  
  .link-image {
    width: 100%;
    height: 200px;
  }
}
</style>
