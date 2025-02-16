<template>
  <div class="body">
    <div class="content">
      <h3 class="section-title">顶图链接预览</h3>
      <div class="top-image-container">
        <el-image :src="topImgUrl" class="top-preview" fit="scale-down"></el-image>
      </div>
      
      <div class="link-list">
        <el-card 
          v-for="item in linkList" 
          :key="item.linkId" 
          class="link-card"
          v-motion
          :initial="{ opacity: 0, y: 50 }"
          :enter="{ opacity: 1, y: 0 }"
        >
          <div class="card-content">
            <div class="link-image hover-scale">
              <el-image :src="imageUrl+item.linkImg" fit="cover"></el-image>
            </div>
            
            <div class="link-info">
              <div class="link-header">
                <h3 class="fade-in">{{ item.linkName }}</h3>
                <div class="link-status">
                  <el-tag 
                    :type="item.isPublic === 1 ? 'success' : 'warning'"
                    class="status-tag"
                  >
                    {{ item.isPublic === 1 ? '已通过' : '待审核' }}
                  </el-tag>
                  <el-tag 
                    v-if="item.isHot === 1" 
                    type="danger"
                    class="status-tag"
                  >热门</el-tag>
                </div>
              </div>
              
              <p class="link-intro">ID: {{item.linkId}} | {{item.linkIntro}}</p>
              
              <div class="action-buttons">
                <el-button
                  type="primary"
                  @click="handle(item.linkUrl)"
                  size="small"
                  class="action-btn"
                >查看</el-button>
                
                <el-button
                  v-if="item.isPublic === 0"
                  type="success"
                  @click="allowLink(item.linkId)"
                  size="small"
                  class="action-btn"
                >通过审核</el-button>
                
                <el-button
                  v-if="item.isPublic === 1"
                  type="warning"
                  @click="refuseLink(item.linkId)"
                  size="small"
                  class="action-btn"
                >撤销通过</el-button>
                
                <el-button
                  v-if="item.isHot === 0"
                  type="danger"
                  @click="setLinkHot(item.linkId)"
                  size="small"
                  class="action-btn"
                >设为热门</el-button>
                
                <el-button
                  v-if="item.isHot === 1"
                  type="info"
                  @click="deleteLinkHot(item.linkId)"
                  size="small"
                  class="action-btn"
                >取消热门</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <div v-if="linkList.length === 0" class="empty-state">
        暂无数据
      </div>
    </div>
  </div>
</template>

<script>
  import store from "@/store";
  import {onMounted, ref} from "vue";
  import {
    allowUserLink,
    deleteLinkHotById,
    getLinkList,
    getTopImgUrl,
    refuseLinkById,
    setLinkHotById
  } from "../../../../api/BSideApi";
  import {ElNotification} from "element-plus";


  export default {
    name: "LinkPanelView",
    computed: {
      store() {
        return store;
      },
    },
    setup(){
      const topImgUrl = ref('')

      async function getTopImg() {
        let promise =  await getTopImgUrl();
        if (promise){
          topImgUrl.value = promise;
        }
      }

      const imageUrl= ref('');

      function handle(url){
        window.open(url,'_blank');
      }


      onMounted( () =>{
        if (store.getters.userinfo && store.getters.userinfo.type === "ADMIN") {
          imageUrl.value = process.env.VUE_APP_IMAGE;
          getTopImg();
          LinkList();
        }
      })

      const linkList= ref([]);
      async function LinkList() {
        linkList.value = await getLinkList();
      }

      async function refuseLink(id) {
        const value = await refuseLinkById(id);
        if (!value){
          ElNotification({
            title: "撤销通过成功",
            message: '------',
            type: "success",
          });
        }
      }
      async function allowLink(id) {
        const promise = await allowUserLink(id);
        if (!promise){
          ElNotification({
            title: "通过",
            message: '-----',
            type: "success",
          });
        }
      }

      async function setLinkHot(id) {
        const value = await setLinkHotById(id);
        if (!value){
          ElNotification({
            title: "设置热门成功",
            message: '-----',
            type: "success",
          });
        }
      }

      async function deleteLinkHot(id) {
        const value = await deleteLinkHotById(id);
        if (!value){
          ElNotification({
            title: "取消热门成功",
            message: '-----',
            type: "success",
          });
        }
      }
      return{
        topImgUrl,
        getTopImg,
        imageUrl,
        linkList,
        handle,
        allowLink,
        refuseLink,
        setLinkHot,
        deleteLinkHot,
      }
    }
  }
</script>

<style scoped>
.body {
  padding: 20px;
  background-color: var(--bgColor2);
  height: calc(100vh - 40px); /* 减去padding的高度 */
  overflow-y: auto; /* 添加垂直滚动 */
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 400px; /* 底部留出更多空间 */
}

.section-title {
  margin-bottom: 20px;
  color: var(--textColor2);
  font-size: 24px;
  font-weight: 600;
  opacity: 0;
  animation: fadeIn 0.5s ease forwards;
}

.top-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin-bottom: 30px;
  opacity: 0;
  animation: fadeIn 0.5s ease 0.2s forwards;
}

.top-preview {
  height: 250px;
  border-radius: 10px;
  max-width: 800px;
  width: 100%;
  transition: transform 0.3s ease;
}

.top-preview:hover {
  transform: scale(1.02);
}

.link-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.link-card {
  background-color: var(--bgColor1);
  border: none;
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
}

.link-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-content {
  display: flex;
  gap: 20px;
  padding: 20px;
}

.link-image {
  width: 200px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.hover-scale:hover {
  transform: scale(1.05);
}

.link-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.link-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.link-header h3 {
  margin: 0;
  color: var(--textColor2);
  font-size: 18px;
}

.link-status {
  display: flex;
  gap: 10px;
}

.status-tag {
  transition: all 0.3s ease;
}

.status-tag:hover {
  transform: scale(1.05);
}

.link-intro {
  color: #909399;
  margin: 0 0 15px 0;
  line-height: 1.5;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.action-btn {
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.empty-state {
  text-align: center;
  padding: 60px;
  color: #909399;
  font-size: 16px;
  animation: fadeIn 0.5s ease;
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

:deep(.el-card__body) {
  padding: 0;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .card-content {
    flex-direction: column;
  }

  .link-image {
    width: 100%;
    height: 160px;
  }

  .action-buttons {
    justify-content: center;
  }
}
</style>
