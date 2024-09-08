<template>
  <div class="container">
    <el-button @click="drawer = true" color="var(--themeColor1)" style="position: absolute">列表</el-button>
    <iframe frameborder="false" :src=url style="width: 100%;height: 100%"></iframe>
  </div>
  <el-drawer v-model="drawer" title="I am the title" :with-header="false" style="background-color: var(--bgColor1);max-width: 400px;min-width: 200px" >
    <div v-for="item in urlList"
         :key="item"
         style="margin-bottom: 35px">
      <span>
        {{item.name}}
        <el-button @click="chooseWork(item.url)" color="var(--themeColor1)" style="float: right;">选择</el-button>
      </span>
    </div>
  </el-drawer>
</template>

<script>
import {onMounted, ref} from "vue";
import {GetWorkList} from "../../api/BSideApi";
import {ElNotification} from "element-plus";

  export default {
    name: 'FunnyView',
    setup(){
      const drawer = ref(false);
      const urlList = ref([]);
      const url = ref('')

      onMounted(async () => {
        urlList.value = await GetWorkList()
        url.value = urlList.value[0].url;
        ElNotification({
          title: "部分作品推荐使用VPN，不然加载可能有点慢",
          type: "info",
        });
      })

      function chooseWork(value){
        url.value=value
      }
      return {
        drawer,
        urlList,
        url,
        chooseWork,

      }
    }
  }
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;
}
</style>