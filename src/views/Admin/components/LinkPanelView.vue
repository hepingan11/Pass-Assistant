<template>
  <div class="body">
    <div class="content">
      顶图链接预览(可在服务器配置修改)：<br>
      <el-image :src="topImgUrl" style="height: 250px;border-radius: 10px" fit="scale-down"></el-image>
      <div style="margin-top: 10px">
        <div v-for="item in linkList" :key="item" class="news-item">
          <div class="news-image">
            <el-image :src="imageUrl+item.linkImg" fit="contain" style="width: 200px"></el-image>
          </div>
          <div class="news-description">
            <h2>{{ item.linkName }}</h2>
            <p>ID:{{item.linkId}},介绍:{{ item.linkIntro}}</p>
          </div>
          <el-button
              type="primary"
              @click="handle(item.linkUrl)"
              target="_blank"
              rel="noopener noreferrer"
              style="margin-top: 50px"
              color="var(--themeColor2)"
          >
            查看
          </el-button>
          <el-button
              v-if="item.isPublic===0"
              type="primary"
              @click="allowLink(item.linkId)"
              target="_blank"
              rel="noopener noreferrer"
              style="margin-top: 50px"
              color="var(--themeColor2)"
          >
            通过审核
          </el-button>
          <el-button
              v-if="item.isPublic===1"
              type="primary"
              @click="refuseLink(item.linkId)"
              target="_blank"
              rel="noopener noreferrer"
              style="margin-top: 50px"
              color="var(--themeColor2)"
          >
            撤销通过
          </el-button>
          <el-button
              v-if="item.isHot===0"
              type="primary"
              @click="setLinkHot(item.linkId)"
              target="_blank"
              rel="noopener noreferrer"
              style="margin-top: 50px"
              color="var(--themeColor2)"
          >
            设为热门
          </el-button>
          <el-button
              v-if="item.isHot===1"
              type="primary"
              @click="deleteLinkHot(item.linkId)"
              target="_blank"
              rel="noopener noreferrer"
              style="margin-top: 50px"
              color="var(--themeColor2)"
          >
            取消热门
          </el-button>
        </div>

      </div>
      <div v-if="linkList.length === 0" style="text-align: center; margin-top: 100px">
        好像没有数据呃の
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
.body{
  background-color: var(--bgColor2);
  scroll-behavior: smooth;
  width: 100%;
  flex-direction: column;
  align-items: center;
  display: flex;
  overflow: auto;
}

.news-item {
  display: flex;
  width: 100%;
  margin-bottom: 15px;

}

.news-image {
  flex: 1;
}

.news-description {
  flex: 2;
  padding: 0 20px;
}



h2 {
  margin-bottom: 10px;
  font-size: 1.5rem;
}

p {
  color: #555;
}
</style>
