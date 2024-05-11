<template>
  <div class="body">
    <div class="content">
      顶图链接预览(可在服务器配置修改)：<br>
      <el-image :src="topImgUrl" style="height: 250px" fit="scale-down"></el-image>
    </div>

  </div>
</template>

<script>
  import store from "@/store";
  import {onMounted, ref} from "vue";
  import {getTopImgUrl} from "../../../../api/BSideApi";


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


      onMounted( () =>{
        if (store.getters.userinfo && store.getters.userinfo.type === "ADMIN") {
          getTopImg();
        }
      })

      return{
        topImgUrl,
        getTopImg,
      }
    }
  }
</script>

<style scoped>
.body{
  scroll-behavior: smooth;
  width: 100%;
  flex-direction: column;
  align-items: center;
  display: flex;
  overflow: auto;
}
</style>
